package com.flag.engine.servelets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.flag.engine.models.Item;
import com.flag.engine.models.PMF;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

public class UploadImage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(UploadImage.class.getName());
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Map<String, List<BlobKey>> blobKeyMap = blobstoreService.getUploads(req);
		Map<String, List<BlobInfo>> blobInfoMap = blobstoreService.getBlobInfos(req);

		List<BlobKey> blobKeys = blobKeyMap.get("image");
		List<BlobInfo> blobInfos = blobInfoMap.get("image");

		String fileName = blobInfos.get(0).getFilename();
		if (fileName.indexOf('.') > -1)
			fileName = fileName.substring(0, fileName.lastIndexOf('.'));

		if (!matchWithItem(fileName, blobKeys.get(0).getKeyString(), req.getParameter("shopId"))) {
			res.setContentType("application/json");
			PrintWriter out = res.getWriter();
			out.print("{\"url\": \"" + blobKeys.get(0).getKeyString() + "\"}");
			out.flush();
		}
	}

	@SuppressWarnings("unchecked")
	private boolean matchWithItem(String fileName, String keyString, String shopId) {
		PersistenceManager pm = PMF.getPersistenceManagerSQL();
		String url = "https://genuine-evening-455.appspot.com/serve?blob-key=" + keyString;
		log.warning("filename : " + fileName);

		Query query = pm.newQuery(Item.class);
		query.setFilter("barcodeId == fileName");
		query.declareParameters("String fileName");
		List<Item> items = (List<Item>) pm.newQuery(query).execute(fileName);

		if (items.isEmpty())
			return false;

		if (items.size() > 1 && shopId != null) {
			for (Item item : items)
				if (item.getShopId().equals(Long.valueOf(shopId))) {
					item.setThumbnailUrl(url);
					pm.close();
				}
		} else {
			Item item = items.get(0);
			item.setThumbnailUrl(url);
			pm.close();
		}

		return true;
	}
}

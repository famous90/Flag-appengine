package com.flag.engine.servelets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

public class Upload extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);

		List<BlobKey> blobKeys = blobs.get("image");

		StringBuilder jsonStr = new StringBuilder("{\"url\": \"");
		if (blobKeys != null && !blobKeys.isEmpty())
			jsonStr.append(blobKeys.get(0).getKeyString());
		jsonStr.append("\"}");
		
		res.setContentType("application/json");
		PrintWriter out = res.getWriter();
		out.print(jsonStr.toString());
		out.flush();
	}
}

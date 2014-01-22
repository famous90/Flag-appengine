package com.anb.flag.engine.apis;

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.anb.flag.engine.constants.Constants;
import com.anb.flag.engine.models.Image;
import com.anb.flag.engine.models.PMF;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;

@Api(name = "flagengine", version = "v1", clientIds = { Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID,
		Constants.API_EXPLORER_CLIENT_ID }, audiences = { Constants.ANDROID_AUDIENCE })
public class Images {
	private static final Logger log = Logger.getLogger(Flags.class.getName());
	
	@ApiMethod(name = "images.insert", httpMethod = "post")
	public Image insert(Image image) {
		log.warning("insert image: " + image.toString());
		
		PersistenceManager pm = PMF.getPersistenceManager();
		pm.makePersistent(image);
		pm.close();
		
		Image response = new Image();
		response.setId(image.getId());
		
		return response;
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "images.get")
	public Image get(@Nullable @Named("id") long id) {
		PersistenceManager pm = PMF.getPersistenceManager();
		
		Query query = pm.newQuery(Image.class);
		query.setFilter("id == imageId");
		query.declareParameters("long imageId");
		List<Image> images = (List<Image>) pm.newQuery(query).execute(id);
		
		if (images.size() < 1)
			return null;
		else
			return images.get(0);
	}
}

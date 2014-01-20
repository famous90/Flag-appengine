package com.anb.flag.engine.apis;

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.anb.flag.engine.constants.Constants;
import com.anb.flag.engine.models.PMF;
import com.anb.flag.engine.models.Shop;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;

@Api(name = "flagengine", version = "v1", clientIds = { Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID,
		Constants.API_EXPLORER_CLIENT_ID }, audiences = { Constants.ANDROID_AUDIENCE })
public class Shops {
	private static final Logger log = Logger.getLogger(Flags.class.getName());

	@ApiMethod(name = "shops.insert", httpMethod = "post")
	public Shop insert(Shop shop) {
		log.warning("insert shop: " + shop.toString());
		
		PersistenceManager pm = PMF.getPersistenceManager();
		pm.makePersistent(shop);
		pm.close();
		
		return shop;
	}
	
	@SuppressWarnings("unchecked")
	@ApiMethod(name = "shops.get")
	public Shop get(@Nullable @Named("id") long id) {
		PersistenceManager pm = PMF.getPersistenceManager();

		Query query = pm.newQuery(Shop.class);
		query.setFilter("id == shopId");
		query.declareParameters("long shopId");
		List<Shop> shops = (List<Shop>) pm.newQuery(query).execute(id);
		
		if (shops.size() < 1)
			return null;
		else
			return shops.get(0);
	}
}

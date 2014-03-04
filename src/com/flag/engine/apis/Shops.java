package com.flag.engine.apis;

import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import com.flag.engine.constants.Constants;
import com.flag.engine.models.PMF;
import com.flag.engine.models.Shop;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;

@Api(name = "flagengine", version = "v1", clientIds = { Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID,
		Constants.API_EXPLORER_CLIENT_ID }, audiences = { Constants.ANDROID_AUDIENCE })
public class Shops {
	private static final Logger log = Logger.getLogger(Flags.class.getName());

	@ApiMethod(name = "shops.insert", path = "shop", httpMethod = "post")
	public Shop insert(Shop shop) {
		log.warning("insert shop: " + shop.toString());

		PersistenceManager pm = PMF.getPersistenceManagerSQL();
		pm.makePersistent(shop);
		pm.close();

		return shop;
	}

	@ApiMethod(name = "shops.get", path = "shop", httpMethod = "get")
	public Shop get(@Nullable @Named("userId") long userId, @Nullable @Named("id") long id) {
		PersistenceManager pm = PMF.getPersistenceManagerSQL();
		Shop shop = null;

		try {
			shop = pm.getObjectById(Shop.class, id);
			shop.setRewardedForUser(userId);
		} catch (JDOObjectNotFoundException e) {
			return null;
		}

		return shop;
	}
	
	@ApiMethod(name = "shops.update", path = "shop", httpMethod = "put")
	public Shop update(Shop shop) {
		log.warning("update shop: " + shop.toString());
		
		PersistenceManager pm = PMF.getPersistenceManagerSQL();
		Shop target = null;
		
		try {
			target = pm.getObjectById(Shop.class, shop.getId());
			target.update(shop);
			pm.makePersistent(target);
		} catch(JDOObjectNotFoundException e) {
			return null;
		} finally {
			pm.close();
		}
		
		return target;
	}
}

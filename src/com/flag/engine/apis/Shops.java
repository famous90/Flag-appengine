package com.flag.engine.apis;

import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import com.flag.engine.constants.Constants;
import com.flag.engine.models.Beacon;
import com.flag.engine.models.PMF;
import com.flag.engine.models.Shop;
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

	@ApiMethod(name = "shops.get")
	public Shop get(@Nullable @Named("userId") long userId, @Nullable @Named("id") long id) {
		PersistenceManager pm = PMF.getPersistenceManager();
		Shop shop = null;

		try {
			shop = pm.getObjectById(Shop.class, id);
			shop.setRewarded(Shop.isRewarded(userId, id));
		} catch (JDOObjectNotFoundException e) {
			return null;
		}

		return shop;
	}

	@ApiMethod(name = "shops.beacon", path = "beacon")
	public Shop beacon(@Nullable @Named("userId") long userId, @Nullable @Named("beaconId") String beaconId) {
		PersistenceManager pm = PMF.getPersistenceManager();
		Shop shop = null;
		
		try {
			Beacon beacon = pm.getObjectById(Beacon.class, beaconId);
			shop = pm.getObjectById(Shop.class, beacon.getShopId());
			shop.setRewarded(Shop.isRewarded(userId, shop.getId()));
		} catch (JDOObjectNotFoundException e) {
			return null;
		}
		
		return shop;
	}
}

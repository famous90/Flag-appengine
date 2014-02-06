package com.flag.engine.apis;

import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.flag.engine.constants.Constants;
import com.flag.engine.models.Beacon;
import com.flag.engine.models.PMF;
import com.flag.engine.models.Shop;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;

@Api(name = "flagengine", version = "v1", clientIds = { Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID,
		Constants.API_EXPLORER_CLIENT_ID }, audiences = { Constants.ANDROID_AUDIENCE })
public class Beacons {
	private static final Logger log = Logger.getLogger(Flags.class.getName());

	@ApiMethod(name = "beacons.insert", path = "beacon", httpMethod = "post")
	public void insert(Beacon beacon) {
		log.warning("insert beacon: " + beacon.toString());

		PersistenceManager pm = PMF.getPersistenceManager();
		pm.makePersistent(beacon);
		pm.close();
	}

	@ApiMethod(name = "beacons.shop", path = "beacon", httpMethod = "get")
	public Shop shop(@Nullable @Named("userId") long userId, @Named("beaconId") String beaconId) {
		PersistenceManager pm = PMF.getPersistenceManager();
		Shop shop = null;

		try {
			Beacon beacon = pm.getObjectById(Beacon.class, beaconId);
			shop = pm.getObjectById(Shop.class, beacon.getShopId());
			shop.setRewardedForUser(userId);
		} catch (JDOObjectNotFoundException e) {
			return null;
		}

		return shop;
	}

	@ApiMethod(name = "beacons.remove", path = "beacon", httpMethod = "delete")
	public void remove(Beacon beacon) {
		PersistenceManager pm = PMF.getPersistenceManager();

		Query query = pm.newQuery(Beacon.class);
		query.setFilter("id == beaconId");
		query.declareParameters("String beaconId");
		query.deletePersistentAll(beacon.getId());

		pm.close();
	}
}

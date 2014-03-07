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
	private static final Logger log = Logger.getLogger(Beacons.class.getName());

	@ApiMethod(name = "beacons.insert", path = "beacon", httpMethod = "post")
	public Beacon insert(Beacon beacon) {
		log.warning("insert beacon: " + beacon.toString());

		PersistenceManager pm = PMF.getPersistenceManager();
		pm.makePersistent(beacon);
		pm.close();
		
		return beacon;
	}

	@ApiMethod(name = "beacons.get", path = "beacon", httpMethod = "get")
	public Shop get(@Nullable @Named("userId") long userId, @Named("beaconId") String beaconId) {
		PersistenceManager pm = PMF.getPersistenceManager();
		PersistenceManager pmSQL = PMF.getPersistenceManagerSQL();
		Shop shop = null;
		
		try {
			Beacon beacon = pm.getObjectById(Beacon.class, beaconId.toUpperCase());
			shop = pmSQL.getObjectById(Shop.class, beacon.getShopId());
			shop.setRewardedForUser(userId);
		} catch (JDOObjectNotFoundException e) {
			return null;
		}

		return shop;
	}

	// TODO beacon list
	
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

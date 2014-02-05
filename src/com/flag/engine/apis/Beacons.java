package com.flag.engine.apis;

import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.flag.engine.constants.Constants;
import com.flag.engine.models.Beacon;
import com.flag.engine.models.PMF;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;

@Api(name = "flagengine", version = "v1", clientIds = { Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID,
		Constants.API_EXPLORER_CLIENT_ID }, audiences = { Constants.ANDROID_AUDIENCE })
public class Beacons {
	private static final Logger log = Logger.getLogger(Flags.class.getName());
	
	@ApiMethod(name = "beacons.insert")
	public void insert(Beacon beacon) {
		log.warning("insert beacon: " + beacon.toString());
		
		PersistenceManager pm = PMF.getPersistenceManager();
		pm.makePersistent(beacon);
		pm.close();
	}
	
	@ApiMethod(name = "beacons.remove")
	public void remove(Beacon beacon) {
		PersistenceManager pm = PMF.getPersistenceManager();
		
		Query query = pm.newQuery(Beacon.class);
		query.setFilter("id == beaconId");
		query.declareParameters("String beaconId");
		query.deletePersistentAll(beacon.getId());
		
		pm.close();
	}
}

package com.flag.engine.apis;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.flag.engine.constants.Constants;
import com.flag.engine.models.Beacon;
import com.flag.engine.models.BeaconTag;
import com.flag.engine.models.Flag;
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
		log.info("insert beacon: " + beacon.toString());

		PersistenceManager pm = PMF.getPersistenceManager();
		PersistenceManager pmSQL = PMF.getPersistenceManagerSQL();
		
		beacon.setId(beacon.getId().toUpperCase());
		beacon.setCreatedAt(new Date().getTime());
		
		try {
			Flag flag = pmSQL.getObjectById(Flag.class, beacon.getFlagId());
			beacon.setShopId(flag.getShopId());
			beacon.setLat(flag.getLat());
			beacon.setLon(flag.getLon());
			beacon.setName(flag.getShopName());
			pm.makePersistent(beacon);
		} catch (JDOObjectNotFoundException e) {
			return null;
		} finally {
			pm.close();
			setTag(new Date().getTime());
		}

		return beacon;
	}

	@ApiMethod(name = "beacons.delete", path = "beacon", httpMethod = "delete")
	public void delete(Beacon beacon) {
		PersistenceManager pm = PMF.getPersistenceManager();

		try {
			Beacon target = pm.getObjectById(Beacon.class, beacon.getId());
			pm.deletePersistent(target);
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
			setTag(new Date().getTime());
		}
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "beacons.get.all", path = "beacon_all", httpMethod = "get")
	public List<Beacon> getAll(@Nullable @Named("tag") long tag) {
		PersistenceManager pm = PMF.getPersistenceManager();

		if (!checkTag(tag))
			return null;
		
		Query query = pm.newQuery(Beacon.class);
		List<Beacon> beacons = (List<Beacon>) pm.newQuery(query).execute();
		return beacons;
	}

	@ApiMethod(name = "beacons.get", path = "beacon", httpMethod = "get")
	public Shop get(@Nullable @Named("userId") long userId, @Named("beaconId") String beaconId) {
		PersistenceManager pm = PMF.getPersistenceManager();
		PersistenceManager pmSQL = PMF.getPersistenceManagerSQL();
		List<Shop> shops = new ArrayList<Shop>();
		
		try {
			Beacon beacon = pm.getObjectById(Beacon.class, beaconId.toUpperCase());
			Shop shop = pmSQL.getObjectById(Shop.class, beacon.getShopId());
			Flag flag = pmSQL.getObjectById(Flag.class, beacon.getFlagId());
			
			Shop beaconShop = new Shop(shop);
			beaconShop.setName(flag.getShopName());
			shops.add(beaconShop);
			Shop.setRelatedVariables(shops, userId);
		} catch (JDOObjectNotFoundException e) {
			return null;
		}

		return shops.get(0);
	}
	
	private void setTag(long timestamp) {
		PersistenceManager pm = PMF.getPersistenceManager();
		BeaconTag tag = new BeaconTag(timestamp);
		pm.makePersistent(tag);
		pm.close();
	}
	
	@SuppressWarnings("unchecked")
	private boolean checkTag(long timestamp) {
		PersistenceManager pm = PMF.getPersistenceManager();
		Query query = pm.newQuery(BeaconTag.class);
		query.setOrdering("id desc");
		query.setRange(0, 1);
		List<BeaconTag> tags = (List<BeaconTag>) pm.newQuery(query).execute();
		
		if (tags.isEmpty())
			return false;
		else if (tags.get(0).getId().longValue() > timestamp)
			return true;
		else
			return false;
	}
}

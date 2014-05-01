package com.flag.engine.apis;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.flag.engine.constants.Constants;
import com.flag.engine.models.Flag;
import com.flag.engine.models.FlagCollection;
import com.flag.engine.models.PMF;
import com.flag.engine.models.Shop;
import com.flag.engine.models.UserInfo;
import com.flag.engine.utils.LocationUtils;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;

@Api(name = "flagengine", version = "v1", clientIds = { Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID,
		Constants.API_EXPLORER_CLIENT_ID }, audiences = { Constants.ANDROID_AUDIENCE })
public class Flags {
	private static final Logger log = Logger.getLogger(Flags.class.getName());

	@ApiMethod(name = "flags.insert", path = "flag", httpMethod = "post")
	public Flag insert(Flag flag) {
		log.info("insert flag: " + flag.toString());

		flag.setCreatedAt(new Date().getTime());

		PersistenceManager pm = PMF.getPersistenceManagerSQL();
		pm.makePersistent(flag);
		pm.close();

		return flag;
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "flags.list", path = "flag", httpMethod = "get")
	public FlagCollection list(@Nullable @Named("userId") long userId, @Nullable @Named("lat") double lat, @Nullable @Named("lon") double lon) {
		log.info("list flag: lat=" + lat + " lon=" + lon);

		PersistenceManager pm = PMF.getPersistenceManagerSQL();

		Query query = pm.newQuery(Flag.class);
		query.setFilter("lon > minLon && lon < maxLon && lat > minLat && lat < maxLat");
		query.declareParameters("double minLon, double maxLon, double minLat, double maxLat");
		List<Flag> flags = (List<Flag>) pm.newQuery(query).executeWithArray(lon - LocationUtils.NEAR_DISTANCE_DEGREE,
				lon + LocationUtils.NEAR_DISTANCE_DEGREE, lat - LocationUtils.NEAR_DISTANCE_DEGREE, lat + LocationUtils.NEAR_DISTANCE_DEGREE);

		return new FlagCollection(flags);
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "flags.list.byshop", path = "flag_list_byshop", httpMethod = "get")
	public FlagCollection listByShop(@Nullable @Named("shopId") long shopId) {
		PersistenceManager pm = PMF.getPersistenceManagerSQL();
		Shop shop;

		try {
			shop = pm.getObjectById(Shop.class, shopId);
		} catch (JDOObjectNotFoundException e) {
			return null;
		}

		Query query;
		StringBuilder sbFilter = new StringBuilder("shopId == id");
		StringBuilder sbParam = new StringBuilder("long id");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("id", shopId);

		if (shop.getType() == Shop.TYPE_HQ) {
			query = pm.newQuery(Shop.class);
			query.setFilter("parentId == shopId");
			query.declareParameters("long shopId");
			List<Shop> brShops = (List<Shop>) pm.newQuery(query).execute(shopId);

			for (int i = 0; i < brShops.size(); i++) {
				sbFilter.append(" || shopId == id" + i);
				sbParam.append(", long id" + i);
				paramMap.put("id" + i, brShops.get(i).getId());
			}
		}

		query = pm.newQuery(Flag.class);
		query.setFilter(sbFilter.toString());
		query.declareParameters(sbParam.toString());
		List<Flag> flags = (List<Flag>) pm.newQuery(query).executeWithMap(paramMap);

		return new FlagCollection(flags);
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "flags.list.close", path = "flag_list", httpMethod = "get")
	public FlagCollection listClose(@Nullable @Named("userId") long userId, @Nullable @Named("lat") double lat, @Nullable @Named("lon") double lon) {
		log.info("list flag: lat=" + lat + " lon=" + lon);

		PersistenceManager pm = PMF.getPersistenceManagerSQL();

		try {
			UserInfo userInfo = pm.getObjectById(UserInfo.class, userId);
			userInfo.setLat(lat);
			userInfo.setLon(lon);
		} catch (JDOObjectNotFoundException e) {
			UserInfo userInfo = new UserInfo();
			userInfo.setUserId(userId);
			userInfo.setLat(lat);
			userInfo.setLon(lon);
			pm.makePersistent(userInfo);
		}
		
		Query query = pm.newQuery(Flag.class);
		query.setFilter("lon > minLon && lon < maxLon && lat > minLat && lat < maxLat");
		query.declareParameters("double minLon, double maxLon, double minLat, double maxLat");
		List<Flag> flags = (List<Flag>) pm.newQuery(query).executeWithArray(lon - LocationUtils.CLOSE_DISTANCE_DEGREE,
				lon + LocationUtils.CLOSE_DISTANCE_DEGREE, lat - LocationUtils.CLOSE_DISTANCE_DEGREE, lat + LocationUtils.CLOSE_DISTANCE_DEGREE);
		
		pm.close();
		
		return new FlagCollection(flags);
	}

	@ApiMethod(name = "flags.delete", path = "flag", httpMethod = "delete")
	public void delete(@Named("flagId") Long flagId) {
		PersistenceManager pm = PMF.getPersistenceManagerSQL();

		try {
			Flag target = pm.getObjectById(Flag.class, flagId);
			pm.deletePersistent(target);
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
	}
}

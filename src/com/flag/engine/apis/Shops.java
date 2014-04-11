package com.flag.engine.apis;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.flag.engine.constants.Constants;
import com.flag.engine.models.Flag;
import com.flag.engine.models.Item;
import com.flag.engine.models.PMF;
import com.flag.engine.models.Shop;
import com.flag.engine.models.ShopCollection;
import com.flag.engine.utils.LocationUtils;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;

@Api(name = "flagengine", version = "v1", clientIds = { Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID,
		Constants.API_EXPLORER_CLIENT_ID }, audiences = { Constants.ANDROID_AUDIENCE })
public class Shops {
	private static final Logger log = Logger.getLogger(Flags.class.getName());

	@ApiMethod(name = "shops.insert", path = "shop", httpMethod = "post")
	public Shop insert(Shop shop) {
		log.info("insert shop: " + shop.toString());

		PersistenceManager pm = PMF.getPersistenceManagerSQL();
		pm.makePersistent(shop);
		pm.close();

		return shop;
	}
	
	@SuppressWarnings("unchecked")
	@ApiMethod(name = "shops.init", path = "shop_init", httpMethod = "get")
	public ShopCollection initShops(@Nullable @Named("userId") long userId, @Nullable @Named("lat") double lat, @Nullable @Named("lon") double lon) {
		log.info("user shop: " + userId + "," + lat + "," + lon);
		
		PersistenceManager pm = PMF.getPersistenceManagerSQL();

		Query query = pm.newQuery(Flag.class);
		query.setFilter("lon > minLon && lon < maxLon && lat > minLat && lat < maxLat");
		query.declareParameters("double minLon, double maxLon, double minLat, double maxLat");
		List<Flag> flags = (List<Flag>) pm.newQuery(query).executeWithArray(lon - LocationUtils.NEAR_DISTANCE_DEGREE, lon + LocationUtils.NEAR_DISTANCE_DEGREE,
				lat - LocationUtils.NEAR_DISTANCE_DEGREE, lat + LocationUtils.NEAR_DISTANCE_DEGREE);

		List<Long> ids = new ArrayList<Long>();
		for (Flag flag : flags)
			if (!ids.contains(flag.getShopId()))
				ids.add(flag.getShopId());
		
		if (ids.size() > 0)
			return list(ids);
		else
			return null;
	}

	@ApiMethod(name = "shops.get", path = "shop", httpMethod = "get")
	public Shop get(@Nullable @Named("userId") long userId, @Nullable @Named("id") long id) {
		PersistenceManager pm = PMF.getPersistenceManagerSQL();
		Shop shop = null;

		try {
			shop = pm.getObjectById(Shop.class, id);
			shop.setRewardedForUser(userId);
		} catch (JDOObjectNotFoundException e) {
		}

		return shop;
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "shops.list", path = "shop_list", httpMethod = "get")
	public ShopCollection list(@Nullable @Named("ids") List<Long> ids) {
		log.info("list shop: " + ids);

		PersistenceManager pm = PMF.getPersistenceManagerSQL();
		List<Object> keys = new ArrayList<Object>();
		List<Shop> shops = null;

		for (Long id : ids)
			keys.add(pm.newObjectIdInstance(Shop.class, id));

		shops = (List<Shop>) pm.getObjectsById(keys);

		return new ShopCollection(shops);
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "shops.all", path = "shop_all", httpMethod = "get")
	public ShopCollection all() {
		log.info("all shop");

		PersistenceManager pm = PMF.getPersistenceManagerSQL();
		Query query = pm.newQuery(Shop.class);
		List<Shop> shops = (List<Shop>) pm.newQuery(query).execute();

		return new ShopCollection(shops);
	}

	@ApiMethod(name = "shops.update", path = "shop", httpMethod = "put")
	public Shop update(Shop shop) {
		log.info("update shop: " + shop.toString());

		PersistenceManager pm = PMF.getPersistenceManagerSQL();
		Shop target = null;

		try {
			target = pm.getObjectById(Shop.class, shop.getId());
			target.update(shop);
		} catch (JDOObjectNotFoundException e) {
			return null;
		} finally {
			pm.close();
		}

		return target;
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "shops.delete", path = "shop", httpMethod = "delete")
	public void delete(@Named("shopId") Long shopId) {
		PersistenceManager pm = PMF.getPersistenceManagerSQL();

		try {
			Query query = pm.newQuery(Flag.class);
			query.setFilter("shopId == theShopId");
			query.declareParameters("long theShopId");
			List<Flags> flags = (List<Flags>) pm.newQuery(query).execute(shopId);
			pm.deletePersistentAll(flags);
			
			query = pm.newQuery(Item.class);
			query.setFilter("shopId == theShopId");
			query.declareParameters("long theShopId");
			List<Items> items = (List<Items>) pm.newQuery(query).execute(shopId);
			pm.deletePersistentAll(items);
			
			Shop target = pm.getObjectById(Shop.class, shopId);
			pm.deletePersistent(target);
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
	}
}

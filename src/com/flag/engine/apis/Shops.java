package com.flag.engine.apis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import com.flag.engine.models.Like;
import com.flag.engine.models.PMF;
import com.flag.engine.models.Shop;
import com.flag.engine.models.ShopCollection;
import com.flag.engine.utils.FlagDistanceComparator;
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
		long startTime = new Date().getTime();

		PersistenceManager pm = PMF.getPersistenceManagerSQL();

		Query query = pm.newQuery(Flag.class);
		query.setFilter("lon > minLon && lon < maxLon && lat > minLat && lat < maxLat");
		query.declareParameters("double minLon, double maxLon, double minLat, double maxLat");
		List<Flag> results = (List<Flag>) pm.newQuery(query).executeWithArray(lon - LocationUtils.NEAR_DISTANCE_DEGREE,
				lon + LocationUtils.NEAR_DISTANCE_DEGREE, lat - LocationUtils.NEAR_DISTANCE_DEGREE, lat + LocationUtils.NEAR_DISTANCE_DEGREE);

		log.warning("flag da time: " + (new Date().getTime() - startTime) + "ms");
		log.warning("flag count: " + results.size());

		List<Flag> sortedResults = new ArrayList<Flag>();
		sortedResults.addAll(results);
		Collections.sort(sortedResults, new FlagDistanceComparator(lat, lon));

		List<Long> exIds = new ArrayList<Long>();
		List<Object> ids = new ArrayList<Object>();
		List<Flag> flags = new ArrayList<Flag>();
		for (Flag sortedResult : sortedResults)
			if (!exIds.contains(sortedResult.getShopId())) {
				exIds.add(sortedResult.getShopId());
				ids.add(pm.newObjectIdInstance(Shop.class, sortedResult.getShopId()));
				flags.add(sortedResult);
			}

		log.warning("shopid process time: " + (new Date().getTime() - startTime) + "ms");

		List<Shop> shops = listByIds(ids);

		log.warning("shop da time: " + (new Date().getTime() - startTime) + "ms");

		Shop.setRelatedVariables(shops, userId);

		log.warning("rel-var da time: " + (new Date().getTime() - startTime) + "ms");

		return new ShopCollection(shops, flags);
	}

	@SuppressWarnings("unchecked")
	private List<Shop> listByIds(List<Object> ids) {
		log.warning("list shops: " + ids);
		if (ids == null || ids.isEmpty())
			return new ArrayList<Shop>();

		PersistenceManager pm = PMF.getPersistenceManagerSQL();
		List<Shop> shops = null;

		try {
			shops = (List<Shop>) pm.getObjectsById(ids);
		} catch (JDOObjectNotFoundException e) {
			ids.remove(e.getFailedObject());
			return listByIds(ids);
		}

		return shops;
	}

	@ApiMethod(name = "shop.list", path = "shop_list", httpMethod = "get")
	private ShopCollection list(@Nullable @Named("userId") long userId, @Nullable @Named("ids") List<Long> ids) {
		PersistenceManager pm = PMF.getPersistenceManagerSQL();
		List<Object> shopIds = new ArrayList<Object>();
		for (Long id : ids)
			shopIds.add(pm.newObjectIdInstance(Shop.class, id));
		
		List<Shop> shops = listByIds(shopIds);
		
		Shop.setRelatedVariables(shops, userId);
		
		return new ShopCollection(shops);
	}
	
	@SuppressWarnings("unchecked")
	@ApiMethod(name = "shops.list.reward", path = "shop_list_reward", httpMethod = "get")
	public ShopCollection listReward(@Nullable @Named("userId") long userId, @Nullable @Named("mark") int mark) {
		PersistenceManager pm = PMF.getPersistenceManagerSQL();
		
		Query query = pm.newQuery(Shop.class);
		// temporary filter
		query.setFilter("reward > zero");
		query.declareParameters("int zero");
		List<Shop> shops = (List<Shop>) pm.newQuery(query).execute(0);
		
		Shop.setRelatedVariables(shops, userId);
		
		return new ShopCollection(shops);
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "shops.list.provider", path = "shop_list_provider", httpMethod = "get")
	public ShopCollection listProvider(@Nullable @Named("providerId") long providerId) {
		PersistenceManager pm = PMF.getPersistenceManagerSQL();
		List<Shop> hqShops = new ArrayList<Shop>();
		List<Shop> brShops = new ArrayList<Shop>();
		List<Shop> shops = new ArrayList<Shop>();

		Query query = pm.newQuery(Shop.class);
		List<Shop> allShops = (List<Shop>) pm.newQuery(query).execute();

		for (Shop allShop : allShops)
			if (allShop.getProviderId().equals(providerId))
				hqShops.add(allShop);

		for (Shop allShop : allShops)
			for (Shop hqShop : hqShops)
				if (hqShop.getType() == Shop.TYPE_HQ && allShop.getParentId().equals(hqShop.getId()))
					brShops.add(allShop);

		shops.addAll(hqShops);
		shops.addAll(brShops);

		return new ShopCollection(shops);
	}

	@ApiMethod(name = "shops.get", path = "shop", httpMethod = "get")
	public Shop get(@Nullable @Named("userId") long userId, @Nullable @Named("id") long id) {
		PersistenceManager pm = PMF.getPersistenceManagerSQL();
		List<Shop> shops = new ArrayList<Shop>();

		try {
			Shop shop = pm.getObjectById(Shop.class, id);
			shops.add(shop);
			Shop.setRelatedVariables(shops, userId);
		} catch (JDOObjectNotFoundException e) {
		}

		return shops.get(0);
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
		PersistenceManager pmSQL = PMF.getPersistenceManagerSQL();
		PersistenceManager pm = PMF.getPersistenceManager();

		try {
			Shop target = pmSQL.getObjectById(Shop.class, shopId);
			pmSQL.deletePersistent(target);

			Query query = pmSQL.newQuery(Flag.class);
			query.setFilter("shopId == theShopId");
			query.declareParameters("long theShopId");
			List<Flags> flags = (List<Flags>) pmSQL.newQuery(query).execute(shopId);
			pmSQL.deletePersistentAll(flags);

			query = pmSQL.newQuery(Item.class);
			query.setFilter("shopId == theShopId");
			query.declareParameters("long theShopId");
			List<Items> items = (List<Items>) pmSQL.newQuery(query).execute(shopId);
			pmSQL.deletePersistentAll(items);

			query = pm.newQuery(Like.class);
			query.setFilter("targetId == shopId");
			query.declareParameters("long shopId");
			List<Like> likes = (List<Like>) pm.newQuery(query).execute(shopId);
			pm.deletePersistentAll(likes);
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pmSQL.close();
			pm.close();
		}
	}
}

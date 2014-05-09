package com.flag.engine.apis;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.flag.engine.constants.Constants;
import com.flag.engine.models.BranchItemMatcher;
import com.flag.engine.models.Flag;
import com.flag.engine.models.Item;
import com.flag.engine.models.ItemCollection;
import com.flag.engine.models.Like;
import com.flag.engine.models.PMF;
import com.flag.engine.models.Shop;
import com.flag.engine.models.UserInfo;
import com.flag.engine.utils.LocationUtils;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;

@Api(name = "flagengine", version = "v1", clientIds = { Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID,
		Constants.API_EXPLORER_CLIENT_ID }, audiences = { Constants.ANDROID_AUDIENCE })
public class Items {
	private static final Logger log = Logger.getLogger(Flags.class.getName());

	@ApiMethod(name = "items.insert", path = "item", httpMethod = "post")
	public Item insert(Item item) {
		log.info("insert item: " + item.toString());

		item.calSale();

		PersistenceManager pm = PMF.getPersistenceManagerSQL();
		pm.makePersistent(item);
		pm.close();

		return item;
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "items.init", path = "item_init", httpMethod = "get")
	public ItemCollection initItems(@Nullable @Named("userId") long userId, @Nullable @Named("mark") int mark) {
		log.info("list item for user: " + userId);
		long startTime = new Date().getTime();

		PersistenceManager pm = PMF.getPersistenceManagerSQL();
		double lat = 0;
		double lon = 0;

		try {
			UserInfo userInfo = pm.getObjectById(UserInfo.class, userId);
			lat = userInfo.getLat();
			lon = userInfo.getLon();
		} catch (JDOObjectNotFoundException e) {
			log.warning("no user info");
		}
		
		log.warning("userinfo da time: " + (new Date().getTime() - startTime) + "ms");

		Query query = pm.newQuery(Flag.class);
		query.setFilter("lon > minLon && lon < maxLon && lat > minLat && lat < maxLat");
		query.declareParameters("double minLon, double maxLon, double minLat, double maxLat");
		List<Flag> flags = (List<Flag>) pm.newQuery(query).executeWithArray(lon - LocationUtils.NEAR_DISTANCE_DEGREE,
				lon + LocationUtils.NEAR_DISTANCE_DEGREE, lat - LocationUtils.NEAR_DISTANCE_DEGREE, lat + LocationUtils.NEAR_DISTANCE_DEGREE);

		log.warning("flag da time: " + (new Date().getTime() - startTime) + "ms");
		log.warning("flag count: " + flags.size());
		
		Set<Long> shopIds = new HashSet<Long>();
		for (Flag flag : flags)
			shopIds.add(flag.getShopId());

		log.warning("shopId process time: " + (new Date().getTime() - startTime) + "ms");
		
		StringBuilder sbFilter = new StringBuilder();
		StringBuilder sbParams = new StringBuilder();
		Map<String, Long> paramMap = new HashMap<String, Long>();
		int i = 0;

		for (Long shopId : shopIds) {
			if (i > 0) {
				sbFilter.append(" || ");
				sbParams.append(", ");
			}

			sbFilter.append("shopId == id" + i);
			sbParams.append("long id" + i);
			paramMap.put("id" + i, shopId);

			i++;
		}
		
		// temporary
		if (shopIds.size() == 0) {
			sbFilter.append("shopId != givyId");
			sbParams.append("long givyId");
			paramMap.put("givyId", 161l);
		}

		log.warning("query filter process time: " + (new Date().getTime() - startTime) + "ms");
		
		query = pm.newQuery(Item.class);
		query.setFilter(sbFilter.toString());
		query.declareParameters(sbParams.toString());
		// query.setOrdering("id desc");
		// query.setRange(mark, mark + 20);
		List<Item> results = (List<Item>) pm.newQuery(query).executeWithMap(paramMap);

		log.warning("item da time: " + (new Date().getTime() - startTime) + "ms");
		
		List<Item> candidates = new ArrayList<Item>();
		for (Item item : results)
			if (!candidates.contains(item))
				candidates.add(item);

		log.warning("candidate process time: " + (new Date().getTime() - startTime) + "ms");
		
		List<Item> items = new ArrayList<Item>();
		List<Integer> picks = ItemCollection.randomIndexes(candidates.size());
		for (int pick : picks)
			items.add(candidates.get(pick));

		log.warning("random pick process time: " + (new Date().getTime() - startTime) + "ms");
		
		Item.setRelatedVariables(items, userId);

		log.warning("rel-var da time: " + (new Date().getTime() - startTime) + "ms");
		
		return new ItemCollection(items);
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "items.list", path = "item", httpMethod = "get")
	public ItemCollection list(@Nullable @Named("userId") long userId, @Nullable @Named("shopId") long shopId) {
		log.info("list item: " + shopId);

		PersistenceManager pm = PMF.getPersistenceManagerSQL();
		Shop shop = null;
		List<Item> items = new ArrayList<Item>();
		List<Item> hiddenItems = new ArrayList<Item>();

		try {
			shop = pm.getObjectById(Shop.class, shopId);
		} catch (JDOObjectNotFoundException e) {
			return null;
		}

		Query query = pm.newQuery(Item.class);
		if (shop.getType() == Shop.TYPE_BR && shop.getParentId() != null) {
			query.setFilter("shopId == id || shopId == parentId");
			query.declareParameters("long id, long parentId");
			List<Item> allItems = (List<Item>) pm.newQuery(query).execute(shop.getId(), shop.getParentId());

			query = pm.newQuery(BranchItemMatcher.class);
			query.setFilter("branchShopId == id");
			query.declareParameters("long id");
			List<BranchItemMatcher> matchers = (List<BranchItemMatcher>) pm.newQuery(query).execute(shop.getId());

			List<Item> brItems = new ArrayList<Item>();
			for (Item item : allItems)
				if (!brItems.contains(item))
					brItems.add(item);
				else if (brItems.get(brItems.indexOf(item)).getShopId().equals(shop.getParentId())) {
					brItems.remove(brItems.indexOf(item));
					brItems.add(item);
				}

			for (Item brItem : brItems) {
				boolean seen = false;
				for (BranchItemMatcher matcher : matchers)
					if (brItem.getId().equals(matcher.getItemId())) {
						seen = true;
						brItem.setRewardable(matcher.isRewardable());
					}

				if (seen)
					items.add(brItem);
				else
					hiddenItems.add(brItem);
			}

		} else {
			query.setFilter("shopId == id");
			query.declareParameters("long id");
			items = (List<Item>) pm.newQuery(query).execute(shop.getId());
		}

		Item.setRelatedVariables(items, userId);

		return new ItemCollection(items, hiddenItems);
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "items.list.user", path = "item_user")
	public ItemCollection listByUser(@Nullable @Named("userId") long userId) {
		PersistenceManager pm = PMF.getPersistenceManager();
		PersistenceManager pmSQL = PMF.getPersistenceManagerSQL();
		List<Item> items = new ArrayList<Item>();

		Query query = pm.newQuery(Like.class);
		query.setFilter("userId == id && type == itemType");
		query.declareParameters("long id, int itemType");
		List<Like> likes = (List<Like>) pm.newQuery(query).execute(userId, Like.TYPE_ITEM);

		query = pmSQL.newQuery(Item.class);
		List<Item> allItems = (List<Item>) pmSQL.newQuery(query).execute();

		for (Like like : likes)
			for (Item item : allItems)
				if (like.getTargetId().equals(item.getId())) {
					items.add(item);
					break;
				}

		Item.setRelatedVariables(items, userId);

		return new ItemCollection(items);
	}

	@ApiMethod(name = "items.get", path = "one_item", httpMethod = "get")
	public Item get(@Nullable @Named("userId") long userId, @Nullable @Named("itemId") long itemId) {
		log.info("get item: " + itemId);

		PersistenceManager pm = PMF.getPersistenceManagerSQL();
		List<Item> items = new ArrayList<Item>();

		try {
			items.add(pm.getObjectById(Item.class, itemId));
			Item.setRelatedVariables(items, userId);
		} catch (JDOObjectNotFoundException e) {
		}

		return items.get(0);
	}

	@ApiMethod(name = "items.update", path = "item", httpMethod = "put")
	public Item update(Item item) {
		log.info("update item: " + item.toString());

		PersistenceManager pm = PMF.getPersistenceManagerSQL();
		Item target = null;

		try {
			target = pm.getObjectById(Item.class, item.getId());
			target.update(item);
		} catch (JDOObjectNotFoundException e) {
			return null;
		} finally {
			pm.close();
		}

		return target;
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "items.delete", path = "item", httpMethod = "delete")
	public void delete(@Nullable @Named("itemId") long itemId) {
		PersistenceManager pmSQL = PMF.getPersistenceManagerSQL();
		PersistenceManager pm = PMF.getPersistenceManager();

		try {
			Item item = pmSQL.getObjectById(Item.class, itemId);
			pmSQL.deletePersistent(item);

			Query query = pm.newQuery(Like.class);
			query.setFilter("type == typeItem && targetId == itemId");
			query.declareParameters("int typeItem, long itemId");
			List<Like> likes = (List<Like>) pm.newQuery(query).execute(Like.TYPE_ITEM, itemId);
			pm.deletePersistentAll(likes);
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pmSQL.close();
			pm.close();
		}
	}

	@ApiMethod(name = "items.branch.expose", path = "item_branch", httpMethod = "post")
	public BranchItemMatcher expose(BranchItemMatcher matcher) {
		PersistenceManager pm = PMF.getPersistenceManagerSQL();
		pm.makePersistent(matcher);
		pm.close();

		return matcher;
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "items.branch.hide", path = "item_branch", httpMethod = "delete")
	public void hide(@Nullable @Named("shopId") long shopId, @Nullable @Named("itemId") long itemId) {
		PersistenceManager pm = PMF.getPersistenceManagerSQL();

		Query query = pm.newQuery(BranchItemMatcher.class);
		query.setFilter("branchShopId == shopId && itemId == theItemId");
		query.declareParameters("long shopId, long theItemId");
		List<BranchItemMatcher> matchers = (List<BranchItemMatcher>) pm.newQuery(query).execute(shopId, itemId);

		pm.deletePersistentAll(matchers);
		pm.close();
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "items.branch.reward", path = "item_branch", httpMethod = "put")
	public BranchItemMatcher toggleReward(BranchItemMatcher matcher) {
		PersistenceManager pm = PMF.getPersistenceManagerSQL();

		Query query = pm.newQuery(BranchItemMatcher.class);
		query.setFilter("branchShopId == shopId && itemId == theItemId");
		query.declareParameters("long shopId, long theItemId");
		List<BranchItemMatcher> matchers = (List<BranchItemMatcher>) pm.newQuery(query).execute(matcher.getBranchShopId(), matcher.getItemId());

		BranchItemMatcher target = null;
		if (!matchers.isEmpty()) {
			target = matchers.get(0);
			if (target.isRewardable())
				target.setRewardable(false);
			else
				target.setRewardable(true);
		}

		pm.close();

		return target;
	}
}

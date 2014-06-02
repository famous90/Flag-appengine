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
import com.flag.engine.models.BranchItemMatcher;
import com.flag.engine.models.Item;
import com.flag.engine.models.ItemCollection;
import com.flag.engine.models.ItemHidden;
import com.flag.engine.models.Like;
import com.flag.engine.models.PMF;
import com.flag.engine.models.Shop;
import com.flag.engine.models.UserInfo;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;

@Api(name = "flagengine", version = "v1", clientIds = { Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID,
		Constants.API_EXPLORER_CLIENT_ID }, audiences = { Constants.ANDROID_AUDIENCE })
public class Items {
	private static final Logger log = Logger.getLogger(Items.class.getName());

	@ApiMethod(name = "items.insert", path = "item", httpMethod = "post")
	public Item insert(Item item) {
		log.info("insert item: " + item.toString());

		item.calSale();

		PersistenceManager pm = PMF.getPersistenceManagerSQL();
		pm.makePersistent(item);
		pm.close();

		return item;
	}

	@ApiMethod(name = "items.get", path = "one_item", httpMethod = "get")
	public Item get(@Nullable @Named("userId") long userId, @Nullable @Named("itemId") long itemId) {
		log.info("get item: " + itemId);

		PersistenceManager pm = PMF.getPersistenceManagerSQL();
		List<Item> items = new ArrayList<Item>();

		try {
			items.add(pm.getObjectById(Item.class, itemId));
			Item.setLikeVariables(items, userId);
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
			if (item.getId() == null)
				throw new JDOObjectNotFoundException();
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
			try {
				ItemHidden item = pmSQL.getObjectById(ItemHidden.class, itemId);
				pmSQL.deletePersistent(item);

				Query query = pmSQL.newQuery(Item.class);
				query.setFilter("barcodeId == id");
				query.declareParameters("String id");
				List<Item> brItems = (List<Item>) pm.newQuery(query).execute(item.getBarcodeId());

				for (Item brItem : brItems)
					if (brItem.getBarcodeId() != null && !brItem.getBarcodeId().trim().isEmpty())
						delete(brItem.getId());

			} catch (JDOObjectNotFoundException e2) {
			}
		} finally {
			pmSQL.close();
			pm.close();
		}
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "items.init", path = "item_init", httpMethod = "get")
	public ItemCollection initItems(@Nullable @Named("userId") long userId, @Nullable @Named("mark") int mark) {
		log.info("list item for user: " + userId);
		long startTime = new Date().getTime();

		PersistenceManager pm = PMF.getPersistenceManagerSQL();

		int sex = 0;
		try {
			UserInfo userInfo = pm.getObjectById(UserInfo.class, userId);
			sex = userInfo.getSex();
		} catch (JDOObjectNotFoundException e) {
		}

		Query query = pm.newQuery(Item.class);
		query.setFilter("sex == unisex || sex == gender");
		query.declareParameters("int unisex, int gender");
		List<Item> results = (List<Item>) pm.newQuery(query).execute(0, sex);

		log.warning("item da time: " + (new Date().getTime() - startTime) + "ms");

		List<Item> items = new ArrayList<Item>();
		List<Integer> picks = ItemCollection.randomIndexes(results.size());
		for (int pick : picks)
			items.add(results.get(pick));

		log.warning("random pick process time: " + (new Date().getTime() - startTime) + "ms");

		Item.setLikeVariables(items, userId);

		log.warning("rel-var da time: " + (new Date().getTime() - startTime) + "ms");

		return new ItemCollection(items);
	}

	@SuppressWarnings("unchecked")
	private List<Item> listByIds(List<Object> ids) {
		log.warning("list items: " + ids);
		if (ids == null || ids.isEmpty())
			return new ArrayList<Item>();

		PersistenceManager pm = PMF.getPersistenceManagerSQL();
		List<Item> items = null;

		try {
			items = (List<Item>) pm.getObjectsById(ids);
		} catch (JDOObjectNotFoundException e) {
			ids.remove(e.getFailedObject());
			return listByIds(ids);
		}

		return items;
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "items.list", path = "item", httpMethod = "get")
	public ItemCollection listByShop(@Nullable @Named("userId") long userId, @Nullable @Named("shopId") long shopId) {
		PersistenceManager pm = PMF.getPersistenceManagerSQL();

		Query query = pm.newQuery(BranchItemMatcher.class);
		query.setFilter("branchShopId == shopId");
		query.declareParameters("long shopId");
		List<BranchItemMatcher> matchers = (List<BranchItemMatcher>) pm.newQuery(query).execute(shopId);

		List<Object> ids = new ArrayList<Object>();
		for (BranchItemMatcher matcher : matchers)
			ids.add(pm.newObjectIdInstance(Item.class, matcher.getItemId()));

		List<Item> items = listByIds(ids);

		for (Item item : items)
			for (BranchItemMatcher matcher : matchers)
				if (matcher.getItemId().equals(item.getId())) {
					if (!matcher.isRewardable())
						item.setReward(0);
					break;
				}

		Item.setRelatedVariables(items, userId);

		return new ItemCollection(items);
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "items.list.manager", path = "item_manager", httpMethod = "get")
	public ItemCollection listForManager(@Nullable @Named("shopId") long shopId) {
		log.info("list item for manager: " + shopId);

		PersistenceManager pm = PMF.getPersistenceManagerSQL();
		Shop shop = null;
		List<Item> items = new ArrayList<Item>();
		List<Item> hiddenItems = new ArrayList<Item>();

		try {
			shop = pm.getObjectById(Shop.class, shopId);
		} catch (JDOObjectNotFoundException e) {
			return null;
		}

		Query query;

		if (shop.getType() == Shop.TYPE_BR) {
			query = pm.newQuery(BranchItemMatcher.class);
			query.setFilter("branchShopId == id");
			query.declareParameters("long id");
			List<BranchItemMatcher> matchers = (List<BranchItemMatcher>) pm.newQuery(query).execute(shop.getId());

			List<Object> ids = new ArrayList<Object>();
			for (BranchItemMatcher matcher : matchers)
				ids.add(pm.newObjectIdInstance(Item.class, matcher.getItemId()));

			items.addAll(listByIds(ids));

			if (shop.getParentId() != null) {
				query = pm.newQuery(ItemHidden.class);
				query.setFilter("shopId == parentId");
				query.declareParameters("long parentId");
				List<ItemHidden> hqItems = (List<ItemHidden>) pm.newQuery(query).execute(shop.getParentId());

				for (ItemHidden hqItem : hqItems)
					hiddenItems.add(new Item(hqItem));
			}

			for (Item item : items)
				if (hiddenItems.contains(item))
					hiddenItems.remove(item);

			Item.setLikeVariables(items, 0);

		} else {
			query = pm.newQuery(ItemHidden.class);
			query.setFilter("shopId == id");
			query.declareParameters("long id");
			List<ItemHidden> hqItems = (List<ItemHidden>) pm.newQuery(query).execute(shop.getId());
			for (ItemHidden hqItem : hqItems)
				items.add(new Item(hqItem));
		}

		return new ItemCollection(items, hiddenItems);
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "items.list.user", path = "item_user", httpMethod = "get")
	public ItemCollection listForUser(@Nullable @Named("userId") long userId) {
		PersistenceManager pm = PMF.getPersistenceManager();
		PersistenceManager pmSQL = PMF.getPersistenceManagerSQL();

		Query query = pm.newQuery(Like.class);
		query.setFilter("userId == id && type == itemType");
		query.declareParameters("long id, int itemType");
		List<Like> likes = (List<Like>) pm.newQuery(query).execute(userId, Like.TYPE_ITEM);

		List<Object> ids = new ArrayList<Object>();
		for (Like like : likes)
			ids.add(pmSQL.newObjectIdInstance(Item.class, like.getTargetId()));

		List<Item> items = listByIds(ids);

		Item.setLikeVariables(items, userId);

		return new ItemCollection(items);
	}

	@SuppressWarnings({ "unchecked" })
	@ApiMethod(name = "items.list.reward", path = "item_reward", httpMethod = "get")
	public ItemCollection listReward(@Nullable @Named("userId") long userId, @Nullable @Named("mark") int mark) {
		PersistenceManager pm = PMF.getPersistenceManagerSQL();

		Query query = pm.newQuery(BranchItemMatcher.class);
		query.setFilter("rewardable == True");
		query.declareParameters("boolean True");
		query.setOrdering("id desc");
		List<BranchItemMatcher> matchers = (List<BranchItemMatcher>) pm.newQuery(query).execute(true);

		List<Long> itemIds = new ArrayList<Long>();
		for (BranchItemMatcher matcher : matchers)
			if (!itemIds.contains(matcher.getItemId()))
				itemIds.add(matcher.getItemId());

		List<Object> ids = new ArrayList<Object>();
		for (int i = mark * 20; i < (mark + 1) * 20; i++)
			try {
				ids.add(pm.newObjectIdInstance(Item.class, itemIds.get(i)));
			} catch (IndexOutOfBoundsException e) {
				break;
			}

		if (ids.isEmpty())
			return null;

		List<Item> items = listByIds(ids);
		Item.setRelatedVariables(items, userId);

		List<Item> ableItems = new ArrayList<Item>();
		for (Item item : items)
			if (!item.isRewarded())
				ableItems.add(item);

		return new ItemCollection(items);
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "items.list.item", path = "item_item", httpMethod = "get")
	public ItemCollection listItem(@Nullable @Named("userId") long userId, @Nullable @Named("itemId") long itemId) {
		PersistenceManager pm = PMF.getPersistenceManagerSQL();

		int type = 0;
		try {
			Item item = pm.getObjectById(Item.class, itemId);
			type = item.getType();
		} catch (JDOObjectNotFoundException e) {
		}

		Query query = pm.newQuery(Item.class);
		query.setFilter("type == theType");
		query.declareParameters("int theType");
		List<Item> items = (List<Item>) pm.newQuery(query).execute(type);

		Item.setLikeVariables(items, userId);

		return new ItemCollection(items);
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "items.branch.expose", path = "item_branch", httpMethod = "post")
	public BranchItemMatcher expose(BranchItemMatcher matcher) {
		PersistenceManager pm = PMF.getPersistenceManagerSQL();
		ItemHidden hqItem = null;

		try {
			hqItem = pm.getObjectById(ItemHidden.class, matcher.getItemId());
		} catch (JDOObjectNotFoundException e) {
			return null;
		}

		Query query = pm.newQuery(Item.class);
		query.setFilter("barcodeId == id");
		query.declareParameters("String id");
		List<Item> items = (List<Item>) pm.newQuery(query).execute(hqItem.getBarcodeId());

		if (items.isEmpty()) {
			Item newItem = new Item(hqItem);
			pm.makePersistent(newItem);
			matcher.setItemId(newItem.getId());
		} else
			matcher.setItemId(items.get(0).getId());

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

		query.setFilter("itemId == theItemId");
		query.declareParameters("long theItemId");
		matchers = (List<BranchItemMatcher>) pm.newQuery(query).execute(itemId);

		if (matchers.isEmpty())
			try {
				Item item = pm.getObjectById(Item.class, itemId);
				pm.deletePersistent(item);
			} catch (JDOObjectNotFoundException e) {
			}

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

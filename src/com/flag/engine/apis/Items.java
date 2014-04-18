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
import com.flag.engine.models.Item;
import com.flag.engine.models.ItemCollection;
import com.flag.engine.models.PMF;
import com.flag.engine.models.Shop;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;

@Api(name = "flagengine", version = "v1", clientIds = { Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID,
		Constants.API_EXPLORER_CLIENT_ID }, audiences = { Constants.ANDROID_AUDIENCE })
public class Items {
	private static final Logger log = Logger.getLogger(Flags.class.getName());

	@ApiMethod(name = "items.insert", path = "item", httpMethod = "post")
	public Item insert(Item item) {
		log.info("insert item: " + item.toString());

		PersistenceManager pm = PMF.getPersistenceManagerSQL();
		pm.makePersistent(item);
		pm.close();

		return item;
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "items.init", path = "item_init", httpMethod = "get")
	public ItemCollection initItems(@Nullable @Named("userId") long userId, @Nullable @Named("mark") int mark) {
		log.info("list item for user: " + userId);

		PersistenceManager pm = PMF.getPersistenceManagerSQL();

		Query query = pm.newQuery(Item.class);
		// query.setOrdering("id desc");
		// query.setRange(mark, mark + 20);
		List<Item> results = (List<Item>) pm.newQuery(query).execute();

		List<Integer> picks = ItemCollection.randomIndexes(results.size());
		List<Item> items = new ArrayList<Item>();
		for (int pick : picks)
			items.add(results.get(pick));

		for (Item item : items) {
			item.setRewardedForUser(userId);
			item.setLikedForUser(userId);
			item.setLikes();
		}

		return new ItemCollection(items);
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "items.list", path = "item", httpMethod = "get")
	public ItemCollection list(@Nullable @Named("userId") long userId, @Nullable @Named("shopId") long shopId) {
		log.info("list item: " + shopId);

		PersistenceManager pm = PMF.getPersistenceManagerSQL();
		Shop shop = null;
		List<Item> items = null;

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

			items = new ArrayList<Item>();
			for (Item item : allItems)
				if (!items.contains(item))
					items.add(item);
				else if (items.get(items.indexOf(item)).getShopId().equals(shop.getParentId())) {
					items.remove(items.indexOf(item));
					items.add(item);
				}
		} else {
			query.setFilter("shopId == id");
			query.declareParameters("long id");
			items = (List<Item>) pm.newQuery(query).execute(shop.getId());
		}

		for (Item item : items) {
			item.setRewardedForUser(userId);
			item.setLikedForUser(userId);
			item.setLikes();
		}

		return new ItemCollection(items);
	}

	@ApiMethod(name = "items.get", path = "one_item", httpMethod = "get")
	public Item get(@Nullable @Named("userId") long userId, @Nullable @Named("itemId") long itemId) {
		log.info("get item: " + itemId);

		PersistenceManager pm = PMF.getPersistenceManagerSQL();
		Item item = null;

		try {
			item = pm.getObjectById(Item.class, itemId);
			item.setRewardedForUser(userId);
			item.setLikedForUser(userId);
			item.setLikes();
		} catch (JDOObjectNotFoundException e) {
		}

		return item;
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

	@ApiMethod(name = "items.delete", path = "item", httpMethod = "delete")
	public void delete(@Named("itemId") Long itemId) {
		PersistenceManager pm = PMF.getPersistenceManagerSQL();

		try {
			Item target = pm.getObjectById(Item.class, itemId);
			pm.deletePersistent(target);
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
	}
}

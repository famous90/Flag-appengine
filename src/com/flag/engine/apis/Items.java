package com.flag.engine.apis;

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.flag.engine.constants.Constants;
import com.flag.engine.models.Item;
import com.flag.engine.models.ItemCollection;
import com.flag.engine.models.PMF;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;

@Api(name = "flagengine", version = "v1", clientIds = { Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID,
		Constants.API_EXPLORER_CLIENT_ID }, audiences = { Constants.ANDROID_AUDIENCE })
public class Items {
	private static final Logger log = Logger.getLogger(Flags.class.getName());
	
	@ApiMethod(name = "items.insert", httpMethod = "post")
	public Item insert(Item item) {
		log.warning("insert item: " + item.toString());
		
		PersistenceManager pm = PMF.getPersistenceManager();
		pm.makePersistent(item);
		pm.close();
		
		return item;
	}
	
	@SuppressWarnings("unchecked")
	@ApiMethod(name = "items.get")
	public Item get(@Nullable @Named("id") long id) {
		PersistenceManager pm = PMF.getPersistenceManager();
		
		Query query = pm.newQuery(Item.class);
		query.setFilter("id == itemId");
		query.declareParameters("long itemId");
		List<Item> items = (List<Item>) pm.newQuery(query).execute(id);
		
		if (items.size() < 1)
			return null;
		else
			return items.get(0);
	}
	
	@SuppressWarnings("unchecked")
	@ApiMethod(name = "items.list", path = "item_list")
	public ItemCollection list(@Nullable @Named("shopId") long shopId) {
		log.warning("list item: " + shopId);
		
		PersistenceManager pm = PMF.getPersistenceManager();
		
		Query query = pm.newQuery(Item.class);
		query.setFilter("shopId == theShopid");
		query.declareParameters("long theShopid");
		List<Item> items = (List<Item>) pm.newQuery(query).execute(shopId);
		
		return new ItemCollection(items);
	}

}

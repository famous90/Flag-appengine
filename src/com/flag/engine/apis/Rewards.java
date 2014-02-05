package com.flag.engine.apis;

import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.flag.engine.constants.Constants;
import com.flag.engine.models.Item;
import com.flag.engine.models.PMF;
import com.flag.engine.models.Reward;
import com.flag.engine.models.Shop;
import com.flag.engine.models.User;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;

@Api(name = "flagengine", version = "v1", clientIds = { Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID,
		Constants.API_EXPLORER_CLIENT_ID }, audiences = { Constants.ANDROID_AUDIENCE })
public class Rewards {
	private static final Logger log = Logger.getLogger(Flags.class.getName());

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "rewards.insert", path = "reward", httpMethod = "post")
	public User insert(Reward reward) {
		log.warning("insert reward: " + reward.toString());

		PersistenceManager pm = PMF.getPersistenceManager();
		
		// redundancy check
		Query query = pm.newQuery(Reward.class);
		query.setFilter("key == theKey");
		query.declareParameters("com.google.appengine.api.datastore.Key theKey");
		List<Reward> rewards = (List<Reward>) pm.newQuery(query).execute(reward.refreshKey());
		
		if (!rewards.isEmpty())
			return null;
		
		// save reward log
		pm.makePersistent(reward);

		// get reward value
		int rewardValue = 0;
		
		if (reward.getType() == Reward.TYPE_SHOP) {
			Query query0 = pm.newQuery(Shop.class);
			query0.setFilter("id == targetId");
			query0.declareParameters("long targetId");
			List<Shop> shops = (List<Shop>) pm.newQuery(query0).execute(reward.getTargetId());
			
			if (shops.isEmpty())
				rewardValue = 0;
			else
				rewardValue = shops.get(0).getReward();
		} else {
			Query query0 = pm.newQuery(Item.class);
			query0.setFilter("id == targetId");
			query0.declareParameters("long targetId");
			List<Item> items = (List<Item>) pm.newQuery(query0).execute(reward.getTargetId());
			
			if (items.isEmpty())
				rewardValue = 0;
			else
				rewardValue = items.get(0).getReward();
		}
		
		// give reward
		User user = null;
		
		Query query0 = pm.newQuery(User.class);
		query0.setFilter("id == userId");
		query0.declareParameters("long userId");
		List<User> users = (List<User>) pm.newQuery(query0).execute(reward.getUserId());
		
		if (users.isEmpty())
			user = null;
		else {
			user = users.get(0);
			user.rewarded(rewardValue);
			pm.makePersistent(user);
		}
		
		// pm.currentTransaction().commit();
		pm.close();
		
		return user;
	}
}

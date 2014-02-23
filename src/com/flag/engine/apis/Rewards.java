package com.flag.engine.apis;

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.flag.engine.constants.Constants;
import com.flag.engine.models.PMF;
import com.flag.engine.models.Reward;
import com.flag.engine.models.RewardCollection;
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
		
		// give reward
		User user = null;
		
		Query query0 = pm.newQuery(User.class);
		query0.setFilter("id == userId");
		query0.declareParameters("long userId");
		List<User> users = (List<User>) pm.newQuery(query0).execute(reward.getUserId());
		
		if (!users.isEmpty()) {
			user = users.get(0);
			user.rewarded(reward.getReward());
			pm.makePersistent(user);
			pm.makePersistent(reward);
		}
		
		pm.close();
		
		return new User(user);
	}
	
	@SuppressWarnings("unchecked")
	@ApiMethod(name = "rewards.list", path = "reward", httpMethod = "get")
	public RewardCollection list(@Nullable @Named("userId") long userId) {
		log.warning("list rewards: " + userId);
		
		PersistenceManager pm = PMF.getPersistenceManager();
		
		Query query = pm.newQuery(Reward.class);
		query.setFilter("userId == theUserId");
		query.declareParameters("long theUserId");
		query.setOrdering("createdAt desc");
		List<Reward> rewards = (List<Reward>) pm.newQuery(query).execute(userId);
		
		return new RewardCollection(rewards);
	}
}

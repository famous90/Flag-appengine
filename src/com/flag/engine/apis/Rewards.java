package com.flag.engine.apis;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.jdo.JDOObjectNotFoundException;
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

	@ApiMethod(name = "rewards.insert", path = "reward", httpMethod = "post")
	public User insert(Reward reward) {
		log.info("insert reward: " + reward.toString());

		PersistenceManager pm = PMF.getPersistenceManager();

		// mark
		reward.refreshId();
		reward.setCreatedAt(new Date().getTime());

		// give reward
		try {
			pm.makePersistent(reward);
			User user = pm.getObjectById(User.class, reward.getUserId());
			user.rewarded(reward.getReward());
			return new User(user);
		} catch (JDOObjectNotFoundException e) {
			return null;
		} finally {
			pm.close();
		}
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "rewards.list", path = "reward", httpMethod = "get")
	public RewardCollection list(@Nullable @Named("userId") long userId) {
		log.info("list rewards: " + userId);

		PersistenceManager pm = PMF.getPersistenceManager();

		Query query = pm.newQuery(Reward.class);
		query.setFilter("userId == theUserId");
		query.declareParameters("long theUserId");
		query.setOrdering("createdAt desc");
		List<Reward> rewards = (List<Reward>) pm.newQuery(query).execute(userId);

		return new RewardCollection(rewards);
	}
}

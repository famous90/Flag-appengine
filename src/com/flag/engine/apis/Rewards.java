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
import com.flag.engine.models.Item;
import com.flag.engine.models.PMF;
import com.flag.engine.models.Provider;
import com.flag.engine.models.Reward;
import com.flag.engine.models.RewardCollection;
import com.flag.engine.models.Shop;
import com.flag.engine.models.User;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;

@Api(name = "flagengine", version = "v1", clientIds = { Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID,
		Constants.API_EXPLORER_CLIENT_ID }, audiences = { Constants.ANDROID_AUDIENCE })
public class Rewards {
	private static final Logger log = Logger.getLogger(Rewards.class.getName());

	@ApiMethod(name = "rewards.insert", path = "reward", httpMethod = "post")
	public User insert(Reward reward) {
		log.info("insert reward: " + reward.toString());

		PersistenceManager pm = PMF.getPersistenceManager();
		PersistenceManager pmSQL = PMF.getPersistenceManagerSQL();

		// mark
		reward.setCreatedAt(new Date().getTime());
		reward.refreshId();

		try {
			// refresh balance
			if (reward.getType() == Reward.TYPE_SHOP) {
				Shop brShop = pmSQL.getObjectById(Shop.class, reward.getTargetId());
				Shop hqShop = pmSQL.getObjectById(Shop.class, brShop.getParentId());
				Provider provider = pm.getObjectById(Provider.class, hqShop.getProviderId());
				provider.setBalance(provider.getBalance() - reward.getReward());
			} else if (reward.getType() == Reward.TYPE_ITEM) {
				Item item = pmSQL.getObjectById(Item.class, reward.getTargetId());
				Shop shop = pmSQL.getObjectById(Shop.class, item.getShopId());
				Provider provider = pm.getObjectById(Provider.class, shop.getProviderId());
				provider.setBalance(provider.getBalance() - reward.getReward());
			}
			
			// give reward
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
	public RewardCollection list(@Nullable @Named("userId") long userId, @Nullable @Named("mark") int mark) {
		log.info("list rewards: " + userId);

		PersistenceManager pm = PMF.getPersistenceManager();

		Query query = pm.newQuery(Reward.class);
		query.setFilter("userId == theUserId");
		query.declareParameters("long theUserId");
		query.setOrdering("createdAt desc");
		query.setRange(mark * 29, (mark + 1) * 29);
		List<Reward> rewards = (List<Reward>) pm.newQuery(query).execute(userId);

		return new RewardCollection(rewards);
	}
}

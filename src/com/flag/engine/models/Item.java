package com.flag.engine.models;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.flag.engine.utils.KeyBuilder;
import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Item {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;

	@Persistent
	@Index
	private Long shopId;

	@Persistent
	private String name;

	@Persistent
	private String description;

	@Persistent
	private String imageUrl;

	@Persistent
	private int reward;

	private boolean rewarded;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getShopId() {
		return shopId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public int getReward() {
		return reward;
	}

	public void setReward(int reward) {
		this.reward = reward;
	}

	public boolean isRewarded() {
		return rewarded;
	}

	public void setRewarded(boolean rewarded) {
		this.rewarded = rewarded;
	}

	public void setRewardedForUser(long userId) {
		PersistenceManager pm = PMF.getPersistenceManager();

		Key key = KeyBuilder.makeRewardKey(userId, id, Reward.TYPE_ITEM);
		try {
			Reward reward = pm.getObjectById(Reward.class, key);
			if (reward != null)
				rewarded = true;
			else
				rewarded = false;
		} catch (JDOObjectNotFoundException e) {
			rewarded = false;
		}
	}
}

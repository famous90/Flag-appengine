package com.flag.engine.models;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION, table = "rewards")
public class Reward {
	public static final long TYPE_SHOP = 101;
	public static final long TYPE_ITEM = 202;

	@Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
	@PrimaryKey
	private Long id;

	@Persistent
	@Index
	@Column(name = "user_id")
	private Long userId;

	@Persistent
	@Column(name = "target_id")
	private Long targetId;

	@Persistent
	@Column(name = "target_name")
	private String targetName;

	@Persistent
	private Long type;

	@Persistent
	private int reward;

	@Persistent
	@Index
	@Column(name = "created_at")
	private long createdAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getTargetId() {
		return targetId;
	}

	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public Long getType() {
		return type;
	}

	public void setType(Long type) {
		this.type = type;
	}

	public int getReward() {
		return reward;
	}

	public void setReward(int reward) {
		this.reward = reward;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	@SuppressWarnings("unchecked")
	public static boolean exists(Long userId, Long targetId, Long type) {
		PersistenceManager pm = PMF.getPersistenceManagerSQL();

		Query query = pm.newQuery(Reward.class);
		query.setFilter("userId == theUserId && targetId == theTargetId && type == theType");
		query.declareParameters("Long theUserId, Long theTargetId, Long theType");
		List<Reward> rewards = (List<Reward>) pm.newQuery(query).execute(userId, targetId, type);

		if (rewards.isEmpty())
			return false;
		else
			return true;
	}
}

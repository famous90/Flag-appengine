package com.flag.engine.models;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION, table = "rewards")
public class Reward {
	public static final long TYPE_SHOP = 1;
	public static final long TYPE_ITEM = 2;

	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private String id;

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

	public String getId() {
		if (id == null)
			refreshId();
		return id;
	}

	public void setId(String id) {
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

	public void refreshId() {
		id = Reward.obtainRewardId(userId, targetId, type);
	}

	public static String obtainRewardId(Long userId, Long targetId, Long type) {
		StringBuilder sb = new StringBuilder();
		sb.append("userId:").append(userId).append("/").append("targetId:").append(targetId).append("/").append("type:").append(type);
		return sb.toString();
	}

	public static boolean exists(Long userId, Long targetId, Long type) {
		PersistenceManager pm = PMF.getPersistenceManager();

		try {
			pm.getObjectById(Reward.class, Reward.obtainRewardId(userId, targetId, type));
			return true;
		} catch (JDOObjectNotFoundException e) {
			return false;
		}
	}
}

package com.flag.engine.models;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Reward extends BaseModel {
	public static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7;
	
	public static final int TYPE_SHOP = 1;
	public static final int TYPE_ITEM = 2;
	public static final int TYPE_REDEMPTION = 10;

	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private String id;

	@Persistent
	@Index
	private Long userId;

	@Persistent
	@Index
	private Long targetId;

	@Persistent
	private String targetName;

	@Persistent
	private int type;

	@Persistent
	private int reward;

	@Persistent
	@Index
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
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
		id = Reward.obtainRewardId(userId, targetId, type, createdAt);
	}

	public static String obtainRewardId(Long userId, Long targetId, int type, long createdAt) {
		StringBuilder sb = new StringBuilder();
		sb.append("userId:").append(userId).append("/targetId:").append(targetId).append("/type:").append(type).append("/createdAt:")
				.append(createdAt);
		return sb.toString();
	}
}

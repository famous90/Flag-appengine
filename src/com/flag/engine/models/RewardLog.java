package com.flag.engine.models;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class RewardLog {
	@PrimaryKey
	@Persistent
	private String keyString;

	@Persistent
	@Index
	private Long userId;

	@Persistent
	private Long rewardId;

	@Persistent
	private String type;

	public String getKeyString() {
		return keyString;
	}

	public void setKeyString(String keyString) {
		this.keyString = keyString;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getRewardId() {
		return rewardId;
	}

	public void setRewardId(Long rewardId) {
		this.rewardId = rewardId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}

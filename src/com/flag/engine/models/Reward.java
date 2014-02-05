package com.flag.engine.models;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.flag.engine.utils.KeyBuilder;
import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Reward {
	public static final long TYPE_SHOP = 101;
	public static final long TYPE_ITEM = 202;

	@PrimaryKey
	@Persistent
	private Key key;

	@Persistent
	@Index
	private Long userId;

	@Persistent
	private Long targetId;

	@Persistent
	private Long type;

	@Persistent
	private int reward;

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
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

	public Key refreshKey() {
		key = makeKey();
		return key;
	}

	public Key makeKey() {
		return KeyBuilder.makeRewardKey(userId, targetId, type);
	}
}

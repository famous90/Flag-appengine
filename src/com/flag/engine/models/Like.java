package com.flag.engine.models;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Like extends BaseModel {
	public static final int TYPE_SHOP = 1;
	public static final int TYPE_ITEM = 2;

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
	@Index
	private int type;

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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void refreshId() {
		id = Like.obtainLikeId(userId, targetId, type);
	}

	public static String obtainLikeId(Long userId, Long targetId, int type) {
		StringBuilder sb = new StringBuilder();
		sb.append("userId:").append(userId).append("/").append("targetId:").append(targetId).append("/").append("type:").append(type);
		return sb.toString();
	}
}

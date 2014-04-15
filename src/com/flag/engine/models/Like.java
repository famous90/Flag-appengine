package com.flag.engine.models;

import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Like {
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

	public static boolean exists(Long userId, Long targetId, int type) {
		PersistenceManager pm = PMF.getPersistenceManager();

		try {
			pm.getObjectById(Like.class, obtainLikeId(userId, targetId, type));
			return true;
		} catch (JDOObjectNotFoundException e) {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public static int count(Long targetId, int type) {
		PersistenceManager pm = PMF.getPersistenceManager();

		Query query = pm.newQuery(Like.class);
		query.setFilter("targetId == theTargetId && type == theType");
		query.declareParameters("Long theTargetId, int theType");
		List<Like> likes = (List<Like>) pm.newQuery(query).execute(targetId, type);

		return likes.size();
	}
}

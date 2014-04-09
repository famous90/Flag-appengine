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
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private String id;

	@Persistent
	@Index
	private Long userId;

	@Persistent
	@Index
	private Long itemId;

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

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public void refreshId() {
		id = Like.obtainLikeId(userId, itemId);
	}

	public static String obtainLikeId(Long userId, Long itemId) {
		StringBuilder sb = new StringBuilder();
		sb.append("userId:").append(userId).append("/").append("itemId:").append(itemId);
		return sb.toString();
	}

	public static boolean exists(Long userId, Long itemId) {
		PersistenceManager pm = PMF.getPersistenceManager();

		try {
			pm.getObjectById(Like.class, obtainLikeId(userId, itemId));
			return true;
		} catch (JDOObjectNotFoundException e) {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public static int count(Long itemId) {
		PersistenceManager pm = PMF.getPersistenceManager();

		Query query = pm.newQuery(Like.class);
		query.setFilter("itemId == theItemId");
		query.declareParameters("Long theItemId");
		List<Like> likes = (List<Like>) pm.newQuery(query).execute(itemId);

		return likes.size();
	}
}

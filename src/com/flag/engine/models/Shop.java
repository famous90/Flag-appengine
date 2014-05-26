package com.flag.engine.models;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION, table = "shops")
public class Shop {
	public static final int TYPE_HQ = 1;
	public static final int TYPE_BR = 2;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.NATIVE)
	private Long id;

	@Persistent
	@Index
	private Long parentId;

	@Persistent
	@Index
	private Long providerId;

	@Persistent
	private String name;

	@Persistent
	private String logoUrl;

	@Persistent
	private String imageUrl;

	@Persistent
	private String description;

	@Persistent
	private int type;

	@Persistent
	private int reward;

	@Persistent
	private boolean onSale;

	@NotPersistent
	private boolean rewarded;

	@NotPersistent
	private int likes;

	@NotPersistent
	private boolean liked;

	public Shop() {
		super();
	}

	public Shop(Shop shop) {
		super();
		this.id = shop.getId();
		this.parentId = shop.getParentId();
		this.providerId = shop.getProviderId();
		this.name = shop.getName();
		this.logoUrl = shop.getLogoUrl();
		this.imageUrl = shop.getImageUrl();
		this.type = shop.getType();
		this.description = shop.getDescription();
		this.reward = shop.getReward();
		this.rewarded = shop.isRewarded();
		this.likes = shop.getLikes();
		this.liked = shop.isLiked();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getParentId() {
		return (parentId == null) ? 0 : parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Long getProviderId() {
		return providerId;
	}

	public void setProviderId(Long providerId) {
		this.providerId = providerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public boolean isOnSale() {
		return onSale;
	}

	public void setOnSale(boolean onSale) {
		this.onSale = onSale;
	}

	public boolean isRewarded() {
		return rewarded;
	}

	public void setRewarded(boolean rewarded) {
		this.rewarded = rewarded;
	}

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public boolean isLiked() {
		return liked;
	}

	public void setLiked(boolean liked) {
		this.liked = liked;
	}

	public void update(Shop shop) {
		if (shop.getName() != null && !shop.getName().isEmpty())
			this.name = shop.getName();
		if (shop.getLogoUrl() != null && !shop.getLogoUrl().isEmpty())
			this.logoUrl = shop.getLogoUrl();
		if (shop.getImageUrl() != null && !shop.getImageUrl().isEmpty())
			this.imageUrl = shop.getImageUrl();
		if (shop.getDescription() != null && !shop.getDescription().isEmpty())
			this.description = shop.getDescription();
		this.reward = shop.getReward();
	}

	@SuppressWarnings("unchecked")
	public static void setRelatedVariables(List<Shop> shops, long userId) {
		if (shops == null || shops.isEmpty())
			return;

		PersistenceManager pm = PMF.getPersistenceManager();

		Query query = pm.newQuery(Like.class);

		StringBuilder sbFilter = new StringBuilder("type == typeShop && (");
		StringBuilder sbParams = new StringBuilder("int typeShop");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("typeShop", Like.TYPE_SHOP);
		int i = 0;

		for (Shop shop : shops) {
			sbFilter.append("targetId == shopId" + i);
			if (i < shops.size() - 1)
				sbFilter.append(" || ");
			sbParams.append(", long shopId" + i);
			paramMap.put("shopId" + i, shop.getId());
			i++;
		}
		sbFilter.append(")");

		query.setFilter(sbFilter.toString());
		query.declareParameters(sbParams.toString());
		List<Like> likes = (List<Like>) pm.newQuery(query).executeWithMap(paramMap);

		for (Like like : likes)
			for (Shop shop : shops)
				if (like.getTargetId().equals(shop.getId())) {
					shop.setLikes(shop.getLikes() + 1);
					if (like.getUserId().equals(userId))
						shop.setLiked(true);
					break;
				}

		sbFilter.append(" && userId == theUserId");
		sbParams.append(", long theUserId");
		paramMap.put("theUserId", userId);
		paramMap.remove("typeItem");
		paramMap.put("typeItem", Reward.TYPE_SHOP);

		query = pm.newQuery(Reward.class);
		query.setFilter(sbFilter.toString());
		query.declareParameters(sbParams.toString());
		List<Reward> rewards = (List<Reward>) pm.newQuery(query).executeWithMap(paramMap);

		for (Reward reward : rewards)
			for (Shop shop : shops)
				if (reward.getTargetId().equals(shop.getId()) && reward.getUserId().equals(userId)
						&& reward.getCreatedAt() > new Date().getTime() - Reward.EXPIRATION_TIME) {
					shop.setRewarded(true);
					break;
				}
	}
}

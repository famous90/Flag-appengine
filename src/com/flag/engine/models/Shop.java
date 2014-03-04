package com.flag.engine.models;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION, table = "shops")
public class Shop {
	public static final int TYPE_STORE = 1;
	public static final int TYPE_MALL = 2;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
	private Long id;

	@Persistent
	@Column(name = "parent_id")
	private Long parentId;

	@Persistent
	private String name;

	@Persistent
	@Column(name = "logo_url")
	private String logoUrl;

	@Persistent
	@Column(name = "image_url")
	private String imageUrl;

	@Persistent
	private int type;

	@Persistent
	private String description;

	@Persistent
	private int reward;

	@NotPersistent
	private boolean rewarded;

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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
		this.rewarded = Reward.exists(userId, id, Reward.TYPE_SHOP);
	}

	public void update(Shop shop) {
		if (shop.getName() != null && !shop.getName().isEmpty())
			this.name = shop.getName();
		if (shop.getImageUrl() != null && !shop.getImageUrl().isEmpty())
			this.imageUrl = shop.getImageUrl();
		if (shop.getDescription() != null && !shop.getDescription().isEmpty())
			this.description = shop.getDescription();
		this.reward = shop.getReward();
	}
}

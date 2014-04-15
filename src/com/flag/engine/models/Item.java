package com.flag.engine.models;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION, table = "items")
public class Item {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.NATIVE)
	private Long id;

	@Persistent
	@Index
	@Column(name = "shop_id")
	private Long shopId;

	@Persistent
	private String name;

	@Persistent
	@Column(name = "thumbnail_url")
	private String thumbnailUrl;

	@Persistent
	private String description;

	@Persistent
	private int sale;

	@Persistent
	@Column(name = "old_price")
	private String oldPrice;

	@Persistent
	private String price;

	@Persistent
	@Column(name = "barcode_id")
	private String barcodeId;

	@Persistent
	private int reward;

	@NotPersistent
	private boolean rewarded;

	@NotPersistent
	private int likes;

	@NotPersistent
	private boolean liked;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getShopId() {
		return shopId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getSale() {
		return sale;
	}

	public void setSale(int sale) {
		this.sale = sale;
	}

	public String getOldPrice() {
		return oldPrice;
	}

	public void setOldPrice(String oldPrice) {
		this.oldPrice = oldPrice;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getBarcodeId() {
		return barcodeId;
	}

	public void setBarcodeId(String barcodeId) {
		this.barcodeId = barcodeId;
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
		this.rewarded = Reward.exists(userId, id, Reward.TYPE_ITEM);
	}

	public int getLikes() {
		return likes;
	}

	public void setLikes() {
		this.likes = Like.count(id, Like.TYPE_ITEM);
	}

	public boolean isLiked() {
		return liked;
	}

	public void setLiked(boolean liked) {
		this.liked = liked;
	}

	public void setLikedForUser(long userId) {
		this.liked = Like.exists(userId, id, Like.TYPE_ITEM);
	}

	public void update(Item item) {
		if (item.getName() != null && !item.getName().isEmpty())
			this.name = item.getName();
		if (item.getThumbnailUrl() != null && !item.getThumbnailUrl().isEmpty())
			this.thumbnailUrl = item.getThumbnailUrl();
		if (item.getDescription() != null && !item.getDescription().isEmpty())
			this.description = item.getDescription();
		if (item.getBarcodeId() != null && !item.getBarcodeId().isEmpty())
			this.barcodeId = item.getBarcodeId();
		if (item.getPrice() != null && !item.getPrice().isEmpty())
			this.price = item.getPrice();
		this.reward = item.getReward();
	}
}

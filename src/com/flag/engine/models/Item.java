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
	@Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
	private Long id;

	@Persistent
	@Index
	@Column(name = "shop_id")
	private Long shopId;

	@Persistent
	private String name;

	@Persistent
	@Column(name = "image_url")
	private String imageUrl;

	@Persistent
	private String description;

	@Persistent
	private Integer price;

	@Persistent
	@Column(name = "barcode_id")
	private String barcodeId;

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

	public int getPrice() {
		return (price == null) ? 0 : price;
	}

	public void setPrice(int price) {
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

	public void update(Item item) {
		if (item.getName() != null && !item.getName().isEmpty())
			this.name = item.getName();
		if (item.getImageUrl() != null && !item.getImageUrl().isEmpty())
			this.imageUrl = item.getImageUrl();
		if (item.getDescription() != null && !item.getDescription().isEmpty())
			this.description = item.getDescription();
		if (item.getBarcodeId() != null && !item.getBarcodeId().isEmpty())
			this.barcodeId = item.getBarcodeId();
		this.price = item.getPrice();
		this.reward = item.getReward();
	}
}

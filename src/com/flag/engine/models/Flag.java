package com.flag.engine.models;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION, table = "flags")
public class Flag {
	@Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
	@PrimaryKey
	private Long id;

	@Persistent
	@Index
	private double lat;

	@Persistent
	@Index
	private double lon;

	@Persistent
	@Column(name = "created_at")
	private long createdAt;

	@Persistent
	@Column(name = "shop_id")
	private Long shopId;

	@Persistent
	@Column(name = "shop_name")
	private String shopName;

	@NotPersistent
	private String shopDescription;

	@NotPersistent
	private int reward;

	@NotPersistent
	private boolean rewarded;

	public Long getId() {
		return id;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	public Long getShopId() {
		return shopId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getShopDescription() {
		return shopDescription;
	}

	public void setShopDescription(String shopDescription) {
		this.shopDescription = shopDescription;
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

	public void setShopInfo(Shop shop) {
		shopDescription = shop.getDescription();
		reward = shop.getReward();
		rewarded = shop.isRewarded();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("id=" + id).append(", lat=" + lat).append(", lon=" + lon).append(", createdAt=" + createdAt).append(", shopId=" + shopId);

		return sb.toString();
	}
}

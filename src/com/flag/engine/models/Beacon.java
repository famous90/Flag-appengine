package com.flag.engine.models;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Beacon {
	@PrimaryKey
	@Persistent
	private String id;

	@Persistent
	private Long flagId;

	@Persistent
	private Long shopId;

	@Persistent
	private double lat;

	@Persistent
	private double lon;

	@Persistent
	private String name;

	@Persistent
	@Index
	private long createdAt;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id.toUpperCase();
	}

	public Long getFlagId() {
		return flagId;
	}

	public void setFlagId(Long flagId) {
		this.flagId = flagId;
	}

	public Long getShopId() {
		return shopId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id: " + id).append("shopId: " + shopId);
		return sb.toString();
	}
}

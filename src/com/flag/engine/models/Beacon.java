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
	@Index
	private Long shopId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id.toLowerCase();
	}

	public Long getShopId() {
		return shopId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id: " + id).append("shopId: " + shopId);
		return sb.toString();
	}
}

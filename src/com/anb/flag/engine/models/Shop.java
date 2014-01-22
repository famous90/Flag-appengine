package com.anb.flag.engine.models;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Shop {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;

	@Persistent
	private String name;
	
	@Persistent
	private String imageUrl;

	@Persistent
	private int type;

	@Persistent
	private String description;

	@Persistent
	private int reward1;

	@Persistent
	private int reward2;
	
	@Persistent
	private int sign;

	public Long getId() {
		return id;
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

	public int getReward1() {
		return reward1;
	}

	public void setReward1(int reward1) {
		this.reward1 = reward1;
	}

	public int getReward2() {
		return reward2;
	}

	public void setReward2(int reward2) {
		this.reward2 = reward2;
	}

	public int getSign() {
		return sign;
	}

	public void setSign(int sign) {
		this.sign = sign;
	}
}

package com.flag.engine.models;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION, table = "userinfos")
public class UserInfo {
	@PrimaryKey
	@Persistent
	private Long userId;

	@Persistent
	@Index
	private boolean sex;

	@Persistent
	@Index
	private int birthYear;

	@Persistent
	private int job;

	@Persistent
	@Index
	private double lat;

	@Persistent
	@Index
	private double lon;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public boolean getSex() {
		return sex;
	}

	public void setSex(boolean sex) {
		this.sex = sex;
	}

	public int getBirthYear() {
		return birthYear;
	}

	public void setBirthYear(int birthYear) {
		this.birthYear = birthYear;
	}

	public int getJob() {
		return job;
	}

	public void setJob(int job) {
		this.job = job;
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

	public void update(UserInfo userInfo) {
		this.sex = userInfo.getSex();
		this.birthYear = userInfo.getBirthYear();
		this.job = userInfo.getJob();
	}
}

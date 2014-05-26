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
	private long birth;

	@Persistent
	private int job;

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

	public long getBirth() {
		return birth;
	}

	public void setBirth(long birth) {
		this.birth = birth;
	}

	public int getJob() {
		return job;
	}

	public void setJob(int job) {
		this.job = job;
	}

	public void update(UserInfo userInfo) {
		this.sex = userInfo.getSex();
		this.birth = userInfo.getBirth();
		this.job = userInfo.getJob();
	}

	public boolean isEmpty() {
		if (!sex && birth == 0 && job == 0)
			return true;
		else
			return false;
	}
}

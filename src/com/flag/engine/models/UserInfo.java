package com.flag.engine.models;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION, table = "userinfos")
public class UserInfo extends BaseModel {
	@PrimaryKey
	@Persistent
	private Long userId;

	@Persistent
	@Index
	private String phone;

	@Persistent
	@Index
	private int sex;

	@Persistent
	@Index
	private long birth;

	@Persistent
	private int job;
	
	@Persistent
	private String verificationCode;

	public UserInfo() {
		super();
	}

	public UserInfo(Long userId) {
		this.userId = userId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
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
	
	public String getVerificationCode() {
		return verificationCode;
	}

	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}

	public void update(UserInfo userInfo) {
		if (userInfo.getPhone() != null && !userInfo.getPhone().isEmpty())
			this.phone = userInfo.getPhone();
		if (userInfo.getSex() != 0)
			this.sex = userInfo.getSex();
		if (userInfo.getBirth() != 0)
			this.birth = userInfo.getBirth();
		if (userInfo.getJob() != 0)
			this.job = userInfo.getJob();
		if (userInfo.getVerificationCode() != null && !userInfo.getVerificationCode().isEmpty())
			this.verificationCode = userInfo.getVerificationCode();
	}

	public boolean isEmpty() {
		if (sex == 0 && birth == 0 && job == 0)
			return true;
		else
			return false;
	}

	public boolean verifyCode(String code) {
		if (verificationCode == null || verificationCode.isEmpty())
			return false;
		
		if (verificationCode.substring(0, verificationCode.indexOf("+")).equals(code))
			return true;
		
		return false;
	}
}

package com.anb.flag.engine.models;

public class ShopHint {
	private Long id;
	private String name;
	private int type;
	private int reward1;
	private int reward2;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
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
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("name=" + name).append(", type=" + type).append(", reward1=" + reward1).append(", reward2=" + reward2);
		
		return sb.toString();
	}
}

package com.anb.flag.engine.protocols;

public class GreetingsMultiplyReq {
	private int times;
	private String msg;

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("times=" + times + ", msg=" + msg);
		
		return sb.toString();
	}
}

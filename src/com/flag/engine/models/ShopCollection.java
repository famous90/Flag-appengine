package com.flag.engine.models;

import java.util.List;

public class ShopCollection {
	private List<Shop> shops;
	private List<Flag> flags;

	public ShopCollection() {
		super();
	}

	public ShopCollection(List<Shop> shops) {
		super();
		this.shops = shops;
	}

	public ShopCollection(List<Shop> shops, List<Flag> flags) {
		super();
		this.shops = shops;
		this.flags = flags;
	}

	public List<Shop> getShops() {
		return shops;
	}

	public void setShops(List<Shop> shops) {
		this.shops = shops;
	}

	public List<Flag> getFlags() {
		return flags;
	}

	public void setFlags(List<Flag> flags) {
		this.flags = flags;
	}
}

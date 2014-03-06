package com.flag.engine.models;

import java.util.List;

public class ShopCollection {
	private List<Shop> shops;

	public ShopCollection() {
		super();
	}

	public ShopCollection(List<Shop> shops) {
		super();
		this.shops = shops;
	}

	public List<Shop> getShops() {
		return shops;
	}

	public void setShops(List<Shop> shops) {
		this.shops = shops;
	}
}

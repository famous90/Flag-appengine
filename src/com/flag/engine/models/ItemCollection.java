package com.flag.engine.models;

import java.util.List;

public class ItemCollection {
	private List<Item> items;

	public ItemCollection() {
		super();
	}

	public ItemCollection(List<Item> items) {
		super();
		this.items = items;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}
}

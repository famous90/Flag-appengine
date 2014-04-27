package com.flag.engine.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ItemCollection {
	private List<Item> items;
	private List<Item> hiddenItems;

	public ItemCollection() {
		super();
	}

	public ItemCollection(List<Item> items) {
		super();
		this.items = items;
	}

	public ItemCollection(List<Item> items, List<Item> hiddenItems) {
		super();
		this.items = items;
		this.hiddenItems = hiddenItems;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public List<Item> getHiddenItems() {
		return hiddenItems;
	}

	public void setHiddenItems(List<Item> hiddenItems) {
		this.hiddenItems = hiddenItems;
	}

	public static List<Integer> randomIndexes(int size) {
		Random random = new Random();
		List<Integer> picks = new ArrayList<Integer>();
		if (size > 0)
			for (int i = 0; i < 20; i++) {
				int pick = random.nextInt(size);
				if (!picks.contains(pick))
					picks.add(pick);
				else
					i--;
			}

		return picks;
	}
}

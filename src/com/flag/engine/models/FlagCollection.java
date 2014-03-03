package com.flag.engine.models;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

public class FlagCollection {
	private List<Flag> flags;

	public FlagCollection() {
		super();
	}

	public FlagCollection(List<Flag> flags) {
		super();
		this.flags = flags;
	}

	public List<Flag> getFlags() {
		return flags;
	}

	public void setFlags(List<Flag> flags) {
		this.flags = flags;
	}

	public void filtLat(double min, double max) {
		List<Flag> targets = new ArrayList<Flag>();

		for (Flag flag : flags)
			if (flag.getLat() < min || flag.getLat() > max)
				targets.add(flag);

		flags.removeAll(targets);
	}

	public void setShopInfosOnFlags(long userId) {
		PersistenceManager pm = PMF.getPersistenceManagerSQL();

		for (Flag flag : flags) {
			try {
				Shop shop = pm.getObjectById(Shop.class, flag.getShopId());
				shop.setRewardedForUser(userId);
				flag.setShopInfo(shop);
			} catch (JDOObjectNotFoundException e) {
				continue;
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (Flag flag : flags)
			sb.append(" id:" + flag.getId());

		return sb.toString();
	}
}

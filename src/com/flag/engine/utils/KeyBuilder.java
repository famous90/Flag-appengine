package com.flag.engine.utils;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class KeyBuilder {
	public static Key makeRewardKey(Long userId, Long targetId, Long type) {
		return new KeyFactory.Builder("User", userId).addChild("Target", targetId).addChild("Reward", type).getKey();
	}
}

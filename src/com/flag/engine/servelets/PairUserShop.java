package com.flag.engine.servelets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.flag.engine.models.Item;
import com.flag.engine.models.Like;
import com.flag.engine.models.PMF;
import com.flag.engine.models.UserShopPair;

public class PairUserShop extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int LIKE_SHOP_P = 2;
	private static final int LIKE_ITEM_P = 1;

	@SuppressWarnings("unchecked")
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) {
		PersistenceManager pm = PMF.getPersistenceManager();

		try {
			Query query = pm.newQuery(Like.class);
			List<Like> likes = (List<Like>) pm.newQuery(query).execute();

			query = pm.newQuery(Item.class);
			List<Item> items = (List<Item>) pm.newQuery(query).execute();

			Map<Long, Map<Long, Integer>> uspMap = new HashMap<Long, Map<Long, Integer>>();

			for (Like like : likes) {
				long shopId = 0;
				int p = 0;
				if (like.getType() == Like.TYPE_SHOP) {
					shopId = like.getTargetId();
					p = LIKE_SHOP_P;
				} else if (like.getType() == Like.TYPE_ITEM) {
					for (Item item : items)
						if (item.getId().equals(like.getTargetId()))
							shopId = item.getShopId();
					p = LIKE_ITEM_P;
				}

				Map<Long, Integer> spMap = uspMap.get(like.getUserId());
				if (spMap == null)
					spMap = new HashMap<Long, Integer>();

				Integer point = spMap.get(shopId);
				if (point == null)
					point = 0;

				point += p;

				spMap.put(shopId, point);
				uspMap.put(like.getUserId(), spMap);
			}

			List<UserShopPair> pairs = new ArrayList<UserShopPair>();

			Set<Long> userIds = uspMap.keySet();
			for (Long userId : userIds) {
				Map<Long, Integer> spMap = uspMap.get(userId);
				Set<Long> shopIds = spMap.keySet();
				for (Long shopId : shopIds) {
					Integer point = spMap.get(shopId);
					pairs.add(new UserShopPair(userId, shopId, point));
				}
			}

			pm.makePersistentAll(pairs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pm.close();
		}
	}
}

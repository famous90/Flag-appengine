package com.flag.engine.servelets;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.flag.engine.models.PMF;
import com.flag.engine.models.UserShopPair;

public class DepairUserShop extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) {
		PersistenceManager pm = PMF.getPersistenceManager();
		Query query = pm.newQuery(UserShopPair.class);
		List<UserShopPair> pairs = (List<UserShopPair>) pm.newQuery(query).execute();
		pm.deletePersistentAll(pairs);
		pm.close();
	}
}

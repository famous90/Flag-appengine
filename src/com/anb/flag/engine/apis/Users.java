package com.anb.flag.engine.apis;

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.anb.flag.engine.constants.Constants;
import com.anb.flag.engine.models.PMF;
import com.anb.flag.engine.models.User;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;

@Api(name = "flagengine", version = "v1", clientIds = { Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID,
		Constants.API_EXPLORER_CLIENT_ID }, audiences = { Constants.ANDROID_AUDIENCE })
public class Users {
	private static final Logger log = Logger.getLogger(Users.class.getName());

	@ApiMethod(name = "users.insert", httpMethod = "post")
	public User insert(User user) {
		log.warning(user.toString());

		PersistenceManager pm = PMF.getPersistenceManager();
		pm.makePersistent(user);
		pm.close();

		return user;
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "users.get")
	public User get(@Nullable @Named("id") Long id) {
		log.warning("get user: id=" + id);

		PersistenceManager pm = PMF.getPersistenceManager();
		Query query = pm.newQuery(User.class);
		query.setFilter("id == idParam");
		query.declareParameters("java.lang.Long idParam");

		List<User> results = (List<User>) pm.newQuery(query).execute(id);
		if (results.isEmpty())
			return null;
		else
			return results.get(0);
	}

	@ApiMethod(name = "users.removeAll")
	public void removeAllUsers() {
		PersistenceManager pm = PMF.getPersistenceManager();
		Query query = pm.newQuery(User.class);
		query.deletePersistentAll();
		pm.close();
	}
}

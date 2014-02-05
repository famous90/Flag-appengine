package com.flag.engine.apis;

import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.flag.engine.constants.Constants;
import com.flag.engine.models.PMF;
import com.flag.engine.models.RetainForm;
import com.flag.engine.models.User;
import com.flag.engine.models.UserForm;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;

@Api(name = "flagengine", version = "v1", clientIds = { Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID,
		Constants.API_EXPLORER_CLIENT_ID }, audiences = { Constants.ANDROID_AUDIENCE })
public class Users {
	private static final Logger log = Logger.getLogger(Users.class.getName());

	@ApiMethod(name = "users.insert", path = "new_user", httpMethod = "post")
	public User insert(UserForm userForm) {
		log.warning("new user: " + userForm.toString());

		User user = new User(userForm);

		PersistenceManager pm = PMF.getPersistenceManager();
		pm.makePersistent(user);
		pm.close();

		return new User(user);
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "users.get", path = "old_user", httpMethod = "post")
	public User get(UserForm userForm) {
		log.warning("old user: " + userForm.toString());

		PersistenceManager pm = PMF.getPersistenceManager();
		Query query = pm.newQuery(User.class);
		query.setFilter("email == theEmail && password == thePassword");
		query.declareParameters("String theEmail, String thePassword");

		List<User> users = (List<User>) pm.newQuery(query).execute(userForm.getEmail(), userForm.getPassword());
		if (users.isEmpty())
			return null;
		else
			return new User(users.get(0));
	}
	
	@SuppressWarnings("unchecked")
	@ApiMethod(name = "users.retain", path = "user", httpMethod = "post")
	public User retain(RetainForm retainForm) {
		log.warning("retain user: " + retainForm.toString());
		
		PersistenceManager pm = PMF.getPersistenceManager();
		Query query = pm.newQuery(User.class);
		query.setFilter("id == theId");
		query.declareParameters("long theId");
		
		List<User> users = (List<User>) pm.newQuery(query).execute(retainForm.getId());
		if (users.isEmpty())
			return null;
		else
			return new User(users.get(0));
	}

	@ApiMethod(name = "users.removeAll")
	public void removeAllUsers() {
		PersistenceManager pm = PMF.getPersistenceManager();
		Query query = pm.newQuery(User.class);
		query.deletePersistentAll();
		pm.close();
	}
}

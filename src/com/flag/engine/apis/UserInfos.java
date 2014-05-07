package com.flag.engine.apis;

import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import com.flag.engine.constants.Constants;
import com.flag.engine.models.PMF;
import com.flag.engine.models.UserInfo;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;

@Api(name = "flagengine", version = "v1", clientIds = { Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID,
		Constants.API_EXPLORER_CLIENT_ID }, audiences = { Constants.ANDROID_AUDIENCE })
public class UserInfos {
	private static final Logger log = Logger.getLogger(UserInfos.class.getName());

	@ApiMethod(name = "usreinfos.insert", path = "user_info", httpMethod = "post")
	public UserInfo insert(UserInfo userInfo) {
		log.info("user info for id: " + userInfo.getUserId());
		
		PersistenceManager pm = PMF.getPersistenceManagerSQL();
		pm.makePersistent(userInfo);
		pm.close();
		
		return userInfo;
	}
	
	@ApiMethod(name = "userinfos.get", path = "user_info", httpMethod = "get")
	public UserInfo get(@Nullable @Named("userId") long userId) {
		PersistenceManager pm = PMF.getPersistenceManagerSQL();
		UserInfo userInfo;
		
		try {
			userInfo = pm.getObjectById(UserInfo.class, userId);
		} catch (JDOObjectNotFoundException e) {
			return null;
		}
		
		return userInfo;
	}
	
	@ApiMethod(name = "userInfos.update", path = "user_info", httpMethod = "put")
	public UserInfo update(UserInfo userInfo) {
		PersistenceManager pm = PMF.getPersistenceManagerSQL();
		UserInfo target;
		
		try {
			target = pm.getObjectById(UserInfo.class, userInfo.getUserId());
			target.update(userInfo);
		} catch (JDOObjectNotFoundException e) {
			target = insert(userInfo);
		} finally {
			pm.close();
		}
		
		return target;
	}
}

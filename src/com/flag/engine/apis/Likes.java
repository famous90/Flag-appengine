package com.flag.engine.apis;

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.flag.engine.constants.Constants;
import com.flag.engine.models.Like;
import com.flag.engine.models.PMF;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;

@Api(name = "flagengine", version = "v1", clientIds = { Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID,
		Constants.API_EXPLORER_CLIENT_ID }, audiences = { Constants.ANDROID_AUDIENCE })
public class Likes {
	private static final Logger log = Logger.getLogger(Likes.class.getName());

	@ApiMethod(name = "likes.insert", path = "like", httpMethod = "post")
	public Like insert(Like like) {
		log.info("insert like: " + like.getUserId() + "/" + like.getTargetId() + "/" + like.getType());
		
		like.refreshId();
		
		PersistenceManager pm = PMF.getPersistenceManager();
		pm.makePersistent(like);
		pm.close();
		
		return like;
	}
	
	@ApiMethod(name = "likes.delete", path = "like", httpMethod = "delete")
	public void delete(@Nullable @Named("userId") Long userId, @Nullable @Named("itemId") Long itemId, @Nullable @Named("type") int type) {
		log.info("delete like: " + userId + "/" + itemId);

		PersistenceManager pm = PMF.getPersistenceManager();
		
		try {
			Like target = pm.getObjectById(Like.class, Like.obtainLikeId(userId, itemId, type));
			pm.deletePersistent(target);
		} catch(JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	@ApiMethod(name = "likes.delete.all", path = "like_all", httpMethod = "delete")
	public void deleteAll() {
		PersistenceManager pm = PMF.getPersistenceManager();
		
		Query query = pm.newQuery(Like.class);
		List<Like> likes = (List<Like>) pm.newQuery(query).execute();
		pm.deletePersistentAll(likes);
		pm.close();
	}
}

package com.flag.engine.apis;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.flag.engine.constants.Constants;
import com.flag.engine.models.Notice;
import com.flag.engine.models.PMF;
import com.flag.engine.models.Version;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;

@Api(name = "flagengine", version = "v1", clientIds = { Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID,
		Constants.API_EXPLORER_CLIENT_ID }, audiences = { Constants.ANDROID_AUDIENCE })
public class Apps {
	private static final Logger log = Logger.getLogger(Apps.class.getName());
	
	@ApiMethod(name = "apps.versions.insert", path = "version", httpMethod = "post")
	public Version insertVersion(Version version) {
		log.warning("insert version: " + version.toString());
		
		version.setCreatedAt(new Date().getTime());
		
		PersistenceManager pm = PMF.getPersistenceManager();
		pm.makePersistent(version);
		pm.close();
		
		return version;
	}
	
	@SuppressWarnings("unchecked")
	@ApiMethod(name = "apps.version.get", path = "version", httpMethod = "get")
	public Version getVersion() {
		PersistenceManager pm = PMF.getPersistenceManager();
		
		Query query = pm.newQuery(Version.class);
		query.setOrdering("createdAt desc");
		query.setRange(0, 1);
		List<Version> versions = (List<Version>) pm.newQuery(query).execute();
		
		if (versions.isEmpty())
			return Version.getDefault();
		else
			return versions.get(0);
	}
	
	@ApiMethod(name = "apps.notice.insert", path = "notice", httpMethod = "post")
	public Notice insertNotice(Notice notice) {
		log.warning("insert notice: " + notice.toString());
		
		notice.setCreatedAt(new Date().getTime());
		
		PersistenceManager pm = PMF.getPersistenceManager();
		pm.makePersistent(notice);
		pm.close();
		
		return notice;
	}
	
	@SuppressWarnings("unchecked")
	@ApiMethod(name = "apps.notice.get", path = "notice", httpMethod = "get")
	public Notice getNotice() {
		PersistenceManager pm = PMF.getPersistenceManager();
		
		Query query = pm.newQuery(Notice.class);
		query.setOrdering("createdAt desc");
		query.setRange(0, 1);
		List<Notice> notices = (List<Notice>) pm.newQuery(query).execute();
		
		if (notices.isEmpty())
			return null;
		else
			return notices.get(0);
	}
}

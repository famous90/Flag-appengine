package com.flag.engine.apis;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.flag.engine.constants.Constants;
import com.flag.engine.models.FeedbackMessage;
import com.flag.engine.models.Notice;
import com.flag.engine.models.PMF;
import com.flag.engine.models.PP;
import com.flag.engine.models.TOU;
import com.flag.engine.models.Version;
import com.flag.engine.utils.MailUtils;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;

@Api(name = "flagengine", version = "v1", clientIds = { Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID,
		Constants.API_EXPLORER_CLIENT_ID }, audiences = { Constants.ANDROID_AUDIENCE })
public class Apps {
	private static final Logger log = Logger.getLogger(Apps.class.getName());

	@ApiMethod(name = "apps.versions.insert", path = "version", httpMethod = "post")
	public Version insertVersion(Version version) {
		log.info("insert version: " + version.toString());

		version.setCreatedAt(new Date().getTime());

		PersistenceManager pm = PMF.getPersistenceManager();
		pm.makePersistent(version);
		pm.close();

		return version;
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "apps.versions.get", path = "version", httpMethod = "get")
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

	@ApiMethod(name = "apps.tous.insert", path = "tou", httpMethod = "post")
	public TOU insertTOU(TOU tou) {
		PersistenceManager pm = PMF.getPersistenceManager();
		
		tou.setId(new Date().getTime());
		
		pm.makePersistent(tou);
		pm.close();

		return tou;
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "apps.tous.get", path = "tou", httpMethod = "get")
	public TOU getTOU() {
		PersistenceManager pm = PMF.getPersistenceManager();

		Query query = pm.newQuery(TOU.class);
		query.setOrdering("id desc");
		query.setRange(0, 1);
		List<TOU> tous = (List<TOU>) pm.newQuery(query).execute();

		if (tous.isEmpty())
			return null;
		else
			return tous.get(0);
	}

	@ApiMethod(name = "apps.pps.insert", path = "pp", httpMethod = "post")
	public PP insertPP(PP pp) {
		PersistenceManager pm = PMF.getPersistenceManager();
		
		pp.setId(new Date().getTime());
		
		pm.makePersistent(pp);
		pm.close();

		return pp;
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "apps.pps.get", path = "pp", httpMethod = "get")
	public PP getPP() {
		PersistenceManager pm = PMF.getPersistenceManager();

		Query query = pm.newQuery(PP.class);
		query.setOrdering("id desc");
		query.setRange(0, 1);
		List<PP> pps = (List<PP>) pm.newQuery(query).execute();

		if (pps.isEmpty())
			return null;
		else
			return pps.get(0);
	}

	@ApiMethod(name = "apps.notices.insert", path = "notice", httpMethod = "post")
	public Notice insertNotice(Notice notice) {
		log.info("insert notice: " + notice.toString());

		notice.setCreatedAt(new Date().getTime());

		PersistenceManager pm = PMF.getPersistenceManager();
		pm.makePersistent(notice);
		pm.close();

		return notice;
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "apps.notices.get", path = "notice", httpMethod = "get")
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

	@ApiMethod(name = "apps.feedbacks.insert", path = "feedback", httpMethod = "post")
	public Void insertFeedback(FeedbackMessage feedbackMessage) {
		feedbackMessage.setCreatedAt(new Date().getTime());

		PersistenceManager pm = PMF.getPersistenceManager();
		pm.makePersistent(feedbackMessage);
		pm.close();

		MailUtils.sendFeedbackMail(feedbackMessage);

		return null;
	}
}

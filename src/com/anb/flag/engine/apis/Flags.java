package com.anb.flag.engine.apis;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.anb.flag.engine.constants.Constants;
import com.anb.flag.engine.models.Flag;
import com.anb.flag.engine.models.FlagCollection;
import com.anb.flag.engine.models.PMF;
import com.anb.flag.engine.utils.LocationUtils;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;

@Api(name = "flagengine", version = "v1", clientIds = { Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID,
		Constants.API_EXPLORER_CLIENT_ID }, audiences = { Constants.ANDROID_AUDIENCE })
public class Flags {
	private static final Logger log = Logger.getLogger(Flags.class.getName());

	@ApiMethod(name = "flags.insert", httpMethod = "post")
	public Flag insert(Flag flag) {
		log.warning("insert flag: " + flag.toString());

		flag.setCreatedAt(new Date().getTime());

		PersistenceManager pm = PMF.getPersistenceManager();
		pm.makePersistent(flag);
		pm.close();

		return flag;
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "flags.list")
	public FlagCollection list(@Nullable @Named("lat") double lat, @Nullable @Named("lon") double lon) {
		log.warning("list flag: lat=" + lat + " lon=" + lon);
		
		PersistenceManager pm = PMF.getPersistenceManager();

		Query query = pm.newQuery(Flag.class);
		query.setFilter("lon > minLon && lon < maxLon");
		query.declareParameters("double minLon, double maxLon");
		List<Flag> flags = (List<Flag>) pm.newQuery(query).execute(lon - LocationUtils.NEAR_DISTANCE_DEGREE, lon + LocationUtils.NEAR_DISTANCE_DEGREE);
		
		FlagCollection response = new FlagCollection(flags);
		response.filtLat(lat - LocationUtils.NEAR_DISTANCE_DEGREE, lat + LocationUtils.NEAR_DISTANCE_DEGREE);

		return response;
	}
	
	@ApiMethod(name = "flags.removeAll")
	public void removeAllFlags() {
		PersistenceManager pm = PMF.getPersistenceManager();
		Query query = pm.newQuery(Flag.class);
		query.deletePersistentAll();
		pm.close();
	}
}

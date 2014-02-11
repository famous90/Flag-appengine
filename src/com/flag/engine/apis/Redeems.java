package com.flag.engine.apis;

import java.util.logging.Logger;

import com.flag.engine.constants.Constants;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;

@Api(name = "flagengine", version = "v1", clientIds = { Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID,
		Constants.API_EXPLORER_CLIENT_ID }, audiences = { Constants.ANDROID_AUDIENCE })
public class Redeems {
	private static final Logger log = Logger.getLogger(Flags.class.getName());
	
	@ApiMethod(name = "redeems.insert", path = "redeem", httpMethod = "post")
	public void insert() {
		log.warning("insert redeem: ");
	}
	
}

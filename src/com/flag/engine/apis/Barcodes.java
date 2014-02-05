package com.flag.engine.apis;

import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.flag.engine.constants.Constants;
import com.flag.engine.models.Barcode;
import com.flag.engine.models.PMF;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;

@Api(name = "flagengine", version = "v1", clientIds = { Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID,
		Constants.API_EXPLORER_CLIENT_ID }, audiences = { Constants.ANDROID_AUDIENCE })
public class Barcodes {
	private static final Logger log = Logger.getLogger(Flags.class.getName());

	@ApiMethod(name = "barcodes.insert", httpMethod = "post")
	public void insert(Barcode barcode) {
		log.warning("insert barcode: " + barcode.toString());

		PersistenceManager pm = PMF.getPersistenceManager();
		pm.makePersistent(barcode);
		pm.close();
	}

	@ApiMethod(name = "barcodes.remove")
	public void remove(Barcode barcode) {
		PersistenceManager pm = PMF.getPersistenceManager();

		Query query = pm.newQuery(Barcode.class);
		query.setFilter("id == barcodeId");
		query.declareParameters("String barcodeId");
		query.deletePersistentAll(barcode.getId());

		pm.close();
	}
}

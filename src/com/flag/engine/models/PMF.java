/* Copyright 2013 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flag.engine.models;

import java.util.HashMap;
import java.util.Map;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

public final class PMF {
	private static Map<String, String> properties = new HashMap<String, String>();

	static {
		properties.put("javax.jdo.option.ConnectionDriverName", System.getProperty("cloudsql.driver"));
		properties.put("javax.jdo.option.ConnectionURL", System.getProperty("cloudsql.url"));
		properties.put("javax.jdo.option.ConnectionUserName", System.getProperty("cloudsql.user"));
		properties.put("javax.jdo.option.ConnectionPassword", System.getProperty("cloudsql.password"));
		properties.put("datanucleus.autoCreateTables", "true");
	}

	private static final PersistenceManagerFactory pmfInstance = JDOHelper.getPersistenceManagerFactory("transactions-optional");
	private static final PersistenceManagerFactory pmfInstanceSQL = JDOHelper.getPersistenceManagerFactory(properties, "transactions-optional");

	private PMF() {
	}

	public static PersistenceManagerFactory get() {
		return pmfInstance;
	}

	public static PersistenceManager getPersistenceManager() {
		return pmfInstance.getPersistenceManager();
	}

	public static PersistenceManagerFactory getSQL() {
		return pmfInstanceSQL;
	}
	
	public static PersistenceManager getPersistenceManagerSQL() {
		return pmfInstanceSQL.getPersistenceManager();
	}
	
}

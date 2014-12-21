/**
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.emm.apkgenerator.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.emm.apkgenerator.exception.ApkGenerationException;

/**
 * This class accepts the data coming in JSON form and translate them to java
 * JSON objects.
 */
public class ObjectReader {

	private JSONObject json;
	private static final Log LOG = LogFactory.getLog(ObjectReader.class);

	/**
	 * Create a JSON object that represent the JSON string passed as a
	 * parameter.
	 *
	 * @param jsonPayload JSON string that needs to be converted to a JSON object.
	 */
	public ObjectReader(String jsonPayload) {
		try {
			json = new JSONObject(jsonPayload);
		} catch (JSONException e) {
			String message = "Error in converting String to JSONObject- the object is:" + jsonPayload;
			LOG.error(message);
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Read a JSON object when the key is provided.
	 *
	 * @param key The key of the JSON value that needs to be retrieved.
	 * @return The value represented by the key.
	 * @throws ApkGenerationException
	 */
	public String read(String key) throws ApkGenerationException {
		try {
			return json.getString(key);
		} catch (JSONException e) {
			String message = "Error in getting String " + key + " from JSONObject.";
			LOG.error(message, e);
			throw new ApkGenerationException(message, e);
		}
	}
}

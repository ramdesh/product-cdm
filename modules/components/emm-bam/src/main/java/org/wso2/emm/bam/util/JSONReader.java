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
package org.wso2.emm.bam.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.emm.bam.PublisherException;

/**
 * This class accepts the data coming as a JSON string and translate them to
 * java JSON
 * objects
 */
public class JSONReader {
	private JSONObject json;
	private static Log log = LogFactory.getLog(JSONReader.class);

	/**
	 * Create a JSON object the represent the JSON string coming as a parameter.
	 * 
	 * @param jsonStr
	 *            JSON string that needs to be converted to a JSON object
	 * @throws PublisherException
	 */
	public JSONReader(String jsonStr) throws PublisherException {
		try {
			json = new JSONObject(jsonStr);
		} catch (JSONException e) {
			String message = "Error in converting String to JSONObject- object is:" + jsonStr;
			log.error(message);
			throw new IllegalArgumentException(message);

		}
	}

	/**
	 * Read a JSON object when the key is provided.
	 * 
	 * @param key
	 *            of the JSON
	 * @return the value represented by the key
	 * @throws PublisherException
	 */
	public String read(String key) throws PublisherException {
		try {
			return json.getString(key);
		} catch (JSONException e) {
			String message = "Error in getting String " + key + " from JSONObject";
			log.error(message, e);
			throw new PublisherException(message, e);
		}
	}

}

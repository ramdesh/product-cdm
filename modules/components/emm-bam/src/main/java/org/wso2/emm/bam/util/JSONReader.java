/**
 *  Copyright (c) WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wso2.emm.bam.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
/*
 * This class accepts the data coming in JSON form and translate them to
 * JSON objects
 */
public class JSONReader {
	public JSONObject json = null;
	private static Log log = LogFactory.getLog(JSONReader.class);

	public JSONReader(String jsObj) {
		try {
			json = new JSONObject(jsObj);
		} catch (JSONException e) {
			log.error("Error in converting String to JSONObject- object is:"+jsObj, e);
		}
	}

	//read a JSON object when the key is provided
	public String read(String key) {
		try {
			return json.getString(key);
		} catch (JSONException e) {
			log.error(
					"Error in getting String " + key + " from JSONObject",
					e);
		} catch (Exception e) {
			log.error("error while reading parameter " + key, e);
		}
		return null;
	}
}
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

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.emm.apkgenerator.generators.CertificateGenerationException;

/**
 * This class accepts the data coming in JSON form and translate them to java
 * JSON objects.
 */
public class ObjectReader {

	private JSONObject json = null;
	private static Logger log = Logger.getLogger(ObjectReader.class);

	/**
	 * Create a JSON object that represent the JSON string passed as a
	 * parameter.
	 * 
	 * @param jsonStr
	 *            JSON string that needs to be converted to a JSON object
	 * @throws CertificateGenerationException
	 */
	public ObjectReader(String jsonStr) throws CertificateGenerationException {
		try {
			json = new JSONObject(jsonStr);
		} catch (JSONException e) {
			log.error("Error in converting String to JSONObject- object is:" + jsonStr, e);
			throw new CertificateGenerationException(
			                                         "Error in converting String to JSONObject- object is:" +
			                                                 jsonStr, e);

		}
	}

	/**
	 * Read a JSON object when the key is provided.
	 * 
	 * @param key
	 *            of the JSON
	 * @return the value represented by the key
	 * @throws CertificateGenerationException
	 */
	public String read(String key) throws CertificateGenerationException {
		try {
			return json.getString(key);
		} catch (JSONException e) {
			log.error("Error in getting String " + key + " from JSONObject, " + e.getMessage(), e);
			throw new CertificateGenerationException("Error in getting String " + key +
			                                         " from JSONObject, " + e.getMessage(), e);
		}
	}
}

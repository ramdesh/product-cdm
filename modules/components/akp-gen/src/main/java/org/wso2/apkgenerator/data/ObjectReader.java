package org.wso2.apkgenerator.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
/*
 * This is used to convert a json string to a Json object and read the values 
 * from the created json obkect
 */
public class ObjectReader {
	public JSONObject json = null;
	private static Log log = LogFactory.getLog(ObjectReader.class);

	public ObjectReader(String jsObj) {
		try {
			json = new JSONObject(jsObj);
		} catch (JSONException e) {
			log.error("Error in converting String to JSONObject", e);
		}
	}

	//read a json object when the key is provided
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
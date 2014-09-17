/*
 * Copyright (c) 2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.mobile.task;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.SystemProperties;
import org.wso2.carbon.ntask.core.Task;
import org.wso2.mobile.task.utils.EMMTaskConfig;

import java.io.IOException;
import java.util.Map;

public class TaskImplementor implements Task {

	private static final Log log = LogFactory.getLog(TaskImplementor.class);

	/*
	 * This function calls the Device Monitor API
	 */
	public void execute() {

		GetMethod getMethod = null;
		try {

			String emmServerURL = EMMTaskConfig.getConfigEntry(EMMTaskConfig.EMM_SERVER_URL);
			String host = SystemProperties.getProperty(EMMTaskConfig.SERVER_HOST);
			String ip = SystemProperties.getProperty(EMMTaskConfig.CARBON_LOCAL_IP);
			String port = SystemProperties.getProperty(EMMTaskConfig.MGT_TRANSPORT_HTTPS_PROXYPORT);
			if (port == null) {
	            port = SystemProperties.getProperty(EMMTaskConfig.MGT_TRANSPORT_HTTPS_PORT);
            }

			String postUrl = "";
			if (emmServerURL == null) {
				if (host == null || host == EMMTaskConfig.LOCALHOST) {
					postUrl = EMMTaskConfig.HTTPS + ip + ":" + port;
				} else {
					postUrl = EMMTaskConfig.HTTPS + host + ":" + port;
				}
			} else {
				postUrl = emmServerURL;
			}

			getMethod = new GetMethod(postUrl + EMMTaskConfig.MONITOR_URL);
			final HttpClient httpClient = new HttpClient();
			getMethod.addRequestHeader(EMMTaskConfig.CONTENT_TYPE, EMMTaskConfig.APPLICATION_JSON);
			httpClient.executeMethod(getMethod);
		} catch (HttpException e) {
			log.error("HTTP exception: " + e);
		} catch (IOException e) {
			log.error("IO Exception: " + e);
		} finally {
			getMethod.releaseConnection();
		}
	}

	public void setProperties(Map<String, String> properties) {

	}

	public void init() {

	}
}

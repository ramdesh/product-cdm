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
package org.wso2.emm.bam;

import org.apache.log4j.Logger;
import org.wso2.carbon.databridge.agent.thrift.AsyncDataPublisher;
import org.wso2.carbon.databridge.agent.thrift.exception.AgentException;
import org.wso2.carbon.databridge.commons.StreamDefinition;
import org.wso2.emm.bam.util.Constants;
/*
 * This class makes the actual call to BAM via AsyncDataPublisher,
 * when the payload and the stream definition is provided
 */
public class Publisher {
	private static Logger logger = Logger.getLogger(Publisher.class);

	//TODO: retrieve constant values from a config file
	public void publish(Object[] payload, StreamDefinition streamDef) {
		System.setProperty("javax.net.ssl.trustStore", Constants.BAM_PATH);
		System.setProperty("javax.net.ssl.trustStorePassword", Constants.BAM_PASSWORD);
		AsyncDataPublisher asyncDataPublisher = new AsyncDataPublisher(
				"tcp://localhost:7611", "admin", "admin");
		asyncDataPublisher.addStreamDefinition(streamDef);

		try {
			asyncDataPublisher.publish(streamDef.getName(),
					streamDef.getVersion(), null, null, payload);
		} catch (AgentException e) {
			logger.error("error while publishing data to BAM ", e);
		}

	}
}
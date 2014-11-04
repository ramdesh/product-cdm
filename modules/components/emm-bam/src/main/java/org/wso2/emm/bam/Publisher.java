/*
 * *
 * * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights
 * Reserved.
 * *
 * * Licensed under the Apache License, Version 2.0 (the "License");
 * * you may not use this file except in compliance with the License.
 * * You may obtain a copy of the License at
 * *
 * * http://www.apache.org/licenses/LICENSE-2.0
 * *
 * * Unless required by applicable law or agreed to in writing, software
 * * distributed under the License is distributed on an "AS IS" BASIS,
 * * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * * See the License for the specific language governing permissions and
 * * limitations under the License.
 */
package org.wso2.emm.bam;

import org.apache.log4j.Logger;
import org.wso2.carbon.databridge.agent.thrift.AsyncDataPublisher;
import org.wso2.carbon.databridge.agent.thrift.exception.AgentException;
import org.wso2.carbon.databridge.commons.StreamDefinition;
import org.wso2.emm.bam.util.Constants;
import org.wso2.emm.bam.util.EMMConfig;

/**
 * This class makes the actual call to BAM via AsyncDataPublisher, when the pay
 * load and the stream definition is provided.
 */
public class Publisher {

	private static Logger logger = Logger.getLogger(Publisher.class);
	private static AsyncDataPublisher asyncDataPublisher = null;

	/**
	 * @param payload
	 *            of {@link Object} type holds the real values of the payload in
	 *            the correct order defined in pay load definition which can be
	 *            find in {@link StreamDefinition}
	 * @param streamDef
	 *            of type {@link StreamDefinition} holds, the stream definition
	 *            created with {@link StreamDefCreator}
	 * @throws PublisherException
	 */

	private static volatile Publisher publisherInstance = null;

	private Publisher() {
	}

	public static Publisher getInstance() throws PublisherException {
		if (publisherInstance == null) {
			synchronized (EMMConfig.class) {
				if (publisherInstance == null) {
					publisherInstance = new Publisher();
					EMMConfig configurations = EMMConfig.getInstance();
					if (asyncDataPublisher == null) {
						asyncDataPublisher = new AsyncDataPublisher(
								configurations
										.getConfigEntry(Constants.RECIEVER_URL_BAM),
								configurations
										.getConfigEntry(Constants.BAM_USERNAME),
								configurations
										.getConfigEntry(Constants.BAM_PASSWORD_STR));
					}

				}
			}
		}
		return publisherInstance;
	}

	public void publish(Object[] payload, StreamDefinition streamDef)
			throws PublisherException {
		asyncDataPublisher.addStreamDefinition(streamDef);
		try {
			asyncDataPublisher.publish(streamDef.getName(),
					streamDef.getVersion(), null, null, payload);
		} catch (AgentException e) {
			logger.error("error while publishing data to BAM ", e);
			throw new PublisherException("error while publishing data to BAM ",
					e);
		}

	}
}

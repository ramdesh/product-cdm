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
package org.wso2.emm.bam;

import org.apache.log4j.Logger;
import org.wso2.carbon.databridge.agent.thrift.AsyncDataPublisher;
import org.wso2.carbon.databridge.agent.thrift.exception.AgentException;
import org.wso2.carbon.databridge.commons.StreamDefinition;
import org.wso2.emm.bam.util.Constants;
import org.wso2.emm.bam.util.EMMConfig;
import org.wso2.emm.bam.util.JSONReader;

/**
 * This can be used to publish different data streams to BAM.
 * This act as the entry point for any stream that needs to be published
 */
public class DataPublisher {
	private static AsyncDataPublisher asyncDataPublisher;
	private static Logger logger = Logger.getLogger(DataPublisher.class);

	public DataPublisher() throws PublisherException {
		EMMConfig configurations = EMMConfig.getInstance();
		asyncDataPublisher =
		                     new AsyncDataPublisher(
		                                            configurations.getConfigEntry(Constants.RECIEVER_URL_BAM),
		                                            configurations.getConfigEntry(Constants.BAM_USERNAME),
		                                            configurations.getConfigEntry(Constants.BAM_PASSWORD));
	}

	public void publish(StreamType streamType, String jsonValue) throws PublisherException {
		EMMStream stream = EMMStreamFactory.getStream(streamType);
		Object[] payload = stream.getPayload(new JSONReader(jsonValue));
		StreamDefinition definition = stream.getStreamDefinition();

		if (!asyncDataPublisher.isStreamDefinitionAdded(definition.getName(),
		                                                definition.getVersion())) {
			asyncDataPublisher.addStreamDefinition(definition);
		}
		try {
			asyncDataPublisher.publish(definition.getName(), definition.getVersion(), null, null,
			                           payload);
		} catch (AgentException e) {
			String message = "error while publishing " + definition.getName() + " to BAM ";
			logger.error(message, e);
			throw new PublisherException(message, e);
		}
	}

}

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
package org.wso2.emm.statistics;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.databridge.commons.AttributeType;
import org.wso2.carbon.databridge.commons.StreamDefinition;
import org.wso2.carbon.databridge.commons.exception.MalformedStreamDefinitionException;
import org.wso2.emm.statistics.util.Constants;
import org.wso2.emm.statistics.util.JSONReader;

/**
 * Defines the stream definition and the pay load format when publishing
 * BlackListed Application related data to BAM.
 */
class BlacklistedAppStream implements EMMStream {

	private static final Log LOG = LogFactory.getLog(BlacklistedAppStream.class);
	private StreamDefinition streamDefinition;

	public BlacklistedAppStream() throws PublisherException {
		String streamName = StreamType.BLACKLISTED_APPS.getStreamType();
		try {
			streamDefinition =
					new StreamDefinition(
							streamName, Constants.StreamVersion.BLACKLISTED_APPS_STREAM_VERSION);
			streamDefinition.addPayloadData(Constants.StreamKey.PACKAGE_NAME, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.APP_NAME, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.PLATFORM, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.TYPE, AttributeType.STRING);

		} catch (MalformedStreamDefinitionException e) {
			String message = "Error getting stream definition for " + streamName + "  , Version-" +
			                 Constants.StreamVersion.BLACKLISTED_APPS_STREAM_VERSION;
			LOG.error(message, e);
			throw new PublisherException(message, e);
		}
	}

	public Object[] getPayload(JSONReader jsonReader) throws PublisherException {
		return new Object[] { jsonReader.read(Constants.StreamKey.PACKAGE_NAME),
		                      jsonReader.read(Constants.StreamKey.APP_NAME),
		                      jsonReader.read(Constants.StreamKey.PLATFORM),
		                      jsonReader.read(Constants.StreamKey.TYPE) };
	}

	public StreamDefinition getStreamDefinition() {
		return streamDefinition;
	}
}
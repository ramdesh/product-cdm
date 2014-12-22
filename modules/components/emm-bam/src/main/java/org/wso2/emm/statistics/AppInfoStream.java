/*
 * Copyright (c) 2014 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
 * Defines the stream definition and the payload format when publishing
 * Application related notifications that comes from devices to BAM.
 */
class AppInfoStream implements EMMStream {

	private static final Log LOG = LogFactory.getLog(AppInfoStream.class);
	private StreamDefinition streamDefinition;

	public AppInfoStream() throws PublisherException {
		String streamName = StreamType.APP_NOTIFICATIONS.getStreamType();
		try {
			streamDefinition =
					new StreamDefinition(
							streamName,
							Constants.StreamVersion.APP_NOTIFICATIONS_STREAM_VERSION);
			streamDefinition.addPayloadData(Constants.StreamKey.USERID, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.STATUS, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.DEVICE_ID, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.SENT_DATE, AttributeType.STRING);
			streamDefinition
					.addPayloadData(Constants.StreamKey.RECEIVED_DATE, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.FEATURE_CODE, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.TENANT, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.MESSAGE_ID, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.GROUP_ID, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.PACKAGE_NAME, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.ICON, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.APP_NAME, AttributeType.STRING);
		} catch (MalformedStreamDefinitionException e) {
			String message = "Error getting stream definition for " + streamName + "  , Version-" +
			                 Constants.StreamVersion.APP_NOTIFICATIONS_STREAM_VERSION;
			LOG.error(message, e);
			throw new PublisherException(message, e);
		}
	}

	public Object[] getPayload(JSONReader jsonReader) throws PublisherException {
		return new Object[] { jsonReader.read(Constants.StreamKey.USERID),
		                      jsonReader.read(Constants.StreamKey.STATUS),
		                      jsonReader.read(Constants.StreamKey.DEVICE_ID),
		                      jsonReader.read(Constants.StreamKey.SENT_DATE),
		                      jsonReader.read(Constants.StreamKey.RECEIVED_DATE),
		                      jsonReader.read(Constants.StreamKey.FEATURE_CODE),
		                      jsonReader.read(Constants.StreamKey.TENANT),
		                      jsonReader.read(Constants.StreamKey.MESSAGE_ID),
		                      jsonReader.read(Constants.StreamKey.GROUP_ID),
		                      jsonReader.read(Constants.StreamKey.PACKAGE_NAME),
		                      jsonReader.read(Constants.StreamKey.ICON),
		                      jsonReader.read(Constants.StreamKey.APP_NAME) };
	}

	public StreamDefinition getStreamDefinition() {
		return streamDefinition;
	}
}

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
 * Policy stream holds the device policy compliance details published by devices
 * time to time.
 */
class PolicyStream implements EMMStream {

	private static final Log LOG = LogFactory.getLog(PolicyStream.class);
	private StreamDefinition streamDefinition;

	public PolicyStream() throws PublisherException {
		String streamName = StreamType.POLICY_NOTIFICATIONS.getStreamType();
		try {
			streamDefinition = new StreamDefinition(streamName,
			                                        Constants.StreamVersion.POLICY_NOTIFICATIONS_STREAM_VERSION);
			streamDefinition.addPayloadData(Constants.StreamKey.USERID, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.STATUS, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.DEVICE_ID, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.SENT_DATE, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.RECEIVED_DATE, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.FEATURE_CODE, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.TENANT, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.MESSAGE_ID, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.GROUP_ID, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.CODE, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.POLICY_STATUS, AttributeType.STRING);

		} catch (MalformedStreamDefinitionException e) {
			String message = "Error getting stream definition for " + streamName + "  , Version-" +
			                 Constants.StreamVersion.POLICY_NOTIFICATIONS_STREAM_VERSION;
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
		                      jsonReader.read(Constants.StreamKey.CODE),
		                      jsonReader.read(Constants.StreamKey.POLICY_STATUS) };
	}

	public StreamDefinition getStreamDefinition() {
		return streamDefinition;
	}
}
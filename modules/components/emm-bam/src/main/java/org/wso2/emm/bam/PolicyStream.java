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
import org.wso2.carbon.databridge.commons.AttributeType;
import org.wso2.carbon.databridge.commons.StreamDefinition;
import org.wso2.carbon.databridge.commons.exception.MalformedStreamDefinitionException;
import org.wso2.emm.bam.util.Constants;
import org.wso2.emm.bam.util.JSONReader;

/**
 * Policy stream holds the device policy compliance details published by devices
 * time to time
 */
class PolicyStream implements EMMStream {

	private static Logger log = Logger.getLogger(PolicyStream.class);
	private StreamDefinition streamDefinition;

	public PolicyStream() throws PublisherException {
		String streamName = StreamType.POLICY_NOTIFICATIONS.getStreamType();
		try {
			streamDefinition =
			                   new StreamDefinition(streamName,
			                                        Constants.POLICY_NOTIFICATIONS_STREAM_VERSION);
			streamDefinition.addPayloadData(Constants.USERID, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.STATUS, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.DEVICEID, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.SENT_DATE, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.RECEIVED_DATE, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.FEATURE_CODE, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.TENANT, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.MESSAGE_ID, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.GROUP_ID, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.CODE, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.POLICY_STATUS, AttributeType.STRING);

		} catch (MalformedStreamDefinitionException e) {
			String message =
			                 "Error getting stream definition for " + streamName + "  , Version-" +
			                         Constants.POLICY_NOTIFICATIONS_STREAM_VERSION;
			log.error(message, e);
			throw new PublisherException(message, e);
		}
	}

	public Object[] getPayload(JSONReader jsonReader) throws PublisherException {
		return new Object[] { jsonReader.read(Constants.USERID), jsonReader.read(Constants.STATUS),
		                     jsonReader.read(Constants.DEVICEID),
		                     jsonReader.read(Constants.SENT_DATE),
		                     jsonReader.read(Constants.RECEIVED_DATE),
		                     jsonReader.read(Constants.FEATURE_CODE),
		                     jsonReader.read(Constants.TENANT),
		                     jsonReader.read(Constants.MESSAGE_ID),
		                     jsonReader.read(Constants.GROUP_ID), jsonReader.read(Constants.CODE),
		                     jsonReader.read(Constants.POLICY_STATUS) };
	}

	public StreamDefinition getStreamDefinition() {
		return streamDefinition;
	}
}
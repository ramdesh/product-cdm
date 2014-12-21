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
 * Defines the stream definition and the payload format when publishing Device
 * related information such as its current GPS coordinates, memory, battery
 * level coming from devices to BAM.
 */
class DeviceInfoStream implements EMMStream {

	private static final Log LOG = LogFactory.getLog(DeviceInfoStream.class);
	private StreamDefinition streamDefinition;

	public DeviceInfoStream() throws PublisherException {
		String streamName = StreamType.DEVICE_INFO_NOTIFICATIONS.getStreamType();
		try {
			streamDefinition =
					new StreamDefinition(
							streamName,
							Constants.StreamVersion.DEVICE_INFO_NOTIFICATIONS_STREAM_VERSION);
			streamDefinition.addPayloadData(Constants.StreamKey.USERID, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.STATUS, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.DEVICE_ID, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.SENT_DATE, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.RECEIVED_DATE, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.FEATURE_CODE, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.TENANT, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.MESSAGE_ID, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.GROUP_ID, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.INTERNAL_MEMORY_TOTAL,
			                                AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.INTERNAL_MEMORY_AVAILABLE,
			                                AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.LATITUDE, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.LONGITUDE, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.EXTERNAL_MEMORY_TOTAL,
			                                AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.EXTERNAL_MEMORY_AVAILABLE,
			                                AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.OPERATOR, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.BATTERY, AttributeType.STRING);
		} catch (MalformedStreamDefinitionException e) {
			String message = "Error getting stream definition for " + streamName + "  , Version-" +
			                 Constants.StreamVersion.DEVICE_INFO_NOTIFICATIONS_STREAM_VERSION;
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
		                      jsonReader.read(Constants.StreamKey.INTERNAL_MEMORY_TOTAL),
		                      jsonReader.read(Constants.StreamKey.INTERNAL_MEMORY_AVAILABLE),
		                      jsonReader.read(Constants.StreamKey.LATITUDE),
		                      jsonReader.read(Constants.StreamKey.LONGITUDE),
		                      jsonReader.read(Constants.StreamKey.EXTERNAL_MEMORY_TOTAL),
		                      jsonReader.read(Constants.StreamKey.EXTERNAL_MEMORY_AVAILABLE),
		                      jsonReader.read(Constants.StreamKey.OPERATOR),
		                      jsonReader.read(Constants.StreamKey.BATTERY) };
	}

	public StreamDefinition getStreamDefinition() {
		return streamDefinition;
	}

}

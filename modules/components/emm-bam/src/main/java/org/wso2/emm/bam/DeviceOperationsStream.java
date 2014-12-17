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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.databridge.commons.AttributeType;
import org.wso2.carbon.databridge.commons.StreamDefinition;
import org.wso2.carbon.databridge.commons.exception.MalformedStreamDefinitionException;
import org.wso2.emm.bam.util.Constants;
import org.wso2.emm.bam.util.JSONReader;

/**
 * Defines the stream definition and the pay load format when publishing
 * operations performed on a device, such as mute, lock, set password, etc.
 */
class DeviceOperationsStream implements EMMStream {

	private static Log logger = LogFactory.getLog(DeviceOperationsStream.class);
	private StreamDefinition streamDefinition;

	public DeviceOperationsStream() throws PublisherException {
		String streamName = StreamType.DEVICE_OPERATIONS.getStreamType();
		try {
			streamDefinition =
					new StreamDefinition(
							streamName,
							Constants.StreamVersion.DEVICE_OPERATIONS_STREAM_VERSION);
			streamDefinition.addPayloadData(Constants.StreamKey.USERID, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.DEVICEID, AttributeType.STRING);
			streamDefinition
					.addPayloadData(Constants.StreamKey.RECEIVED_DATE, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.CODE, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.DATA, AttributeType.STRING);
		} catch (MalformedStreamDefinitionException e) {
			String message = "Error getting stream definition for " + streamName + "  , Version-" +
			                 Constants.StreamVersion.DEVICE_OPERATIONS_STREAM_VERSION;
			logger.error(message, e);
			throw new PublisherException(message, e);
		}
	}

	public Object[] getPayload(JSONReader jsonReader) throws PublisherException {
		return new Object[] { jsonReader.read(Constants.StreamKey.USERID),
		                      jsonReader.read(Constants.StreamKey.DEVICEID),
		                      jsonReader.read(Constants.StreamKey.RECEIVED_DATE),
		                      jsonReader.read(Constants.StreamKey.CODE),
		                      jsonReader.read(Constants.StreamKey.DATA) };
	}

	public StreamDefinition getStreamDefinition() {
		return streamDefinition;
	}
}

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
 * Defines the stream definition and the pay load format when publishing Device
 * enrollment information coming from devices to BAM.
 */
class DeviceRegisterStream implements EMMStream {

	private static Log logger = LogFactory.getLog(DeviceRegisterStream.class);
	private StreamDefinition streamDefinition;

	public DeviceRegisterStream() throws PublisherException {
		String streamName = StreamType.DEVICE_REGISTRATIONS.getStreamType();
		try {
			streamDefinition =
			                   new StreamDefinition(streamName,
			                                        Constants.StreamVersion.DEVICE_STREAM_VERSION);
			streamDefinition.addPayloadData(Constants.StreamKey.TENANT_ID, AttributeType.INT);
			streamDefinition.addPayloadData(Constants.StreamKey.USERID, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.PLATFORM_ID, AttributeType.INT);
			streamDefinition.addPayloadData(Constants.StreamKey.REG_ID, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.OS_VERSION, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.PROPERTIES, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.CREATED_DATE, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.BYOD, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.VENDOR, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.MAC, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.StreamKey.DEVICEID, AttributeType.STRING);

		} catch (MalformedStreamDefinitionException e) {
			String message =
			                 "Error getting stream definition for " + streamName + "  , Version-" +
			                         Constants.StreamVersion.DEVICE_OPERATIONS_STREAM_VERSION;
			logger.error(message, e);
			throw new PublisherException(message, e);
		}
	}

	public Object[] getPayload(JSONReader jsonReader) throws PublisherException {
		return new Object[] { (Integer.parseInt(jsonReader.read(Constants.StreamKey.TENANT_ID))),
		                     jsonReader.read(Constants.StreamKey.USERID),
		                     (Integer.parseInt(jsonReader.read(Constants.StreamKey.PLATFORM_ID))),
		                     jsonReader.read(Constants.StreamKey.REG_ID),
		                     jsonReader.read(Constants.StreamKey.OS_VERSION),
		                     jsonReader.read(Constants.StreamKey.PROPERTIES),
		                     jsonReader.read(Constants.StreamKey.CREATED_DATE),
		                     jsonReader.read(Constants.StreamKey.BYOD),
		                     jsonReader.read(Constants.StreamKey.VENDOR),
		                     jsonReader.read(Constants.StreamKey.MAC),
		                     jsonReader.read(Constants.StreamKey.DEVICEID) };
	}

	public StreamDefinition getStreamDefinition() {
		return streamDefinition;
	}
}

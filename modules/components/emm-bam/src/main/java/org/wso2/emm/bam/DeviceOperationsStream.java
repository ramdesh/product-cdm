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
import org.wso2.carbon.databridge.commons.AttributeType;
import org.wso2.carbon.databridge.commons.StreamDefinition;
import org.wso2.carbon.databridge.commons.exception.MalformedStreamDefinitionException;
import org.wso2.emm.bam.util.Constants;
import org.wso2.emm.bam.util.JSONReader;

/**
 * Defines the stream definition and the pay load format when publishing
 * operations performed on a device, such as mute, lock, set password, etc.
 */
public class DeviceOperationsStream implements EMMStream {

	private static Logger log = Logger.getLogger(DeviceOperationsStream.class);
	private StreamDefinition streamDefinition;

	public DeviceOperationsStream() throws PublisherException {
		try {
			streamDefinition = new StreamDefinition(
					Constants.DEVICE_OPERATIONS_STREAM_NAME,
					Constants.DEVICE_OPERATIONS_STREAM_VERSION);
			streamDefinition.addPayloadData(Constants.USERID,
					AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.DEVICEID,
					AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.RECIEVED_DATE,
					AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.CODE,
					AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.DATA,
					AttributeType.STRING);
		} catch (MalformedStreamDefinitionException e) {
			log.error(
					"Error getting stream definition for "
							+ Constants.DEVICE_OPERATIONS_STREAM_NAME
							+ "  , Version-"
							+ Constants.DEVICE_OPERATIONS_STREAM_VERSION + " ,"
							+ e.getMessage(), e);
			throw new PublisherException("Error getting stream definition for "
					+ Constants.DEVICE_OPERATIONS_STREAM_NAME + "  , Version-"
					+ Constants.DEVICE_OPERATIONS_STREAM_VERSION + " ,"
					+ e.getMessage(), e);
		}
	}

	public Object[] getPayload(JSONReader jsonReader) throws PublisherException {

		return new Object[] { jsonReader.read(Constants.USERID),
				jsonReader.read(Constants.DEVICEID),
				jsonReader.read(Constants.RECIEVED_DATE),
				jsonReader.read(Constants.CODE),
				jsonReader.read(Constants.DATA) };
	}

	public StreamDefinition getStreamDefinition() {
		return streamDefinition;
	}
}


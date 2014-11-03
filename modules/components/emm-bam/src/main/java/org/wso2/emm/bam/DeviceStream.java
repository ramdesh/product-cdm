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
 * Defines the stream definition and the pay load format when publishing Device
 * enrollment information coming from devices to BAM.
 */
public class DeviceStream implements EMMStream {

	private static Logger log = Logger.getLogger(DeviceStream.class);
	private StreamDefinition streamDefinition;

	public DeviceStream() throws PublisherException {
		try {
			streamDefinition = new StreamDefinition(
					Constants.DEVICE_STREAM_NAME,
					Constants.DEVICE_STREAM_VERSION);
			streamDefinition.addPayloadData(Constants.TENANT_ID,
					AttributeType.INT);
			streamDefinition.addPayloadData(Constants.USERID,
					AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.PLATFORM_ID,
					AttributeType.INT);
			streamDefinition.addPayloadData(Constants.REG_ID,
					AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.OS_VERSION,
					AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.PROPERTIES,
					AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.CREATED_DATE,
					AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.BYOD,
					AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.VENDOR,
					AttributeType.STRING);
			streamDefinition
					.addPayloadData(Constants.MAC, AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.DEVICEID,
					AttributeType.STRING);

		} catch (MalformedStreamDefinitionException e) {
			log.error(
					"Error getting stream definition for registration stream :"
							+ e.getMessage(), e);
			throw new PublisherException(
					"Error getting stream definition for registration stream",
					e);
		}
	}

	public Object[] getPayload(JSONReader jsonReader) throws PublisherException {
		if (log.isDebugEnabled()) {
			log.debug("Call to publish device registration details");
		}

		return new Object[] {
				(Integer.parseInt(jsonReader.read(Constants.TENANT_ID))),
				jsonReader.read(Constants.USERID),
				(Integer.parseInt(jsonReader.read(Constants.PLATFORM_ID))),
				jsonReader.read(Constants.REG_ID),
				jsonReader.read(Constants.OS_VERSION),
				jsonReader.read(Constants.PROPERTIES),
				jsonReader.read(Constants.CREATED_DATE),
				jsonReader.read(Constants.BYOD),
				jsonReader.read(Constants.VENDOR),
				jsonReader.read(Constants.MAC),
				jsonReader.read(Constants.DEVICEID) };
	}

	public StreamDefinition getStreamDefinition() {
		return streamDefinition;
	}
}

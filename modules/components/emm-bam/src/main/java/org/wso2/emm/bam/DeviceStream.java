/*
 * *
 *  *  Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *
 */
package org.wso2.emm.bam;

import org.apache.log4j.Logger;
import org.wso2.carbon.databridge.commons.AttributeType;
import org.wso2.carbon.databridge.commons.StreamDefinition;
import org.wso2.carbon.databridge.commons.exception.MalformedStreamDefinitionException;
import org.wso2.emm.bam.dto.DeviceDto;
import org.wso2.emm.bam.util.Constants;
import org.wso2.emm.bam.util.JSONReader;

public class DeviceStream implements EMMStreamDefinition{
	private static Logger log = Logger.getLogger(DeviceStream.class);
	private StreamDefCreator definition;

	

	public Object[] getPayload(JSONReader jsonReader) {
		if(log.isDebugEnabled()){
			log.debug("Call to publish device registration details");
		}
		DeviceDto dto = new DeviceDto();
		dto.setTenantId(Integer.parseInt(jsonReader.read("tenantId")));
		dto.setUserId(jsonReader.read("userId"));
		dto.setPlatformId(Integer.parseInt(jsonReader.read("platformId")));
		dto.setRegId(jsonReader.read("regId"));
		dto.setOsVersion(jsonReader.read("osVersion"));
		dto.setProperties(jsonReader.read("properties"));
		dto.setCreatedDate(jsonReader.read("createdDate"));
		dto.setByod(jsonReader.read("byod"));
		dto.setStatus(jsonReader.read("vendor"));
		dto.setStatus(jsonReader.read("mac"));

		return new Object[] {  dto.getTenantId(), dto.getUserId(),
				dto.getPlatformId(), dto.getRegId(), dto.getOsVersion(),
				dto.getProperties(), dto.getCreatedDate(), dto.getByod() 
				,dto.getVendor(),dto.getMac() };
	}

	public StreamDefinition getStreamDefinition(){
		definition = new StreamDefCreator(Constants.DEVICE_STREAM_NAME,
				Constants.DEVICE_STREAM_VERSION,
				Constants.DEVICE_STREAM_NICKNAME,
				Constants.DEVICE_STREAM_DESCRIPTION);
	
		definition.setPayloadDefinition("tenantId", AttributeType.INT);
		definition.setPayloadDefinition("userId", AttributeType.STRING);
		definition.setPayloadDefinition("platformId", AttributeType.INT);
		definition.setPayloadDefinition("regId", AttributeType.STRING);
		definition.setPayloadDefinition("osVersion", AttributeType.STRING);
		definition.setPayloadDefinition("properties", AttributeType.STRING);
		definition.setPayloadDefinition("createdDate", AttributeType.STRING);
		definition.setPayloadDefinition("byod", AttributeType.STRING);
		definition.setPayloadDefinition("vendor", AttributeType.STRING);
		definition.setPayloadDefinition("mac", AttributeType.STRING);
		try{
			return definition.getStreamDef();
		} catch (MalformedStreamDefinitionException e) {
			log.error("Error getting stream definition for registration stream", e);
		}
		return null;
	}
}

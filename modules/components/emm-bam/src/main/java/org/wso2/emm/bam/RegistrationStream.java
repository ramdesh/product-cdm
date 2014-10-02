/**
 *  Copyright (c) WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wso2.emm.bam;

import org.apache.log4j.Logger;
import org.wso2.carbon.databridge.commons.AttributeType;
import org.wso2.carbon.databridge.commons.StreamDefinition;
import org.wso2.carbon.databridge.commons.exception.MalformedStreamDefinitionException;
import org.wso2.emm.bam.dto.RegistrationDataDto;
import org.wso2.emm.bam.util.Constants;
import org.wso2.emm.bam.util.JSONReader;
/*
 * Implementation of the Registration stream
 */
public class RegistrationStream implements EMMStreamDefinition{
	
	private static Logger log = Logger.getLogger(RegistrationStream.class);
	private StreamDefCreator definition;
	private static String username="username";

	public Object[] getPayload(JSONReader jsonReader) {
		RegistrationDataDto dto = new RegistrationDataDto();
		dto.setUsername(jsonReader.read(username));
		dto.setTimestamp(Long.toString(System.currentTimeMillis()));
		return new Object[] { dto.getUsername(), dto.getTimestamp() };
	}

	public StreamDefinition getStreamDefinition() {
		definition = new StreamDefCreator(
				Constants.REGISTRATION_STREAM_NAME,
				Constants.REGISTRATION_STREAM_VERSION,
				Constants.REGISTRATION_STREAM_NICKNAME,
				Constants.REGISTRATION_STREAM_DESCRIPTION);
		definition.setPayloadDefinition("username", AttributeType.STRING);
		definition.setPayloadDefinition("timestamp", AttributeType.STRING);
		try {
			return definition.getStreamDef();
		} catch (MalformedStreamDefinitionException e) {
			log.error("Error getting stream definition for registration stream", e);
		}
		return null;
	}
}

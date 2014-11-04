package org.wso2.emm.bam;

import org.apache.log4j.Logger;
import org.wso2.carbon.databridge.commons.AttributeType;
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
import org.wso2.carbon.databridge.commons.StreamDefinition;
import org.wso2.carbon.databridge.commons.exception.MalformedStreamDefinitionException;
import org.wso2.emm.bam.dto.NotificationsDto;
import org.wso2.emm.bam.dto.RegistrationDataDto;
import org.wso2.emm.bam.util.Constants;
import org.wso2.emm.bam.util.JSONReader;

/*
 * Handles all the API calls received by notifications end point
 */
public class NotificationsStream implements EMMStreamDefinition {

	private static Logger log = Logger.getLogger(NotificationsStream.class);
	private StreamDefCreator definition;
	private static String userId = "userId";
	private static String status = "status";
	private static String deviceId = "deviceId";
	private static String sentDate = "sentDate";
	private static String recievedDate = "recievedDate";
	private static String featureCode = "featureCode";
	private static String tenent = "tenent";

	public Object[] getPayload(JSONReader jsonReader) {
		NotificationsDto dto = new NotificationsDto();
		dto.setUserId(jsonReader.read(userId));
		dto.setStatus(jsonReader.read(status));
		dto.setDeviceId(jsonReader.read(deviceId));
		dto.setSentDate(jsonReader.read(sentDate));
		dto.setRecievedDate(jsonReader.read(recievedDate));
		dto.setFeatureCode(jsonReader.read(featureCode));
		dto.setTetant(jsonReader.read(tenent));
		return new Object[] { dto.getUserId(), dto.getStatus(),
				dto.getDeviceId(), dto.getSentDate(), dto.getRecievedDate(),
				dto.getFeatureCode(), dto.getTetant() };
	}

	public StreamDefinition getStreamDefinition() {
		definition = new StreamDefCreator(Constants.NOTIFICATIONS_STREAM_NAME,
				Constants.NOTIFICATIONS_STREAM_VERSION,
				Constants.NOTIFICATIONS_STREAM_NICKNAME,
				Constants.NOTIFICATIONS_STREAM_DESCRIPTION);
		definition.setPayloadDefinition(userId, AttributeType.STRING);
		definition.setPayloadDefinition(status, AttributeType.STRING);
		definition.setPayloadDefinition(deviceId, AttributeType.STRING);
		definition.setPayloadDefinition(sentDate, AttributeType.STRING);
		definition.setPayloadDefinition(recievedDate, AttributeType.STRING);
		definition.setPayloadDefinition(featureCode, AttributeType.STRING);
		definition.setPayloadDefinition(tenent, AttributeType.STRING);
		try {
			return definition.getStreamDef();
		} catch (MalformedStreamDefinitionException e) {
			log.error(
					"Error getting stream definition for notifications stream",
					e);
		}
		return null;
	}
}

package org.wso2.emm.bam;

import org.apache.log4j.Logger;
import org.wso2.carbon.databridge.commons.AttributeType;
import org.wso2.carbon.databridge.commons.StreamDefinition;
import org.wso2.carbon.databridge.commons.exception.MalformedStreamDefinitionException;
import org.wso2.emm.bam.dto.ApplicationDto;
import org.wso2.emm.bam.util.Constants;
import org.wso2.emm.bam.util.JSONReader;

public class AppInfoStream implements EMMStreamDefinition {

	private static Logger log = Logger.getLogger(AppInfoStream.class);
	private StreamDefCreator definition;

//	userId,status,deviceId,sentDate,recievedDate,featureCode,tenantId,
//	messageId,groupId

	public Object[] getPayload(JSONReader jsonReader) {
		ApplicationDto dto = new ApplicationDto();
		dto.setUserId(jsonReader.read(Constants.userId));
		dto.setStatus(jsonReader.read(Constants.status));
		dto.setDeviceId(jsonReader.read(Constants.deviceId));
		dto.setSentDate(jsonReader.read(Constants.sentDate));
		dto.setRecievedDate(jsonReader.read(Constants.recievedDate));
		dto.setFeatureCode(jsonReader.read(Constants.featureCode));
		dto.setTetant(jsonReader.read(Constants.tenent));
		dto.setMessageId(jsonReader.read(Constants.messageId));
		dto.setGroupId(jsonReader.read(Constants.groupId));
		dto.setPackageName(jsonReader.read(Constants.packageName));
		dto.setIcon(jsonReader.read(Constants.icon));
		dto.setAppName(jsonReader.read(Constants.appName));
		
		return new Object[] { dto.getUserId(), dto.getStatus(),
				dto.getDeviceId(), dto.getSentDate(), dto.getRecievedDate(),
				dto.getFeatureCode(), dto.getTetant(), dto.getMessageId()
				, dto.getGroupId(), dto.getPackageName(), dto.getIcon()
				, dto.getAppName()};
	}

	public StreamDefinition getStreamDefinition() {
		definition = new StreamDefCreator(Constants.APP_NOTIFICATIONS_STREAM_NAME,
				Constants.APP_NOTIFICATIONS_STREAM_VERSION,
				Constants.APP_NOTIFICATIONS_STREAM_NICKNAME,
				Constants.APP_NOTIFICATIONS_STREAM_DESCRIPTION);
		definition.setPayloadDefinition(Constants.userId, AttributeType.STRING);
		definition.setPayloadDefinition(Constants.status, AttributeType.STRING);
		definition.setPayloadDefinition(Constants.deviceId, AttributeType.STRING);
		definition.setPayloadDefinition(Constants.sentDate, AttributeType.STRING);
		definition.setPayloadDefinition(Constants.recievedDate, AttributeType.STRING);
		definition.setPayloadDefinition(Constants.featureCode, AttributeType.STRING);
		definition.setPayloadDefinition(Constants.tenent, AttributeType.STRING);
		definition.setPayloadDefinition(Constants.messageId, AttributeType.STRING);
		definition.setPayloadDefinition(Constants.groupId, AttributeType.STRING);
		definition.setPayloadDefinition(Constants.packageName, AttributeType.STRING);
		definition.setPayloadDefinition(Constants.icon, AttributeType.STRING);
		definition.setPayloadDefinition(Constants.appName, AttributeType.STRING);			

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

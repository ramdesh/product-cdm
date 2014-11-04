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

import java.util.ArrayList;
import java.util.List;

import org.wso2.carbon.databridge.commons.StreamDefinition;
import org.wso2.carbon.databridge.commons.exception.MalformedStreamDefinitionException;
import org.wso2.carbon.databridge.commons.Attribute;
import org.wso2.carbon.databridge.commons.AttributeType;

/*
 * This is used to create Stream definitions. This acts as a template for
 * any stream definition, where you can set pay loads,meta data, and 
 * correlation data.
 */
public class StreamDefCreator {

	private String streamName;
	private String version;
	private String nickName;
	private String description;
	private List<Attribute> metaList=null;
	private List<Attribute> correlationList=null;
	private List<Attribute> payloadList=null;

	public StreamDefCreator(String streamName, String version, String nickName,
			String description) {
		this.streamName = streamName;
		this.version = version;
		this.nickName = nickName;
		this.description = description;
		payloadList= new ArrayList<Attribute>();

	}

	public StreamDefinition getStreamDef()
			throws MalformedStreamDefinitionException {

		StreamDefinition streamDefinition = new StreamDefinition(streamName,
				version);
		streamDefinition.setDescription(description);
		streamDefinition.setNickName(nickName);
		streamDefinition.setMetaData(getMetaDefinitions());
		streamDefinition.setPayloadData(getPayloadDefinition());
		streamDefinition.setCorrelationData(getCorrelationDefinition());
		return streamDefinition;
	}

	private List<Attribute> getMetaDefinitions() {
		 return metaList;
	}
	
	public void setPayloadDefinition(String attributeName,  AttributeType type) {
		payloadList.add(new Attribute(attributeName, type));
	}

	private List<Attribute> getPayloadDefinition() {
		return payloadList;
	}

	private List<Attribute> getCorrelationDefinition() {
		return correlationList;
	}
}
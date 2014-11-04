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
 * BlackListed Application related data to BAM.
 */
public class BlacklistedAppStream implements EMMStream {

	private static Logger log = Logger.getLogger(BlacklistedAppStream.class);
	private StreamDefinition streamDefinition;

	public BlacklistedAppStream() throws PublisherException {
		try {
			streamDefinition = new StreamDefinition(
					Constants.BLACKLISTED_APPS_STREAM_NAME,
					Constants.BLACKLISTED_APPS_STREAM_VERSION);
			streamDefinition.addPayloadData(Constants.PACKAGE_NAME,
					AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.APP_NAME,
					AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.PLATFORM,
					AttributeType.STRING);
			streamDefinition.addPayloadData(Constants.TYPE,
					AttributeType.STRING);
		} catch (MalformedStreamDefinitionException e) {
			log.error(
					"Error getting stream definition for blaklisted apps stream :"
							+ e.getMessage(), e);
			throw new PublisherException(
					"Error getting stream definition for blaklisted apps stream",
					e);
		}
	}

	public Object[] getPayload(JSONReader jsonReader) throws PublisherException {
		return new Object[] { jsonReader.read(Constants.PACKAGE_NAME),
				jsonReader.read(Constants.APP_NAME),
				jsonReader.read(Constants.PLATFORM),
				jsonReader.read(Constants.TYPE) };
	}

	public StreamDefinition getStreamDefinition() throws PublisherException {
		return streamDefinition;
	}
}
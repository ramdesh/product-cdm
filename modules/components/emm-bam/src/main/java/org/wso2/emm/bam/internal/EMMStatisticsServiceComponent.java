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
package org.wso2.emm.bam.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.wso2.emm.bam.DeviceOperationsStream;
import org.wso2.emm.bam.EMMStream;
import org.wso2.emm.bam.EMMStreamFactory;
import org.wso2.emm.bam.PublisherException;
import org.wso2.emm.bam.DataPublisher;
import org.wso2.emm.bam.util.Constants;

/**
 * @scr.component name="emm.bam.service.component" immediate="true"
 */

public class EMMStatisticsServiceComponent {

	private static final Log log = LogFactory
			.getLog(EMMStatisticsServiceComponent.class);

	protected void activate(ComponentContext ctx) {
		DataPublisher ph = DataPublisher.getInstance();
		EMMStreamFactory streamFactory = new EMMStreamFactory();
		
		try {
			EMMStream deviceStream;
			deviceStream = streamFactory
					.getStream(Constants.DEVICE_STREAM_NAME);
			ph.register(deviceStream.getStreamDefinition().getName(), deviceStream);
			EMMStream appInfoStream = streamFactory
					.getStream(Constants.APP_NOTIFICATIONS_STREAM_NAME);
			ph.register(appInfoStream.getStreamDefinition().getName(),
					appInfoStream);
			EMMStream deviceInfoStream = streamFactory
					.getStream(Constants.DEVICE_INFO_NOTIFICATIONS_STREAM_NAME);
			ph.register(deviceInfoStream.getStreamDefinition().getName(),
					deviceInfoStream);
			EMMStream policyStream = streamFactory
					.getStream(Constants.POLICY_NOTIFICATIONS_STREAM_NAME);
			ph.register(policyStream.getStreamDefinition().getName(), policyStream);
			EMMStream blacklistedAppStream = streamFactory
					.getStream(Constants.BLACKLISTED_APPS_STREAM_NAME);
			ph.register(blacklistedAppStream.getStreamDefinition().getName(),
					blacklistedAppStream);
			EMMStream deviceOperationStream = streamFactory
					.getStream(Constants.DEVICE_OPERATIONS_STREAM_NAME);
			ph.register(deviceOperationStream.getStreamDefinition().getName(),
					deviceOperationStream);
			if (log.isDebugEnabled()) {
				log.debug("EMM-BAM bundle is activated ");
			}
		} catch (PublisherException e) {
			log.error(
					"Error while registering streams in EMM-BAM bundle activation"
							+ e.getMessage(), e);
		}
		
	}

	protected void deactivate(ComponentContext ctx) {
		if (log.isDebugEnabled()) {
			log.debug("EMM-BAM bundle is deactivated ");
		}
	}
}


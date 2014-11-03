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

import org.wso2.emm.bam.util.Constants;

/**
 * Factory of EMM Streams, which can be used to generate new stream objects that
 * implements EMMStreamm interface.
 */
public class EMMStreamFactory {

	/**
	 * Can be used to generate a new stream object according to the stream name
	 * provided.
	 * 
	 * @param {@link String} streamType is the name of the stream that needs to
	 *        be retrieved.
	 * @return A stream object of type {@link EMMStream}
	 * @throws PublisherException
	 */
	public EMMStream getStream(String streamType) throws PublisherException {
		if (streamType == null) {
			return null;
		}
		if (streamType
				.equalsIgnoreCase(Constants.APP_NOTIFICATIONS_STREAM_NAME)) {
			return new AppInfoStream();
		} else if (streamType
				.equalsIgnoreCase(Constants.BLACKLISTED_APPS_STREAM_NAME)) {
			return new BlacklistedAppStream();
		} else if (streamType
				.equalsIgnoreCase(Constants.DEVICE_INFO_NOTIFICATIONS_STREAM_NAME)) {
			return new DeviceInfoStream();
		} else if (streamType
				.equalsIgnoreCase(Constants.DEVICE_OPERATIONS_STREAM_NAME)) {
			return new DeviceOperationsStream();
		} else if (streamType.equalsIgnoreCase(Constants.DEVICE_STREAM_NAME)) {
			return new DeviceStream();
		} else if (streamType
				.equalsIgnoreCase(Constants.POLICY_NOTIFICATIONS_STREAM_NAME)) {
			return new PolicyStream();
		}
		return null;
	}
}

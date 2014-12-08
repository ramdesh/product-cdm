/**
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.emm.bam;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Factory of EMM Streams, which can be used to generate new stream objects that
 * implements EMMStreamm interface.
 */
public class EMMStreamFactory {

	private static Logger log = Logger.getLogger(EMMStreamFactory.class);
	private static Map<String, EMMStream> streams = new HashMap<String, EMMStream>();

	/**
	 * Can be used to generate a new stream object according to the stream name
	 * provided.
	 * 
	 * @param {@link String} streamType is the name of the stream that needs to
	 *        be retrieved.
	 * @return A stream object of type {@link EMMStream}
	 * @throws PublisherException
	 */
	public static EMMStream getStream(StreamType streamType) throws PublisherException {
		if (streamType == null) {
			String message = "Stream type cannot be null";
			log.error(message);
			throw new PublisherException(message);
		}
		EMMStream stream = streams.get(streamType);
		if (stream == null) {
			synchronized (streamType.getStreamType().intern()) {
				if (streams.get(streamType) == null) {
					String streamName = streamType.getStreamType();
					switch (streamType) {
						case APP_NOTIFICATIONS:
							stream = new AppInfoStream();
							streams.put(streamName, stream);
							break;
						case BLACKLISTED_APPS:
							stream = new BlacklistedAppStream();
							streams.put(streamName, stream);
							break;
						case DEVICE_INFO_NOTIFICATIONS:
							stream = new DeviceInfoStream();
							streams.put(streamName, stream);
							break;
						case DEVICE_OPERATIONS:
							stream = new DeviceOperationsStream();
							streams.put(streamName, stream);
							break;
						case DEVICE_REGISTRATIONS:
							stream = new DeviceRegisterStream();
							streams.put(streamName, stream);
							break;
						case POLICY_NOTIFICATIONS:
							stream = new PolicyStream();
							streams.put(streamName, stream);
							break;
					}
				}
			}

		}
		return stream;
	}
}

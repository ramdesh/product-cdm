/*
 * Copyright (c) 2014 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.emm.statistics;

/**
 * This defines the names of the streams which can be used to identify streams.
 */
public enum StreamType {

	APP_NOTIFICATIONS("app_notifications_stream"), BLACKLISTED_APPS("blacklisted_apps_stream"),
	DEVICE_INFO_NOTIFICATIONS("device_info_notifications_stream"),
	DEVICE_OPERATIONS("device_operations_stream"),
	DEVICE_REGISTRATIONS("device_register_stream"),
	POLICY_NOTIFICATIONS("policy_info_notifications_stream");

	private String streamType;

	private StreamType(String streamType) {
		this.streamType = streamType;
	}

	public String getStreamType() {
		return streamType;
	}
}

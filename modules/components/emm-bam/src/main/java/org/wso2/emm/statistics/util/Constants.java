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
package org.wso2.emm.statistics.util;

/**
 * Constants used throughout the publisher are mentioned here.
 */
public final class Constants {

	/**
	 * Constants related to stream version published to BAM.
	 */
	public static class StreamVersion {
		private StreamVersion() {
			throw new AssertionError();
		}

		public static final String APP_NOTIFICATIONS_STREAM_VERSION = "1.0.0";
		public static final String DEVICE_INFO_NOTIFICATIONS_STREAM_VERSION = "1.0.0";
		public static final String POLICY_NOTIFICATIONS_STREAM_VERSION = "1.0.0";
		public static final String BLACKLISTED_APPS_STREAM_VERSION = "1.0.0";
		public static final String DEVICE_STREAM_VERSION = "1.0.0";
		public static final String DEVICE_OPERATIONS_STREAM_VERSION = "1.0.0";
	}

	/**
	 * Keys used in streams that are sent to BAM.
	 */
	public static class StreamKey {
		private StreamKey() {
			throw new AssertionError();
		}

		public static final String INTERNAL_MEMORY_TOTAL = "internalMemoryTotal";
		public static final String INTERNAL_MEMORY_AVAILABLE = "internalMemoryAvailable";
		public static final String LATITUDE = "latitude";
		public static final String LONGITUDE = "longitude";
		public static final String EXTERNAL_MEMORY_TOTAL = "externalMemoryTotal";
		public static final String EXTERNAL_MEMORY_AVAILABLE = "externalMemoryAvailable";
		public static final String OPERATOR = "operator";
		public static final String BATTERY = "battery";
		public static final String CODE = "code";
		public static final String POLICY_STATUS = "policyStatus";
		public static final String PACKAGE_NAME = "packageName";
		public static final String ICON = "icon";
		public static final String APP_NAME = "appName";
		public static final String USERID = "userId";
		public static final String STATUS = "status";
		public static final String DEVICE_ID = "deviceId";
		public static final String SENT_DATE = "sentDate";
		public static final String RECEIVED_DATE = "relievedDate";
		public static final String FEATURE_CODE = "featureCode";
		public static final String TENANT = "tenant";
		public static final String MESSAGE_ID = "messageId";
		public static final String GROUP_ID = "groupId";
		public static final String PLATFORM = "platform";
		public static final String TYPE = "type";
		public static final String TENANT_ID = "tenantId";
		public static final String PLATFORM_ID = "platformId";
		public static final String REG_ID = "regId";
		public static final String OS_VERSION = "osVersion";
		public static final String PROPERTIES = "properties";
		public static final String CREATED_DATE = "createdDate";
		public static final String BYOD = "byod";
		public static final String VENDOR = "vendor";
		public static final String MAC = "macId";
		public static final String DATA = "data";
	}
}

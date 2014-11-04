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
package org.wso2.emm.bam.util;

/**
 * Constants used throughout the publisher are mentioned here for clarity
 * 
 */
public class Constants {

	// variables that represents streams
	public static final String NOTIFICATIONS = "notifications";
	public static final String NOTIFICATIONS_STREAM_NAME = "notifications_stream";
	public static final String NOTIFICATIONS_STREAM_VERSION = "1.0.0";
	public static final String NOTIFICATIONS_STREAM_NICKNAME = "NotificationsAPI";
	public static final String NOTIFICATIONS_STREAM_DESCRIPTION = "holds notification API Data";
	public static final String APP_NOTIFICATIONS_STREAM_NAME = "app_notifications_streams";
	public static final String APP_NOTIFICATIONS_STREAM_VERSION = "1.0.0";
	public static final String APP_NOTIFICATIONS_STREAM_NICKNAME = "APPNotifications";
	public static final String APP_NOTIFICATIONS_STREAM_DESCRIPTION = "holds app notification Data";
	public static final String DEVICE_INFO_NOTIFICATIONS_STREAM_NAME = "device_info_notifications_stream";
	public static final String DEVICE_INFO_NOTIFICATIONS_STREAM_VERSION = "1.0.0";
	public static final String DEVICE_INFO_NOTIFICATIONS_STREAM_NICKNAME = "DeviceInfoNotifications";
	public static final String DEVICE_INFO_NOTIFICATIONS_STREAM_DESCRIPTION = "holds app notification Data";
	public static final String POLICY_NOTIFICATIONS_STREAM_NAME = "policy_info_notifications_stream";
	public static final String POLICY_NOTIFICATIONS_STREAM_VERSION = "1.0.0";
	public static final String POLICY_NOTIFICATIONS_STREAM_NICKNAME = "PolicyInfoNotifications";
	public static final String POLICY_NOTIFICATIONS_STREAM_DESCRIPTION = "holds policy notification Data";
	public static final String BLACKLISTED_APPS_STREAM_NAME = "blacklisted_apps_stream";
	public static final String BLACKLISTED_APPS_STREAM_VERSION = "1.0.0";
	public static final String BLACKLISTED_APPS_STREAM_NICKNAME = "BlacklistedAppsNotifications";
	public static final String BLACKLISTED_APPS_STREAM_DESCRIPTION = "holds policy notification Data";
	public static final String DEVICE_STREAM_NAME = "device_register_streamz2";
	public static final String DEVICE_STREAM_VERSION = "1.0.0";
	public static final String DEVICE_STREAM_NICKNAME = "AddingNewDevice";
	public static final String DEVICE_STREAM_DESCRIPTION = "Device Request Data";
	public static final String DEVICE_OPERATIONS_STREAM_NAME = "device_operations_stream";
	public static final String DEVICE_OPERATIONS_STREAM_VERSION = "1.0.0";
	public static final String DEVICE_OPERATIONS_STREAM_NICKNAME = "DEVICEOPERATIONS";
	public static final String DEVICE_OPERATIONS_STREAM_DESCRIPTION = "holds device operations";
	public static final String TRUSTSTORE_KEY = "javax.net.ssl.trustStore";
	public static final String TRUSTSTORE_PASSWORD_KEY = "javax.net.ssl.trustStorePassword";
	public static final String BAM_PASSWORD = "wso2carbon";

	// related to noifications
	public static final String USERID = "userId";
	public static final String STATUS = "status";
	public static final String DEVICEID = "deviceId";
	public static final String SENT_DATE = "sentDate";
	public static final String RECIEVED_DATE = "recievedDate";
	public static final String FEATURE_CODE = "featureCode";
	public static final String TENANT = "tenent";
	public static final String MESSAGE_ID = "messageId";
	public static final String GROUP_ID = "groupId";

	// AppInfo stream
	public static final String PACKAGE_NAME = "packageName";
	public static final String ICON = "icon";
	public static final String APP_NAME = "appName";

	// DeviceInfo stream
	public static final String INTERNAL_MEMORY_TOTAL = "internalMemoryTotal";
	public static final String INTERNAL_MEMORY_AVAILABLE = "internalMemoryAvailable";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String EXTERNAL_MEMORY_TOTAL = "externalMemoryTotal";
	public static final String EXTERNAL_MEMORY_AVAILABLE = "externalMemoryAvailable";
	public static final String OPERATOR = "operator";
	public static final String BATTERY = "battery";
	public static final String POLICY_ID = "policyId";
	public static final String CODE = "code";
	public static final String POLICY_STATUS = "policyStatus";

	// Blacklisted apps stream
	public static final String PLATFORM = "platform";
	public static final String TYPE = "type";

	// Device stream
	public static final String TENANT_ID = "tenantId";
	public static final String PLATFORM_ID = "platformId";
	public static final String REG_ID = "regId";
	public static final String OS_VERSION = "osVersion";
	public static final String PROPERTIES = "properties";
	public static final String CREATED_DATE = "createdDate";
	public static final String BYOD = "byod";
	public static final String VENDOR = "vendor";
	public static final String MAC = "macId";

	// Device operations stream
	public static final String DATA = "data";
	
	public static final String RECIEVER_URL_BAM = "RecieverUrlBAM";
	public static final String BAM_USERNAME = "BAMUsername";
	public static final String BAM_PASSWORD_STR = "BAMPassword";

}

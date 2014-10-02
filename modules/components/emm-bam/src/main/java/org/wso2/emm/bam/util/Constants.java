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
package org.wso2.emm.bam.util;

public class Constants {
	public static final String REGISTRATION_STREAM_NAME = "registration_stream";
	public static final String REGISTRATION_STREAM_VERSION = "1.0.0";
	public static final String REGISTRATION_STREAM_NICKNAME = "EMMdeviceregister";
	public static final String REGISTRATION_STREAM_DESCRIPTION = "Request Data";
	
	public static final String DEVICE_STREAM_NAME = "device_stream";
	public static final String DEVICE_STREAM_VERSION = "1.0.0";
	public static final String DEVICE_STREAM_NICKNAME = "AddingNewDevice";
	public static final String DEVICE_STREAM_DESCRIPTION = "Device Request Data";
	
	public static final String NOTIFICATIONS="notifications";
	public static final String NOTIFICATIONS_STREAM_NAME = "notifications_stream";
	public static final String NOTIFICATIONS_STREAM_VERSION = "1.0.0";
	public static final String NOTIFICATIONS_STREAM_NICKNAME = "NotificationsAPI";
	public static final String NOTIFICATIONS_STREAM_DESCRIPTION = "holds notification API Data";
	
	public static final String APP_NOTIFICATIONS_STREAM_NAME = "app_notifications_streams";
	public static final String APP_NOTIFICATIONS_STREAM_VERSION = "1.0.0";
	public static final String APP_NOTIFICATIONS_STREAM_NICKNAME = "APPNotifications";
	public static final String APP_NOTIFICATIONS_STREAM_DESCRIPTION = "holds app notification Data";
	
	public static final String BAM_PATH="/home/inoshp/Documents/work/wso2bam-2.4.1/repository/resources/security"
			+ "/client-truststore.jks";

	public static final String BAM_PASSWORD="wso2carbon";
	
	
	//related to noifications
	public static final String userId = "userId";
	public static final String status = "status";
	public static final String deviceId = "deviceId";
	public static final String sentDate = "sentDate";
	public static final String recievedDate = "recievedDate";
	public static final String featureCode = "featureCode";
	public static final String tenent = "tenent";
	public static final String messageId = "messageId";
	public static final String groupId = "groupId";
	
	//AppInfo stream
	public static final String packageName = "packageName";
	public static final String icon = "icon";
	public static final String appName = "appName";
	
	
	

}
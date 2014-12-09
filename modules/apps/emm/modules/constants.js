/*
 * *
 *  *  Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *
 */

const DEVICE_INFO = "500A";
const POLICY_ENFORCE = "500P";
const LOCATION = "501A";
const POLICY_MONTORING = "501P";
const APP_INFO = "502A";
const POLICY_REVOKE = "502P";
const DEVICE_LOCK = "503A";
const WIPE = "504A";
const CLEARPASSCODE = "505A";
const MESSAGE = "506A";
const WIFI = "507A";
const CAMERA = "508A";
const INSTALLAPP = "509A";
const UNINSTALLAPP = "510A";
const ENCRYPT = "511A";
const APN = "512A";
const MUTE = "513A";
const WEBCLIP = "518A";
const PASSWORDPOLICY = "519A";
const EMAIL = "520A";
const GOOGLECALENDAR = "521A";
const VPN = "523A";
const LDAP = "524A";
const CHANGEPASSWORD = "526A";
const ENTERPRISEWIPE = "527A";
const BLACKLISTAPPS = "528B";
const USER_AGENT = "User-Agent";
const ANDROID = "Android";
const IOS = "iOS";
const UNKNOWN = "unknown";
const ANDROID_MIN_VERSION = 4.0;
const IOS_MIN_VERSION = 5;
const OPERATION_ENTERPRISEWIPE = "ENTERPRISEWIPE";
const CARBONSUPER_TENANTID = "-1234";
const CARBONSUPER_TENANT_NAME = "carbon.super";
const MONITORING = "MONITORING";
const INFO = "INFO";
const APPLIST = "APPLIST";
const BOOL_TRUE = "true";
const BOOL_FALSE = "false";
const INSERT = "INSERT";
const UPDATE = "UPDATE";
const MUTE_DEVICE = "Device Muted"
const OPERATION_MUTE = "MUTE";
const POLICY = "POLICY";
const GCM = "GCM";
const GCM_MESSAGE = "CONTACT SERVER";
const OPERATION_CLEARPASSWORD = "CLEARPASSWORD";
const OPERATION_NOTIFICATION = "NOTIFICATION";
const OPERATION_REVOKEPOLICY = "REVOKEPOLICY";
const INTERNAL_EVERYONE = "Internal/everyone";
const ANONYMOUS_ROLE = "wso2.anonymous.role";
const INTERNAL_REVIEWER = "Internal/reviewer";
const REVIEWER = "reviewer";
const INTERNAL_STORE = "Internal/store";
const INTERNAL_PUBLISHER = "Internal/publisher";
const PORTAL = "portal";
const USERS = "USERS";
const USER = "USER";
const PLATFORMS = "PLATFORMS";
const ROLES = "ROLES";
const UNDEFINED = "undefined";
const DEFAULT = "default";
const POLICY_USER = "user";
const POLICY_PLATFORM = "platform";
const POLICY_GROUP = "group";
const ROLE_ADMIN = "ADMIN";
const ADMINISTRATOR = "ADMINISTRATOR";
const EMMADMIN = "EMMADMIN";
const ROLE_INTERNAL_EMMADMIN = "INTERNAL/EMMADMIN";
const ROLE_INTERNAL_PUBLISHER = "INTERNAL/PUBLISHER";
const ROLE_INTERNAL_REVIEWER = "INTERNAL/REVIEWER";
const ROLE_INTERNAL_STORE = "INTERNAL/STORE";
const ROLE_INTERNAL_MAMADMIN = "INTERNAL/MAMADMIN";
const PRIVATE_ROLE_PREFIX = "Internal/private_";
const TYPE_ADMINISTRATOR = "administrator";
const TYPE_ADMIN = "admin";
const TYPE_EMM_ADMIN = "emmadmin";
const TYPE_MAM = "mam";
const ROLE_VIEW_ADMINISTRATOR = "Administrator";
const ROLE_VIEW_USER = "User";
const ADMIN_USER = "admin";
const ANONYMOUS_USER = "wso2.anonymous.user";
const ADMIN_EMAIL_USER = "admin@admin.com";
const IPAD = "iPad";
const IPHONE = "iPhone";
const IPOD = "iPod";
const STATUS_PENDING = "pending";
const STATUS_SKIPPED = "skipped";
const SUCCESS = "SUCCESS";
const IOS_ITMS_DOWNLOAD = "itms-services://?action=download-manifest&url=itms-services://?action=download-manifest&url=";
const IOS_DOWNLOAD_APP = "/emm/api/devices/ios/download";
const EMAIL_SMTP_HOST = "emailSmtpHost";
const EMAIL_SMTP_PORT = "emailSmtpPort";
const EMAIL_USERNAME = "emailUsername";
const EMAIL_PASSWORD = "emailPassword";
const EMAIL_SENDER_ADDRESS = "emailSenderAddress";
const EMAIL_TEMPLATE = "emailTemplate";
const UI_TITLE = "uiTitle";
const UI_COPYRIGHT = "uiCopyright";
const UI_LICENSE = "uiLicence";
const COMPANY_NAME = "companyName";
const ANDROID_NOTIFIER = "androidNotifier";
const ANDROID_NOTIFIER_FREQUENCY = "androidNotifierFreq";
const ANDROID_API_KEYS = "androidApiKeys";
const ANDROID_SENDER_IDS = "androidSenderIds";
const PRODUCTION = "production";
const DEVELOPER = "developer";
const NOTIFIER_LOCAL = "LOCAL";
const CARBON_SUPER = "carbon.super";
const BYOD = "BYOD";
const AUTH_FAIL_MESSAGE = "Authentication Failure";
const PASSWORD_CHANGE_FAIL_MESSAGE = "Error occurred while changing password";
const GENERIC_ERROR_MESSAGE = "Error Occurred";
const USER_NOT_FOUND_MESSAGE = "User not found";
const USER_EMAIL_NOT_FOUND_MESSAGE = "User email not found";
const ERROR_ALL_READY_EXIST = "Already Exist";
const ALL_READY_EXIST_MESSAGE = "User already exist with the email address";
const ERROR_BAD_REQUEST = "Bad Request";
const USER_CREATION_ERROR_MESSAGE = "Error occurred while creating the user";
const USER_RETRIEVAL_ERROR_MESSAGE = "Error occurred while retrieving the user";
const USER_DELETE_MESSAGE = "User Deleted";
const USER_DELETE_ERROR_MESSAGE = "Cannot delete user, associated devices exist";
const GET_APP_FEATURE_CODE = "502A";
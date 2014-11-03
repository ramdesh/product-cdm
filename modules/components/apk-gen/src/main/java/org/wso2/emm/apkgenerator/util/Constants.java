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
package org.wso2.emm.apkgenerator.util;

import java.io.File;

/**
 * Constants used throughout the project
 * 
 */
public class Constants {
	public static final String jksFolder = "jks/";
	public static final String BKS_File = "emm_truststore.bks";
	public static final String PEM_file = "ca_cert.pem";
	public static final String RA_file = "ra_cert.pem";
	public static final String IA_file = "ia.crt";
	public static final String ANDROID_AGENT = "emm-agent-android";
	public static final String COMMON_UTIL = ANDROID_AGENT + File.separator
			+ "src" + File.separator + "org" + File.separator + "wso2"
			+ File.separator + "emm" + File.separator + "agent"
			+ File.separator + "utils" + File.separator
			+ "CommonUtilities.java";
	public static final String PK12_CA_PASSWORD = "cacert";
	public static final String PK12_RA_PASSWORD = "racert";
	public static final String PK12_CA_ALIAS = "cacert";
	public static final String PK12_RA_ALIAS = "racert";
	public static final String CA_P12 = "ca.p12";
	public static final String RA_P12 = "ra.p12";
	public static final String AI_P12 = "ia.p12";
	public static final String WSO2CARBON = "wso2carbon";
	public static final String WSO2CARBON_JKS = "wso2carbon.jks";
	public static final String CLIENT_TRUST_JKS = "client-truststore.jks";
	public static final String WSO2EMM_JKS = "wso2emm.jks";
	public static final String WSO2CARBON_JKS_PASS = "wso2carbon";
	public static final String WSO2EMM_JKS_PASSWORD = "wso2carbon";
	public static final String ALGORITHM = "RSA";
	public static final String PROVIDER = "BC";
	public static final String ENCRIPTION = "SHA1withRSA";
	public static final String ANDROID_AGENT_RAW = ANDROID_AGENT
			+ File.separator + "res" + File.separator + "raw" + File.separator;
	public static final String ANDROID_AGENT_POM_FAKE = ANDROID_AGENT
			+ File.separator + "pom.xml.txt";
	public static final String ANDROID_AGENT_POM = ANDROID_AGENT
			+ File.separator + "pom.xml";
	public static final String ANDROID_AGENT_APK = ANDROID_AGENT
			+ File.separator + "target" + File.separator + "MDMAgent.apk";
	public static final String MAVEN_HOME_FOLDER = "apache-maven-3.2.1";
	public static final String RA = "RA";
	public static final String BKS = "BKS";
	public static final String BKS_ALIAS = "cert-alias";
	public static final String JKS = "JKS";
	public static final String SSL = "SSL";
	public static final String APK_FOLDER = "/Apk/";
	public static final String COUNTRY_CA = "countryCA";
	public static final String STATE_CA = "stateCA";
	public static final String LOCALITY_CA = "localityCA";
	public static final String ORGANIZATION_CA = "organizationCA";
	public static final String ORGANIZATION_UNIT_CA = "organizationUCA";
	public static final String DAYS_CA = "daysCA";
	public static final String COMMON_NAME_CA = "commonNameCA";
	public static final String COUNTRY_RA = "countryRA";
	public static final String STATE_RA = "stateRA";
	public static final String LOCALITY_RA = "localityRA";
	public static final String ORGANIZATION_RA = "organizationRA";
	public static final String ORGANIZATION_UNIT_RA = "organizationURA";
	public static final String DAYS_RA = "daysRA";
	public static final String COMMON_NAME_RA = "commonNameRA";
	public static final String COUNTRY_SSL = "countrySSL";
	public static final String STATE_SSL = "stateSSL";
	public static final String LOCALITY_SSL = "localitySSL";
	public static final String ORGANIZATION_SSL = "organizationSSL";
	public static final String ORGANIZATION_UNIT_SSL = "organizationUSSL";
	public static final String DAYS_SSL = "daysSSL";
	public static final String SERVER_IP = "serverIp";
	public static final String WORKING_DIR = "workingDir";
	public static final String PASSWORD = "password";
	public static final String USERSNAME = "usersname";
	public static final String COMPANY = "company";
}
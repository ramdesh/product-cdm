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
package org.wso2.emm.apkgenerator.generators;

import org.apache.log4j.Logger;
import org.wso2.emm.apkgenerator.data.CSRData;
import org.wso2.emm.apkgenerator.data.ObjectReader;
import org.wso2.emm.apkgenerator.util.Constants;
import org.wso2.emm.apkgenerator.util.FileOperator;

/**
 * This is the class that provides access for the Jaggery app to communicate and
 * provide the data collected thought the EMM Wizard interface, to generate
 * Android APK with BKS injected and certificates.
 */
public class ApkGenerator {

	private ObjectReader reader;
	private static Logger log = Logger.getLogger(ApkGenerator.class);
	public static String workingDir;
	public static String truststorePassword;

	/**
	 * This is used to perform the sequence of actions necessary to generate
	 * certificates, key stores, and apk
	 * 
	 * @param jsonStr
	 *            is the JSON coming from the client.
	 * @return the path of the final zip file
	 * @throws CertificateGenerationException
	 */
	public String generateApk(String jsonStr)
			throws CertificateGenerationException {
		CertificateChainGenerator generator;
		if (log.isDebugEnabled()) {
			log.debug("Call to generate Certificates and APK");
		}
		reader = new ObjectReader(jsonStr);
		// directory of the running Jaggery app needs to be sent from the
		// Jaggery UI along with the trust store password to be used
		workingDir = FileOperator.getPath(reader.read(Constants.WORKING_DIR));
		truststorePassword = reader.read(Constants.PASSWORD);
		// construct a name for the zip file to store final output
		String zipFileName = reader.read(Constants.USERSNAME) + "_"
				+ reader.read(Constants.COMPANY) +Constants.ARCHIEVE_TYPE;
		CSRData csrDate = new CSRData(reader);
		// generate the certificates
		generator = new CertificateChainGenerator(csrDate);
		generator.generate();
		// convert generated certificates and keys to JKS
		KeyStoreGenerator.convertCertsToKeyStore(generator.keyPairCA,
				generator.keyPairRA, generator.keyPairSSL, generator.caCert,
				generator.raCert, generator.sslCert);
		// Generate BKS using CA pem
		BksGenerator.generateBKS(generator.caCert);
		// Copy BKS to Android source folder
		FileOperator.copyFile(ApkGenerator.workingDir + Constants.BKS_File,
				ApkGenerator.workingDir + Constants.ANDROID_AGENT_RAW
						+ Constants.BKS_File);
		String zipFolderPath = reader.read("zipPath") + Constants.APK_FOLDER;
		FileOperator.makeFolder(zipFolderPath);
		// generate APK using Maven and create a zip
		return Apk.compileApk(ApkGenerator.workingDir + Constants.COMMON_UTIL,
				reader.read("serverIp"), truststorePassword, zipFileName,
				zipFolderPath);
	}
}


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
package org.wso2.emm.apkgenerator.generators;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.emm.apkgenerator.data.CSRData;
import org.wso2.emm.apkgenerator.data.ObjectReader;
import org.wso2.emm.apkgenerator.data.TruststoreData;
import org.wso2.emm.apkgenerator.util.Constants;
import org.wso2.emm.apkgenerator.util.FileOperator;

import java.io.File;

/**
 * This is the class that provides access for the Jaggery app to communicate and
 * provide the data collected thought the EMM Wizard interface, to generate
 * Android APK with BKS injected and certificates.
 */
public class ApkGenerator {

	private ObjectReader reader;
	private static Log log = LogFactory.getLog(ApkGenerator.class);
	public static String workingDir;
	private static String truststorePassword;
	public static final String PK12_CA_PASSWORD = "cacert";
	public static final String PK12_RA_PASSWORD = "racert";
	public static final String PK12_CA_ALIAS = "cacert";
	public static final String PK12_RA_ALIAS = "racert";
	public static final String WSO2EMM_JKS_PASSWORD = "wso2carbon";
	public static final String WSO2CARBON = "wso2carbon";

	/**
	 * This is used to perform the sequence of actions necessary to generate
	 * certificates, key stores, and apk.
	 *
	 * @param jsonStr is the JSON coming from the client.
	 * @return the path of the final zip file
	 * @throws ApkGenerationException
	 */
	public String generateApk(String jsonStr) throws ApkGenerationException {
		CertificateChainGenerator generator;
		if (log.isDebugEnabled()) {
			log.debug("Call to generate Certificates and APK.");
		}
		reader = new ObjectReader(jsonStr);
		// Directory of the running Jaggery app needs to be sent from the
		// Jaggery UI along with the trust store password to be used
		workingDir = FileOperator.getPath(reader.read(Constants.FilePath.WORKING_DIR));
		truststorePassword = reader.read(Constants.CSRDataKeys.PASSWORD);
		// Construct a name for the zip file to store final output
		String zipFileName =
				reader.read(Constants.CSRDataKeys.USERSNAME) + "_" +
				reader.read(Constants.CSRDataKeys.COMPANY) + Constants.ARCHIEVE_TYPE;
		CSRData csrData = new CSRData(reader);
		TruststoreData truststoreData = new TruststoreData(reader);
		// Generate the certificates.
		generator = new CertificateChainGenerator(csrData);
		generator.generate();
		// Convert generated certificates and keys to JKS.
		KeyStoreGenerator.convertCertsToKeyStore(generator.getKeyPairCA(), generator.getKeyPairRA(),
		                                         generator.getKeyPairSSL(), generator.getCaCert(),
		                                         generator.getRaCert(), generator.getSslCert(),
		                                         truststoreData);
		// Generate BKS using CA pem.
		Bks.generateBKS(generator.getCaCert(),
		                ApkGenerator.workingDir + Constants.FilePath.BKS_FILE,
		                truststorePassword);
		// Copy BKS to Android source folder.
		FileOperator.copyFile(ApkGenerator.workingDir + Constants.FilePath.BKS_FILE,
		                      ApkGenerator.workingDir + Constants.FilePath.ANDROID_AGENT_RAW +
		                      Constants.FilePath.BKS_FILE
		);
		String zipFolderPath =
				reader.read("zipPath") + File.separator + Constants.FilePath.APK_FOLDER +
				File.separator;
		FileOperator.makeFolder(zipFolderPath);
		// Generate APK using Maven and create a zip.
		return Apk.compileApk(ApkGenerator.workingDir + Constants.FilePath.COMMON_UTIL,
		                      reader.read("serverIp"), truststorePassword, zipFileName,
		                      zipFolderPath);
	}
}

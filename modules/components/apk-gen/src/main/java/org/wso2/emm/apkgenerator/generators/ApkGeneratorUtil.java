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
package org.wso2.emm.apkgenerator.generators;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.osgi.signedcontent.InvalidContentException;
import org.wso2.emm.apkgenerator.data.CSRData;
import org.wso2.emm.apkgenerator.data.CertificateData;
import org.wso2.emm.apkgenerator.data.ObjectReader;
import org.wso2.emm.apkgenerator.data.TruststoreData;
import org.wso2.emm.apkgenerator.exception.ApkGenerationException;
import org.wso2.emm.apkgenerator.util.Constants;
import org.wso2.emm.apkgenerator.util.FileOperator;

import java.io.File;

/**
 * This is the class that provides access for the Jaggery application to communicate and
 * provide the data collected thought the EMM Wizard interface, to generate
 * Android APK with BKS injected and certificates.
 */
public class ApkGeneratorUtil {

	private static final Log LOG = LogFactory.getLog(ApkGeneratorUtil.class);

	/**
	 * This is used to perform the sequence of actions necessary to generate
	 * certificates, key stores, and apk.
	 *
	 * @param jsonPayload The JSON coming from the client.
	 * @return The path of the final zip file.
	 * @throws ApkGenerationException
	 */
	public static String generateApk(String jsonPayload)
			throws ApkGenerationException, InvalidContentException {
		CertificateData certificateData;
		if (LOG.isDebugEnabled()) {
			LOG.debug("Call to generate Certificates and APK.");
		}
		ObjectReader reader = new ObjectReader(jsonPayload);
		// Directory of the running Jaggery app needs to be sent from the
		// Jaggery UI along with the trust store password to be used.
		String workingDir = reader.read(Constants.FilePath.WORKING_DIR);
		String truststorePassword = reader.read(Constants.CSRDataKeys.PASSWORD);
		// Construct a name for the zip file to store final output.
		String zipFileName =
				reader.read(Constants.CSRDataKeys.USERSNAME) + "_" +
				reader.read(Constants.CSRDataKeys.COMPANY) + Constants.ARCHIVE_TYPE;
		CSRData csrData = getCSRData(reader);
		TruststoreData truststoreData = getTruststoerData(reader);
		// Generate the certificates.
		certificateData = CertificateChainGenerator.generate(csrData);
		// Convert generated certificates and keys to JKS.
		KeyStoreGenerator.convertCertsToKeyStore(certificateData.getKeyPairCA(),
		                                         certificateData.getKeyPairRA(),
		                                         certificateData.getKeyPairSSL(),
		                                         certificateData.getCaCert(),
		                                         certificateData.getRaCert(),
		                                         certificateData.getSslCert(),
		                                         truststoreData, workingDir);
		// Generate BKS using CA pem.
		BksUtil.generateBKS(certificateData.getCaCert(),
		                    workingDir + Constants.FilePath.BKS_FILE,
		                    truststorePassword);
		// Copy BKS to Android source folder.
		FileOperator.copyFile(workingDir + Constants.FilePath.BKS_FILE,
		                      workingDir + Constants.FilePath.ANDROID_AGENT_RAW +
		                      Constants.FilePath.BKS_FILE
		);
		String zipFolderPath =
				reader.read(Constants.FilePath.ZIP_PATH) + File.separator +
				Constants.FilePath.APK_FOLDER +
				File.separator;
		FileOperator.makeFolder(zipFolderPath);
		// Generate APK using Maven and create a zip.
		ApkUtil.compileApk(reader.read(Constants.CSRDataKeys.SERVER_IP), truststorePassword,
		                   zipFolderPath + zipFileName,
		                   workingDir);
		return zipFolderPath + zipFileName;
	}

	private static CSRData getCSRData(ObjectReader reader) throws ApkGenerationException {
		CSRData csrData = new CSRData();
		csrData.setCountryCA(reader.read(Constants.CSRDataKeys.COUNTRY_CA));
		csrData.setStateCA(reader.read(Constants.CSRDataKeys.STATE_CA));
		csrData.setLocalityCA(reader.read(Constants.CSRDataKeys.LOCALITY_CA));
		csrData.setOrganizationCA(reader.read(Constants.CSRDataKeys.ORGANIZATION_CA));
		csrData.setOrganizationUCA(reader.read(Constants.CSRDataKeys.ORGANIZATION_UNIT_CA));
		csrData.setDaysCA(reader.read(Constants.CSRDataKeys.DAYS_CA));
		csrData.setCommonNameCA(reader.read(Constants.CSRDataKeys.COMMON_NAME_CA));
		csrData.setCountryRA(reader.read(Constants.CSRDataKeys.COUNTRY_RA));
		csrData.setStateRA(reader.read(Constants.CSRDataKeys.STATE_RA));
		csrData.setLocalityRA(reader.read(Constants.CSRDataKeys.LOCALITY_RA));
		csrData.setOrganizationRA(reader.read(Constants.CSRDataKeys.ORGANIZATION_RA));
		csrData.setOrganizationURA(reader.read(Constants.CSRDataKeys.ORGANIZATION_UNIT_RA));
		csrData.setDaysRA(reader.read(Constants.CSRDataKeys.DAYS_RA));
		csrData.setCommonNameRA(reader.read(Constants.CSRDataKeys.COMMON_NAME_RA));
		csrData.setCountrySSL(reader.read(Constants.CSRDataKeys.COUNTRY_SSL));
		csrData.setStateSSL(reader.read(Constants.CSRDataKeys.STATE_SSL));
		csrData.setLocalitySSL(reader.read(Constants.CSRDataKeys.LOCALITY_SSL));
		csrData.setOrganizationSSL(reader.read(Constants.CSRDataKeys.ORGANIZATION_SSL));
		csrData.setOrganizationUSSL(reader.read(Constants.CSRDataKeys.ORGANIZATION_UNIT_SSL));
		csrData.setDaysSSL(reader.read(Constants.CSRDataKeys.DAYS_SSL));
		csrData.setCommonNameSSL(reader.read(Constants.CSRDataKeys.SERVER_IP));
		return csrData;
	}

	private static TruststoreData getTruststoerData(ObjectReader reader)
			throws ApkGenerationException {
		TruststoreData truststoreData = new TruststoreData();
		truststoreData.setPasswordPK12CA(reader.read(Constants.TruststoreKeys.PASSWORD_PK12_CA));
		truststoreData.setPasswordPK12RA(reader.read(Constants.TruststoreKeys.PASSWORD_PK12_RA));
		truststoreData.setAliasPK12CA(reader.read(Constants.TruststoreKeys.ALIAS_PK12_CA));
		truststoreData.setAliasPK12RA(reader.read(Constants.TruststoreKeys.ALIAS_PK12_RA));
		truststoreData.setPasswordWSO2EMMJKS(
				reader.read(Constants.TruststoreKeys.PASSWORD_WSO2_EMM_JKS));
		truststoreData.setAliasClientTruststore(
				reader.read(Constants.TruststoreKeys.ALIAS__CLIENT_TRUSTSTORE));
		truststoreData.setPasswordClientTruststore(
				reader.read(Constants.TruststoreKeys.PASSWORD_CLIENT_TRUSTSTORE));
		truststoreData.setAliasWSO2Carbon(reader.read(Constants.TruststoreKeys.ALIAS_WSO2_CARBON));
		truststoreData.setPasswordWSO2Carbon(
				reader.read(Constants.TruststoreKeys.PASSWORD_WSO2_CARBON));
		return truststoreData;

	}
}

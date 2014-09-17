package org.wso2.apkgenerator.generators;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.apkgenerator.data.CSRData;
import org.wso2.apkgenerator.data.ObjectReader;
import org.wso2.apkgenerator.util.Constants;
import org.wso2.apkgenerator.util.FileOperator;

import java.io.File;
import java.io.IOException;
import java.lang.Exception;
import java.lang.System;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/* This is the class that provides access for the jaggery app to
 * communicate and provide the data collected thought the EMM Wizard interface,
 * to generate APK and certificates.
 * */
public class APKGenerator {
	private CertificateGenerator generator;
	private ObjectReader reader;
	private static Log log = LogFactory.getLog(APKGenerator.class);
	public static String workingDir;
	public static String truststorePassword = "wso2carbon";

	/*
	 * This is used to perform the sequence of actions necessary to generate
	 * certificates, key stores, and apk
	 */
	public String generateApk(String jsonStr) {
		if (log.isDebugEnabled()) {
			log.debug("Call to generate APK");
		}

		// read the json string passed and convert it to a Json object
		reader = new ObjectReader(jsonStr);
		if (reader.json == null) {
			return "Error while parsing the sent data";// tell jaggery UI
		}
		// create a data object which stores necessery information to generate
		// certificates. These details are taken from the created ObjectReader
		CSRData csrDate = new CSRData(reader);

		// directory of the running Jaggery app needs to be sent from the
		// Jaggery UI along with the trust store password to be used
		workingDir = FileOperator.getPath(reader.read("workingDir"));
		if (workingDir == null) {
			return "Error trying to get EMM wizard path";// tell jaggery UI
		}
		truststorePassword = reader.read("password");// and password
		// construct a name for the zip file to store final output
		String zipFileName = reader.read("usersname") + "_"
				+ reader.read("company") + ".zip";

		// generate CA,RA and SSL certificates
		generator = new CertificateGenerator(csrDate);
		if (!generator.generate()) {
			return "could not generate certificates";// tell jaggery UI
		}

		// convert generated certificates and keys to JKS
		boolean generated = KeyStoreGenerator.convertCertsToKeyStore(
				generator.keyPairCA, generator.keyPairRA, generator.keyPairSSL,
				generator.caCert, generator.raCert, generator.sslCert);
		if(!generated){
			return "failed to generate keystores";// tell jaggery UI
		}

		// Generate BKS using CA pem
		if(!BksGenerator.generateBKS(generator.caCert)){
			return "failed to generate BKS file";// tell jaggery UI
		}
		// Copy BKS to Android source folder
		if(!FileOperator.copyFile(APKGenerator.workingDir + Constants.BKS_File,
				APKGenerator.workingDir + Constants.ANDROID_AGENT_RAW
						+ Constants.BKS_File)){
			// tell jaggery UI
			return "Error while copying BKS to Android source folder";
		}

		//Creating a directory to store the apk
		String zipFolderPath=reader.read("zipPath") + "/Apk/";
		try {
			boolean success = (new File(zipFolderPath)
					.mkdirs());
		} catch (Exception e) {
			log.error("Error when creating directory to store the apk", e);
			return "Error when creating directory to store the apk";
		}

		// generate apk using maven and create a zip
		return Apk.generateApk(APKGenerator.workingDir + Constants.COMMON_UTIL,
				reader.read("serverIp"), truststorePassword, zipFileName,
				zipFolderPath);
	}
}

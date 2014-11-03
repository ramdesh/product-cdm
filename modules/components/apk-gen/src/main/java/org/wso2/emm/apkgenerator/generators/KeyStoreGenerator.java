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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.log4j.Logger;
import org.wso2.emm.apkgenerator.util.Constants;
import org.wso2.emm.apkgenerator.util.FileOperator;

/**
 * Creation of key stores and injecting certificates to the key stores is
 * handled here
 */
public class KeyStoreGenerator {

	private static Logger log = Logger.getLogger(KeyStoreGenerator.class);

	/**
	 * To inject certificates to correct key stores and to generate new key
	 * stores, that is necessary for EMM to function properly are generated
	 * here. manages the flow of certificate to key store conversion
	 * 
	 * @param keyPairCA
	 *            public and private key pair of CA certificate
	 * @param keyPairRA
	 *            public and private key pair of RA certificate
	 * @param keyPairSSL
	 *            public and private key pair of SSL certificate
	 * @param caCert
	 *            CA certificate content
	 * @param raCert
	 *            RA certificate content
	 * @param sslCert
	 *            SSL certificate content
	 * @return
	 */
	public static void convertCertsToKeyStore(KeyPair keyPairCA,
			KeyPair keyPairRA, KeyPair keyPairSSL, X509Certificate caCert,
			X509Certificate raCert, X509Certificate sslCert)
			throws CertificateGenerationException {

		// insert CA and RA to emm JKS
		generateMobileEMM(keyPairCA.getPrivate(),
				new X509Certificate[] { caCert }, Constants.PK12_CA_PASSWORD,
				Constants.WSO2EMM_JKS, Constants.PK12_CA_ALIAS, true,
				Constants.WSO2EMM_JKS_PASSWORD);
		generateMobileEMM(keyPairRA.getPrivate(), new X509Certificate[] {
				raCert, caCert }, Constants.PK12_RA_PASSWORD,
				Constants.WSO2EMM_JKS, Constants.PK12_RA_ALIAS, false,
				Constants.WSO2EMM_JKS_PASSWORD);

		// take a copy of jks files in the jksFolder which is the temp
		// folder and and copy to working dir. This is to avoid original
		// files getting corrupted in case
		FileOperator.copyFile(ApkGenerator.workingDir + Constants.jksFolder
				+ Constants.CLIENT_TRUST_JKS, ApkGenerator.workingDir
				+ Constants.CLIENT_TRUST_JKS);
		FileOperator.copyFile(ApkGenerator.workingDir + Constants.jksFolder
				+ Constants.WSO2CARBON_JKS, ApkGenerator.workingDir
				+ Constants.WSO2CARBON_JKS);
		// insert SSL cert to client trust store JKS and wso2carbon JKS
		generateCarbonJksFiles(keyPairSSL.getPrivate(),
				new X509Certificate[] { sslCert }, Constants.WSO2CARBON,
				Constants.CLIENT_TRUST_JKS, Constants.WSO2CARBON, caCert,
				Constants.PK12_CA_ALIAS);
		generateCarbonJksFiles(keyPairSSL.getPrivate(),
				new X509Certificate[] { sslCert }, Constants.WSO2CARBON,
				Constants.WSO2CARBON_JKS, Constants.WSO2CARBON, caCert,
				Constants.PK12_CA_ALIAS);
	}

	/**
	 * Create new JKS keystore or an exiting key store is opened and a
	 * certificate chain can be added.
	 * 
	 * @param key
	 *            is the private key of certificate to be inserted
	 * @param cert
	 *            is an array of {@link X509Certificate} which needs to be
	 *            inserted
	 * @param password
	 *            of the key store
	 * @param outFile
	 *            name of the output file
	 * @param alias
	 *            is the alias name
	 * @param createJks
	 *            this means instead of creating a new store, use the existing
	 *            one mentioned @param outFile. value is true if it is needed to
	 *            create a new store, false in order to use the same store
	 * @param storePass
	 *            is the password of the key store
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws KeyStoreException
	 */
	public static void generateMobileEMM(Key key, X509Certificate[] cert,
			String password, String outFile, String alias, boolean createJks,
			String storePass) throws CertificateGenerationException {

		KeyStore keyStore = getKeyStore();
		String resultFile = ApkGenerator.workingDir + outFile;
		if (createJks == false) {// create a new store
			loadToStore(keyStore, storePass.toCharArray(), resultFile);
		} else {
			loadToStore(keyStore, null, null);
		}

		try {
			keyStore.setKeyEntry(alias, (Key) key, password.toCharArray(), cert);
		} catch (KeyStoreException e) {
			log.error(
					"Generic KeyStore error while creating new JKS ,"
							+ e.getMessage(), e);
			throw new CertificateGenerationException(
					"Generic KeyStore error while creating new JKS ,"
							+ e.getMessage(), e);
		}
		writKeyStoreToFile(resultFile, keyStore, storePass.toCharArray());

	}

	/**
	 * Write a provided key store to a physical file
	 * 
	 * @param resultFile
	 *            name of the destination file to be created
	 * @param keyStore
	 *            is the key store object that needs to be written to a file
	 * @param storePass
	 *            password of the key store
	 * @throws CertificateGenerationException
	 */
	public static void writKeyStoreToFile(String resultFile, KeyStore keyStore,
			char[] storePass) throws CertificateGenerationException {
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(resultFile);
		} catch (FileNotFoundException e) {
			log.error(
					"Cannot open the file ," + resultFile + ", "
							+ e.getMessage(), e);
			throw new CertificateGenerationException("Cannot open the file ,"
					+ resultFile + ", " + e.getMessage(), e);
		}

		try {
			keyStore.store(fileOutputStream, storePass);
		} catch (KeyStoreException e) {
			log.error(
					"Generic KeyStore error while creating new JKS ,"
							+ e.getMessage(), e);
			throw new CertificateGenerationException(
					"Generic KeyStore error while creating new JKS ,"
							+ e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			log.error(
					"cryptographic algorithm is requested but"
							+ " it is not available in the environment, "
							+ e.getMessage(), e);
			throw new CertificateGenerationException(
					"cryptographic algorithm is requested but"
							+ " it is not available in the environment, "
							+ e.getMessage(), e);
		} catch (CertificateException e) {
			log.error("Error working with certificate related to, "
					+ resultFile + " , " + e.getMessage(), e);
			throw new CertificateGenerationException(
					"Error working with certificate related to, " + resultFile
							+ " , " + e.getMessage(), e);
		} catch (IOException e) {
			log.error("file error while working with file, " + resultFile
					+ ", " + e.getMessage(), e);
			throw new CertificateGenerationException(
					"file error while working with files, " + resultFile + ", "
							+ e.getMessage(), e);
		}

		try {
			if (fileOutputStream != null) {
				fileOutputStream.close();
			}
		} catch (IOException e) {
			log.error("file error while closing the file, " + resultFile + ", "
					+ e.getMessage(), e);
			throw new CertificateGenerationException(
					"file error while closing the file, " + resultFile + ", "
							+ e.getMessage(), e);
		}
	}

	/**
	 * Load/initiate a key store from a provided file.
	 * 
	 * @param keyStore
	 *            is the destination key store which needs to be loaded.
	 * @param storePass
	 *            password of the key store
	 * @param resultFile
	 *            the source key store file
	 * @throws CertificateGenerationException
	 */
	public static void loadToStore(KeyStore keyStore, char[] storePass,
			String resultFile) throws CertificateGenerationException {
		FileInputStream fileInputStream = null;
		if (resultFile != null) {
			fileInputStream = FileOperator.getFileInputStream(resultFile);
		}
		try {
			keyStore.load(fileInputStream, storePass);
		} catch (NoSuchAlgorithmException e) {
			log.error(
					Constants.ALGORITHM
							+ " cryptographic algorithm is requested but"
							+ " it is not available in the environment, "
							+ e.getMessage(), e);
			throw new CertificateGenerationException(Constants.ALGORITHM
					+ " cryptographic algorithm is requested but"
					+ " it is not available in the environment, "
					+ e.getMessage(), e);
		} catch (CertificateException e) {
			log.error("Error working with certificate related to, "
					+ resultFile + " , " + e.getMessage(), e);
			throw new CertificateGenerationException(
					"Error working with certificate related to, " + resultFile
							+ " , " + e.getMessage(), e);
		} catch (IOException e) {
			log.error("file error while working with file, " + resultFile
					+ ", " + e.getMessage(), e);
			throw new CertificateGenerationException(
					"file error while working with files, " + resultFile + ", "
							+ e.getMessage(), e);
		}

		try {
			if (fileInputStream != null) {
				fileInputStream.close();
			}
		} catch (IOException e) {
			log.error("file error while closing the file, " + resultFile + ", "
					+ e.getMessage(), e);
			throw new CertificateGenerationException(
					"file error while closing the file, " + resultFile + ", "
							+ e.getMessage(), e);
		}
	}

	/**
	 * Input certificate chains and individual certificate to carbon jks files
	 * in the server
	 * 
	 * @param key
	 *            the private key of certificate to be inserted
	 * @param cert
	 *            is an array of {@link X509Certificate} which needs to be
	 *            inserted
	 * @param password
	 *            of the key store
	 * @param outFile
	 *            name of the output file
	 * @param alias
	 *            is the alias name
	 * @param secondCert
	 *            any other certificate other than the main certificate chain
	 * @param seconderyCertAlias
	 *            alias name of the secondary certificate
	 * @throws CertificateGenerationException
	 */
	public static void generateCarbonJksFiles(Key key, X509Certificate[] cert,
			String password, String outFile, String alias,
			X509Certificate secondCert, String seconderyCertAlias)
			throws CertificateGenerationException {

		String resultFile = ApkGenerator.workingDir + outFile;
		KeyStore keyStore = getKeyStore();
		loadToStore(keyStore, password.toCharArray(), resultFile);

		if (secondCert != null) {
			try {
				keyStore.setCertificateEntry(seconderyCertAlias, secondCert);
			} catch (KeyStoreException e) {
				log.error(
						"KeyStore error while adding certificate with alias "
								+ seconderyCertAlias + "to key store, "
								+ e.getMessage(), e);
				throw new CertificateGenerationException(
						"KeyStore error while adding certificate with alias "
								+ seconderyCertAlias + "to key store, "
								+ e.getMessage(), e);
			}
		}
		try {
			keyStore.setKeyEntry(alias, (Key) key, password.toCharArray(), cert);
		} catch (KeyStoreException e) {
			log.error(
					"KeyStore error while adding certificate chanin with alias "
							+ alias + "to key store, " + e.getMessage(), e);
			throw new CertificateGenerationException(
					"KeyStore error while adding certificate chanin with alias "
							+ alias + "to key store, " + e.getMessage(), e);
		}
		writKeyStoreToFile(resultFile, keyStore, password.toCharArray());
	}

	/**
	 * Generates new JKS key stores
	 * 
	 * @return A new JKS key store
	 * @throws CertificateGenerationException
	 */
	private static KeyStore getKeyStore() throws CertificateGenerationException {
		try {
			return KeyStore.getInstance(Constants.JKS);
		} catch (KeyStoreException e) {
			log.error(
					"KeyStore error while creating new JKS ," + e.getMessage(),
					e);
			throw new CertificateGenerationException(
					"KeyStore error while creating new JKS ," + e.getMessage(),
					e);
		}

	}

}

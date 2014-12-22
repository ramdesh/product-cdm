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
import org.wso2.emm.apkgenerator.data.TruststoreData;
import org.wso2.emm.apkgenerator.exception.ApkGenerationException;
import org.wso2.emm.apkgenerator.util.Constants;
import org.wso2.emm.apkgenerator.util.FileOperator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Creation of key stores and injecting certificates to the key stores is
 * handled here.
 */
public class KeyStoreGenerator {

	private static final Log LOG = LogFactory.getLog(KeyStoreGenerator.class);

	/**
	 * To inject certificates to correct key stores and to generate new key
	 * stores, that is necessary for EMM to function properly are generated
	 * here. Also manages the flow of certificate to key store conversion.
	 *
	 * @param keyPairCA      Public and private key pair of CA certificate.
	 * @param keyPairRA      Public and private key pair of RA certificate.
	 * @param keyPairSSL     Public and private key pair of SSL certificate.
	 * @param caCert         CA certificate content.
	 * @param raCert         RA certificate content.
	 * @param sslCert        SSL certificate content.
	 * @param truststoreData Holds details that are relevant to keystores.
	 * @param workingDir     Directory where the temporary files are saved.
	 * @throws ApkGenerationException
	 */
	public static void convertCertsToKeyStore(KeyPair keyPairCA, KeyPair keyPairRA,
	                                          KeyPair keyPairSSL, X509Certificate caCert,
	                                          X509Certificate raCert, X509Certificate sslCert,
	                                          TruststoreData truststoreData, String workingDir)
			throws ApkGenerationException {

		// Insert CA and RA to emm JKS.
		generateEMMTruststore(keyPairCA.getPrivate(), new X509Certificate[] { caCert },
		                      truststoreData.getPasswordPK12CA(), Constants.FilePath.WSO2EMM_JKS,
		                      truststoreData.getAliasPK12CA(), true,
		                      truststoreData.getPasswordWSO2EMMJKS(), workingDir);
		generateEMMTruststore(keyPairRA.getPrivate(), new X509Certificate[] { raCert, caCert },
		                      truststoreData.getPasswordPK12RA(), Constants.FilePath.WSO2EMM_JKS,
		                      truststoreData.getAliasPK12RA(), false,
		                      truststoreData.getPasswordWSO2EMMJKS(), workingDir);

		// Take a copy of jks files in the jks Folder which is the temp
		// folder and and copy to working dir. This is to avoid original
		// files getting corrupted in case.
		FileOperator.copyFile(
				workingDir + Constants.FilePath.JKS_FOLDER + File.separator +
				Constants.FilePath.CLIENT_TRUST_JKS, workingDir +
				                                     Constants.FilePath.CLIENT_TRUST_JKS
		);
		FileOperator.copyFile(
				workingDir + Constants.FilePath.JKS_FOLDER + File.separator +
				Constants.FilePath.WSO2CARBON_JKS, workingDir +
				                                   Constants.FilePath.WSO2CARBON_JKS
		);
		// Insert SSL cert to client trust store JKS and wso2carbon JKS.
		generateSecurityTruststore(keyPairSSL.getPrivate(), new X509Certificate[] { sslCert },
		                           truststoreData.getPasswordClientTruststore(),
		                           Constants.FilePath.CLIENT_TRUST_JKS,
		                           truststoreData.getAliasClientTruststore(), caCert,
		                           truststoreData.getAliasPK12CA(), workingDir);
		generateSecurityTruststore(keyPairSSL.getPrivate(), new X509Certificate[] { sslCert },
		                           truststoreData.getPasswordWSO2Carbon(),
		                           Constants.FilePath.WSO2CARBON_JKS,
		                           truststoreData.getAliasWSO2Carbon(), caCert,
		                           truststoreData.getAliasPK12CA(), workingDir);
	}

	/**
	 * Create new JKS keystore or an exiting key store is opened and a
	 * certificate chain can be added.
	 *
	 * @param key          The private key of certificate to be inserted.
	 * @param certificates An array of {@link X509Certificate} which needs to be
	 *                     inserted
	 * @param password     The password of the key store.
	 * @param outFile      Name of the output file.
	 * @param alias        The alias name of the certificate entry to be inserted.
	 * @param createJks    This means instead of creating a new store, use the existing
	 *                     one mentioned @param outFile. Value is true if it is needed to
	 *                     create a new store, false in order to use the same store.
	 * @param storePass    The password of the key store.
	 * @param workingDir   Directory where the temporary files are saved.
	 * @throws ApkGenerationException
	 */
	public static void generateEMMTruststore(Key key, X509Certificate[] certificates,
	                                         String password,
	                                         String outFile, String alias, boolean createJks,
	                                         String storePass, String workingDir)
			throws ApkGenerationException {

		KeyStore keyStore = getKeyStore();
		String resultFile = workingDir + outFile;
		// Create a new store
		if (!createJks) {
			loadToStore(keyStore, storePass.toCharArray(), resultFile);
		} else {
			loadToStore(keyStore, null, null);
		}

		try {
			keyStore.setKeyEntry(alias, key, password.toCharArray(), certificates);
		} catch (KeyStoreException e) {
			String message = "Generic KeyStore error while creating new JKS.";
			LOG.error(message, e);
			throw new ApkGenerationException(message, e);
		}
		writKeyStoreToFile(resultFile, keyStore, storePass.toCharArray());

	}

	/**
	 * Write a provided key store to a physical file.
	 *
	 * @param resultFile Name of the destination file to be created.
	 * @param keyStore   The key store object that needs to be written to a file.
	 * @param storePass  Password of the key store.
	 * @throws ApkGenerationException
	 */
	public static void writKeyStoreToFile(String resultFile, KeyStore keyStore, char[] storePass)
			throws ApkGenerationException {
		FileOutputStream fileOutputStream = null;

		try {
			fileOutputStream = new FileOutputStream(resultFile);
			keyStore.store(fileOutputStream, storePass);
		} catch (KeyStoreException e) {
			String message = "Generic KeyStore error while creating new JKS.";
			LOG.error(message, e);
			throw new ApkGenerationException(message, e);
		} catch (NoSuchAlgorithmException e) {
			String message = "Cryptographic algorithm is requested but"
			                 + " it is not available in the environment.";
			LOG.error(message, e);
			throw new ApkGenerationException(message, e);
		} catch (CertificateException e) {
			String message = "Error working with certificate related to, " + resultFile;
			LOG.error(message, e);
			throw new ApkGenerationException(message, e);
		} catch (IOException e) {
			String message = "File error while working with file, " + resultFile;
			LOG.error(message, e);
			throw new ApkGenerationException(message, e);
		} finally {
			try {
				if (fileOutputStream != null) {
					fileOutputStream.close();
				}
			} catch (IOException e) {
				String message = "File error while closing the file, " + resultFile;
				LOG.error(message, e);
			}
		}
	}

	/**
	 * Load/initiate a key store from a provided file.
	 *
	 * @param keyStore   The destination key store which needs to be loaded.
	 * @param storePass  Password of the key store.
	 * @param resultFile The source key store file.
	 * @throws ApkGenerationException
	 */
	public static void loadToStore(KeyStore keyStore, char[] storePass, String resultFile)
			throws ApkGenerationException {
		FileInputStream fileInputStream = null;

		try {
			if (resultFile != null) {
				fileInputStream = FileOperator.getFileInputStream(resultFile);
				keyStore.load(fileInputStream, storePass);
			}
		} catch (NoSuchAlgorithmException e) {
			String message = Constants.ALGORITHM + " cryptographic algorithm is requested but" +
			                 " it is not available in the environment.";
			LOG.error(message, e);
			throw new ApkGenerationException(message, e);
		} catch (CertificateException e) {
			String message = "Error working with certificate related to, " + resultFile;
			LOG.error(message, e);
			throw new ApkGenerationException(message, e);
		} catch (IOException e) {
			String message = "File error while working with file, " + resultFile;
			LOG.error(message, e);
			throw new ApkGenerationException(message, e);
		} finally {
			try {
				if (fileInputStream != null) {
					fileInputStream.close();
				}
			} catch (IOException e) {
				String message = "File error while closing the file, " + resultFile;
				LOG.error(message, e);
			}
		}
	}

	/**
	 * Input certificate chains and individual certificate to carbon jks files
	 * in the server.
	 *
	 * @param key                The private key of certificate to be inserted
	 * @param certificates               An array of {@link X509Certificate} which needs to be
	 *                           inserted
	 * @param password           The password of the key store.
	 * @param outFile            Name of the output file.
	 * @param alias              The alias name of the certificate entry to be inserted.
	 * @param secondCert         Any other certificate other than the main certificate chain that needs to be added.
	 * @param secondaryCertAlias Alias name of the secondary certificate.
	 * @param workingDir         Directory where the temporary files are saved.
	 * @throws ApkGenerationException
	 */
	public static void generateSecurityTruststore(Key key, X509Certificate[] certificates, String password,
	                                              String outFile, String alias,
	                                              X509Certificate secondCert,
	                                              String secondaryCertAlias, String workingDir)
			throws ApkGenerationException {

		String resultFile = workingDir + outFile;
		KeyStore keyStore = getKeyStore();
		loadToStore(keyStore, password.toCharArray(), resultFile);

		if (secondCert != null) {
			try {
				keyStore.setCertificateEntry(secondaryCertAlias, secondCert);
			} catch (KeyStoreException e) {
				String message = "KeyStore error while adding certificate with alias " +
				                 secondaryCertAlias + "to key store.";
				LOG.error(message, e);
				throw new ApkGenerationException(message, e);
			}
		}
		try {
			keyStore.setKeyEntry(alias, key, password.toCharArray(), certificates);
		} catch (KeyStoreException e) {
			String message = "KeyStore error while adding certificate chain with alias " + alias +
			                 "to key store.";
			LOG.error(message, e);
			throw new ApkGenerationException(message, e);
		}
		writKeyStoreToFile(resultFile, keyStore, password.toCharArray());
	}

	/**
	 * Generates new JKS key stores.
	 *
	 * @return A new JKS key store.
	 * @throws ApkGenerationException
	 */
	private static KeyStore getKeyStore() throws ApkGenerationException {
		try {
			return KeyStore.getInstance(Constants.JKS);
		} catch (KeyStoreException e) {
			String message = "KeyStore error while creating new JKS.";
			LOG.error(message, e);
			throw new ApkGenerationException(message, e);
		}

	}

}

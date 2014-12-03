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

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.log4j.Logger;
import org.wso2.emm.apkgenerator.util.Constants;

/**
 * BKS is a key store which is commonly used in Android to hold certificates.
 * This class can be used to generate BKS and insert certificates to it.
 * 
 */
public class BksGenerator {

	private static Logger log = Logger.getLogger(BksGenerator.class);

	/**
	 * @param cert
	 *            the {@link X509Certificate} certificate that needs to be
	 *            inserted.
	 * @throws CertificateGenerationException
	 */
	public static void generateBKS(X509Certificate cert) throws CertificateGenerationException {
		KeyStore keystore;
		String bksFile = ApkGenerator.workingDir + Constants.BKS_File;
		try {

			Provider bcProvider = new org.bouncycastle.jce.provider.BouncyCastleProvider();
			keystore = KeyStore.getInstance(Constants.BKS, bcProvider);
			keystore.load(null);
			keystore.setCertificateEntry(Constants.BKS_ALIAS, cert);

			FileOutputStream fos = new FileOutputStream(bksFile);
			keystore.store(fos, ApkGenerator.truststorePassword.toCharArray());
			fos.close();
		} catch (KeyStoreException e) {
			log.error("KeyStore error while creating new BKS ," + e.getMessage(), e);
			throw new CertificateGenerationException("KeyStore error while creating new BKS ," +
			                                         e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			log.error("Cryptographic algorithm is requested but" +
			          " it is not available in the environment, " + e.getMessage(), e);
			throw new CertificateGenerationException("Cryptographic algorithm is requested but" +
			                                         " it is not available in the environment, " +
			                                         e.getMessage(), e);
		} catch (CertificateException e) {
			log.error("Error working with certificate, " + e.getMessage(), e);
			throw new CertificateGenerationException("Error working with certificate, " +
			                                         e.getMessage(), e);
		} catch (IOException e) {
			log.error("File error while working with file, " + e.getMessage(), e);
			throw new CertificateGenerationException("File error while working with files, " +
			                                         bksFile + ", " + e.getMessage(), e);
		}
	}
}

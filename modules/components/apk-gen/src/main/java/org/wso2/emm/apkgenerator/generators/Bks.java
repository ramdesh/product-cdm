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
import org.wso2.emm.apkgenerator.util.Constants;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * BKS is a key store which is commonly used in Android to hold certificates.
 * This class can be used to generate BKS and insert certificates to it.
 */
public class Bks {

	private static Log log = LogFactory.getLog(Bks.class);

	/**
	 * @param cert the {@link X509Certificate} certificate that needs to be
	 *             inserted.
	 * @throws ApkGenerationException
	 */
	public static void generateBKS(X509Certificate cert, String bksFilePath,
	                               String truststorePassword) throws ApkGenerationException {
		KeyStore keystore;
		try {

			keystore =
					KeyStore.getInstance(Constants.BKS,
					                     new org.spongycastle.jce.provider.BouncyCastleProvider());
			keystore.load(null);
			keystore.setCertificateEntry(Constants.BKS_ALIAS, cert);

			FileOutputStream fos = new FileOutputStream(bksFilePath);
			keystore.store(fos, truststorePassword.toCharArray());
			fos.close();
		} catch (KeyStoreException e) {
			String message = "KeyStore error while creating new BKS.";
			log.error(message, e);
			throw new ApkGenerationException(message, e);
		} catch (NoSuchAlgorithmException e) {
			String message =
					"Cryptographic algorithm is requested but"
					+ " it is not available in the environment.";
			log.error(message, e);
			throw new ApkGenerationException(message, e);
		} catch (CertificateException e) {
			String message = "Error working with certificates.";
			log.error(message, e);
			throw new ApkGenerationException(message, e);
		} catch (IOException e) {
			String message = "File error while working with files - " + bksFilePath;
			log.error(message, e);
			throw new ApkGenerationException(message, e);
		}
	}
}

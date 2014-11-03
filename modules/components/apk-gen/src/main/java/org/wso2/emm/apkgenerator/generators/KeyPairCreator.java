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

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import org.apache.log4j.Logger;
import org.wso2.emm.apkgenerator.util.Constants;

/**
 * Private and public key pair generation is handled by this class.
 */
public class KeyPairCreator {

	private static Logger log = Logger.getLogger(KeyPairCreator.class);

	/**
	 * Generate a public and a private key pair set.
	 * 
	 * @return a key pair of public and private key combination
	 * @throws CertificateGenerationException
	 */
	public static KeyPair getKeyPair() throws CertificateGenerationException {
		if (log.isDebugEnabled()) {
			log.debug("generating key pair");
		}
		KeyPairGenerator keyPairGenerator;
		KeyPair keyPair = null;
		try {
			keyPairGenerator = KeyPairGenerator.getInstance(
					Constants.ALGORITHM, Constants.PROVIDER);
			keyPairGenerator.initialize(1024, new SecureRandom());
			keyPair = keyPairGenerator.generateKeyPair();
			return keyPair;
		} catch (NoSuchAlgorithmException e) {
			log.error(Constants.ALGORITHM
					+ " cryptographic algorithm is requested but"
					+ " it is not available in the environment", e);
			throw new CertificateGenerationException(Constants.ALGORITHM
					+ " cryptographic algorithm is requested but"
					+ " it is not available in the environment ,"
					+ e.getMessage(), e);
		} catch (NoSuchProviderException e) {
			log.error(
					Constants.PROVIDER
							+ " security provider is requested but it is not available in "
							+ "the environment. ", e);
			throw new CertificateGenerationException(
					Constants.PROVIDER
							+ " security provider is requested but it is not available in "
							+ "the environment ," + e.getMessage(), e);
		} catch (Exception e) {
			log.error("Error while generating keys, " + e.getMessage(), e);
			throw new CertificateGenerationException(
					"Error while generating keys, " + e.getMessage(), e);
		}
	}
}

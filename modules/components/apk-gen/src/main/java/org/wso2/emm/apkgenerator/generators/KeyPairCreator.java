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
import org.wso2.emm.apkgenerator.exception.ApkGenerationException;
import org.wso2.emm.apkgenerator.util.Constants;

import java.security.*;

/**
 * Private and public key pair generation is handled by this class.
 */
public class KeyPairCreator {

	private static final Log LOG = LogFactory.getLog(KeyPairCreator.class);

	/**
	 * Generate a public and a private key pair set.
	 *
	 * @return a key pair of public and private key combination
	 * @throws ApkGenerationException
	 */
	/**
	 * @param algorithm Algorithm to be used to get key pair.
	 * @param provider  The name of the provider.
	 * @return A key pair of public and private key combination.
	 * @throws ApkGenerationException
	 */
	public static KeyPair generateKeyPair(String algorithm, String provider)
			throws ApkGenerationException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Generating key pair.");
		}
		KeyPairGenerator keyPairGenerator;
		try {
			keyPairGenerator = KeyPairGenerator.getInstance(algorithm, provider);
			keyPairGenerator.initialize(1024, new SecureRandom());
			return keyPairGenerator.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			String message = Constants.ALGORITHM + " cryptographic algorithm is requested but" +
			                 " it is not available in the environment.";
			LOG.error(message, e);
			throw new ApkGenerationException(message, e);
		} catch (NoSuchProviderException e) {
			String message = Constants.PROVIDER +
			                 " security provider is requested but it is not available in the environment.";
			LOG.error(message, e);
			throw new ApkGenerationException(message, e);
		}
	}
}

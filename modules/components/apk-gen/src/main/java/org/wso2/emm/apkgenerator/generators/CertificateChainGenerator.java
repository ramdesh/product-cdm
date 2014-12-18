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

import org.wso2.emm.apkgenerator.data.CSRData;
import org.wso2.emm.apkgenerator.data.CertificateData;
import org.wso2.emm.apkgenerator.util.Constants;

import java.security.Security;

/**
 * This class coordinates the certificate generation process which must happen
 * sequentially, in order to generate the chain.
 */
public class CertificateChainGenerator {
	static {
		Security.addProvider(new org.spongycastle.jce.provider.BouncyCastleProvider());
	}

	/**
	 * Generate CA,RA and SSL certificates respectively.
	 *
	 * @param csrData data needed to create certificates.
	 * @return
	 * @throws ApkGenerationException
	 */
	public static CertificateData generate(CSRData csrData) throws ApkGenerationException {
		CertificateData certificateData = new CertificateData();
		// Generate CA certificate and keys.
		certificateData.setKeyPairCA(
				KeyPairCreator.generateKeyPair(Constants.ALGORITHM, Constants.PROVIDER));
		certificateData.setCaCert(CertificateUtil.generateCACert(csrData.getDaysCA(),
		                                                            csrData.getCADistinguishedName(),
		                                                            certificateData
				                                                            .getKeyPairCA()
		));

		// Generate RA certificate and keys.
		certificateData.setKeyPairRA(
				KeyPairCreator.generateKeyPair(Constants.ALGORITHM, Constants.PROVIDER));
		certificateData.setRaCert(
				CertificateUtil.buildIntermediateCert(Constants.REGISTRATION_AUTHORITY,
				                                         certificateData.getKeyPairRA().getPublic(),
				                                         certificateData.getKeyPairCA()
				                                                        .getPrivate(),
				                                         certificateData.getCaCert(),
				                                         csrData.getRADistinguishedName(),
				                                         csrData.getDaysRA()
				)
		);

		// Generate SSL certificate and keys.
		certificateData.setKeyPairSSL(
				KeyPairCreator.generateKeyPair(Constants.ALGORITHM, Constants.PROVIDER));
		certificateData.setSslCert(
				CertificateUtil.buildIntermediateCert(Constants.SSL,
				                                         certificateData.getKeyPairSSL()
				                                                        .getPublic(),
				                                         certificateData.getKeyPairCA()
				                                                        .getPrivate(),
				                                         certificateData.getCaCert(),
				                                         csrData.getSSLDistinguishedName(),
				                                         csrData.getDaysSSL()
				)
		);

		return certificateData;
	}
}

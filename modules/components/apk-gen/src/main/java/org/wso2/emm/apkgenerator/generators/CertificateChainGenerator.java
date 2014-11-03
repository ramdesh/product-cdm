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

import java.security.cert.X509Certificate;
import java.security.KeyPair;
import java.security.Security;

import org.wso2.emm.apkgenerator.data.CSRData;
import org.wso2.emm.apkgenerator.util.Constants;
import org.wso2.emm.apkgenerator.util.FileOperator;

/**
 * This class coordinates the certificate generation process which must happen
 * sequentially, in order to generate the chain.
 */
public class CertificateChainGenerator {

	private CSRData csrDataSrc;
	public KeyPair keyPairCA, keyPairRA, keyPairSSL;
	public X509Certificate caCert, raCert, sslCert;
	static {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}

	/**
	 * Initialize the certificate details needed to generate certificates.
	 * 
	 * @param csrData
	 *            is data necessary to generate certificates
	 */
	public CertificateChainGenerator(CSRData csrData) {
		csrDataSrc = csrData;
	}

	/**
	 * Generate CA,RA and SSL certificates respectively and finally write the
	 * root(CA) certificate to working directory for future usage.
	 */
	public void generate() throws CertificateGenerationException {
		// generate CA cert and keys.
		keyPairCA = KeyPairCreator.getKeyPair();
		caCert = X509V3Certificates.generateCACert(csrDataSrc.getDaysCA(),
				csrDataSrc.getCADistinguishedName(), keyPairCA);

		// generate RA cert and keys.
		keyPairRA = KeyPairCreator.getKeyPair();
		raCert = X509V3Certificates.buildIntermediateCert("RA",
				keyPairRA.getPublic(), keyPairCA.getPrivate(), caCert,
				csrDataSrc.getRADistinguishedName(), csrDataSrc.getDaysRA());

		// generate SSL cert and keys.
		keyPairSSL = KeyPairCreator.getKeyPair();
		sslCert = X509V3Certificates.buildIntermediateCert("SSL",
				keyPairSSL.getPublic(), keyPairCA.getPrivate(), caCert,
				csrDataSrc.getSSLDistinguishedName(), csrDataSrc.getDaysSSL());

		// CA certificate is written to a file for later usage.
		FileOperator.writePem(ApkGenerator.workingDir + Constants.PEM_file,
				caCert);
	}
}

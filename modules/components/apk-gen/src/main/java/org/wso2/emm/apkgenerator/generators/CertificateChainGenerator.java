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
import org.wso2.emm.apkgenerator.util.Constants;

import java.security.KeyPair;
import java.security.Security;
import java.security.cert.X509Certificate;

/**
 * This class coordinates the certificate generation process which must happen
 * sequentially, in order to generate the chain.
 */
public class CertificateChainGenerator {

	private CSRData csrData;
	private KeyPair keyPairCA, keyPairRA, keyPairSSL;
	private X509Certificate caCert, raCert, sslCert;

	static {
		Security.addProvider(new org.spongycastle.jce.provider.BouncyCastleProvider());
	}

	/**
	 * Initialize the certificate details needed to generate certificates.
	 *
	 * @param csrData is data necessary to generate certificates
	 */
	public CertificateChainGenerator(CSRData csrData) {
		this.csrData = csrData;
	}

	/**
	 * Generate CA,RA and SSL certificates respectively and finally write the
	 * root(CA) certificate to working directory for future usage.
	 */
	public void generate() throws ApkGenerationException {
		// Generate CA cert and keys.
		setKeyPairCA(KeyPairCreator.getKeyPair(Constants.ALGORITHM, Constants.PROVIDER));
		setCaCert(X509V3Certificates.generateCACert(csrData.getDaysCA(),
		                                            csrData.getCADistinguishedName(),
		                                            getKeyPairCA()));

		// Generate RA cert and keys.
		setKeyPairRA(KeyPairCreator.getKeyPair(Constants.ALGORITHM, Constants.PROVIDER));
		setRaCert(X509V3Certificates.buildIntermediateCert(Constants.REGISTRATION_AUTHORITY,
		                                                   getKeyPairRA().getPublic(),
		                                                   getKeyPairCA().getPrivate(), getCaCert(),
		                                                   csrData.getRADistinguishedName(),
		                                                   csrData.getDaysRA()));

		// Generate SSL cert and keys.
		setKeyPairSSL(KeyPairCreator.getKeyPair(Constants.ALGORITHM, Constants.PROVIDER));
		setSslCert(
				X509V3Certificates.buildIntermediateCert(Constants.SSL, getKeyPairSSL().getPublic(),
				                                         getKeyPairCA().getPrivate(), getCaCert(),
				                                         csrData.getSSLDistinguishedName(),
				                                         csrData.getDaysSSL())
		);

	}

	public KeyPair getKeyPairCA() {
		return keyPairCA;
	}

	public void setKeyPairCA(KeyPair keyPairCA) {
		this.keyPairCA = keyPairCA;
	}

	public KeyPair getKeyPairSSL() {
		return keyPairSSL;
	}

	public void setKeyPairSSL(KeyPair keyPairSSL) {
		this.keyPairSSL = keyPairSSL;
	}

	public KeyPair getKeyPairRA() {
		return keyPairRA;
	}

	public void setKeyPairRA(KeyPair keyPairRA) {
		this.keyPairRA = keyPairRA;
	}

	public X509Certificate getCaCert() {
		return caCert;
	}

	public void setCaCert(X509Certificate caCert) {
		this.caCert = caCert;
	}

	public X509Certificate getSslCert() {
		return sslCert;
	}

	public void setSslCert(X509Certificate sslCert) {
		this.sslCert = sslCert;
	}

	public X509Certificate getRaCert() {
		return raCert;
	}

	public void setRaCert(X509Certificate raCert) {
		this.raCert = raCert;
	}
}

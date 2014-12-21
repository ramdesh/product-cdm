/**
 *  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wso2.emm.apkgenerator.data;

import java.io.Serializable;
import java.security.KeyPair;
import java.security.cert.X509Certificate;

/**
 * Data related to certificates to be generated such as CA,RA and SSL
 * are stored here.
 */
public class CertificateData implements Serializable {

	private static final long serialVersionUID = 4589743573497534L;
	private KeyPair keyPairCA, keyPairRA, keyPairSSL;
	private X509Certificate caCert, raCert, sslCert;

	public KeyPair getKeyPairCA() {
		return keyPairCA;
	}

	public void setKeyPairCA(KeyPair keyPairCA) {
		this.keyPairCA = keyPairCA;
	}

	public KeyPair getKeyPairRA() {
		return keyPairRA;
	}

	public void setKeyPairRA(KeyPair keyPairRA) {
		this.keyPairRA = keyPairRA;
	}

	public KeyPair getKeyPairSSL() {
		return keyPairSSL;
	}

	public void setKeyPairSSL(KeyPair keyPairSSL) {
		this.keyPairSSL = keyPairSSL;
	}

	public X509Certificate getCaCert() {
		return caCert;
	}

	public void setCaCert(X509Certificate caCert) {
		this.caCert = caCert;
	}

	public X509Certificate getRaCert() {
		return raCert;
	}

	public void setRaCert(X509Certificate raCert) {
		this.raCert = raCert;
	}

	public X509Certificate getSslCert() {
		return sslCert;
	}

	public void setSslCert(X509Certificate sslCert) {
		this.sslCert = sslCert;
	}

}

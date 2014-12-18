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
import org.spongycastle.asn1.x509.BasicConstraints;
import org.spongycastle.asn1.x509.Extension;
import org.spongycastle.asn1.x509.KeyUsage;
import org.spongycastle.cert.CertIOException;
import org.spongycastle.cert.X509v3CertificateBuilder;
import org.spongycastle.cert.jcajce.JcaX509CertificateConverter;
import org.spongycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.spongycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.spongycastle.operator.ContentSigner;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.operator.jcajce.JcaContentSignerBuilder;
import org.wso2.emm.apkgenerator.util.Constants;

import javax.security.auth.x500.X500Principal;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Random;

/**
 * Generate X509 V3 certificates. CA, RA and SSL can be generated, where
 * intermediate certificates are signed from the root certificate to generate
 * the chain.
 */
public class CertificateUtil {
	private static final Log LOG = LogFactory.getLog(CertificateUtil.class);

	/**
	 * Generate a self-signed root certificate (CA certificate)
	 *
	 * @param days             that the certificate is valid
	 * @param distinguisedName is the parameters which is found in a CSR.
	 * @param pair             is a key pair generated for CA certificate to use. This is a
	 *                         combination of public and private key.
	 * @return A self-signed CA certificate
	 * @throws ApkGenerationException
	 */
	public static X509Certificate generateCACert(String days, String distinguisedName, KeyPair pair)
			throws ApkGenerationException {

		Random rand = new Random();
		int randomNum = rand.nextInt((100000 - 1000) + 1) + 1000;
		BigInteger serial = BigInteger.valueOf(randomNum);

		X500Principal principal = new X500Principal(distinguisedName);
		X509v3CertificateBuilder certBldr =
				buildX509v3Certificate(principal, days, principal,
				                       serial, pair.getPublic());
		ContentSigner sigGen = null;
		try {
			// Creating a signature and self signing it by signing with it's own key.
			sigGen =
					new JcaContentSignerBuilder(Constants.ENCRIPTION)
							.setProvider(Constants.PROVIDER)
							.build(pair.getPrivate());
			certBldr.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
			return new JcaX509CertificateConverter().setProvider(Constants.PROVIDER)
			                                        .getCertificate(certBldr.build(sigGen));
		} catch (OperatorCreationException e) {
			String message = "Error creating ContentSigner with JcaContentSignerBuilder"
			                 + " with the private key provided.";
			LOG.error(message, e);
			throw new ApkGenerationException(message, e);
		} catch (CertIOException e) {
			String message = "Error adding extension basic constraints";
			LOG.error(message, e);
			throw new ApkGenerationException(message, e);
		} catch (CertificateException e) {
			String message = "Error building certificate.";
			LOG.error(message, e);
			throw new ApkGenerationException(message, e);
		}
	}

	/**
	 * Build intermediate/end entity certificate using the root certificate.
	 *
	 * @param type             of the certificate that needs to be generated. This either RA
	 *                         or SSL
	 * @param publicKey        of the certificate that needs to be created
	 * @param caPrivateKey     is the private key of the root certificate
	 * @param caCert           is the CA certificate generated
	 * @param distinguisedName is the parameters which is found in a CSR.
	 * @param days             that the certificate is valid
	 * @return An intermediate certificate signed by a root certificate
	 * @throws ApkGenerationException
	 */
	public static X509Certificate buildIntermediateCert(String type, PublicKey publicKey,
	                                                    PrivateKey caPrivateKey,
	                                                    X509Certificate caCert,
	                                                    String distinguisedName, String days)
			throws ApkGenerationException {
		X509v3CertificateBuilder certBldr =
				buildX509v3Certificate(caCert.getSubjectX500Principal(),
				                       days,
				                       new X500Principal(
						                       distinguisedName),
				                       BigInteger.valueOf(1), publicKey
				);

		JcaX509ExtensionUtils extUtils;
		ContentSigner signer;
		try {
			extUtils = new JcaX509ExtensionUtils();
		} catch (NoSuchAlgorithmException e) {
			String message = "Cryptographic algorithm is requested but it is not available in " +
			                 "the environment.";
			LOG.error(message, e);
			throw new ApkGenerationException(message, e);
		}
		try {
			if (type.equalsIgnoreCase(Constants.REGISTRATION_AUTHORITY)) {
				certBldr.addExtension(Extension.authorityKeyIdentifier, false,
				                      extUtils.createAuthorityKeyIdentifier(caCert)).
						        addExtension(Extension.subjectKeyIdentifier, false,
						                     extUtils.createSubjectKeyIdentifier(publicKey)).
						        addExtension(Extension.basicConstraints, true,
						                     new BasicConstraints(0)).
						        addExtension(Extension.keyUsage,
						                     true,
						                     new KeyUsage(KeyUsage.digitalSignature |
						                                  KeyUsage.keyCertSign | KeyUsage.cRLSign |
						                                  KeyUsage.dataEncipherment |
						                                  KeyUsage.keyAgreement |
						                                  KeyUsage.keyEncipherment)
						        );
			} else if (type.equalsIgnoreCase(Constants.SSL)) {
				certBldr.addExtension(Extension.authorityKeyIdentifier, false,
				                      extUtils.createAuthorityKeyIdentifier(caCert)).
						        addExtension(Extension.subjectKeyIdentifier, false,
						                     extUtils.createSubjectKeyIdentifier(publicKey)).
						        // Mark it as an end certificate by setting constraint false
								        addExtension(Extension.basicConstraints, true,
								                     new BasicConstraints(false)).
						        addExtension(Extension.keyUsage,
						                     true,
						                     new KeyUsage(KeyUsage.digitalSignature |
						                                  KeyUsage.keyEncipherment)
						        );
			}
		} catch (CertificateEncodingException e) {
			String message = "Certificate Encroding issue while adding extensions.";
			LOG.error(message, e);
			throw new ApkGenerationException(message, e);
		} catch (CertIOException e) {
			String message = "Error adding extension basic constraints.";
			LOG.error(message, e);
			throw new ApkGenerationException(message, e);
		}

		try {
			signer =
					new JcaContentSignerBuilder(Constants.ENCRIPTION)
							.setProvider(Constants.PROVIDER)
							.build(caPrivateKey);
			return new JcaX509CertificateConverter().setProvider(Constants.PROVIDER)
			                                        .getCertificate(certBldr.build(signer));
		} catch (OperatorCreationException e) {
			String message =
					"Error creating ContentSigner with JcaContentSignerBuilder"
					+ " with the private key provided.";
			LOG.error(message, e);
			throw new ApkGenerationException(message, e);
		} catch (CertificateException e) {
			String message = "Error building certificate.";
			LOG.error(message, e);
			throw new ApkGenerationException(message, e);
		}
	}

	/**
	 * Builds a generic {@link X509v3CertificateBuilder}, which can used to
	 * generate a certificate.
	 *
	 * @param rootPrinciple is the {@link X500Principal} that contains the distinguished
	 *                      name of the immediate upper certificate in the certificate
	 *                      chain. In case of self-sign SLL or RA certificate, this is the
	 *                      distinguished name of the CA certificate.
	 * @param days          which the certificate is valid for.
	 * @param principal     is the {@link X500Principal} that contains the distinguished
	 *                      name of the certificate to be generated
	 * @param serial        is a unique number for the self-sign CA
	 * @param publicKey     of the certificate that needs to be created
	 * @return A {@link X509v3CertificateBuilder} that can be used for certificate generation
	 */
	private static X509v3CertificateBuilder buildX509v3Certificate(X500Principal rootPrinciple,
	                                                               String days,
	                                                               X500Principal principal,
	                                                               BigInteger serial,
	                                                               PublicKey publicKey) {
		long millisecondsInADay = 86400000;
		int noOfDays = Integer.parseInt(days);
		// Add days to current time to get the validity period.
		Date validityEndDate = new Date(System.currentTimeMillis() + noOfDays * millisecondsInADay);
		return new JcaX509v3CertificateBuilder(rootPrinciple, serial,
		                                       new Date(System.currentTimeMillis()),
		                                       validityEndDate, principal, publicKey);

	}
}

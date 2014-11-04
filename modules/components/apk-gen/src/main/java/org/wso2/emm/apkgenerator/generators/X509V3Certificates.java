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

import javax.security.auth.x500.X500Principal;

import org.apache.log4j.Logger;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.wso2.emm.apkgenerator.util.Constants;

/**
 * Generate X509 V3 certificates. CA, RA and SSL can be generated, where
 * Intermediate certificates are signed from the root certificate to generate
 * the chain
 */
public class X509V3Certificates {
	private static Logger log = Logger.getLogger(X509V3Certificates.class);

	/**
	 * Generate a self-signed root certificate (CA certificate)
	 * 
	 * @param days
	 *            that the certificate is valid
	 * @param distinguisedName
	 *            is the parameters which is found in a CSR.
	 * @param pair
	 *            is a key pair generated for CA certificate to use. This is a
	 *            combination of public and private key.
	 * @return A self-signed CA certificate
	 */
	public static X509Certificate generateCACert(String days,
			String distinguisedName, KeyPair pair)
			throws CertificateGenerationException {

		Random rand = new Random();
		// avoid negative
		int randomNum = rand.nextInt((100000 - 1000) + 1) + 1000;
		BigInteger serial = BigInteger.valueOf(randomNum);

		X500Principal principal = new X500Principal(distinguisedName);
		X509v3CertificateBuilder certBldr = buildX509v3Certificate(principal,
				days, principal, serial, pair.getPublic());
		ContentSigner sigGen = null;
		try {
			// creating a signature and self signing it by signing with
			// it's own key
			sigGen = new JcaContentSignerBuilder(Constants.ENCRIPTION)
					.setProvider(Constants.PROVIDER).build(pair.getPrivate());
			// make basic constraint true to tell this is a root CA
			certBldr.addExtension(Extension.basicConstraints, true,
					new BasicConstraints(true));
			return new JcaX509CertificateConverter().setProvider(
					Constants.PROVIDER).getCertificate(certBldr.build(sigGen));
		} catch (OperatorCreationException e) {
			log.error(
					"Error creating ContentSigner with JcaContentSignerBuilder"
							+ " with the private key provided", e);
			throw new CertificateGenerationException(
					"Error creating ContentSigner with JcaContentSignerBuilder"
							+ " with the private key provided, "
							+ e.getMessage(), e);
		} catch (CertIOException e) {
			log.error("Error adding extension BasicConstraints", e);
			throw new CertificateGenerationException(
					"Error adding extension BasicConstraints, "
							+ e.getMessage(), e);
		} catch (CertificateException e) {
			log.error("Error building certificate", e);
			throw new CertificateGenerationException(
					"Error building certificate ," + e.getMessage(), e);
		}
	}

	/**
	 * Build intermediate/end entity certificate using the root certificate.
	 * 
	 * @param type
	 *            of the certificate that needs to be generated. This either RA
	 *            or SSL
	 * @param publicKey
	 *            of the certificate that needs to be created
	 * @param caPrivateKey
	 *            is the private key of the root certificate
	 * @param caCert
	 *            is the CA certificate generated
	 * @param distinguisedName
	 *            is the parameters which is found in a CSR.
	 * @param days
	 *            that the certificate is valid
	 * @return An intermediate certificate signed by a root certificate
	 * @throws CertificateGenerationException
	 */
	public static X509Certificate buildIntermediateCert(String type,
			PublicKey publicKey, PrivateKey caPrivateKey,
			X509Certificate caCert, String distinguisedName, String days)
			throws CertificateGenerationException {
		X509v3CertificateBuilder certBldr = buildX509v3Certificate(
				caCert.getSubjectX500Principal(), days, new X500Principal(
						distinguisedName), BigInteger.valueOf(1), publicKey);

		JcaX509ExtensionUtils extUtils = null;
		try {
			extUtils = new JcaX509ExtensionUtils();
		} catch (NoSuchAlgorithmException e) {
			log.error(
					"cryptographic algorithm is requested but"
							+ " it is not available in the environment, "
							+ e.getMessage(), e);
			throw new CertificateGenerationException(
					"cryptographic algorithm is requested but"
							+ " it is not available in the environment, "
							+ e.getMessage(), e);
		}
		try {
			if (type.equalsIgnoreCase(Constants.RA)) {
				certBldr.addExtension(Extension.authorityKeyIdentifier, false,
						extUtils.createAuthorityKeyIdentifier(caCert))
						.addExtension(Extension.subjectKeyIdentifier, false,
								extUtils.createSubjectKeyIdentifier(publicKey))
						// mark it as a intermediate by setting constraint 0
						.addExtension(Extension.basicConstraints, true,
								new BasicConstraints(0))
						.addExtension(
								Extension.keyUsage,
								true,
								new KeyUsage(KeyUsage.digitalSignature
										| KeyUsage.keyCertSign
										| KeyUsage.cRLSign
										| KeyUsage.dataEncipherment
										| KeyUsage.keyAgreement
										| KeyUsage.keyEncipherment));
			} else if (type.equalsIgnoreCase(Constants.SSL)) {
				certBldr.addExtension(Extension.authorityKeyIdentifier, false,
						extUtils.createAuthorityKeyIdentifier(caCert))
						.addExtension(Extension.subjectKeyIdentifier, false,
								extUtils.createSubjectKeyIdentifier(publicKey))
						// mark it as an end certificate by seting constraint
						// false
						.addExtension(Extension.basicConstraints, true,
								new BasicConstraints(false))
						.addExtension(
								Extension.keyUsage,
								true,
								new KeyUsage(KeyUsage.digitalSignature
										| KeyUsage.keyEncipherment));
			}
		} catch (CertificateEncodingException e) {
			log.error("Certificate Encroding issue while adding extensions, "
					+ e.getMessage(), e);
			throw new CertificateGenerationException(
					"Certificate Encroding issue while adding extensions, "
							+ e.getMessage(), e);
		} catch (CertIOException e) {
			log.error(
					"Error adding extension BasicConstraints, "
							+ e.getMessage(), e);
			throw new CertificateGenerationException(
					"Error adding extension BasicConstraints, "
							+ e.getMessage(), e);
		}

		ContentSigner signer = null;
		try {
			signer = new JcaContentSignerBuilder(Constants.ENCRIPTION)
					.setProvider(Constants.PROVIDER)// sign with CA
					.build(caPrivateKey);
			return new JcaX509CertificateConverter().setProvider(
					Constants.PROVIDER).getCertificate(certBldr.build(signer));
		} catch (OperatorCreationException e) {
			log.error(
					"Error creating ContentSigner with JcaContentSignerBuilder"
							+ " with the private key provided ,"
							+ e.getMessage(), e);
			throw new CertificateGenerationException(
					"Error creating ContentSigner with JcaContentSignerBuilder"
							+ " with the private key provided ,"
							+ e.getMessage(), e);
		} catch (CertificateException e) {
			log.error("Error building certificate, " + e.getMessage(), e);
			throw new CertificateGenerationException(
					"Error building certificate, " + e.getMessage(), e);
		}
	}

	/**
	 * Builds a generic {@link X509v3CertificateBuilder}, which can used to
	 * generate a certificate
	 * 
	 * @param rootPrinciple
	 *            is the {@link X500Principal} that contains the distinguished
	 *            name of the immediate upper certificate in the certificate
	 *            chain. In case of self-sign SLL or RA certificate, this is the
	 *            distinguished name of the CA certificate
	 * @param days
	 *            which the certificate is valid for.
	 * @param principal
	 *            is the {@link X500Principal} that contains the distinguished
	 *            name of the certificate to be generated
	 * @param serial
	 *            is a unique number for the self-sign CA
	 * @param publicKey
	 *            of the certificate that needs to be created
	 * @return
	 */
	private static X509v3CertificateBuilder buildX509v3Certificate(
			X500Principal rootPrinciple, String days, X500Principal principal,
			BigInteger serial, PublicKey publicKey) {
		long milisecondsInADay = 86400000;
		// certificate is valid from now
		Date validityBeginDate = new Date(System.currentTimeMillis());
		int noOfDays = Integer.parseInt(days);
		// Add days to current time to get the validity period
		Date validityEndDate = new Date(System.currentTimeMillis() + noOfDays
				* milisecondsInADay);
		return new JcaX509v3CertificateBuilder(rootPrinciple, serial,
				validityBeginDate, validityEndDate, principal, publicKey);

	}
}


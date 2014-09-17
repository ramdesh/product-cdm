package org.wso2.apkgenerator.generators;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.x500.X500Name;
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
import org.wso2.apkgenerator.util.Constants;

//Generate X509V3 certificates. CA, RA and SSL
public class X509V3Certificates {
	private static Log log = LogFactory.getLog(X509V3Certificates.class);

	// Generate a self signed root certificate. CA certificate
	public static X509Certificate generateCACert(String days,
			String distinguisedName, KeyPair pair) {

		Random rand = new Random();
		// avoid negative
		int randomNum = rand.nextInt((100000 - 1000) + 1) + 1000;
		BigInteger serial = BigInteger.valueOf(randomNum);

		X500Principal principal = new X500Principal(distinguisedName);
		X509v3CertificateBuilder certBldr = buildX509v3Certificate(principal,
				days, principal, serial, pair.getPublic());
		ContentSigner sigGen = null;
		try {
			// creating a signatture and self signing it by signing with
			// it's own key
			sigGen = new JcaContentSignerBuilder(Constants.ENCRIPTION)
					.setProvider(Constants.PROVIDER).build(pair.getPrivate());
		} catch (OperatorCreationException e) {
			log.error(
					"Error creating ContentSigner with JcaContentSignerBuilder"
							+ " with the private key provided", e);
		}
		try {
			// make basic constraint to tell this is a root CA
			certBldr.addExtension(Extension.basicConstraints, true,
					new BasicConstraints(true));
		} catch (CertIOException e1) {
			log.error("Error adding extension BasicConstraints", e1);
		}
		try {
			return new JcaX509CertificateConverter().setProvider(
					Constants.PROVIDER).getCertificate(certBldr.build(sigGen));
		} catch (CertificateException e) {
			log.error("Error building certificate", e);
		}

		return null;
	}

	// build intermediate/end entity certificate using CA
	public static X509Certificate buildIntermediateCert(String type,
			PublicKey publicKey, PrivateKey caKey, X509Certificate caCert,
			String distinguisedName, String days) {
		X509v3CertificateBuilder certBldr = buildX509v3Certificate(
				caCert.getSubjectX500Principal(), days, new X500Principal(
						distinguisedName), BigInteger.valueOf(1), publicKey);

		JcaX509ExtensionUtils extUtils = null;
		try {
			extUtils = new JcaX509ExtensionUtils();
		} catch (NoSuchAlgorithmException e1) {
			log.error("cryptographic algorithm is requested but"
					+ " it is not available in the environment", e1);
		}
		try {
			if (type.equalsIgnoreCase("RA")) {
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
			} else if (type.equalsIgnoreCase("SSL")) {
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
		} catch (CertificateEncodingException e2) {
			log.error("Certificate Encroding issue while adding extensions", e2);
		} catch (CertIOException e) {
			log.error("Error adding extension BasicConstraints", e);
		}

		ContentSigner signer = null;
		try {
			signer = new JcaContentSignerBuilder(Constants.ENCRIPTION)
					.setProvider(Constants.PROVIDER)// sign with CA
					.build(caKey);
		} catch (OperatorCreationException e) {
			log.error(
					"Error creating ContentSigner with JcaContentSignerBuilder"
							+ " with the private key provided", e);
		}
		try {
			return new JcaX509CertificateConverter().setProvider(
					Constants.PROVIDER).getCertificate(certBldr.build(signer));
		} catch (CertificateException e) {
			log.error("Error building certificate", e);
		}
		return null;
	}

	private static X509v3CertificateBuilder buildX509v3Certificate(
			X500Principal rootPrinciple, String days, X500Principal principal,
			BigInteger serial, PublicKey publicKey) {
		int milisecondsInADay = 24 * 60 * 60 * 1000;
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

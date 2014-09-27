package org.wso2.apkgenerator.generators;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Random;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.wso2.apkgenerator.util.Constants;



public class Cert {
	public static X509Certificate generateCert(String days, String distinguisedName, KeyPair pair) {
		System.out.println("generateCert");
		X509Certificate cert = null;
		//1  hour before
		Date validityBeginDate = new Date(System.currentTimeMillis() - 60 * 60 * 1000);
		int noOfDays = Integer.parseInt(days);
		//Add days to current time to get the validity period
		Date validityEndDate = new Date(System.currentTimeMillis() + noOfDays * 24 * 60 * 60 *
		                                1000);

		Random rand = new Random();
		int randomNum = rand.nextInt((100000 - 1000) + 1) + 1000;
		BigInteger serial = BigInteger.valueOf(System.currentTimeMillis() + randomNum);

		X500Principal principal = new X500Principal(distinguisedName);
		X509v3CertificateBuilder certBldr = new JcaX509v3CertificateBuilder(principal, serial,
		                                                                    validityBeginDate,
		                                                                    validityEndDate,
		                                                                    principal,
		                                                                    pair.getPublic());

		ContentSigner sigGen = null;
		try {
            sigGen = new JcaContentSignerBuilder(Constants.ENCRIPTION)// creating a signature and self sign it
            .setProvider(Constants.PROVIDER).build(pair.getPrivate());// by signing with its own key
        } catch (OperatorCreationException e) {
        }
		try {
			//make basic constraint to tell this is a root CA
            certBldr.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
        } catch (CertIOException e1) {
        }
		try {
            cert = new JcaX509CertificateConverter().setProvider(Constants.PROVIDER)
                                                    .getCertificate(certBldr.build(sigGen));
        } catch (CertificateException e) {
        }

	return cert;
	}
}
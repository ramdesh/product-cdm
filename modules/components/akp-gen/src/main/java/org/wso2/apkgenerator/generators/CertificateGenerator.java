package org.wso2.apkgenerator.generators;

import java.security.cert.X509Certificate;
import java.security.KeyPair;
import java.security.Security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.wso2.apkgenerator.data.CSRData;
import org.wso2.apkgenerator.data.DistinguishName;
import org.wso2.apkgenerator.util.Constants;
import org.wso2.apkgenerator.util.FileOperator;

/*This class coordinates the certificate generation process which must happen
 * one after the other*/
public class CertificateGenerator {

	private DistinguishName dn;
	private CSRData csrDataSrc;
	public KeyPair keyPairCA, keyPairRA, keyPairSSL;
	public X509Certificate caCert, raCert, sslCert;
	private static Log log = LogFactory.getLog(CertificateGenerator.class);
	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public CertificateGenerator(CSRData csrData) {
		csrDataSrc = csrData;
		dn = new DistinguishName(csrData);// pass data to initialize
											// distinguished names
	}

	public boolean generate() {
		// generate CA cert and keys
		keyPairCA = KeyPairCreator.getKeyPair();
		// Cert.generateCert(csrDataSrc.getDaysCA(), dn.getCAName(),
		// keyPairCA);
		caCert = X509V3Certificates.generateCACert(csrDataSrc.getDaysCA(),
				dn.getCAName(), keyPairCA);

		// generate RA cert and keys
		keyPairRA = KeyPairCreator.getKeyPair();
		raCert = X509V3Certificates.buildIntermediateCert("RA",
				keyPairRA.getPublic(), keyPairCA.getPrivate(), caCert,
				dn.getRAName(), csrDataSrc.getDaysRA());

		// generate SSL cert and keys
		keyPairSSL = KeyPairCreator.getKeyPair();
		sslCert = X509V3Certificates.buildIntermediateCert("SSL",keyPairSSL.getPublic(),
				keyPairCA.getPrivate(), caCert, dn.getSSLName(),
				csrDataSrc.getDaysSSL());
		if (keyPairCA == null || caCert == null || keyPairRA == null
				|| raCert == null || keyPairSSL == null || sslCert == null) {
			return false;
		}
		//CA certificate is written to a file for later usage
		if(!FileOperator.writePem(APKGenerator.workingDir + Constants.PEM_file, caCert)){
			return false;
		}

        /*
        //This section can be enabled if it is necessery to write, certificates to actual file
		FileOperator.writePem(APKGenerator.workingDir+ "ca.crt",caCert);
		FileOperator.writePem(APKGenerator.workingDir+
		"ca_private.pem",keyPairCA.getPrivate());
		FileOperator.writePem(APKGenerator.workingDirR+
		"ca_private.key",keyPairCA.getPrivate());

		FileOperator.writePem(APKGenerator.workingDir+ "ra_cert.pem",raCert);
		FileOperator.writePem(APKGenerator.workingDir+
		"ra_private.pem",keyPairRA.getPrivate());

		FileOperator.writePem(APKGenerator.workingDir+ "ia.crt",sslCert);
		FileOperator.writePem(APKGenerator.workingDir+
		"ia.key",keyPairSSL.getPrivate());
		*/
		return true;
	}

}

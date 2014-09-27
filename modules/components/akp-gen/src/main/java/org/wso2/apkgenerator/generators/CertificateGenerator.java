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

	public void generator() throws Exception {
		System.out.println("generator");
		Security.addProvider(new BouncyCastleProvider());
		System.out.println("generator2");
		//generate CA cert and keys
		keyPairCA = KeyPairCreator.getKeyPair();
		System.out.println("generator3");
		try{
//			caCert =	Cert.generateCert(csrDataSrc.getDaysCA(),dn.getCAName(), keyPairCA);
		caCert = X509V3Certificates.generateCert(csrDataSrc.getDaysCA(),dn.getCAName(), keyPairCA);
		}
		catch(Exception e){
			System.out.println("ee"+e.getMessage());
		}
		System.out.println("generator4");
		
		//generate RA cert and keys
		keyPairRA = KeyPairCreator.getKeyPair();
	    raCert = X509V3Certificates.buildIntermediateCert(keyPairRA.getPublic(), keyPairCA.getPrivate(),
	                                                     caCert,dn.getRAName(),csrDataSrc.getDaysRA());		
	    //generate SSL cert and keys
	    keyPairSSL = KeyPairCreator.getKeyPair();
	    sslCert = X509V3Certificates.buildEndEntityCert(keyPairSSL.getPublic(), keyPairCA.getPrivate(), 
	                                                   caCert,dn.getSSLName(),csrDataSrc.getDaysSSL());
	    
	    
	    
	    FileOperator.writePem(APKGenerator.workingDir+ Constants.PEM_file,caCert);
//	    FileOperator.writePem(Constants.WORKING_DIR+ "ca.crt",caCert);
//	    FileOperator.writePem(Constants.WORKING_DIR+ "ca_private.pem",keyPairCA.getPrivate());
//	    FileOperator.writePem(Constants.WORKING_DIR+ "ca_private.key",keyPairCA.getPrivate());
//	    
//	    FileOperator.writePem(Constants.WORKING_DIR+ "ra_cert.pem",raCert);
//	    FileOperator.writePem(Constants.WORKING_DIR+ "ra_private.pem",keyPairRA.getPrivate());
//	    
//	    FileOperator.writePem(Constants.WORKING_DIR+ "ia.crt",sslCert);
//	    FileOperator.writePem(Constants.WORKING_DIR+ "ia.key",keyPairSSL.getPrivate());
	}
	

	
	
}
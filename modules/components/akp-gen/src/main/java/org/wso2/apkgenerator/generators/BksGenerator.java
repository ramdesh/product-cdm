package org.wso2.apkgenerator.generators;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.wso2.apkgenerator.data.ObjectReader;
import org.wso2.apkgenerator.util.Constants;

public class BksGenerator {
	private static Log log = LogFactory.getLog(BksGenerator.class);
	
	public static boolean generateBKS(X509Certificate caCert){		
	    KeyStore keystore;
        try {
        	
    	    Provider bcProvider = new BouncyCastleProvider(); 
    	    keystore = KeyStore.getInstance("BKS",bcProvider);
    	    keystore.load(null);
    	    keystore.setCertificateEntry("cert-alias", caCert);
    	    
    	    FileOutputStream fos = new FileOutputStream(APKGenerator.workingDir+Constants.BKS_File);
            keystore.store(fos, APKGenerator.truststorePassword.toCharArray());
            fos.close();
            return true;
        } 
	 catch (NoSuchAlgorithmException e1) {
		log.error("cryptographic algorithm is requested but"
				+ " it is not available in the environment", e1);
	} catch (CertificateException e2) {
		log.error("Error building certificate", e2);
	} catch (IOException e3) {
		log.error("file error while working with files", e3);
	} catch (KeyStoreException e4) {
		log.error("generic KeyStore exception working with BKS ", e4);
	}
        
        catch (Exception e4) {
        	log.error("Error while generating BKS ", e4);
		}
		return false;
       

	   // return context.getSocketFactory();
	}
}

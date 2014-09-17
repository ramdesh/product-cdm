package org.wso2.apkgenerator.generators;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.wso2.apkgenerator.data.ObjectReader;
import org.wso2.apkgenerator.util.Constants;
/*Private and public key pair generation is handled by this class*/
public class KeyPairCreator {
	private static Log log = LogFactory.getLog(KeyPairCreator.class);
	
	public static KeyPair getKeyPair(){
		if (log.isDebugEnabled()) {
			log.debug("generating key pair");
		}
    	Security.addProvider(new BouncyCastleProvider());
    	KeyPairGenerator keyPairGenerator;
    	KeyPair keyPair=null;
    	try {
	        keyPairGenerator = KeyPairGenerator.getInstance(Constants.ALGORITHM, Constants.PROVIDER);
	        keyPairGenerator.initialize(1024, new SecureRandom());
	        keyPair = keyPairGenerator.generateKeyPair();
	        return keyPair;
        } catch (NoSuchAlgorithmException e) {
        	log.error(Constants.ALGORITHM+" cryptographic algorithm is requested but" +
        			" it is not available in the environment", e);
        } catch (NoSuchProviderException e) {
        	log.error(Constants.PROVIDER+" security provider is requested but it is not available in " +
        			"the environment. ", e);
        }catch (Exception e) {
        	log.error("Error while generating keys", e);
        }
		return null;
	    
    }
}

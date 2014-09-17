package org.wso2.apkgenerator.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/*
 * Distinguish names necessary to create certificates are generated from the 
 * CSR data
 */
public class DistinguishName {
	CSRData names;
	private static Log log = LogFactory.getLog(DistinguishName.class);

	public DistinguishName(CSRData source) {
		names = source;
	}

	public String getCAName() {

		String dnCA =
		              "C=" + names.getCountryCA() + ", ST=" + names.getStateCA() + ", L=" +
		                      names.getLocalityCA() + ", O=" + names.getOrganizationCA() + ", OU=" +
		                      names.getOrganizationUCA() + ", CN=" + names.getCommonNameCA();
		if (log.isDebugEnabled()) {
			log.debug("CA Distinguish Name: "+dnCA);
		}
		return dnCA;

	}

	public String getRAName() {
		String dnRA =
		              "C=" + names.getCountryRA() + ", ST=" + names.getStateRA() + ", L=" +
		                      names.getLocalityRA() + ", O=" + names.getOrganizationRA() + ", OU=" +
		                      names.getOrganizationURA() + ", CN=" + names.getCommonNameRA();
		if (log.isDebugEnabled()) {
			log.debug("RA Distinguish Name: "+dnRA);
		}
		return dnRA;
	}

	public String getSSLName() {
		String dnSSL =
		               "C=" + names.getCountrySSL() + ", ST=" + names.getStateSSL() + ", L=" +
		                       names.getLocalitySSL() + ", O=" + names.getOrganizationSSL() +
		                       ", OU=" + names.getOrganizationUSSL() + ", CN=" +
		                       names.getCommonNameSSL();
		if (log.isDebugEnabled()) {
			log.debug("SSL Distinguish Name: "+dnSSL);
		}
		return dnSSL;
	}

}

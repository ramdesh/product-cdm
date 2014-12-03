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
package org.wso2.emm.apkgenerator.data;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.wso2.emm.apkgenerator.generators.CertificateGenerationException;
import org.wso2.emm.apkgenerator.util.Constants;

/**
 * Store data needed to create certificates. Using these data, distinguished
 * named for certificates can be created
 * 
 */
public class CSRData implements Serializable {

	private static final long serialVersionUID = 1L;
	String countryCA, stateCA, localityCA, organizationCA, organizationUCA, daysCA, commonNameCA;
	String countryRA, stateRA, localityRA, organizationRA, organizationURA, daysRA, commonNameRA;
	String countrySSL, stateSSL, localitySSL, organizationSSL, organizationUSSL, daysSSL, serverIp;
	private static final String COMMON_NAME_KEY = ", CN=";
	private static final String ORGANIZATION_UNIT_KEY = ", OU=";
	private static final String ORGANIZATION_KEY = ", O=";
	private static final String LOCALITY_KEY = ", L=";
	private static final String STATE_KEY = ", ST=";
	private static final String COUNTRY_KEY = "C=";
	private static Logger log = Logger.getLogger(CSRData.class);

	/**
	 * Read the Object passed and create a CRS object to hold data required
	 * during certificate generation.
	 * 
	 * @param {@link ObjectReader} reader holds the parameters necessary to
	 *        create CSR data
	 * @throws CertificateGenerationException
	 */
	public CSRData(ObjectReader reader) throws CertificateGenerationException {
		setCountryCA(reader.read(Constants.COUNTRY_CA));
		setStateCA(reader.read(Constants.STATE_CA));
		setLocalityCA(reader.read(Constants.LOCALITY_CA));
		setOrganizationCA(reader.read(Constants.ORGANIZATION_CA));
		setOrganizationUCA(reader.read(Constants.ORGANIZATION_UNIT_CA));
		setDaysCA(reader.read(Constants.DAYS_CA));
		setCommonNameCA(reader.read(Constants.COMMON_NAME_CA));
		setCountryRA(reader.read(Constants.COUNTRY_RA));
		setStateRA(reader.read(Constants.STATE_RA));
		setLocalityRA(reader.read(Constants.LOCALITY_RA));
		setOrganizationRA(reader.read(Constants.ORGANIZATION_RA));
		setOrganizationURA(reader.read(Constants.ORGANIZATION_UNIT_RA));
		setDaysRA(reader.read(Constants.DAYS_RA));
		setCommonNameRA(reader.read(Constants.COMMON_NAME_RA));
		setCountrySSL(reader.read(Constants.COUNTRY_SSL));
		setStateSSL(reader.read(Constants.STATE_SSL));
		setLocalitySSL(reader.read(Constants.LOCALITY_SSL));
		setOrganizationSSL(reader.read(Constants.ORGANIZATION_SSL));
		setOrganizationUSSL(reader.read(Constants.ORGANIZATION_UNIT_SSL));
		setDaysSSL(reader.read(Constants.DAYS_SSL));
		setCommonNameSSL(reader.read(Constants.SERVER_IP));

	}

	public String getCountryCA() {
		return countryCA;
	}

	public void setCountryCA(String countryCA) {
		this.countryCA = countryCA;
	}

	public String getStateCA() {
		return stateCA;
	}

	public void setStateCA(String stateCA) {
		this.stateCA = stateCA;
	}

	public String getLocalityCA() {
		return localityCA;
	}

	public void setLocalityCA(String localityCA) {
		this.localityCA = localityCA;
	}

	public String getOrganizationCA() {
		return organizationCA;
	}

	public void setOrganizationCA(String organizationCA) {
		this.organizationCA = organizationCA;
	}

	public String getOrganizationUCA() {
		return organizationUCA;
	}

	public void setOrganizationUCA(String organizationUCA) {
		this.organizationUCA = organizationUCA;
	}

	public String getDaysCA() {
		return daysCA;
	}

	public void setDaysCA(String daysCA) {
		this.daysCA = daysCA;
	}

	public String getCommonNameCA() {
		return commonNameCA;
	}

	public void setCommonNameCA(String commonNameCA) {
		this.commonNameCA = commonNameCA;
	}

	public String getCountryRA() {
		return countryRA;
	}

	public void setCountryRA(String countryRA) {
		this.countryRA = countryRA;
	}

	public String getStateRA() {
		return stateRA;
	}

	public void setStateRA(String stateRA) {
		this.stateRA = stateRA;
	}

	public String getLocalityRA() {
		return localityRA;
	}

	public void setLocalityRA(String localityRA) {
		this.localityRA = localityRA;
	}

	public String getOrganizationRA() {
		return organizationRA;
	}

	public void setOrganizationRA(String organizationRA) {
		this.organizationRA = organizationRA;
	}

	public String getOrganizationURA() {
		return organizationURA;
	}

	public void setOrganizationURA(String organizationURA) {
		this.organizationURA = organizationURA;
	}

	public String getDaysRA() {
		return daysRA;
	}

	public void setDaysRA(String daysRA) {
		this.daysRA = daysRA;
	}

	public String getCommonNameRA() {
		return commonNameRA;
	}

	public void setCommonNameRA(String commonNameRA) {
		this.commonNameRA = commonNameRA;
	}

	public String getCountrySSL() {
		return countrySSL;
	}

	public void setCountrySSL(String countrySSL) {
		this.countrySSL = countrySSL;
	}

	public String getStateSSL() {
		return stateSSL;
	}

	public void setStateSSL(String stateSSL) {
		this.stateSSL = stateSSL;
	}

	public String getLocalitySSL() {
		return localitySSL;
	}

	public void setLocalitySSL(String localitySSL) {
		this.localitySSL = localitySSL;
	}

	public String getOrganizationSSL() {
		return organizationSSL;
	}

	public void setOrganizationSSL(String organizationSSL) {
		this.organizationSSL = organizationSSL;
	}

	public String getOrganizationUSSL() {
		return organizationUSSL;
	}

	public void setOrganizationUSSL(String organizationUSSL) {
		this.organizationUSSL = organizationUSSL;
	}

	public String getDaysSSL() {
		return daysSSL;
	}

	public void setDaysSSL(String daysSSL) {
		this.daysSSL = daysSSL;
	}

	public String getCommonNameSSL() {
		return serverIp;
	}

	public void setCommonNameSSL(String serverIp) {
		this.serverIp = serverIp;
	}

	/**
	 * @return distinguished name for CA certificate generated using CRS data
	 */
	public String getCADistinguishedName() {
		StringBuilder builder = new StringBuilder();
		builder.append(COUNTRY_KEY);
		builder.append(getCountryCA());
		builder.append(STATE_KEY);
		builder.append(getStateCA());
		builder.append(LOCALITY_KEY);
		builder.append(getLocalityCA());
		builder.append(ORGANIZATION_KEY);
		builder.append(getOrganizationCA());
		builder.append(ORGANIZATION_UNIT_KEY);
		builder.append(getOrganizationUCA());
		builder.append(COMMON_NAME_KEY);
		builder.append(getCommonNameCA());
		String distinguishedNameCA = builder.toString();
		if (log.isDebugEnabled()) {
			log.debug("CA Distinguish Name: " + distinguishedNameCA);
		}
		return distinguishedNameCA;

	}

	/**
	 * @return distinguished name for RA certificate generated using CRS data
	 */
	public String getRADistinguishedName() {
		StringBuilder builder = new StringBuilder();
		builder.append(COUNTRY_KEY);
		builder.append(getCountryRA());
		builder.append(STATE_KEY);
		builder.append(getStateRA());
		builder.append(LOCALITY_KEY);
		builder.append(getLocalityRA());
		builder.append(ORGANIZATION_KEY);
		builder.append(getOrganizationRA());
		builder.append(ORGANIZATION_UNIT_KEY);
		builder.append(getOrganizationURA());
		builder.append(COMMON_NAME_KEY);
		builder.append(getCommonNameRA());
		String distinguishedNameRA = builder.toString();
		if (log.isDebugEnabled()) {
			log.debug("RA Distinguish Name: " + distinguishedNameRA);
		}
		return distinguishedNameRA;
	}

	/**
	 * @return distinguished name for SSL certificate generated using CRS data
	 */
	public String getSSLDistinguishedName() {
		StringBuilder builder = new StringBuilder();
		builder.append(COUNTRY_KEY);
		builder.append(getCountrySSL());
		builder.append(STATE_KEY);
		builder.append(getStateSSL());
		builder.append(LOCALITY_KEY);
		builder.append(getLocalitySSL());
		builder.append(ORGANIZATION_KEY);
		builder.append(getOrganizationSSL());
		builder.append(ORGANIZATION_UNIT_KEY);
		builder.append(getOrganizationUSSL());
		builder.append(COMMON_NAME_KEY);
		builder.append(getCommonNameSSL());
		String distinguishedNameSSL = builder.toString();
		if (log.isDebugEnabled()) {
			log.debug("SSL Distinguish Name: " + distinguishedNameSSL);
		}
		return distinguishedNameSSL;
	}

}

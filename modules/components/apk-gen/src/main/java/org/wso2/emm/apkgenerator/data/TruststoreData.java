/**
 *  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wso2.emm.apkgenerator.data;

import org.wso2.emm.apkgenerator.generators.ApkGenerationException;
import org.wso2.emm.apkgenerator.util.Constants;

import java.io.Serializable;

/**
 * Details related to truststore data that are useful when injecting certificates
 * to relevant stores.
 */
public class TruststoreData implements Serializable {

	private static final long serialVersionUID = 33793037383988323L;
	private String passwordPK12CA;
	private String passwordPK12RA;
	private String aliasPK12CA;
	private String aliasPK12RA;
	private String passwordWSO2EMMJKS;
	private String aliasClientTruststore;
	private String passwordClientTruststore;
	private String aliasWSO2Carbon;
	private String passwordWSO2Carbon;

	public TruststoreData(ObjectReader reader) throws ApkGenerationException {
		setPasswordPK12CA(reader.read(Constants.TruststoreKeys.PASSWORD_PK12_CA));
		setPasswordPK12RA(reader.read(Constants.TruststoreKeys.PASSWORD_PK12_RA));
		setAliasPK12CA(reader.read(Constants.TruststoreKeys.ALIAS_PK12_CA));
		setAliasPK12RA(reader.read(Constants.TruststoreKeys.ALIAS_PK12_RA));
		setPasswordWSO2EMMJKS(reader.read(Constants.TruststoreKeys.PASSWORD_WSO2_EMM_JKS));
		setAliasClientTruststore(reader.read(Constants.TruststoreKeys.ALIAS__CLIENT_TRUSTSTORE));
		setPasswordClientTruststore(
				reader.read(Constants.TruststoreKeys.PASSWORD_CLIENT_TRUSTSTORE));
		setAliasWSO2Carbon(reader.read(Constants.TruststoreKeys.ALIAS_WSO2_CARBON));
		setPasswordWSO2Carbon(reader.read(Constants.TruststoreKeys.PASSWORD_WSO2_CARBON));
	}

	public String getPasswordPK12CA() {
		return passwordPK12CA;
	}

	public void setPasswordPK12CA(String passwordPK12CA) {
		this.passwordPK12CA = passwordPK12CA;
	}

	public String getPasswordPK12RA() {
		return passwordPK12RA;
	}

	public void setPasswordPK12RA(String passwordPK12RA) {
		this.passwordPK12RA = passwordPK12RA;
	}

	public String getAliasPK12CA() {
		return aliasPK12CA;
	}

	public void setAliasPK12CA(String aliasPK12CA) {
		this.aliasPK12CA = aliasPK12CA;
	}

	public String getAliasPK12RA() {
		return aliasPK12RA;
	}

	public void setAliasPK12RA(String aliasPK12RA) {
		this.aliasPK12RA = aliasPK12RA;
	}

	public String getPasswordWSO2EMMJKS() {
		return passwordWSO2EMMJKS;
	}

	public void setPasswordWSO2EMMJKS(String passwordWSO2EMMJKS) {
		this.passwordWSO2EMMJKS = passwordWSO2EMMJKS;
	}

	public String getAliasClientTruststore() {
		return aliasClientTruststore;
	}

	public void setAliasClientTruststore(String aliasClientTruststore) {
		this.aliasClientTruststore = aliasClientTruststore;
	}

	public String getPasswordClientTruststore() {
		return passwordClientTruststore;
	}

	public void setPasswordClientTruststore(String passwordClientTruststore) {
		this.passwordClientTruststore = passwordClientTruststore;
	}

	public String getAliasWSO2Carbon() {
		return aliasWSO2Carbon;
	}

	public void setAliasWSO2Carbon(String aliasWSO2Carbon) {
		this.aliasWSO2Carbon = aliasWSO2Carbon;
	}

	public String getPasswordWSO2Carbon() {
		return passwordWSO2Carbon;
	}

	public void setPasswordWSO2Carbon(String passwordWSO2Carbon) {
		this.passwordWSO2Carbon = passwordWSO2Carbon;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}

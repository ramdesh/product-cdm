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
package org.wso2.emm.bam.util;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.emm.bam.PublisherException;

@XmlRootElement(name = "EnterpriseMobilityManager")
public class Configurations {

	private static volatile Configurations configInstance = null;
	private static final String EMM_CONFIG_XML = "emm-config.xml";
	public static final String RECIEVER_URL_BAM = "RecieverUrlBAM";
	public static final String BAM_USERNAME = "BAMUsername";
	public static final String BAM_PASSWORD = "BAMPassword";
	public static final String BAM_CONFIGURATIONS = "BAMConfigurations";
	private static final Log logger = LogFactory.getLog(Configurations.class);

	private Configurations() throws JAXBException {
	}

	private static Configurations readXml() throws PublisherException {
		File file =
		            new File(CarbonUtils.getCarbonConfigDirPath() + File.separator + EMM_CONFIG_XML);
		JAXBContext jaxbContext = null;
		try {
			jaxbContext = JAXBContext.newInstance(Configurations.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			return (Configurations) jaxbUnmarshaller.unmarshal(file);
		} catch (JAXBException e) {
			String message = "error while initializing Configurations";
			logger.error(message, e);
			throw new PublisherException(message, e);
		}
	}

	/**
	 * @return An instance of {@link Configurations} that can be used to
	 *         retrieve configuration values.
	 * @throws PublisherException
	 */
	public static Configurations getInstance() throws PublisherException {
		if (configInstance == null) {
			synchronized (Configurations.class) {
				if (configInstance == null) {
					configInstance = readXml();
				}
			}
		}
		return configInstance;
	}

	private BAMConfiguration BAMConfigurations;

	public BAMConfiguration getBAMConfigurations() {
		return BAMConfigurations;
	}

	@XmlElement(name = BAM_CONFIGURATIONS)
	public void setBAMConfigurations(BAMConfiguration DeviceMonitorFrequency) {
		this.BAMConfigurations = DeviceMonitorFrequency;
	}

	public static class BAMConfiguration {

		String recieverUrlBAM;
		String username;
		String password;

		public String getRecieverUrlBAM() {
			return recieverUrlBAM;
		}

		@XmlElement(name = RECIEVER_URL_BAM)
		public void setRecieverUrlBAM(String recieverUrlBAM) {
			this.recieverUrlBAM = recieverUrlBAM;
		}

		public String getBAMUsername() {
			return username;
		}

		@XmlElement(name = BAM_USERNAME)
		public void setBAMUsername(String username) {
			this.username = username;
		}

		public String getBAMPassword() {
			return password;
		}

		@XmlElement(name = BAM_PASSWORD)
		public void setBAMPassword(String password) {
			this.password = password;
		}

	}
}
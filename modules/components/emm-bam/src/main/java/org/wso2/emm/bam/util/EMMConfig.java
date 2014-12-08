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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.SystemProperties;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.wso2.emm.bam.PublisherException;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Read configuration values relevant to BAM publisher from EMM configuration
 * file
 * 
 */
public class EMMConfig {

	private static final String CONF_LOCATION = "conf.location";
	private static final String EMM_CONFIG_XML = "emm-config.xml";

	private static final String DeviceMonitorFrequency = "DeviceMonitorFrequency";
	private static final String BAM_PATH = "BAMPath";
	private static final String TRUSTSTORE_PASSWORD = "TrustStorePassword";
	private static EMMConfig emmTaskConfig;
	private static Map<String, String> configMap;
	private static String[] configEntryNames = { Constants.RECIEVER_URL_BAM,
	                                            DeviceMonitorFrequency, Constants.BAM_USERNAME,
	                                            Constants.BAM_PASSWORD, BAM_PATH,
	                                            TRUSTSTORE_PASSWORD };
	private static final Log logger = LogFactory.getLog(EMMConfig.class);
	private static volatile EMMConfig configInstance = null;

	private EMMConfig() {
	}

	/**
	 * @return An instance of {@link EMMConfig} that can be used to retrieve
	 *         configuration values.
	 */
	public static EMMConfig getInstance() {
		if (configInstance == null) {
			synchronized (EMMConfig.class) {
				if (configInstance == null) {
					configInstance = new EMMConfig();
				}
			}
		}
		return configInstance;
	}

	/**
	 * This function reads the EMM Configurations XML file and load it to a Map.
	 * 
	 * @return A {@link Map} of {@link String} key-value, representing the
	 *         configuration file
	 * @throws PublisherException
	 */
	private static synchronized Map<String, String> readEMMConfigurationXML()
	                                                                         throws PublisherException {
		String confLocation =
		                      SystemProperties.getProperty(CONF_LOCATION) + File.separator +
		                              EMM_CONFIG_XML;

		if (emmTaskConfig == null) {
			emmTaskConfig = new EMMConfig();
			configMap = new HashMap<String, String>();

			Document document = null;

			File fXmlFile = new File(confLocation);
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder;
			try {
				documentBuilder = documentBuilderFactory.newDocumentBuilder();
				document = documentBuilder.parse(fXmlFile);
			} catch (ParserConfigurationException e) {
				logger.error("Serious configuration error. ", e);
				throw new PublisherException("Serious configuration error ", e);
			} catch (SAXException e) {
				logger.error("Error parsing configuration file", e);
				throw new PublisherException("Error parsing configuration file ", e);
			} catch (IOException e) {
				logger.error("Error opening file", e);
				throw new PublisherException("Error opening file", e);
			}

			for (String configEntry : configEntryNames) {
				NodeList elements = document.getElementsByTagName(configEntry);
				if (elements != null && elements.getLength() > 0) {
					configMap.put(configEntry, elements.item(0).getTextContent());
				}
			}
		}
		return configMap;
	}

	/**
	 * Read the parameters from the configuration file, when the key needs to be
	 * read is provided.
	 * 
	 * @param key
	 *            is the that needs to be extracted from the configuration file
	 * @return {@link String} value correspond to the key provided
	 * @throws PublisherException
	 */
	public String getConfigEntry(final String key) throws PublisherException {
		Map<String, String> configurationMap = readEMMConfigurationXML();
		String configValue = configurationMap.get(key);
		if (configValue == null) {
			String message =
			                 "Cannot find the key:" + key + " in configuration file " +
			                         EMM_CONFIG_XML;
			logger.error(message);
			throw new IllegalArgumentException(message);

		}
		return configValue.trim();

	}
}

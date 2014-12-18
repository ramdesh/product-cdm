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
package org.wso2.emm.statistics.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.wso2.emm.statistics.DataPublisher;
import org.wso2.emm.statistics.EMMStatisticsPublisher;
import org.wso2.emm.statistics.PublisherException;

public class EMMStatisticsActivator implements BundleActivator {

	private static final Log LOG = LogFactory.getLog(EMMStatisticsActivator.class);
	private ServiceRegistration serviceRegistration;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		try {
			serviceRegistration =
					context.registerService(EMMStatisticsPublisher.class.getName(),
					                        new DataPublisher(), null);
		} catch (PublisherException e) {
			LOG.error("Error while starting the EMM to BAM statisctics publishing bundle.", e);
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("EMM to BAM statisctics publishing bundle started");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext arg0) throws Exception {
		serviceRegistration.unregister();
		if (LOG.isDebugEnabled()) {
			LOG.debug("EMM to BAM statisctics publishing bundle stoped");
		}
	}
}
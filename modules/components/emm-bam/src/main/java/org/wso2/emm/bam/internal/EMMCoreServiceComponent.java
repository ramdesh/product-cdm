
/*
 * *
 *  *  Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *
 */

package org.wso2.emm.bam.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.wso2.emm.bam.AppInfoStream;
import org.wso2.emm.bam.NotificationsStream;
import org.wso2.emm.bam.PublisherHandlerImp;
import org.wso2.emm.bam.RegistrationStream;
import org.wso2.emm.bam.util.Constants;

/**
 * @scr.component name="emm.bam.service.component" immediate="true"
 */

public class EMMCoreServiceComponent {

	private static final Log log = LogFactory.getLog(EMMCoreServiceComponent.class);

	protected void activate(ComponentContext ctx) {
		PublisherHandlerImp ph=PublisherHandlerImp.getInstance();
		ph.register("Registration", new RegistrationStream());
		ph.register("Notifications", new NotificationsStream());
		ph.register(Constants.APP_NOTIFICATIONS_STREAM_NAME, new AppInfoStream());
	}

	protected void deactivate(ComponentContext ctx) {
		log.debug("EMM-Core bundle is deactivated ");
	}


	
}

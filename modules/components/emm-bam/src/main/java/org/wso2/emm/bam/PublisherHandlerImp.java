/**
 *  Copyright (c) WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.emm.bam;

import java.util.HashMap;
import java.util.Map;

import org.wso2.carbon.databridge.commons.StreamDefinition;
import org.wso2.emm.bam.util.JSONReader;
/*
 * Implementation of PublisherHandler, which can register EMMStreamDefinitions
 * at the start and later these can be retrieved when it is necessary to publish
 * data to the specific EMMStreamDefinitions stream.
 */
public class PublisherHandlerImp extends Publisher implements PublisherHandler {
	public static Map<String, EMMStreamDefinition> plugins = null;
	public static PublisherHandlerImp handler = null;

    private PublisherHandlerImp() {}

    public static synchronized PublisherHandlerImp getInstance() {
    	if (plugins == null) {
        	plugins = new HashMap<String, EMMStreamDefinition>();
        }
    	if(handler==null){
    		handler = new PublisherHandlerImp();
    	}
        return handler; 
    }
	
	public void publish(String name, String jsonValue) {
		EMMStreamDefinition streamDef = plugins.get(name);
		Object[] payload=streamDef.getPayload(new JSONReader(jsonValue));
		StreamDefinition definition=streamDef.getStreamDefinition();
		publish(payload,definition);
	}

	public void register(String name, EMMStreamDefinition streamDef) {
		plugins.put(name, streamDef);
	}

}

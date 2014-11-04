<<<<<<< HEAD
/*
 * *
 * * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights
 * Reserved.
 * *
 * * Licensed under the Apache License, Version 2.0 (the "License");
 * * you may not use this file except in compliance with the License.
 * * You may obtain a copy of the License at
 * *
 * * http://www.apache.org/licenses/LICENSE-2.0
 * *
 * * Unless required by applicable law or agreed to in writing, software
 * * distributed under the License is distributed on an "AS IS" BASIS,
 * * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * * See the License for the specific language governing permissions and
 * * limitations under the License.
 */
package org.wso2.emm.bam;

/**
 * Handler interface which can be used to create a class that can register,
 * different streams at the bundle start. This can be implemented to write a
 * custom publish handler.
 */
public interface PublisherHandler {

	/**
	 * Called by device when it is necessary to publish a stream
	 * 
	 * @param name
	 *            : Name of the stream which needs to be published to
	 * @param value
	 *            : pay load that needs to be published as a JSON object
	 * @throws PublisherException
	 */
	public void publish(String name, String value) throws PublisherException;

	/**
	 * Registering a new stream at the time of osgi initialization
	 * 
	 * @param name
	 *            Name of the stream that needs to be registered
	 * @param streamDef
	 *            object of a class implementing EMMStreamDefinition
	 */
	public void register(String name, EMMStream streamDef);
=======
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
/*
 * Handler interface which can be used to create a class that can register,
 * different streams at the bundle start
 */
public interface PublisherHandler {
	public void publish(String name,String value);
	public void register(String name, EMMStreamDefinition streamDef);
>>>>>>> ecfbac8555c0c9e7131494ff1d7838d556cf79a4
}

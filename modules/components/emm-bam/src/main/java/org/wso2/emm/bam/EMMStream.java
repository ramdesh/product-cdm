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
package org.wso2.emm.bam;

import org.wso2.carbon.databridge.commons.StreamDefinition;
import org.wso2.emm.bam.util.JSONReader;

/**
 * This is the interface that provides generalization of a stream. It holds the
 * stream definition and the data it holds.
 */
public interface EMMStream {

	/**
	 * Retrieve the stream definition format related to the stream which
	 * implements the interface
	 *
	 * @return org.wso2.carbon.databridge.commons.StreamDefinition
	 */
	public StreamDefinition getStreamDefinition();

	/**
	 * When the pay load is passed as JSON String, it is converted a pay load
	 * based on the implementation
	 *
	 * @param JSONReader
	 * @return Object[]
	 * @throws PublisherException
	 */
	public Object[] getPayload(JSONReader jsReader) throws PublisherException;
}

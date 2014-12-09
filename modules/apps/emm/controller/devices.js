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

var featureModule = require('/modules/feature.js').feature;
var feature = new featureModule(db);

var groupModule = require('/modules/group.js').group;
var group = new groupModule(db);


management = function(appController){   
	context = appController.context();	
	
	
	
	var features
	try{
		features =feature.getAllFeatures({});
	}catch(e){
		 features = [];
	}
	
	try{
		var groups = group.getGroupsByType({type:context.contextData.user.role});		
	}catch(e){
		
		var groups = [];
	}
	
			

	context.title = context.title + " |  Devices Management";
	context.page = "management";
	context.jsFile= "devices/management.js";
	context.data = {		
		tenantId:session.get("emmConsoleUser").tenantId,
		features: features,
		groups: groups,
        url: context.config.HTTPS_URL
	};
	return context;

};


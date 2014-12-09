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

var groupModule = require('/modules/group.js').group;
var group = new groupModule(db);

var featureModule = require('/modules/feature.js').feature;
var feature = new featureModule(db);



configuration = function(appController){		
		
	context = appController.context();	
	
	try{
		var permissionGroup = JSON.parse(get(appController.getServiceURLs("permissionsCRUD", "")).data);	
	}catch(e){
		var permissionGroup = [];
	}
	
	try{
		var groups = group.getGroupsByType({type:context.contextData.user.role});		
	}catch(e){
		var groups = [];
	}
	
	
	context.jsFile= "permissions/configuration.js";
	context.title = context.title + " | Configuration";		
	context.page = "configuration";
	context.data = {
			configOption : "permissions",
			permissionGroup: permissionGroup,
			groups: groups
		
	};
	return context;
};


add = function(appController){	
	
	context = appController.context();
	
	
	try{
		var groups = group.getGroups({});		
	}catch(e){
		var groups = [];
	}
	
		
	try{
		var features =feature.getAllFeatures({});
	}catch(e){
		var features = [];
	}
	
	context.jsFile= "permissions/add.js";
	context.title = context.title + " | Configuration";	
	context.page = "configuration";
	context.data = {
			configOption : "permissions",
			groups: groups,
			features: features
	};
	return context;
};





add_bundle = function(appController){	
	
	context = appController.context();
		
	try{
		var groups = group.getGroups({});		
	}catch(e){
		var groups = [];
	}	
	
	try{
		var features = feature.getAllFeatures({});
	}catch(e){
		var features = [];
	}
	
	context.jsFile= "permissions/add_bundle.js";
	context.title = context.title + " | Configuration";	
	context.page = "configuration";
	context.data = {
			configOption : "permissions",
			groups: groups,
			features: features
	};
	return context;
};
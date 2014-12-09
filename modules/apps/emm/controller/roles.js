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

var webconsoleModule = require('/modules/webconsole.js').webconsole;
var webconsole = new webconsoleModule(db);

var featureModule = require('/modules/feature.js').feature;
var feature = new featureModule(db);

var userModule = require('/modules/user.js').user;
var user = new userModule(db);

var deviceModule = require('/modules/device.js').device;
var device = new deviceModule(db);

var user_groupModule = require('/modules/user_group.js').user_group;
var user_group = new user_groupModule(db);


configuration = function(appController){

	context = appController.context();
	
	try{
		var groups = group.getGroupsByType({type:context.contextData.user.role});
        for(i = 0; i < groups.length; i++){
            if(groups[i].name == 'Internal/publisher' || groups[i].name == 'Internal/reviewer' || groups[i].name == 'Internal/store' || groups[i].name == 'subscriber'){
                groups[i]['isMam'] = true;
            }else{
                groups[i]['isMam'] = false;
            }
        }
	}catch(e){
		
		var groups = [];
	}
	
	
	
	context.title = context.title + " | Configuration";
	context.page = "configuration";
	context.jsFile= "roles/configuration.js";
	context.data = {
		configOption : "roles",
		groups : groups
	};
	return context;

};


management = function(appController){
	context = appController.context();
	var groups;
	try{
		groups = webconsole.getDevicesCountAndUserCountForAllGroups({});
	}catch(e){
		groups = [];
	}
	var features
	try{
		features =feature.getAllFeatures({});
	}catch(e){
		 features = [];
	}

	context.title = context.title + " | Management";
	context.page = "management";
	context.jsFile= "roles/management.js";
	context.data = {
		configOption : "roles",
		groups: groups,
		features: features,
		tenantId:session.get("emmConsoleUser").tenantId
	};
	return context;

};


users = function(appController){
	context = appController.context();
	var role = request.getParameter('role');
	if(!role){
		role = session.get('emmConsoleSelectedRole');
	}
	session.put('emmConsoleSelectedRole', role)
	try{
		var users = group.getUsersOfGroup({'groupid':role});
	}catch(e){
		var users = [];
	}
	for (var i = 0; i < users.length; i++) {

		if(users[i].no_of_devices > 0){
			var devices = user.getDevices({'userid':users[i].username});

			for (var j = 0; j < devices.length; j++) {
		  		devices[j].properties = JSON.parse(devices[j].properties);
		  		try{
		  			featureList = device.getFeaturesFromDevice({"deviceid":devices[j].id});
		  		}catch(e){
		  			featureList = [];
		  		}
		  		devices[j].features = featureList;

			}

			users[i].devices = devices;
		}

	}
	var features = feature.getAllFeatures({});
	context.title = context.title + " | Users";
	context.page = "management";
	context.jsFile= "roles/users.js";
	context.data = {
		configOption : "roles",
		users: users,
		features: features
	};
	return context;

};


add = function(appController){
	context = appController.context();
	try{
		var users = user.getUsersByType({type:context.contextData.user.role});
	}catch(e){
		var users = [];
	}
	log.debug(session.get("emmConsoleUser"));
	
	context.title = context.title + " | Add Role";
	context.page = "configuration";
	context.jsFile= "roles/add.js";
	context.data = {
		configOption : "roles",
		users: users,
		tenantId:session.get("emmConsoleUser").tenantId
	};
	return context;
};

edit = function(appController){
	context = appController.context();
	var role = request.getParameter('group');
	log.debug(session.get("emmConsoleUser"));
	
	context.title = context.title + " | Edit Role";
	context.page = "configuration";
	context.jsFile= "roles/edit.js";
	context.data = {
		configOption : "roles",
		role: role,
		tenantId:session.get("emmConsoleUser").tenantId
	};
	return context;
};



assign_users = function(appController){

	var groupId = request.getParameter('group');

	try{
		var users = user_group.getUsersOfRoleByAssignment({groupid: groupId});
	}catch(e){
		var users = [];
	}
	log.debug(session.get("emmConsoleUser"));
	context = appController.context();
	context.title = context.title + " | Assign Users to group";
	context.page = "configuration";
	context.jsFile= "roles/assign_users.js";
	context.data = {
		configOption : "roles",
		users: users,
		tenantId:session.get("emmConsoleUser").tenantId,
		groupId: groupId
	};
	return context;
};



assign_permissions = function(appController){

	var groupId = request.getParameter('group');
	
	context = appController.context();
	context.title = context.title + " | Assign permissions to group";
	context.page = "configuration";
	context.jsFile= "roles/assign_permissions.js";
	context.data = {
		configOption : "roles",		
		tenantId:session.get("emmConsoleUser").tenantId,
		groupId: groupId
	};
	return context;
};


view_users = function(appController){


	var groupId = request.getParameter('group');



	try{
        log.debug("Test Group ID"+groupId);
		var users = group.getUsersOfGroup({groupid: groupId});
        log.debug("Test Result"+users);
	}catch(e){
		var users = [];
	}
	try{
		var groups = group.getAllGroups({});
	}catch(e){
		var groups = [];
	}
	context = appController.context();
	context.title = context.title + " | Configuration";
	context.page = "configuration";
	context.jsFile= "users/configuration.js";
	context.data = {
		configOption : "roles",
		users: users,
		groups: groups,
		groupId: groupId
	};
	return context;
};


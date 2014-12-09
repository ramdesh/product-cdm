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

var policyModule = require('/modules/policy.js').policy;
var policy = new policyModule(db);

var mamModule = require('/modules/mam.js').mam;
var mam = new mamModule(db);

var userModule = require('/modules/user.js').user;
var user = new userModule(db);



index = function(appController){

	context = appController.context();	
	
	var testData =  notification.getNotifications({deviceid: 998});

	print(testData);
	
	return {};
	

};
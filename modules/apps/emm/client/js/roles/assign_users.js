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

$("#btn-add").click(function() {

	var groupName = $('#inputName').val();
	var users = $('#inputUsers').val();
	
	var tenantId = $('#tenantId').val();
	
	
	
	var usersArray = []
	if (users != null) {
		usersArray = users.toString().split(",");
	}
	

	var removedUsers = Array();
	
	//this is not a good thing to have it in the UI, but backend logic need it badly
	$("#inputUsers option").each(function(){ 
		if(usersArray.indexOf($(this).val()) < 0){
				 removedUsers.push($(this).val());
		}  		
	});
	
		
	jso = {
		"tenant_id" : tenantId,
		"name" : groupName,
		"added_users" : usersArray,
		"removed_users" : removedUsers
	};

	
	
	
	jQuery.ajax({
		url : getServiceURLs("groupsCRUD", encodeURIComponent(groupName) + "/users"),
		type : "PUT",
		async : "false",
		data : JSON.stringify(jso),
		contentType : "application/json",
		dataType : "json",
		statusCode: {
						400: function() {
							noty({
								text : 'Error occured!',
								'layout' : 'center',
								'type': 'error'
							});
						},
						404: function() {
							noty({
								text : 'API Not Found',
								'layout' : 'center',
								'type': 'error'
							});
						},
						500: function() {
							noty({
								text : 'Fatal error occured!',
								'layout' : 'center',
								'type': 'error'
							});
						},
						200: function() {
							noty({
								text : 'Users are assigned to group successfully!',
								'layout' : 'center'
							});
							window.location.assign("configuration");
						}
			}
	});
	
	

});



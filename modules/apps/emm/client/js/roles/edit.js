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
	
	$( 'form').parsley( 'validate' );	
	if(!$('form').parsley('isValid')){
		noty({
				text : 'Input validation failed!',
				'layout' : 'center',
				'type' : 'error'
		});		
		return;
	}
	

	var name = $('#inputName').val();
	var type = $('#inputType').val();
	var users = $('#inputUsers').val();
	var tenantId = $('#tenantId').val();

	var usersArray = []
	if (users != null) {
		usersArray = users.toString().split(",");
	}
	// alert(JSON.stringify(userGroupsArray));
	jso = {
		"tenant_id" : tenantId,
		"name" : name	
	};

	var previousName = getURLParameter('group');
	

	jQuery.ajax({
		url : getServiceURLs("groupsCRUD", previousName),
		type : "PUT",		
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
					text : 'API not found!',
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
					text : 'Role edited successfully!',
					'layout' : 'center'
				});
				window.location.assign("configuration");
			},
            403: function() {
				noty({
					text : 'Role already exist!',
					'layout' : 'center',
					'type': 'error'
				});				
			},
			409: function() {
				noty({
					text : 'Role already exist!',
					'layout' : 'center',
					'type': 'error'
				});				
			}
		}			
	});
	
	
	

});


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

	var id = $('#inputId').val();
	var groups = $('#inputGroups').val();
	var users = $('#inputUsers').val();
	var platforms = $('#inputPlatforms').val();
	
	var tenantId = $('#tenantId').val();
	
	
	
	var groupsArray = [];
	if (groups != null) {
		groupsArray = groups.toString().split(",");
	}
	
	var usersArray = [];
	if (users != null) {
		usersArray = users.toString().split(",");
	}
	
	var platformsArray = [];
	if (platforms != null) {
		platformsArray = platforms.toString().split(",");
	}
	
	

	var removedGroups = Array();
	$("#inputGroups option").each(function(){ 
		if(groupsArray.indexOf($(this).val()) < 0){
				 removedGroups.push($(this).val());
		}  		
	});
	
	var removedUsers = Array();
	$("#inputUsers option").each(function(){ 
		if(usersArray.indexOf($(this).val()) < 0){
				 removedUsers.push($(this).val());
		}  		
	});
	
	var removedPlatforms = Array();
	$("#inputPlatforms option").each(function(){ 
		if(platformsArray.indexOf($(this).val()) < 0){
				 removedPlatforms.push($(this).val());
		}  		
	});
	
		
	jso = {
		"tenant_id" : tenantId,
		"id" : id,
		"added_groups" : groupsArray,
		"added_users" : usersArray,
		"added_platforms" : platformsArray,
		"removed_groups" : removedGroups,
		"removed_users" : removedUsers,
		"removed_platforms" : removedPlatforms,
		
	};

	

	jQuery.ajax({
		url : getServiceURLs("policiesCRUD", id + "/groups"),
		type : "PUT",
		async : "false",
		data : JSON.stringify(jso),
		contentType : "application/json",
		dataType : "json"
	});
	
	$( document ).ajaxComplete(function() {
		window.location.assign("configuration");
	});
	
	noty({
		text : 'Resources are assigned to policies successfully!',
		'layout' : 'center'
	});
	

});


$( "#inputAssignTo" ).change(function() {
	$("#roles-box").css("display", "none");
	$("#users-box").css("display", "none");
	$("#platforms-box").css("display", "none");
	
	if( $(this).val() == "roles"){
		$("#roles-box").css("display", "block");		
	}
	if( $(this).val() == "users"){
		$("#users-box").css("display", "block");
	}
	if( $(this).val() == "platforms"){
		$("#platforms-box").css("display", "block");
	}
});

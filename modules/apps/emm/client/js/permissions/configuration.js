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

var selectedPermission = null;

$(document).ready( function () {
	oTable = $('#main-table').dataTable( {
		"sDom": "<'row-fluid'<'tabel-filter-group span8'T><'span4'f>r>t<'row-fluid'<'span6'i><'span6'p>>",
		"iDisplayLength": 6,
		 "bStateSave": false,
		"oTableTools": {
			"aButtons": [
				"copy",
				"print",
				{
					"sExtends":    "collection",
					"sButtonText": 'Save <span class="caret" />',
					"aButtons":    [ "csv", "xls", "pdf" ]
				}
			]
		}
	} );
	
} );


$(".add-group-link").click(function() {
	var name = $(this).data("name");
	selectedPermission = $(this).data("permission");
	$('#assign-group-heading').html("Assign groups to " + name);
	

});




$("#btn-assign-group").click(function() {	
	var userGroups = $('#inputGroups').val();
	var userGroupsArray = []
	if (userGroups != null) {
		userGroupsArray = userGroups.toString().split(",");
	}


	noty({
				text : 'Groups are assigned to the permission successfully!',
				'layout' : 'center'
	});
	
	jQuery.ajax({
		url : getServiceURLs("permissionsCRUD", ""),
		type : "POST",
		async : "false",
		data : JSON.stringify({permission: selectedPermission, groups: userGroupsArray}),
		contentType : "application/json",
		dataType : "json"		
	});
	
	
});
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
	
	var groupId =  $("#inputGroupId").val();
	var features = $('#inputFeatures').val();
	
	var featuresArray = []
	if (features != null) {
		featuresArray = features.toString().split(",");
	}	
	
	jQuery.ajax({
		url : getServiceURLs("permissionsCRUD", ""),
		type : "POST",
		async : "false",
		data: JSON.stringify({groupId: groupId, features: featuresArray}),		
		contentType : "application/json",
		dataType : "json",		
	});
	
	noty({
		text : 'Permission Added successfully!',
		'layout' : 'center',
		'modal': false
	});
	
	//window.location.reload(true);
	
});
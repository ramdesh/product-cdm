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

	var firstname = $('#inputFirstName').val();
	var lastname = $('#inputLastName').val();
	var type = $('input[name="inputType"]:checked').val();
	var username = $('#inputUsername').val();
    var email = $('#inputEmail').val();	
	
	var existing_user = getURLParameter('user'); 	
	
	jso = {
		"tenant_id" : tenantId,
		"username" : username,
        "email" : email,
		"existing_username": existing_user,		
		"first_name" : firstname,
		"last_name" : lastname,
		"type": type
		
	};	
	
	 var n = noty({
					text : 'Editing user, please wait....',
					'layout' : 'center',
					timeout: false				
								
	});
	
	
	
		
	jQuery.ajax({
		url : getServiceURLs("usersCRUD", ""),
		type : "POST",		
		data : JSON.stringify(jso),		
		contentType : "application/json",
     	dataType : "json",
     	statusCode: {
			400: function() {				
				n.setText('Error occured!');	
				n.setType('error');
				n.setTimeout(1000);			
			},
			404: function() {				
				n.setText('API not found!');	
				n.setType('error');	
				n.setTimeout(1000);		
			},
			500: function() {				
				n.setText('Fatal error occured!');	
				n.setType('error');
				n.setTimeout(1000);			
			},
			201: function() {				
				n.setText('User Edited successfully!');	
				window.location.assign("configuration");
			},
			409: function() {				
				n.setText('User already exist!');	
				n.setType('error');
				n.setTimeout(1000);			
			}
		}				
	});
	

});





$( ".radioUserType" ).change(function() {
	var value = $(this).val();	
	//$(".inputGroupsSelect .box1 .filter").val(value);	
	//$(".inputGroupsSelect .box1 .filter" ).change();
	
	if(value == 'user'){
		$("#userSeletBox").css("display", "block");		
	}else{
		$("#userSeletBox").css("display", "none");		
	}
});




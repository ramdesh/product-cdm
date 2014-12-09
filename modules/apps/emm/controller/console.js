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

var userModule = require('/modules/user.js').user;
var user = new userModule(db);

login = function(appController){	
	if(request.getMethod() == 'POST'){
		username = request.getParameter('username');
		password =  request.getParameter('password');
		var data = {'username': username, 'password': password};		
		var objUser = user.authenticate(data);

		if(objUser != null){
				response.status=200;
				var userAgent= request.getHeader("User-Agent");
			    var android = userAgent.indexOf("Android");
				//Fix this when Android client is resolved
			    if(android > 0){
					print("200");
				}else{
					var userFeed = {};
					userFeed.tenantId = stringify(objUser["tenantId"]);
					userFeed.username = objUser["username"];
					userFeed.email = objUser["email"];
					userFeed.firstName = objUser["firstName"];
					userFeed.lastName = objUser["lastName"];
					userFeed.mobile = objUser["mobile"];
					userFeed.tenentDomain = user.getTenantDomainFromID(stringify(objUser["tenantId"]));
					var parsedRoles = parse(objUser["roles"]);
					var isEMMAdmin = false;
					var isMAdmin = false;
					for (var i = 0; i < parsedRoles.length; i++) {
						if(parsedRoles[i] == 'Internal/emmadmin') {
							isEMMAdmin = true;
							break;
						}
						if(parsedRoles[i] == 'admin') {
							isAdmin = true;
							isEMMAdmin = true;
							break;
						}
					}				
					
					return;	
					userFeed.isEMMAdmin = isEMMAdmin;
					userFeed.isAdmin = isAdmin;
					session.put("emmConsoleUserLogin", "true");
					session.put("emmConsoleUser", userFeed);
					if(isAdmin){
						response.sendRedirect('dashboard');
					}else{
						response.sendRedirect(appController.appInfo().server_url + 'users/devices?user=' + userFeed.username);
					}
				}
	    }else{
			response.status=401;
		    print("Authentication Failed");
		}
	}
	context = appController.context();
	context.title = context.title + " | Login";		
	context.data = {
		
	};
	return context;	

};

logout = function(appController){
	
};

dashboard = function(appController){
	context = appController.context();
	context.title = context.title + " | Dashboard";	
	context.jsFile= "console/dashboard.js";
	context.page = "dashboard";
	context.data = {		
	};
	return context;	
	
};
configuration = function(appController){	
	context = appController.context();
	context.title = context.title + " | Configuration";	
	context.page = "configuration";
	context.data = {
		configOption : "emmsettings"
	};
	return context;	
};

management = function(appController){		
	context = appController.context();
	context.title = context.title + " | Management";
	context.page = "management";	
	context.data = {
		
	};
	return context;	
	
};
info = function(appController){		
	print(session.get("emmConsoleUser"));
	return null;
};
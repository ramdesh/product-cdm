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

var repostsModule = require('/modules/mdm_reports.js').mdm_reports;
var report = new repostsModule(db);


index = function(appController){		
	
	context = appController.context();
	context.title = context.title + " | Reports";	
	context.page = "reports";
	context.data = {		
	};
	return context;	
	
};


all_reg_devices = function(appController){	
	
	var results = null; 
	
	var dashboard = request.getParameter('dashboard');
	
	
	if(request.getMethod() == 'POST' || dashboard == 'true'){
		
		//print(request.getParameter('dashboard') == 'true');
		
		var startdate = request.getParameter('startdate');
		var enddate = request.getParameter('enddate');
		var platform = request.getParameter('platform');
		
		try{
			var reportResults = report.getDevicesByRegisteredDate({startDate: startdate, endDate: enddate, platformType : platform});	
		}catch(e){
			var reportResults = [];
		}
		
		
		results = reportResults;
	}
	
		
	context = appController.context();
	context.title = context.title + " | Reports";	
	context.jsFile= "reports/reports.js";
	context.page = "reports";
	context.data = {
		results: results,
		inputData : {startdate: startdate, enddate: enddate, platform : platform}		
	};
	return context;	
	
};


devices_complience = function(appController){	
	
	var results = null; 
	
	var dashboard = request.getParameter('dashboard');
	
	
	
	if(request.getMethod() == 'POST' || dashboard == 'true'){
		
		var startdate = request.getParameter('startdate');
		var enddate = request.getParameter('enddate');
		var username = request.getParameter('username');
		var status = request.getParameter('status');
		
		if(username == null){
			username = "";
		}
		
		//print(startdate + enddate + username + status);
		
		var reportResults = report.getDevicesByComplianceState({startDate: startdate, endDate: enddate, username: username, status: status});
		
		//print(reportResults);
		
		results = reportResults;
	}
	
		
	context = appController.context();
	context.title = context.title + " | Reports";	
	context.jsFile= "reports/reports.js";
	context.page = "reports";
	context.data = {
		results: results,
		inputData : {startdate: startdate, enddate: enddate, username: username, status: status}		
	};
	return context;	
	
};


devices_status = function(appController){	
	
	    var results = null; 
	
	
		
		var startdate = request.getParameter('startdate');
		var enddate = request.getParameter('enddate');
		var deviceId = request.getParameter('deviceid');
		var imei = request.getParameter('imei');
		
		//print(startdate + enddate + username + status);
		
		var reportResults = report.getComplianceStatus({startDate: startdate, endDate: enddate, deviceID: deviceId});
		
		//print(reportResults);
		
		results = reportResults;
	
	
		
	context = appController.context();
	context.title = context.title + " | Reports";	
	context.jsFile= "reports/reports.js";
	context.page = "reports";
	context.data = {
		results: results,
		inputData : {startdate: startdate, enddate: enddate, deviceId: deviceId, imei: imei}		
	};
	return context;	
	
};




top_ten_apps = function(appController){	
	
	var results = null; 
	
	if(request.getMethod() == 'POST'){
		
		
		var platform = request.getParameter('platform');
		
		var reportResults = report.getInstalledApps({platformType : platform});
		
		//print(reportResults);
		
		results = reportResults;
	}
	
		
	context = appController.context();
	context.title = context.title + " | Reports";	
	context.jsFile= "reports/reports.js";
	context.page = "reports";
	context.data = {
		results: results,
		inputData : {platform : platform}		
	};
	return context;	
	
};



install_app_sum = function(appController){	
	
	var results = null; 
	
	if(request.getMethod() == 'POST'){
		
		var username = request.getParameter('username');
		
		var reportResults = report.getInstalledAppsByUser({userid: username});
		
		results = reportResults;
	}
	
		
	context = appController.context();
	context.title = context.title + " | Reports";	
	context.jsFile= "reports/reports.js";
	context.page = "reports";
	context.data = {
		results: results,
		inputData : {username: username}		
	};
	return context;	
	
};
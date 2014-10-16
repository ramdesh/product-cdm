var repostsModule = require('/modules/mdm_reports.js').mdm_reports;
var report = new repostsModule(db);


/*
 Report view index function
 */
index = function(appController){

    var context = appController.context();
	context.title = context.title + " | Reports";	
	context.page = "reports";
	context.data = {		
	};

	return context;	
	
};



/*
 Report all registered devices function
 */
all_reg_devices = function(appController){	
	
	var results = null; 
	
	var dashboard = request.getParameter('dashboard');
	
	
	if(request.getMethod() == 'POST' || dashboard == 'true'){

		var startdate = request.getParameter('startdate');
		var enddate = request.getParameter('enddate');
		var platform = request.getParameter('platform');

        var reportResults;
        try{
			reportResults = report.getDevicesByRegisteredDate({startDate: startdate, endDate: enddate, platformType : platform});
		}catch(e){
			reportResults = [];
		}

		results = reportResults;
	}


    var context = appController.context();
	context.title = context.title + " | Reports";	
	context.jsFile= "reports/reports.js";
	context.page = "reports";
	context.data = {
		results: results,
		inputData : {startdate: startdate, enddate: enddate, platform : platform}		
	};

	return context;	
	
};



/*
 Report all registered devices function
 */
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

		var reportResults = report.getDevicesByComplianceState({startDate: startdate, endDate: enddate, username: username, status: status});

		results = reportResults;
	}


    var context = appController.context();
	context.title = context.title + " | Reports";	
	context.jsFile= "reports/reports.js";
	context.page = "reports";
	context.data = {
		results: results,
		inputData : {startdate: startdate, enddate: enddate, username: username, status: status}		
	};
	return context;	
	
};



/*
 Report device status function
 */
devices_status = function(appController){	
	
	    var results = null;
		var startdate = request.getParameter('startdate');
		var enddate = request.getParameter('enddate');
		var deviceId = request.getParameter('deviceid');
		var imei = request.getParameter('imei');
		var reportResults = report.getComplianceStatus({startDate: startdate, endDate: enddate, deviceID: deviceId});

		results = reportResults;


    var context = appController.context();
	context.title = context.title + " | Reports";	
	context.jsFile= "reports/reports.js";
	context.page = "reports";
	context.data = {
		results: results,
		inputData : {startdate: startdate, enddate: enddate, deviceId: deviceId, imei: imei}		
	};

	return context;	
	
};



/*
 Report top ten apps function
 */
top_ten_apps = function(appController){	
	
	var results = null; 
	
	if(request.getMethod() == 'POST'){
		
		var platform = request.getParameter('platform');
		var reportResults = report.getInstalledApps({platformType : platform});
		results = reportResults;
	}


    var context = appController.context();
	context.title = context.title + " | Reports";	
	context.jsFile= "reports/reports.js";
	context.page = "reports";
	context.data = {
		results: results,
		inputData : {platform : platform}		
	};

	return context;	
	
};


/*
 Report installed apps summery function
 */
install_app_sum = function(appController){	
	
	var results = null; 
	
	if(request.getMethod() == 'POST'){
		
		var username = request.getParameter('username');
		var reportResults = report.getInstalledAppsByUser({userid: username});
		
		results = reportResults;
	}


    var context = appController.context();
	context.title = context.title + " | Reports";	
	context.jsFile= "reports/reports.js";
	context.page = "reports";
	context.data = {
		results: results,
		inputData : {username: username}		
	};

	return context;	
	
};
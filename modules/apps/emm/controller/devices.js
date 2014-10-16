
//importing modules
var featureModule = require('/modules/feature.js').feature;
var feature = new featureModule(db);

var groupModule = require('/modules/group.js').group;
var group = new groupModule(db);



/*
 Device Management Function
 */
management = function(appController){

    var context = appController.context();

	var features;
	try{
		features =feature.getAllFeatures({});
	}catch(e){
         log.error("Error while retrieving features from the backend : " + e);
		 features = [];
	}

    var groups;
	try{
		groups = group.getGroupsByType({type:context.contextData.user.role});
	}catch(e){
        log.error("Error while retrieving groups from the backend : " + e);
		groups = [];
	}

	context.title = context.title + " |  Devices Management";
	context.page = "management";
	context.jsFile= "devices/management.js";
	context.data = {		
		tenantId:session.get("emmConsoleUser").tenantId,
		features: features,
		groups: groups,
        url: context.config.HTTPS_URL
	};


	return context;

};


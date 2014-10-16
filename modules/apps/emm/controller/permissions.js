//importing modules
var groupModule = require('/modules/group.js').group;
var group = new groupModule(db);

var featureModule = require('/modules/feature.js').feature;
var feature = new featureModule(db);



/*
    Permission configuration Function
 */
configuration = function(appController){

    var context = appController.context();

    var permissionGroup;
    try{
		permissionGroup = JSON.parse(get(appController.getServiceURLs("permissionsCRUD", "")).data);
	}catch(e){
        log.error("Error while retrieving permission groups from the backend : " + e);
		permissionGroup = [];
	}

    var groups;
	try{
		groups = group.getGroupsByType({type:context.contextData.user.role});
	}catch(e){
        log.error("Error while retrieving groups from the backend : " + e);
		groups = [];
	}
	
	context.jsFile= "permissions/configuration.js";
	context.title = context.title + " | Configuration";		
	context.page = "configuration";
	context.data = {
			configOption : "permissions",
			permissionGroup: permissionGroup,
			groups: groups
		
	};
	return context;
};



/*
 Permission add Function
 */
add = function(appController){

    var context = appController.context();

    var groups;
	try{
		groups = group.getGroups({});
	}catch(e){
        log.error("Error while retrieving groups from the backend : " + e);
		groups = [];
	}

    var features;
	try{
		features =feature.getAllFeatures({});
	}catch(e){
        log.error("Error while retrieving features from the backend : " + e);
		features = [];
	}
	
	context.jsFile= "permissions/add.js";
	context.title = context.title + " | Configuration";	
	context.page = "configuration";
	context.data = {
			configOption : "permissions",
			groups: groups,
			features: features
	};

	return context;
};


/*
    Permission add bundle Function
 */
add_bundle = function(appController){	
	
	var context = appController.context();

    var groups;
	try{
		groups = group.getGroups({});
	}catch(e){
        log.error("Error while retrieving groups from the backend : " + e);
        groups = [];
	}	

    var features;
    try{
		features = feature.getAllFeatures({});
	}catch(e){
        log.error("Error while retrieving features from the backend : " + e);
		features = [];
	}
	
	context.jsFile= "permissions/add_bundle.js";
	context.title = context.title + " | Configuration";	
	context.page = "configuration";
	context.data = {
			configOption : "permissions",
			groups: groups,
			features: features
	};

	return context;
};
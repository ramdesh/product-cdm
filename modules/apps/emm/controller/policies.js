var groupModule = require('/modules/group.js').group;
var group = new groupModule(db);

var featureModule = require('/modules/feature.js').feature;
var feature = new featureModule(db);

var policyModule = require('/modules/policy.js').policy;
var policy = new policyModule(db);


var userModule = require('/modules/user.js').user;
var user = new userModule(db);

var storeModule = require('/modules/store.js').store;
var store = new storeModule(db);




/*
 Policies configuration function
 */
configuration = function(appController){	
	
	try{
		var policies = policy.getAllPoliciesForMDM({});
	}catch(e){
        log.error("Error while retrieving policies from the backend : " + e);
		var policies = [];
	}
			
	try{
		var groups = group.getGroups({});
	}catch(e){
        log.error("Error while retrieving groups from the backend : " + e);
		var groups = [];
	}

    var context = appController.context();
	context.jsFile= "policies/configuration.js";
	context.title = context.title + " | Configuration";		
	context.page = "configuration";
	context.data = {
			configOption : "policies",
			policies: policies,
			groups: groups
		
	};
	return context;
};


/*
 Policies assign to groups function
 */
assign_groups = function(appController){	
	
	
	var policyId = request.getParameter('policy');
	var policyName = request.getParameter('policyName');
	
	var hasGroups = false;
	var hasUsers = false;
	var hasPlatforms = false;
		
	try{
		var groups = policy.getGroupsByPolicy({policyid: policyId});		
	}catch(e){
        log.error("Error while retrieving groups from the backend : " + e);
		var groups = [];
	}
	
	for(var i = 0; i < groups.length; i++){		
		if(groups[i].available){			
			hasGroups = true;
		}
	}

	
	try{
		var users = policy.getUsersByPolicy({policyid: policyId});
	}catch(e){
        log.error("Error while retrieving users from the backend : " + e);
		var users = [];
	}
	
	for(var i = 0; i < users.length; i++){		
		if(users[i].available){			
			hasUsers = true;
		}
	}
	
	
	try{
		var platforms = policy.getPlatformsByPolicy({policyid: policyId});		
	}catch(e){
        log.error("Error while retrieving users from the backend : " + e);
		var platforms = [];
	}
	
	for(var i = 0; i < platforms.length; i++){		
		if(platforms[i].available){			
			hasPlatforms = true;
		}
	}

    var context = appController.context();
	context.title = context.title + " | Assign Users to group";	
	context.page = "configuration";	
	context.jsFile= "policies/assign_groups.js";
	context.data = {
		configOption : "policies",
		groups: groups,
		tenantId:session.get("emmConsoleUser").tenantId,
		policyId: policyId,
		platforms: platforms,
		users: users,
		policyName: policyName,
		hasResources : {hasGroups: hasGroups, hasUsers: hasUsers, hasPlatforms: hasPlatforms}
	};

	return context;
};



/*
 Policies assign to resources (groups/ users / platforms)  function
 */
assign_resources = function(appController){

    var context = appController.context();
	
	var policyId = request.getParameter('policy');
	var policyName = request.getParameter('policyName');

    var groups;
	try{
		groups = group.getGroupsByType({type:context.contextData.user.role});
	}catch(e){
		log.error("Error form the Backend to UI >> " + e);
		groups = [];
	}
	

    var users;
	try{
		users = policy.getUsersByPolicy({policyid: policyId});
	}catch(e){
        log.error("Error while retrieving users from the backend : " + e);
		users = [];
	}

    var platforms;
	try{
		platforms = policy.getPlatformsByPolicy({policyid: policyId});
	}catch(e){
        log.error("Error while retrieving platforms from the backend : " + e);
		platforms = [];
	}
	
	var policies;
	try{
		policies = policy.getAllPolicies({});
	}catch(e){
        log.error("Error while retrieving policies from the backend : " + e);
		policies = [];
	}
	
					
	
	context.title = context.title + " | Assign Users to group";	
	context.page = "policies";	
	context.jsFile= "policies/assign_resources.js"
	context.data = {
		configOption : "policies",
		groups: groups,
		tenantId:session.get("emmConsoleUser").tenantId,
		policyId: policyId,
		platforms: platforms,
		users: users,
		policyName: policyName,
		policies: policies
	};

	return context;
};



/*
 Policies add  function
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

    var installedApps;
    try{
		installedApps =  store.getAppsFromStoreFormatted();
	}catch(e){
        log.error("Error while retrieving installed apps from the backend : " + e);
		installedApps = [];
	}
    
		
	context.jsFile= "policies/add.js";
	context.title = context.title + " | Configuration";	
	context.page = "configuration";
	context.data = {
			configOption : "policies",
			groups: groups,
			features: features,
            installedApps: installedApps
	};
	return context;
};



/*
 Policies edit  function
 */
edit = function(appController){

    var context = appController.context();
	
	
	var policyId = request.getParameter('policy');
	var policyName = request.getParameter('policy');

    var installedApps;
    try{
		installedApps =  store.getAppsFromStoreFormatted();
	}catch(e){
        log.error("Error while retrieving installed apps from the backend : " + e);
		installedApps = [];
	}
	
	
	context.jsFile= "policies/edit.js";
	context.title = context.title + " | Configuration";	
	context.page = "configuration";
	context.data = {
			configOption : "policies",
			policyId: policyId,
			policyName: policyName,
            installedApps: installedApps
			
	}
	return context;
}



/*
 Policies add bundle function
 */
add_bundle = function(appController){

    var context = appController.context();

    var groups;
    try{
		groups = group.getGroups({});
	}catch(e){
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
	}
	return context;

}
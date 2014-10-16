var groupModule = require('/modules/group.js').group;
var group = new groupModule(db);

var webconsoleModule = require('/modules/webconsole.js').webconsole;
var webconsole = new webconsoleModule(db);

var featureModule = require('/modules/feature.js').feature;
var feature = new featureModule(db);

var userModule = require('/modules/user.js').user;
var user = new userModule(db);

var deviceModule = require('/modules/device.js').device;
var device = new deviceModule(db);

var user_groupModule = require('/modules/user_group.js').user_group;
var user_group = new user_groupModule(db);



/*
 Roles configurations function
 */
configuration = function(appController){

    var context = appController.context();

	var groups;
	try{
		groups = group.getGroupsByType({type:context.contextData.user.role});
        for(i = 0; i < groups.length; i++){
            if(groups[i].name == 'Internal/publisher' || groups[i].name == 'Internal/reviewer' || groups[i].name == 'Internal/store' || groups[i].name == 'subscriber'){
                groups[i]['isMam'] = true;
            }else{
                groups[i]['isMam'] = false;
            }
        }
	}catch(e){
        log.error("Error while retrieving groups from the backend : " + e);
		groups = [];
	}
	
	context.title = context.title + " | Configuration";
	context.page = "configuration";
	context.jsFile= "roles/configuration.js";
	context.data = {
		configOption : "roles",
		groups : groups
	};
	return context;

};






/*
 Roles management function
 */
management = function(appController){

    var context = appController.context();

	var groups;
	try{
		groups = webconsole.getDevicesCountAndUserCountForAllGroups({});
	}catch(e){
        log.error("Error while retrieving groups from the backend : " + e);
		groups = [];
	}


	var features
	try{
		features =feature.getAllFeatures({});
	}catch(e){
         log.error("Error while retrieving features from the backend : " + e);
		 features = [];
	}

	context.title = context.title + " | Management";
	context.page = "management";
	context.jsFile= "roles/management.js";
	context.data = {
		configOption : "roles",
		groups: groups,
		features: features,
		tenantId:session.get("emmConsoleUser").tenantId
	};
	return context;

};






/*
 Roles configurations function
 */
users = function(appController){

    var context = appController.context();

	var role = request.getParameter('role');

	if(!role){
		role = session.get('emmConsoleSelectedRole');
	}
	session.put('emmConsoleSelectedRole', role);

    var users;
	try{
		users = group.getUsersOfGroup({'groupid':role});
	}catch(e){
        log.error("Error while retrieving users from the backend : " + e);
        users = [];
	}

	for (var i = 0; i < users.length; i++) {

		if(users[i].no_of_devices > 0){
			var devices = user.getDevices({'userid':users[i].username});

			for (var j = 0; j < devices.length; j++) {
		  		devices[j].properties = JSON.parse(devices[j].properties);
		  		try{
		  			featureList = device.getFeaturesFromDevice({"deviceid":devices[j].id});
		  		}catch(e){
		  			featureList = [];
		  		}
		  		devices[j].features = featureList;

			}

			users[i].devices = devices;
		}

	}

	var features;
    try{
        features = feature.getAllFeatures({});
    }catch(e){
        log.error("Error while retrieving features from the backend : " + e);
        features = [];
    }

	context.title = context.title + " | Users";
	context.page = "management";
	context.jsFile= "roles/users.js";
	context.data = {
		configOption : "roles",
		users: users,
		features: features
	};

	return context;

};


/*
 Roles add function
 */
add = function(appController){

    var context = appController.context();

    var users;
	try{
		users = user.getUsersByType({type:context.contextData.user.role});
	}catch(e){
        log.error("Error while retrieving users from the backend : " + e);
		users = [];
	}

	log.debug(session.get("emmConsoleUser"));
	
	context.title = context.title + " | Add Role";
	context.page = "configuration";
	context.jsFile= "roles/add.js";
	context.data = {
		configOption : "roles",
		users: users,
		tenantId:session.get("emmConsoleUser").tenantId
	};

	return context;
};



/*
 Roles edit function
 */
edit = function(appController){

    var context = appController.context();

	var role = request.getParameter('group');
	log.debug(session.get("emmConsoleUser"));
	
	context.title = context.title + " | Edit Role";
	context.page = "configuration";
	context.jsFile= "roles/edit.js";
	context.data = {
		configOption : "roles",
		role: role,
		tenantId:session.get("emmConsoleUser").tenantId
	};

	return context;
};



/*
 Roles assign to users function
 */
assign_users = function(appController){

	var groupId = request.getParameter('group');

	try{
		var users = user_group.getUsersOfRoleByAssignment({groupid: groupId});
	}catch(e){
        log.error("Error while retrieving users from the backend : " + e);
		var users = [];
	}

    var context = appController.context();
	context.title = context.title + " | Assign Users to group";
	context.page = "configuration";
	context.jsFile= "roles/assign_users.js";
	context.data = {
		configOption : "roles",
		users: users,
		tenantId:session.get("emmConsoleUser").tenantId,
		groupId: groupId
	};

	return context;
};



/*
 Assign roles to permission function
 */
assign_permissions = function(appController){

	var groupId = request.getParameter('group');

    var context = appController.context();
	context.title = context.title + " | Assign permissions to group";
	context.page = "configuration";
	context.jsFile= "roles/assign_permissions.js";
	context.data = {
		configOption : "roles",		
		tenantId:session.get("emmConsoleUser").tenantId,
		groupId: groupId
	};

	return context;
};



/*
 View users belong to a perticular role function
 */
view_users = function(appController){

	var groupId = request.getParameter('group');

	try{
        log.debug("Test Group ID"+groupId);
		var users = group.getUsersOfGroup({groupid: groupId});
        log.debug("Test Result"+users);
	}catch(e){
        log.error("Error while retrieving users from the backend : " + e);
		var users = [];
	}


	try{
		var groups = group.getAllGroups({});
	}catch(e){
        log.error("Error while retrieving groups from the backend : " + e);
		var groups = [];
	}
    var context = appController.context();
	context.title = context.title + " | Configuration";
	context.page = "configuration";
	context.jsFile= "users/configuration.js";
	context.data = {
		configOption : "roles",
		users: users,
		groups: groups,
		groupId: groupId
	};

	return context;
};


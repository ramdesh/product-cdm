var userGModule = require('/modules/user_group.js').user_group;
var userG = new userGModule(db);

var userModule = require('/modules/user.js').user;
var user = new userModule(db);

var groupModule = require('/modules/group.js').group;
var group = new groupModule(db);

var deviceModule = require('/modules/device.js').device;
var device = new deviceModule(db);

var notificationModule = require('/modules/notification.js').notification;
var notification = new notificationModule(db);

var common = require("/modules/common.js");


/*
 Users configuration function
 */
configuration = function (appController) {
    //TODO put var to context
    var context = appController.context();

    context.title = context.title + " | Configuration";
    context.page = "configuration";
    context.jsFile = "users/configuration.js";
    context.data = {
        configOption: "users",
        url: context.config.HTTPS_URL
    };
    return context;
};


/*
 Users add function
 */
add = function (appController) {

    var context = appController.context();

    try {
        var groups = group.getGroupsByType({type: context.contextData.user.role});

    } catch (e) {
        log.error("Error while retrieving groups from the backend : " + e);
        var groups = [];
    }

    context.title = context.title + " | Add User";
    context.page = "configuration";
    context.jsFile = "users/add.js";
    context.data = {
        configOption: "users",
        groups: groups,
        tenantId: session.get("emmConsoleUser").tenantId
    };
    context.auth_roles = ['Internal/everyone'];
    return context;

};


/*
 Users edit function
 */
edit = function (appController) {

    var userid = request.getParameter('user');

    var selectedUser;
    try {
        selectedUser = user.getUser({userid: userid});
    } catch (e) {
        log.error("Error while retrieving selected user from the backend : " + e);
        selectedUser = {};
    }

    var context = appController.context();

    context.title = context.title + " | Add User";
    context.page = "configuration";
    context.jsFile = "users/edit.js";
    context.data = {
        configOption: "users",
        user: selectedUser
    };

    return context;

};



/*
 Users view function
 */
view = function (appController) {

    var context = appController.context();

    var userId = request.getParameter('user');
    if (!userId) {
        userId = session.get('emmConsoleSelectedUser');
    }
    session.put('emmConsoleSelectedUser', userId);

    var objUser;
    try {
        objUser = user.getUser({"userid": userId});
    } catch (e) {
        log.error("Error while retrieving selected user from the backend : " + e);
        objUser = {};
    }


    try {
        var groups = userG.getRolesOfUserByAssignment({username: userId, removeAdmins: false});
    } catch (e) {
        log.error("Error while retrieving groups from the backend : " + e);
        var groups = [];
    }


    context.title = context.title + " | View User";
    context.page = "configuration";
    context.data = {
        user: objUser,
        groups: groups
    };
    return context;

};



/*
 Users devices function
 */
devices = function (appController) {

    var context = appController.context();

    var userId = request.getParameter('user');

    if (!userId) {
        userId = session.get('emmConsoleSelectedUser');
    }
    session.put('emmConsoleSelectedUser', userId);

    var objUser;
    try {
        objUser = user.getUser({"userid": userId});
    } catch (e) {
        log.error("Error while retrieving selected user from the backend : " + e);
        var objUser = {};
    }

    var devices;
    try {
        // TODO remove var
        var devices = user.getDevices({"userid": userId});
    } catch (e) {
        log.error("Error while retrieving devices from the backend : " + e);
        // TODO remove var
        var devices = [];
    }


    noDevices = devices.length <= 0;


    for (var i = 0; i < devices.length; i++) {

        try {
            var allPolicies = notification.getPolicyState({deviceid: devices[i].id});
            if (!allPolicies.length > 0) {
                //TO DO use square brackets
                allPolicies = new Array();
            }
            var policies = {violated: false, policies: allPolicies};

            // this is a policy validation patch added to UI. since the backend filtering does not support.
            for (var j = 0; j < allPolicies.length; j++) {
                if (!allPolicies[j].status) {
                    policies.violated = true;
                }
            }
            //end of patch

            devices[i].policies = policies;

        } catch (e) {
            log.error("Error while retrieving policies from the backend : " + e);
        }

        devices[i].properties = JSON.parse(devices[i].properties);

        try {
            featureList = device.getFeaturesFromDevice({"deviceid": devices[i].id, roles: context.currentUser.roles});
        } catch (e) {
            log.error("Error while retrieving feature list from the backend : " + e);
            featureList = [];
        }

        devices[i].features = featureList;
    }

    context.title = context.title + " | Add User";
    context.page = "management";
    context.jsFile = "users/devices.js";
    context.googleMaps = true;
    context.data = {
        configOption: "users",
        devices: devices,
        user: objUser,
        noDevices: noDevices
    };

    return context;

};



/*
 Users assign to groups function
 */
assign_groups = function (appController) {

    var username = request.getParameter('user');

    try {
        var groups = userG.getRolesOfUserByAssignment({username: username, removeAdmins: true});
    } catch (e) {
        log.error("Error while retrieving groups from the backend : " + e);
        var groups = [];
    }

    var context = appController.context();


    // Array Remove - By John Resig (MIT Licensed)
    Array.prototype.remove = function (from, to) {
        var rest = this.slice((to || from) + 1 || this.length);
        this.length = from < 0 ? this.length + from : from;
        return this.push.apply(this, rest);
    };


    if (context.contextData.user.role != 'masteradmin') {
        for (var i = 0; i < groups.length; i++) {
            if (groups[i].name == 'masteradmin' || groups[i].name == "admin") {
                groups.remove(i);
            }
        }
    }

    context.title = context.title + " | Assign Users to group";
    context.page = "configuration";
    context.jsFile = "users/assign_groups.js";
    context.data = {
        configOption: "policies",
        groups: groups,
        tenantId: session.get("emmConsoleUser").tenantId,
        username: username

    };

    return context;
};
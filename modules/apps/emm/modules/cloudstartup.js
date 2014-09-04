var startup = (function () {

    var routes = new Array();
    var log = new Log();
    var db;
    var driver, user, apimgr;
    var sqlscripts = require('/sqlscripts/db.js');
    var userModule = require('/modules/user.js').user;
	var apimgrModule = require("/modules/apicloud.js").apimgr;
    var carbon = require('carbon');

    var module = function (dbs) {
        db = dbs;
        driver = require('driver').driver(db);
        user = new userModule(db);
		apimgr = new apimgrModule(db);
    };


    // prototype
    module.prototype = {
        constructor: module,

        //this executes after user loggedin
        onUserLogin: function(ctx){ 
            
            //log.info("USER LOGGED " + ctx.username);
            if(ctx.isAdmin){
                //Executed only if it is admin
                var rolePermissions = driver.query(sqlscripts.permissions.select1, 'admin', ctx.tenantId);
                log.debug("Role Permissions" + rolePermissions);
                if(rolePermissions == ""){
                    log.debug("No permissions for admin adding");
                    var defaultAdminPermssion = ["ENTERPRISEWIPE", "ENCRYPT", "MUTE", "CAMERA", "CLEARPASSCODE", "WIPE", "LOCK", "NOTIFICATION", "CHANGEPASSWORD", "LDAP", "VPN", "GOOGLECALENDAR", "EMAIL", "PASSWORDPOLICY", "WEBCLIP", "APN", "WIFI"];
                    sucessAddingPermissions = driver.query(sqlscripts.permissions.insert1,'admin',defaultAdminPermssion, ctx.tenantId);
                }

                //this is for automatically publish, publisher and reviwer roles at the time of tenent admin login
                var server = application.get("SERVER");
                var um = new carbon.user.UserManager(server,  ctx.tenantId);
                var arrPermission = {};
                arrPermission["/permission/admin/login"] = ["ui.execute"];

                var roles = ["Internal/reviewer", "Internal/publisher", "Internal/store" ];
                for(var i = 0; i < roles.length; i++){
                   
                    if (um.roleExists(roles[i])) {
                        um.authorizeRole(roles[i],  arrPermission);
                    } else {
                        log.debug("ROLE CREATED" + roles[i] );
                        um.addRole(roles[i], [], arrPermission);
                        um.authorizeRole(roles[i], arrPermission);
                    }
                }
				
                //publishing APIs / subscribing APIs / consumer key and consumer secret
                var tenantInfo = {};
                tenantInfo.tenantId = ctx.tenantId;
                //log.info("tenant id " + ctx.tenantId);
                tenantInfo.domain = carbon.server.tenantDomain(ctx);
                var oAuthClientKey  = user.getOAuthClientKey(parseInt(ctx.tenantId));
                //log.info("Stored API consumer keys " + stringify(oAuthClientKey));
                if(!oAuthClientKey){
                   var properties = apimgr.initAPISubscription(tenantInfo);
                    //log.info( "API consumer keys " +  stringify(properties));
                   if(properties.prodConsumerKey && properties.prodConsumerSecret){
                       user.saveOAuthClientKey(parseInt(ctx.tenantId), properties.prodConsumerKey, properties.prodConsumerSecret);
                   }else{
                       log.error("Error in getting Consumer key & secret.");
                   }
                }
            }
            
            var tenantId = parseInt(common.getTenantID());
            user.defaultTenantConfiguration(tenantId);
        }
    };

    // return module
    return module;
})();
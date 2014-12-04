var user = (function() {
    var log = new Log();
    var module = function(db, router) {
        var userModule = require('/modules/user.js').user;
        var user = new userModule(db);
        var common = require("/modules/common.js");
        var constants = require("/modules/constants.js");
        router.post('users/authenticate/', function(ctx) {
            var objUser = user.authenticate(ctx);
            if (!objUser) {
                response.status = 401;
                print(constants.AUTH_FAIL_MESSAGE);
            } else {
                //If user category is admin or if normal user has devices allow login
                response.status = 200;
                var userAgent = request.getHeader("User-Agent");
                var android = userAgent.indexOf("Android");
                if (android > 0) {
                    print("200");
                } else {
                    var userFeed = {};
                    userFeed.tenantId = String(objUser["tenantId"]);
                    userFeed.username = objUser["username"];
                    userFeed.email = objUser["email"];
                    userFeed.firstName = objUser["firstName"];
                    userFeed.lastName = objUser["lastName"];
                    userFeed.mobile = objUser["mobile"];
                    var parsedRoles = parse(objUser["roles"]);
                    var isAdmin = false;
                    for (var i = 0; i < parsedRoles.length; i++) {
                        if (parsedRoles[i] == 'admin') {
                            isAdmin = true;
                            break;
                        }
                    }
                    userFeed.isAdmin = isAdmin;
                    log.debug("User object - " + stringify(userFeed));
                    print(stringify(userFeed));
                }
            }
        });
        router.get('users/authenticate/', function(ctx) {
            var objUser = session.get("user");
            if (objUser != null) {
                print(objUser);
                return;
            }
            response.status = 401;
            print(constants.AUTH_FAIL_MESSAGE);
        });
        router.get('users/unauthenicate/', function(ctx) {
            session.put("user", null);
            response.status = 200;
        });
        router.post('users/changepassword/', function(ctx) {
            try {
                user.changePassword(ctx);
            } catch (e) {
                response.status = 400;
                log.error(e);
                print(constants.PASSWORD_CHANGE_FAIL_MESSAGE)
            }
        });
        router.get('users/devices/enrolled/{+userid}', function(ctx) {
            var hasDevices = user.hasDevicesenrolled(ctx);
            if (hasDevices) {
                response.status = 200;
                response.content = hasDevices;
            } else {
                response.status = 404;
                print(constants.GENERIC_ERROR_MESSAGE);
            }
        });
        router.get('users/{userid}/sendmail', function(ctx) {
            var u = user.getUser(ctx)[0];
            if (u) {
                if (user.isEmailConfigured) {
                    user.sendEmail(u.email, u.first_name);
                    log.debug('Email sent to user with id ' + u.email);
                    return;
                } else {
                    response.status = 403;
                    print(constants.USER_EMAIL_NOT_FOUND_MESSAGE);
                    return;
                }
            }
            response.status = 404;
            print(constants.USER_NOT_FOUND_MESSAGE);
        });
        router.get('users/{userid}', function(ctx) {
            var userObj = user.getUser(ctx);
            if (userObj) {
                print(userObj);
                response.status = 200;
            } else {
                print(constants.USER_NOT_FOUND_MESSAGE);
                response.status = 404;
            }
        });
        router.post('users/', function(ctx) {
            var returnMsg = user.addUser(ctx);
            if (returnMsg.status == constants.ERROR_ALL_READY_EXIST) {
                response.status = 409;
                response.content = returnMsg.error;
            } else if (returnMsg.status == constants.SUCCESS) {
                ctx.generatedPassword = returnMsg.generatedPassword;
                ctx.firstName = returnMsg.firstName;
                if (user.isEmailConfigured()) {
                    user.sendEmail(ctx);
                    response.status = 201;
                    print(constants.SUCCESS);
                } else {
                    response.status = 201;
                    response.content = user.getEnrollmentInfo(ctx);
                }
            } else if (returnMsg.status == constants.ERROR_BAD_REQUEST) {
                response.status = 400;
                print(returnMsg.error);
            } else {
                print(constants.GENERIC_ERROR_MESSAGE);
                response.status = 400;
            }
        });
        router.delete('users/{+userid}', function(ctx) {
            var result = user.deleteUser(ctx);
            if (result == 404) {
                response.status = 404;
                print(constants.USER_DELETE_ERROR_MESSAGE);
            } else if (result == 200) {
                response.status = 200;
                print(constants.USER_DELETE_MESSAGE);
            }
        });
        router.get('users/{+username}/groups/', function(ctx) {
            var groups = user.getRolesByUser(ctx);
            if (groups[0]) {
                response.status = 200;
                print(stringify(groups));
            }
        });
        router.put('users/groups', function(ctx) {
            var groups = user.updateRoleListOfUser(ctx);
            response.status = 200;
            print(groups);
        });
        router.put('users/{+username}/groups/', function(ctx) {
            var groups = user.updateRoleListOfUser(ctx);
            response.status = 200;
            print(groups);
        });
        router.get('users/', function(ctx) {
            var users = user.getAllUsers(ctx);
            if (users[0]) {
                print(users);
                response.status = 200;
            } else {
                response.status = 404;
            }
        });
        router.get('users/{userid}/devices', function(ctx) {
            var devices = user.devices(ctx);
            if (devices[0]) {
                print(devices);
                response.status = 200;
            } else {
                response.status = 404;
            }
        });
        router.put('users/invite', function(ctx) {
            var u = user.getUser(ctx);
            if (u) {
                if (user.isEmailConfigured()) {
                    user.sendEmail({
                        'email': String(u.email),
                        'firstName': String(u.firstName)
                    });
                    return;
                } else {
                    response.status = 403;
                    print(constants.USER_EMAIL_NOT_FOUND_MESSAGE);
                    return;
                }
            }
            response.status = 404;
            print(constants.USER_NOT_FOUND_MESSAGE);
        });
        router.post('users/{userid}/operations/{operation}', function(ctx) {
            device.sendMsgToUserDevices(ctx);
        });
        /*
            	Api that sends the Tenant configurations
         	*/
        router.post('tenant/configuration', function(ctx) {
            session.remove("uiTenantConf");
            try {
                var iosMDMCert = request.getFile("iosMDMCert");
                var iosAPNSCert = request.getFile("iosAPNSCert");
                var status = user.saveTenantConfiguration(ctx, iosMDMCert, iosAPNSCert);
                if (status) {
                    response.status = 200;
                } else {
                    response.status = 400;
                }
            } catch (e) {
                log.error(e);
                response.status = 400;
                print(constants.GENERIC_ERROR_MESSAGE);
            }
        });
        /*
            Api to retrieve the Tenant Configurations
         */
        router.get('tenant/configuration', function(ctx) {
            try {
                var configData = user.getTenantConfiguration(ctx);
                print(configData);
            } catch (e) {
                log.error(e);
                response.status = 400;
                print(constants.GENERIC_ERROR_MESSAGE);
            }
        });
    };
    // prototype
    module.prototype = {
        constructor: module
    };
    // return module
    return module;
})();
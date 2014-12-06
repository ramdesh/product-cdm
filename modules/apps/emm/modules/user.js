var TENANT_CONFIGS = 'tenant.configs';
var USER_MANAGER = 'user.manager';
var USER_OPTIONS = 'server.user.options';
//Need to change this
var USER_SPACE = '/_system/governance/';
var EMM_USER_SESSION = "emmConsoleUser";
var user = (function() {
    var config = require('/config/emm.js').config();
    var routes = [];
    var log = new Log();
    var db;
    var driver;
    var common = require("/modules/common.js");
    var sqlscripts = require('/sqlscripts/db.js');
    var constants = require('/modules/constants.js');
    var carbon = require('carbon');
    var current_user = session.get(EMM_USER_SESSION);
    var server = function() {
        return application.get("SERVER");
    }
    var claimEmail = "http://wso2.org/claims/emailaddress";
    var claimFirstName = "http://wso2.org/claims/givenname";
    var claimLastName = "http://wso2.org/claims/lastname";
    var claimMobile = "http://wso2.org/claims/mobile";
    var storeRegistry = require('store').server;
    var module = function(dbs) {
        db = dbs;
        driver = require('driver').driver(db);
    };
    /**
     * Returns the user's registry space. This should be called once with the username,
     * then can be called without the username.
     * @param usr user object
     * @return {*}
     */
    var userSpace = function(username, tenantId) {
        try {
            var indexUser = username.replace("@", ":");
            return USER_SPACE + '/' + indexUser;
        } catch (e) {
            log.error(e);
            return null;
        }
    };
    var configs = function(tenantId) {
        var configg = application.get(TENANT_CONFIGS);
        if (!tenantId) {
            return configg;
        }
        return configs[tenantId] || (configs[tenantId] = {});
    };
    /**
     * Returns the user manager of the given tenant.
     * @param tenantId
     * @return {*}
     */
    var userManager = function(tenantId) {
        var config = configs(tenantId);
        if (!config || !config[USER_MANAGER]) {
            var um = new carbon.user.UserManager(server, tenantId);
            config[USER_MANAGER] = um;
            return um;
        }
        return configs(tenantId)[USER_MANAGER];
    };
    var createPrivateRolePerUser = function(username, roleState) {
        var um = userManager(common.getTenantID());
        var indexUser = username.replace("@", ":");
        var arrPermission = {};
        var space = userSpace(username, common.getTenantID());
        var permission = [
            carbon.registry.actions.GET,
            carbon.registry.actions.PUT,
            carbon.registry.actions.DELETE,
            carbon.registry.actions.AUTHORIZE
        ];
        arrPermission[space] = permission;
        arrPermission["/permission/admin/login"] = ["ui.execute"];
        if (roleState.toUpperCase() == constants.EMMADMIN) {
            arrPermission["/permission/admin/manage"] = ["ui.execute"];
        }
        if (!um.roleExists("Internal/private_" + indexUser)) {
            var private_role = "Internal/private_" + indexUser;
            um.addRole(private_role, [username], arrPermission);
            um.authorizeRole(private_role, arrPermission);
        }
    }
    var getUserType = function(user_roles) {
        for (var i = user_roles.length - 1; i >= 0; i--) {
            var role = user_roles[i].toUpperCase();
            if (role == constants.ROLE_ADMIN || role == constants.ROLE_INTERNAL_EMMADMIN || 
                role == constants.ROLE_INTERNAL_MAMADMIN) {
                return constants.ROLE_VIEW_ADMINISTRATOR;
            } else {
                return constants.ROLE_VIEW_USER;
            }
        };
    }

        function generatePassword() {
            var length = 6,
                charset = "abcdefghijklnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789",
                retVal = "";
            for (var i = 0, n = charset.length; i < length; ++i) {
                retVal += charset.charAt(Math.floor(Math.random() * n));
            }
            return retVal;
        }
        // prototype
    module.prototype = {
        constructor: module,
        generatePassword: generatePassword,
        addUser: function(ctx) {
            var claimMap = new java.util.HashMap();
            var roleState = null;
            claimMap.put(claimEmail, ctx.email);
            claimMap.put(claimFirstName, ctx.first_name);
            claimMap.put(claimLastName, ctx.last_name);
            claimMap.put(claimMobile, ctx.mobile_no);
            var proxyUser = {};
            try {
                var tenantId = common.getTenantID();
                var users_list = [];
                if (tenantId) {
                    var um = userManager(tenantId);
                    if (um.userExists(ctx.username)) {
                        proxyUser.error = constants.ALL_READY_EXIST_MESSAGE;
                        proxyUser.status = constants.ERROR_ALL_READY_EXIST;
                    } else {
                        var generated_password = generatePassword();
                        if (ctx.type.toUpperCase() == constants.USER) {
                            roleState = "";
                            um.addUser(ctx.username, generated_password, ctx.groups, claimMap, null);
                        } else if (ctx.type.toUpperCase() == constants.ADMINISTRATOR) {
                            roleState = constants.TYPE_EMM_ADMIN;
                            um.addUser(ctx.username, generated_password, 
                                [constants.ROLE_INTERNAL_EMMADMIN], claimMap, null);
                        }
                        createPrivateRolePerUser(ctx.username, roleState);
                        proxyUser.status = constants.SUCCESS;
                        proxyUser.firstName = ctx.first_name;
                        proxyUser.generatedPassword = generated_password;
                    }
                } else {
                    log.error('Error in getting the tenantId from session');
                    print(constants.GENERIC_ERROR_MESSAGE);
                }
            } catch (e) {
                log.error(e);
                proxyUser.status = constants.ERROR_BAD_REQUEST;
                proxyUser.error = constants.USER_CREATION_ERROR_MESSAGE;
            }
            return proxyUser;
        },
        getUser: function(ctx) {
            try {
                var proxyUser = {};
                var username = ctx.userid;
                if (username.indexOf("@") < 1) {
                    username = username + "@"+ constants.CARBONSUPER_TENANT_NAME;
                }
                var tenantUser = carbon.server.tenantUser(username);
                if (ctx.login) {
                    var um = userManager(tenantUser.tenantId);
                } else {
                    var um = userManager(common.getTenantID());
                }
                var user = um.getUser(tenantUser.username);
                var userRoles = user.getRoles();
                var claims = [claimEmail, claimFirstName, claimLastName];
                var claimResult = user.getClaimsForSet(claims, null);
                proxyUser.email = claimResult.get(claimEmail);
                proxyUser.firstName = claimResult.get(claimFirstName);
                proxyUser.lastName = claimResult.get(claimLastName);
                proxyUser.mobile = claimResult.get(claimMobile);
                proxyUser.username = tenantUser.username;
                proxyUser.tenantId = tenantUser.tenantId;
                proxyUser.roles = stringify(userRoles);
                proxyUser.user_type = getUserType(userRoles);
                if (proxyUser.roles.indexOf(constants.TYPE_ADMIN) >= 0) {
                    if (proxyUser.firstName) {
                        proxyUser.firstName = constants.ROLE_VIEW_ADMINISTRATOR;
                        proxyUser.lastName = constants.ROLE_VIEW_ADMINISTRATOR;
                    }
                }
                return proxyUser;
            } catch (e) {
                log.error(e);
                var error = constants.USER_RETRIEVAL_ERROR_MESSAGE;
                return error;
            }
        },
        //Deprecated
        getAllUsers: function(ctx) {
            var tenantId = common.getTenantID();
            var usersList = [];
            if (tenantId) {
                var um = userManager(tenantId);
                var allUsers = um.listUsers();
                var removeUsers = ["wso2.anonymous.user", "admin", "admin@admin.com"];
                var users = common.removeNecessaryElements(allUsers, removeUsers);
                for (var i = 0; i < users.length; i++) {
                    var user = um.getUser(users[i]);
                    var claims = [claimEmail, claimFirstName, claimLastName];
                    var claimResult = user.getClaimsForSet(claims, null);
                    var proxyUser = {};
                    proxyUser.username = users[i];
                    proxyUser.email = claimResult.get(claimEmail);
                    proxyUser.firstName = claimResult.get(claimFirstName);
                    proxyUser.lastName = claimResult.get(claimLastName);
                    proxyUser.mobile = claimResult.get(claimMobile);
                    proxyUser.tenantId = tenantId;
                    proxyUser.roles = stringify(user.getRoles());
                    usersList.push(proxyUser);
                }
            } else {
                print(constants.GENERIC_ERROR_MESSAGE);
            }
            return usersList;
        },
        getAllUserNames: function(filter) {
            var tenantId = common.getTenantID();
            var userList = [];
            if (tenantId) {
                var um = userManager(common.getTenantID());
                if (filter) {
                    var allUsers = um.listUsers(filter);
                } else {
                    var allUsers = um.listUsers();
                }
                var removeUsers = [constants.ANONYMOUS_USER, constants.ADMIN_USER, constants.ADMIN_EMAIL_USER];
                var users = common.removeNecessaryElements(allUsers, removeUsers);
                userList = users;
            } else {
                print(constants.GENERIC_ERROR_MESSAGE);
            }
            return userList;
        },
        getAllUserNamesByRole: function(ctx) {
            var tenantId = common.getTenantID();
            var userList = [];
            if (tenantId) {
                var um = userManager(common.getTenantID());
                var usersByRole = um.getUserListOfRole(ctx.groupid);
                var removeUsers = [constants.ANONYMOUS_USER, constants.ADMIN_USER, constants.ADMIN_EMAIL_USER];
                var users = common.removeNecessaryElements(usersByRole, removeUsers);
                userList = users;
            } else {
                print(constants.GENERIC_ERROR_MESSAGE);
            }
            return userList;
        },
        deleteUser: function(ctx) {
            var result = driver.query(sqlscripts.devices.select36, ctx.userid);
            if (result && result[0].length > 0) {
                return 404;
            } else {
                var um = userManager(common.getTenantID());
                um.removeUser(ctx.userid);
                var private_role = ctx.userid.replace("@", ":");
                um.removeRole(constants.PRIVATE_ROLE_PREFIX + private_role);
                return 200;
            }
        },
        getUserRoles: function(ctx) {
            var username = ctx.username;
            if (username.indexOf("@") < 1) {
                username = username + "@"+constants.CARBONSUPER_TENANT_NAME;
            }
            var tenantUser = carbon.server.tenantUser(username);
            var um = userManager(common.getTenantID());
            var roles = um.getRoleListOfUser(tenantUser.username);
            var roleList = common.removePrivateRole(roles);
            return roleList;
        },
        updateRoleListOfUser: function(ctx) {
            var existingRoles = this.getUserRoles(ctx);
            var addedRoles = ctx.added_groups;
            var newRoles = [];
            for (var i = 0; i < addedRoles.length; i++) {
                var flag = false;
                for (var j = 0; j < existingRoles.length; j++) {
                    if (addedRoles[i] == existingRoles[j]) {
                        flag = true;
                        break;
                    } else {
                        flag = false;
                    }
                }
                if (flag == false) {
                    newRoles.push(addedRoles[i]);
                }
            }
            var removedRoles = ctx.removed_groups;
            var deletedRoles = [];
            for (var i = 0; i < removedRoles.length; i++) {
                var flag = false;
                for (var j = 0; j < existingRoles.length; j++) {
                    if (removedRoles[i] == existingRoles[j]) {
                        flag = true;
                        break;
                    } else {
                        flag = false;
                    }
                }
                if (flag == true) {
                    deletedRoles.push(removedRoles[i]);
                }
            }
            var um = userManager(common.getTenantID());
            um.updateRoleListOfUser(ctx.username, deletedRoles, newRoles);
        },
        getUsersByType: function(ctx) { 
            var type = ctx.type;
            var usersByType = [];
            var users = this.getAllUsers();
            for (var i = 0; i < users.length; i++) {
                var roles = this.getUserRoles({
                    'username': users[i].username
                });
                var flag = 0;
                for (var j = 0; j < roles.length; j++) {
                    var role = roles[j].toUpperCase();
                    if ((role == constants.ROLE_ADMIN) || (role == constants.ROLE_INTERNAL_EMMADMIN)) {
                        flag = 1;
                        break;
                    } else if ((role == constants.ROLE_INTERNAL_PUBLISHER) || (role == constants.ROLE_INTERNAL_REVIEWER) || (role == constants.ROLE_INTERNAL_STORE) || (role == constants.ROLE_INTERNAL_MAMADMIN)) {
                        flag = 2;
                        break;
                    } else {
                        flag = 0;
                    }
                }
                if (flag == 1) {
                    users[i].type = constants.TYPE_ADMINISTRATOR;
                    if (type == constants.TYPE_ADMIN) {
                        usersByType.push(users[i]);
                    }
                } else if (flag == 2) {
                    users[i].type = constants.TYPE_MAM;
                    usersByType.push(users[i]);
                } else {
                    users[i].type = constants.POLICY_USER;
                    usersByType.push(users[i]);
                }
            }
            return usersByType;
        },
        hasDevicesenrolled: function(ctx) {
            //Check if user has any devices enrolled
            try {
                var tenantId = common.getTenantID();
                if (tenantId) {
                    var devices = driver.query(sqlscripts.devices.select46, ctx.userid, tenantId);
                    if (devices && devices[0]) {
                        if (devices[0].count > 0) {
                            return true;
                        }
                    }
                    return false;
                } else {
                    log.debug("Not able to get Tenant ID from Session");
                }
            } catch (e) {
                print(constants.GENERIC_ERROR_MESSAGE);
                log.error(e);
            }
        },
        /*
         Save default values to the tenant
         */
        defaultTenantConfiguration: function(tenantId) {
            var properties = this.getTenantCopyRight(tenantId);
            if (properties == null) {
                var defaultData = '{' + '"' + constants.EMAIL_SMTP_HOST + '"  : "' + config.DEFAULT.EMAIL.SMTPHOST + '", ' + '"' + constants.EMAIL_SMTP_PORT + '" : "' + config.DEFAULT.EMAIL.SMTPPORT + '", ' + '"' + constants.EMAIL_USERNAME + '" : "' + config.DEFAULT.EMAIL.USERNAME + '", ' + '"' + constants.EMAIL_PASSWORD + '" : "' + config.DEFAULT.EMAIL.PASSWORD + '", ' + '"' + constants.EMAIL_SENDER_ADDRESS + '" : "' + config.DEFAULT.EMAIL.SENDERADDRESS + '", ' + '"' + constants.EMAIL_TEMPLATE + '" : "' + config.DEFAULT.EMAIL.TEMPLATE + '", ' + '"' + constants.UI_TITLE + '" : "' + config.DEFAULT.UITITLE + '", ' + '"' + constants.UI_COPYRIGHT + '" : "' + config.DEFAULT.UICOPYRIGHT + '", ' + '"' + constants.UI_LICENSE + '" : "' + config.DEFAULT.UILICENSE + '", ' + '"' + constants.COMPANY_NAME + '" : "' + config.DEFAULT.COMPANYNAME + '", ' + '"' + constants.ANDROID_NOTIFIER + '" : "' + config.DEFAULT.ANDROID.NOTIFIER + '", ' + '"' + constants.ANDROID_NOTIFIER_FREQUENCY + '" : "' + config.DEFAULT.ANDROID.NOTIFIER_INTERVAL + '", ' + '"' + constants.ANDROID_API_KEYS + '" : "' + config.DEFAULT.ANDROID.APIKEY + '", ' + '"' + constants.ANDROID_SENDER_IDS + '" : "' + config.DEFAULT.ANDROID.SENDERID + '"' + '}';
                this.saveTenantConfiguration(parse(defaultData), null, null, tenantId, "true");
            }
        },
        /*
         Save tenant configuration to the Registry
         */
        saveTenantConfiguration: function(ctx, iOSMDMFile, iOSAPNSFile, tenantId, defaultConfig) {
            if (tenantId) {
                tenantId = parseInt(tenantId);
            } else {
                tenantId = parseInt(common.getTenantID());
            }
            var sessionInfo = common.getSessionInfo();
            var sessionUserId;
            if (sessionInfo) {
                sessionUserId = sessionInfo.username;
            }
            try {
                var isSuccess = storeRegistry.sandbox({
                    tenantId: tenantId,
                    username: sessionUserId
                }, function() {
                    var registry = storeRegistry.systemRegistry(tenantId);
                    if (defaultConfig == null) {
                        defaultConfig = constants.BOOL_FALSE;
                        var iosMDMTopic = ctx.iosMDMTopic;
                        var iOSMDMPassword = ctx.iosMDMPass;
                        var iOSAPNSPassword = ctx.iosAPNSPass;
                        var iOSMDMProduction, iOSAPNSProduction;
                        var iOSMDMStream = "",
                            iOSAPNSStream = "";
                        if (ctx.iosAPNSMode == constants.PRODUCTION) {
                            iOSAPNSProduction = constants.BOOL_TRUE;
                        } else {
                            iOSAPNSProduction = constants.BOOL_FALSE;
                        }
                        if (ctx.iosMDMMode == constants.PRODUCTION) {
                            iOSMDMProduction = constants.BOOL_TRUE;
                        } else {
                            iOSMDMProduction = constants.BOOL_FALSE;
                        }
                        if (ctx.iosMDMCertModified == constants.BOOL_TRUE) {
                            if (iOSMDMFile == null) {
                                registry.remove(config.registry.iOSMDMCertificate);
                            } else {
                                iOSMDMFile.open("r");
                                iOSMDMStream = iOSMDMFile.getStream();
                                registry.put(config.registry.iOSMDMCertificate, {
                                    content: iOSMDMStream,
                                    properties: {
                                        TopicID: iosMDMTopic,
                                        Password: iOSMDMPassword,
                                        Production: iOSMDMProduction,
                                        Filename: iOSMDMFile.getName()
                                    }
                                });
                                iOSMDMFile.close();
                            }
                        }
                        if (ctx.iosAPNSCertModified == constants.BOOL_TRUE) {
                            if (!iOSAPNSFile|| !iOSAPNSPassword|| !iOSAPNSProduction) {
                                registry.remove(config.registry.iOSAppCertificate);
                            } else {
                                iOSAPNSFile.open("r");
                                iOSAPNSStream = iOSAPNSFile.getStream();
                                registry.put(config.registry.iOSAppCertificate, {
                                    content: iOSAPNSStream,
                                    properties: {
                                        Password: iOSAPNSPassword,
                                        Production: iOSAPNSProduction,
                                        Filename: iOSAPNSFile.getName()
                                    }
                                });
                                iOSAPNSFile.close();
                            }
                        }
                        if (ctx.iosSCEPEmail.trim()) {
                            //C="COUNTRY" ST="STATE" L="LOCALITY" O="ORGANISATION" OU="ORGANISATIONUNIT" E="Email"
                            registry.put(config.registry.scepConfiguration, {
                                content: config.registry.scepConfiguration,
                                properties: {
                                    E: ctx.iosSCEPEmail.trim(),
                                    C: ctx.iosSCEPCountry.trim(),
                                    ST: ctx.iosSCEPState.trim(),
                                    L: ctx.iosSCEPLocality.trim(),
                                    O: ctx.iosSCEPOrganisation.trim(),
                                    OU: ctx.iosSCEPOrganisationUnit.trim()
                                }
                            });
                        }
                    }
                    if (!ctx.emailUsername|| !ctx.emailSmtpHost|| !ctx.emailSmtpPort) {
                        registry.remove(config.registry.emailConfiguration);
                    } else {
                        registry.put(config.registry.emailConfiguration, {
                            content: config.registry.emailConfiguration,
                            properties: {
                                SMTP: ctx.emailSmtpHost.trim(),
                                Port: ctx.emailSmtpPort.trim(),
                                UserName: ctx.emailUsername.trim(),
                                Password: ctx.emailPassword.trim(),
                                SenderAddress: ctx.emailSenderAddress.trim(),
                                EmailTemplate: ctx.emailTemplate.trim()
                            }
                        });
                    }
                    //Client Serect and Client Key
                    if (ctx.clientkey) {
                        registry.put(config.registry.oauthClientKey, {
                            content: config.registry.oauthClientKey,
                            properties: {
                                ClientKey: ctx.clientkey.trim(),
                                ClientSecret: ctx.clientsecret.trim()
                            }
                        });
                    }
                    //Android GCM keys
                    registry.put(config.registry.androidGCMKeys, {
                        content: config.registry.androidGCMKeys,
                        properties: {
                            APIKeys: ctx.androidApiKeys.trim(),
                            SenderIds: ctx.androidSenderIds.trim(),
                            AndroidMonitorType: ctx.androidNotifier.trim(),
                            AndroidNotifierFreq: ctx.androidNotifierFreq
                        }
                    });
                    if (!ctx.uiLicence|| !ctx.uiLicence.trim()) {
                        registry.remove(config.registry.tenantLicense);
                    } else {
                        registry.put(config.registry.tenantLicense, {
                            content: ctx.uiLicence.trim()
                        });
                    }
                    registry.put(config.registry.copyright, {
                        content: config.registry.copyright,
                        properties: {
                            CompanyName: ctx.companyName.trim(),
                            Title: ctx.uiTitle.trim(),
                            Footer: ctx.uiCopyright.trim(),
                            default: defaultConfig
                        }
                    });
                    return true;
                });
                if (isSuccess == true) {
                    return true;
                } else {
                    return false;
                }
            } catch (e) {
                print(constants.GENERIC_ERROR_MESSAGE);
                log.error(e);
            }
        },
        /*
         Retrieve the Tenant configuration
         */
        getTenantConfiguration: function(ctx) {
            var tenantId = parseInt(common.getTenantID());
            var androidGCMKeys = this.getAndroidGCMKeys(tenantId);
            var iOSMDMConfigurations = this.getiOSMDMConfigurations(tenantId);
            var iOSAPNSConfigurations = this.getiOSAPNSConfigurations(tenantId);
            var emailConfigurations = this.getEmailConfigurations(tenantId);
            var scepConfiguration = this.getSCEPConfiguration(tenantId);
            var license = this.getTenantLicense(tenantId);
            var tenantCopyRight = this.getTenantCopyRight(tenantId);
            var jsonBuilder = {};
            if (androidGCMKeys) {
                jsonBuilder.androidApiKeys = androidGCMKeys.APIKeys[0];
                jsonBuilder.androidSenderIds = androidGCMKeys.SenderIds[0];
                jsonBuilder.androidNotifier = androidGCMKeys.AndroidMonitorType[0];
                jsonBuilder.androidNotifierFreq = androidGCMKeys.AndroidNotifierFreq[0];
            } else {
                jsonBuilder.androidApiKeys = "";
                jsonBuilder.androidSenderIds = "";
                jsonBuilder.androidNotifier = contants.NOTIFIER_LOCAL;
                jsonBuilder.androidNotifierFreq = "0";
            }
            if (iOSMDMConfigurations) {
                jsonBuilder.iosMDMPass = iOSMDMConfigurations.properties.Password[0];
                jsonBuilder.iosMDMCertFileName = iOSMDMConfigurations.properties.Filename[0];
                jsonBuilder.iosMDMTopic = iOSMDMConfigurations.properties.TopicID[0];
                if (iOSMDMConfigurations.properties.Production[0] = constants.BOOL_TRUE) {
                    jsonBuilder.iosMDMMode = constants.PRODUCTION;
                } else {
                    jsonBuilder.iosMDMMode = constants.PRODUCTION;
                }
            } else {
                jsonBuilder.iosMDMPass = "";
                jsonBuilder.iosMDMTopic = "";
                jsonBuilder.iosMDMMode = constants.PRODUCTION;
            }
            if (iOSAPNSConfigurations != null) {
                jsonBuilder.iosAPNSPass = iOSAPNSConfigurations.properties.Password[0];
                jsonBuilder.iosAPNSCertFileName = iOSAPNSConfigurations.properties.Filename[0];
                if (iOSAPNSConfigurations.properties.Production[0] = constants.BOOL_TRUE) {
                    jsonBuilder.iosAPNSMode = constants.PRODUCTION;
                } else {
                    jsonBuilder.iosAPNSMode = constants.DEVELOPER;
                }
            } else {
                jsonBuilder.iosAPNSPass = "";
                jsonBuilder.iosAPNSMode = constants.PRODUCTION;
            }
            if (emailConfigurations) {
                jsonBuilder.emailSmtpHost = emailConfigurations.SMTP[0];
                jsonBuilder.emailSmtpPort = emailConfigurations.Port[0];
                jsonBuilder.emailUsername = emailConfigurations.UserName[0];
                jsonBuilder.emailPassword = emailConfigurations.Password[0];
                jsonBuilder.emailSenderAddress = emailConfigurations.SenderAddress[0];
                jsonBuilder.emailTemplate = emailConfigurations.EmailTemplate[0];
            }
            if (scepConfiguration) {
                jsonBuilder.iosSCEPEmail = scepConfiguration.E[0];
                jsonBuilder.iosSCEPCountry = scepConfiguration.C[0];
                jsonBuilder.iosSCEPState = scepConfiguration.ST[0];
                jsonBuilder.iosSCEPLocality = scepConfiguration.L[0];
                jsonBuilder.iosSCEPOrganisation = scepConfiguration.O[0];
                jsonBuilder.iosSCEPOrganisationUnit = scepConfiguration.OU[0];
            } else {
                jsonBuilder.iosSCEPEmail = "";
                jsonBuilder.iosSCEPCountry = "";
                jsonBuilder.iosSCEPState = "";
                jsonBuilder.iosSCEPLocality = "";
                jsonBuilder.iosSCEPOrganisation = "";
                jsonBuilder.iosSCEPOrganisationUnit = "";
            }
            if (license) {
                jsonBuilder.uiLicence = license.toString();
            }
            if (tenantCopyRight) {
                jsonBuilder.companyName = tenantCopyRight.CompanyName[0];
                jsonBuilder.uiTitle = tenantCopyRight.Title[0];
                jsonBuilder.uiCopyright = tenantCopyRight.Footer[0];
            } else {
                jsonBuilder.uiTitle = "";
                jsonBuilder.uiCopyright = "";
            }
            return jsonBuilder;
        },
        /*
         Retrieve registry value
         */
        getRegistry: function(tenantId, registryPath) {
            return storeRegistry.sandbox({
                tenantId: tenantId
            }, function() {
                var registryObj = {};
                var registry = storeRegistry.systemRegistry(tenantId);
                var resoucre = registry.get(registryPath);
                if (resoucre) {
                    registryObj.content = resoucre.content;
                    registryObj.properties = resoucre.properties();
                    return registryObj;
                } else {
                    return null;
                }
            });
        },
        /*
         Retrieve the Android GCM Keys for tenant from registry
         */
        getAndroidGCMKeys: function(tenantId) {
            var resource = this.getRegistry(tenantId, config.registry.androidGCMKeys);
            if (resource) {
                return resource.properties;
            }
            return null;
        },
        /*
         Retrieve email configuration for tenant from registry
         */
        getEmailConfigurations: function(tenantId) {
            var resource = this.getRegistry(tenantId, config.registry.emailConfiguration);
            if (resource) {
                return resource.properties;
            }
            return null;
        },
        /*
         Retrieve SCEP configuration
         */
        getSCEPConfiguration: function(tenantId) {
            var resource = this.getRegistry(tenantId, config.registry.scepConfiguration);
            if (resource) {
                return resource.properties;
            }
            return null;
        },
        /*
         Retrieve Copyright
         */
        getTenantCopyRight: function(tenantId) {
            var resource = this.getRegistry(tenantId, config.registry.copyright);
            if (resource) {
                return resource.properties;
            }
            return null;
        },
        /*
         Retrieve Tenant's OAuth Client ID and Client Secret
         */
        getOAuthClientKey: function(tenantId) {
            var resource = this.getRegistry(tenantId, config.registry.oauthClientKey);
            if (resource) {
                return resource.properties;
            }
            return null;
        },
        /*
         Retrieve License
         */
        getTenantLicense: function(tenantId) {
            var resource = this.getRegistry(tenantId, config.registry.tenantLicense);
            if (resource) {
                return resource.content;
            } else {
                return null;
            }
        },
        /*
         Retrieve MDM Configurations
         */
        getiOSMDMConfigurations: function(tenantId) {
            var resource = this.getRegistry(tenantId, config.registry.iOSMDMCertificate);
            if (resource) {
                var iOSMDMConfiguration = {};
                iOSMDMConfiguration.inputStream = resource.content;
                iOSMDMConfiguration.properties = resource.properties;
                return iOSMDMConfiguration;
            } else {
                return null;
            }
        },
        /*
         Retrieve APNS Configurations
         */
        getiOSAPNSConfigurations: function(tenantId) {
            var resource = this.getRegistry(tenantId, config.registry.iOSAppCertificate);
            if (resource) {
                var iOSAppConfiguration = {};
                iOSAppConfiguration.inputStream = resource.content;
                iOSAppConfiguration.properties = resource.properties;
                return iOSAppConfiguration;
            } else {
                return null;
            }
        },
        /*
         Save Consumer Key and Consumer Secret to Registry
         */
        saveOAuthClientKey: function(tenantId, consumerKey, consumerSecret) {
            var sessionInfo = common.getSessionInfo();
            var sessionUserId;
            if (sessionInfo) {
                sessionUserId = sessionInfo.username;
            } else {
                sessionUserId = null;
            }
            try {
                var isSuccess = storeRegistry.sandbox({
                    tenantId: tenantId,
                    username: sessionUserId
                }, function() {
                    var registry = storeRegistry.systemRegistry(tenantId);
                    registry.put(config.registry.oauthClientKey, {
                        content: config.registry.oauthClientKey,
                        properties: {
                            ClientKey: consumerKey,
                            ClientSecret: consumerSecret
                        }
                    });
                    return true;
                });
                if (isSuccess == true) {
                    return true;
                } else {
                    return false;
                }
            } catch (e) {
                log.error(e);
                return null;
            }
        },
        authenticate: function(ctx) {
            ctx.username = ctx.username;
            try {
                var authStatus = server().authenticate(ctx.username, ctx.password);
            } catch (e) {
                log.error(e);
                return null;
            }
            if (!authStatus) {
                return null;
            }
            var user = this.getUser({
                'userid': ctx.username,
                login: true
            });
            return user;
        },
        /*send email to particular user*/
        sendEmail: function(ctx) {
            var tenantId = parseInt(common.common.getTenantID());
            var emailConfigurations = this.getEmailConfigurations(tenantId);
            var tenantCopyRight = this.getTenantCopyRight(tenantId);
            if (emailConfigurations != null) {
                if (String(emailConfigurations.UserName[0].trim()) != "") {
                    var password_text = "";
                    if (ctx.generatedPassword) {
                        password_text = "Your password to your login : " + ctx.generatedPassword;
                    }
                    content = "Dear " + ctx.firstName + ", \n\n" +
                        emailConfigurations.EmailTemplate[0] + "\n\n"
                        + config.HTTPS_URL + "/emm/api/device_enroll \n\n" + password_text +
                        "\n\n" + tenantCopyRight.CompanyName[0];
                    subject = "EMM Enrollment";
                    var email = require('email');
                    var sender = new email.Sender(String(emailConfigurations.SMTP[0]),
                        String(emailConfigurations.Port[0]),String(emailConfigurations.UserName[0]),
                        String(emailConfigurations.Password[0]), "tls");
                    sender.from = String(emailConfigurations.SenderAddress[0]);
                    sender.to = String(ctx.email);
                    sender.subject = subject;
                    sender.text = content;
                    try {
                        sender.send();
                        log.info("Email sent to -> " + ctx.email);
                    } catch (e) {
                        log.error(e);
                    }
                }
            }
        },
        /*
         Function that returns whether the email settings has been configured or not
         */
        isEmailConfigured: function() {
            var tenantId = parseInt(common.common.getTenantID());
            var emailConfigurations = this.getEmailConfigurations(tenantId);
            if (emailConfigurations != null) {
                if (String(emailConfigurations.UserName[0].trim()) != "") {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        },
        /*get user enrollment info*/
        getEnrollmentInfo: function(ctx) {
            var info = {};
            info.password = ctx.generatedPassword;
            info.enroll_url = config.HTTPS_URL + "/emm/api/device_enroll";
            return info;
        },
        /*Get all devices belongs to particular user*/
        getDevices: function(obj) {
            log.debug("begin");
            log.debug(String(obj.userid));
            log.debug(common.getTenantID());
            log.debug("end");
            var devices = driver.query(sqlscripts.devices.select26, String(obj.userid), common.getTenantID());
            return devices;
        },
        //To get the tenant name using the tenant domain
        getTenantNameByUser: function() {
            var carbon = require('carbon');
            log.debug("Username : " + arguments[0]);
            var tenantUser = carbon.server.tenantUser(arguments[0]);
            var tenantDomain = tenantUser.domain;
            log.debug("Domain : " + tenantDomain);
            if (tenantDomain == constants.CARBON_SUPER) {
                return this.getTenantName(constants.CARBON_SUPER);
            }
            return this.getTenantName(tenantDomain);
        },
        getTenantNameFromID: function() {
            var tenantId;
            if (arguments[0] == constants.CARBONSUPER_TENANTID) {
                return this.getTenantName(constants.CARBON_SUPER);
            }
            var tenantId = parseInt(arguments[0]);
            var tenantCopyRight = this.getTenantCopyRight(tenantId);
            if (tenantCopyRight != null) {
                return tenantCopyRight.CompanyName[0];
            } else {
                return "WSO2";
            }
        },
        /*
         Get Tenant Name from Domain
         */
        getTenantName: function() {
            try {
                var options = {};
                options.domain = arguments[0];
                var tenantId = carbon.server.tenantId(options);
                if (tenantId == null) {
                    tenantId = constants.CARBONSUPER_TENANTID;
                }
                var tenantCopyRight = this.getTenantCopyRight(tenantId);
                if (tenantCopyRight != null) {
                    return tenantCopyRight.CompanyName[0];
                } else {
                    return "WSO2";
                }
            } catch (e) {
                return "WSO2";
            }
        },
        /*
         Retrieve the Policy Agreement for the Tenant
         */
        getLicenseByDomain: function() {
            var options = {};
            if (!(arguments[0]) || (arguments[0].trim() == "")) {
                options.domain = constants.CARBON_SUPER;
            } else {
                options.domain = arguments[0];
            }
            try {
                var tenantId = carbon.server.tenantId(options);
                if (tenantId == null) {
                    tenantId = constants.CARBONSUPER_TENANTID;
                }
            } catch (e) {
                tenantId = constants.CARBONSUPER_TENANTID;
            }
            var message = this.getTenantLicense(parseInt(tenantId));
            //var message = this.getTenantLicenseSample(parseInt(tenantId));
            if (message == null) {
                return null;
            }
            return message.toString();
        },
        getTenantDomainFromID: function() {
            if (arguments[0] == constants.CARBONSUPER_TENANTID) {
                return constants.CARBON_SUPER;
            }
            var carbon = require('carbon');
            var ctx = {};
            ctx.tenantId = arguments[0];
            try {
                var tenantDomain = carbon.server.tenantDomain(ctx);
                if (tenantDomain == null) {
                    tenantDomain = constants.DEFAULT;
                }
            } catch (e) {
                tenantDomain = constants.DEFAULT;
            }
            var file = new File('/config/tenants/' + tenantDomain + '/config.json');
            if (!file.isExists()) {
                tenantDomain = constants.DEFAULT;
            }
            return tenantDomain;
        },
        getTouchDownConfig: function(ctx) {
            var data = {};
            var domain = this.getTenantDomainFromID(ctx.tenant_id);
            try {
                var tenantConfig = require('/config/tenants/' + domain + '/config.json');
            } catch (e) {
                var tenantConfig = require('/config/tenants/default/config.json');
            }
            data.userid = ctx.user_id;
            data.domain = tenantConfig.touchdown.domain;
            data.email = ctx.user_id;
            data.server = tenantConfig.touchdown.server;
            return data;
        },
        changePassword: function(ctx) {
            var new_password = ctx.new_password;
            var old_password = ctx.old_password;
            if (current_user) {
                var um = userManager(common.getTenantID());
                um.changePassword(current_user.username, new_password, old_password);
                response.status = 200;
            } else {
                print("User not found");
                response.status = 401;
            }
        }
    };
    return module;
})();
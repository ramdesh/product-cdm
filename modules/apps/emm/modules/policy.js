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


var policy = (function () {

    var userModule = require('user.js').user;
    var user;
    var usergModule = require('user_group.js').user_group;
    var userg
    var groupModule = require('group.js').group;
    var group;
    var deviceModule = require('device.js').device;
    var device;
    var common = require("common.js");
    var sqlscripts = require('/sqlscripts/db.js');
    var constants = require('/modules/constants.js');

    var configs = {
        CONTEXT: "/"
    };
    var routes = new Array();
    var log = new Log();
    var db;
    var driver;
    var module = function (dbs) {
        db = dbs;
        user = new userModule(db);
        userg = new usergModule(db);
        group = new groupModule(db);
        device = new deviceModule(db);
        driver = require('driver').driver(db);
        //mergeRecursive(configs, conf);
    };

    function mergeRecursive(obj1, obj2) {
        for (var p in obj2) {
            try {
                // Property in destination object set; update its value.
                if (obj2[p].constructor == Object) {
                    obj1[p] = mergeRecursive(obj1[p], obj2[p]);
                } else {
                    obj1[p] = obj2[p];
                }
            } catch (e) {
                // Property in destination object not set; create it and set its value.
                obj1[p] = obj2[p];
            }
        }
        return obj1;
    }

    function isResourceExist(policyID, resource, type) {
        if (type == constants.POLICY_USER) {
            var result = driver.query(sqlscripts.user_policy_mapping.select2, policyID, resource);
            if (typeof result != constants.UNDEFINED && result != null && typeof result[0] != constants.UNDEFINED &&
                result[0] != null) {
                return true;
            }
            return false;
        } else if (type == constants.POLICY_PLATFORM) {
            var result = driver.query(sqlscripts.platform_policy_mapping.select2, policyID, resource);
            if (typeof result != constants.UNDEFINED && result != null && typeof result[0] != constants.UNDEFINED &&
                result[0] != null) {
                return true;
            }
            return false;
        } else {
            var result = driver.query(sqlscripts.group_policy_mapping.select2, policyID, resource);
            if (typeof result != constants.UNDEFINED && result != null && typeof result[0] != constants.UNDEFINED &&
                result[0] != null) {
                return true;
            }
            return false;
        }
    }

    function policyByOsType(jsonData, os) {
        for (var n = 0; n < jsonData.length; n++) {
            if (jsonData[n].code == '509B' || jsonData[n].code == constants.BLACKLISTAPPS) {
                var apps = jsonData[n].data;
                var appsByOs = new Array();
                for (var k = 0; k < apps.length; k++) {
                    if (apps[k].os == os) {
                        appsByOs.push(apps[k]);
                    }
                }
                var obj1 = {};
                obj1.code = jsonData[n].code;
                obj1.data = appsByOs;
                jsonData[n] = obj1;
            }
        }
        return  jsonData;
    }

    function getPolicyIdFromDevice(deviceId) {

        var devices = driver.query(sqlscripts.devices.select1, String(deviceId));
        var userId = devices[0].user_id;
        var platform = '';
        if (devices[0].platform_id == 1) {
            platform = constants.ANDROID.toLowerCase();
        } else {
            platform = constants.IOS.toLowerCase();
        }
        var upresult = driver.query(sqlscripts.policies.select4, userId);
        if (upresult != undefined && upresult != null && upresult[0] != undefined && upresult[0] != null) {
            return upresult[0].id;
        }
        var ppresult = driver.query(sqlscripts.policies.select5, platform);
        if (ppresult != undefined && ppresult != null && ppresult[0] != undefined && ppresult[0] != null) {
            return ppresult[0].id;
        }
        var roleList = user.getUserRoles({'username': userId});
        var removeRoles = new Array(constants.INTERNAL_EVERYONE, constants.PORTAL, constants.ANONYMOUS_ROLE,
            constants.INTERNAL_REVIEWER);

        var roles = common.removeNecessaryElements(roleList, removeRoles);
        var role = roles[0];

        var gpresult = driver.query(sqlscripts.policies.select6, role);
        return gpresult[0].id;
    }

    function revokePolicy(policyId) {

        var policyPayload = require("/config/config.json").emptyPolicy;
        var users1 = driver.query(sqlscripts.user_policy_mapping.select1, String(policyId));
        for (var i = 0; i < users1.length; i++) {
            var devices1 = driver.query(sqlscripts.devices.select26, users1[i].user_id, common.getTenantID());
            for (var j = 0; j < devices1.length; j++) {
                device.sendToDevice({'deviceid': devices1[j].id, 'operation': constants.REVOKEPOLICY,
                    'data': policyPayload});
            }
        }

        var platforms = driver.query(sqlscripts.platform_policy_mapping.select1, String(policyId));
        for (var i = 0; i < platforms.length; i++) {
            if (platforms[i].platform_id == constants.ANDROID.toLowerCase()) {
                var devices2 = driver.query(sqlscripts.devices.select47, common.getTenantID());
                for (var j = 0; j < devices2.length; j++) {
                    var tempId = getPolicyIdFromDevice(devices2[j].id);
                    if (tempId == policyId) {
                        device.sendToDevice({'deviceid': devices2[j].id, 'operation': constants.REVOKEPOLICY,
                            'data': policyPayload});
                    }
                }
            } else {
                var devices3 = driver.query(sqlscripts.devices.select37);
                for (var j = 0; j < devices3.length; j++) {
                    var tempId = getPolicyIdFromDevice(devices3[j].id);
                    if (tempId == policyId) {
                        device.sendToDevice({'deviceid': devices3[i].id, 'operation': constants.REVOKEPOLICY,
                            'data': policyPayload});
                    }
                }
            }

        }

        var groups = driver.query(sqlscripts.group_policy_mapping.select1, String(policyId));
        for (var i = 0; i < groups.length; i++) {
            var users2 = group.getUsersOfGroup({'groupid': groups[i].group_id});
            for (var j = 0; j < users2.length; j++) {
                var devices4 = driver.query(sqlscripts.devices.select26, users2[j].username, common.getTenantID());
                for (var k = 0; k < devices4.length; k++) {
                    var tempId = getPolicyIdFromDevice(devices4[k].id);
                    if (tempId == policyId) {
                        device.sendToDevice({'deviceid': devices4[k].id, 'operation': constants.REVOKEPOLICY,
                            'data': policyPayload});
                    }
                }
            }
        }

    }

    module.prototype = {
        constructor: module,
        updatePolicy: function (ctx) {
            var policyId = '';
            var result;
            var policy = driver.query(sqlscripts.policies.select7, ctx.policyName);
            if (typeof policy != constants.UNDEFINED && policy != null && typeof policy[0] != constants.UNDEFINED &&
                policy[0] != null) {
                policyId = policy[0].id;
                if (ctx.category == 1) {
                    if (policy != undefined && policy != null && policy[0] != undefined && policy[0] != null) {
                        result = driver.query(sqlscripts.policies.update1, ctx.policyData, ctx.policyType,
                            ctx.policyMamData, ctx.policyName, common.getTenantID());
                        this.enforcePolicy({"policyid": policyId});
                    } else {
                        result = this.addPolicy(ctx);
                    }
                }
            } else {
                if (this.addPolicy(ctx) == 201) {
                    result = 1;
                }
            }
            return result;
        },

        addPolicy: function (ctx) {
            var existingPolicies = driver.query(sqlscripts.policies.select14, ctx.policyName, common.getTenantID());
            if (ctx.category == 1) {
                if (existingPolicies != undefined && existingPolicies != null && existingPolicies[0] != undefined &&
                    existingPolicies[0] != null) {
                    return 409;
                }
                var result = driver.query(sqlscripts.policies.insert1, ctx.policyName, ctx.policyData, ctx.policyType,
                    ctx.category, common.getTenantID(), ctx.policyMamData);
            }
            return 201;
        },

        addDefaultPolicy: function (ctx) {
            var existingPolicies = driver.query(sqlscripts.policies.select14, constants.DEFAULT, common.getTenantID());
            if (existingPolicies.length < 0) {
                driver.query(sqlscripts.policies.insert2, constants.DEFAULT, common.getTenantID());
            }
        },

        getAllPoliciesForMDM: function (ctx) {
            var result = driver.query(sqlscripts.policies.select8, common.getTenantID());
            return result;
        },

        getAllPoliciesForMAM: function (ctx) {
            var result = driver.query(sqlscripts.policies.select9, common.getTenantID());
            return result;
        },

        getPolicy: function (ctx) {
            var result = driver.query(sqlscripts.policies.select10, ctx.policyid, common.getTenantID());
            return result[0];
        },

        deletePolicy: function (ctx) {
            this.removeDevicePolicy(ctx.policyid);
            var result = driver.query(sqlscripts.policies.delete1, ctx.policyid, common.getTenantID());
            driver.query(sqlscripts.group_policy_mapping.delete1, ctx.policyid);
            driver.query(sqlscripts.user_policy_mapping.delete2, ctx.policyid);
            driver.query(sqlscripts.platform_policy_mapping.delete2, ctx.policyid);
            driver.query(sqlscripts.device_policy.update3, ctx.policyid);
            return result;
        },

        assignGroupsToPolicy: function (ctx) {
            this.assignUsersToPolicy(ctx);
            this.assignPlatformsToPolicy(ctx);
            var deletedGroups = ctx.removed_groups;
            var newGroups = ctx.added_groups;
            var policyId = ctx.policyid;

            var revokeGroups = {};
            var revokeGroupsArray = new Array();
            var assignGroups = {};
            var assignGroupsArray = new Array();
            revokeGroups.policyid = policyId;
            assignGroups.policyid = policyId;

            for (var i = 0; i < deletedGroups.length; i++) {
                if (isResourceExist(policyId, deletedGroups[i], constants.POLICY_GROUP) == true) {
                    var result = driver.query(sqlscripts.group_policy_mapping.delete2, policyId, deletedGroups[i]);
                    revokeGroupsArray.push(deletedGroups[i]);
                }
            }
            revokeGroups.array = revokeGroupsArray;

            for (var i = 0; i < newGroups.length; i++) {
                try {
                    if (isResourceExist(policyId, newGroups[i], constants.POLICY_GROUP) == false) {
                        var result = driver.query(sqlscripts.group_policy_mapping.insert1, newGroups[i], policyId);
                        assignGroupsArray.push(newGroups[i]);
                    }
                } catch (e) {
                    log.debug(e);
                }
            }
            assignGroups.array = assignGroupsArray;

            this.enforceGroupsToPolicy(revokeGroups, assignGroups);
        },

        assignUsersToPolicy: function (ctx) {
            var deletedUsers = ctx.removed_users;
            var newUsers = ctx.added_users;
            var policyId = ctx.policyid;

            var revokeUsers = {};
            var revokeUsersArray = new Array();
            var assignUsers = {};
            var assignUsersArray = new Array();
            revokeUsers.policyid = policyId;
            assignUsers.policyid = policyId;

            for (var i = 0; i < deletedUsers.length; i++) {
                if (isResourceExist(policyId, deletedUsers[i], constants.POLICY_USER) == true) {
                    var result = driver.query(sqlscripts.user_policy_mapping.delete1, policyId, deletedUsers[i]);
                    revokeUsersArray.push(deletedUsers[i]);
                }
            }
            revokeUsers.array = revokeUsersArray;

            for (var i = 0; i < newUsers.length; i++) {
                try {
                    if (isResourceExist(policyId, newUsers[i], constants.POLICY_USER) == false) {
                        var result = driver.query(sqlscripts.user_policy_mapping.insert1, newUsers[i], policyId);
                        assignUsersArray.push(newUsers[i]);
                    }
                } catch (e) {
                    log.debug(e);
                }
            }
            assignUsers.array = assignUsersArray;

            this.enforceUsersToPolicy(revokeUsers, assignUsers);
        },

        assignPlatformsToPolicy: function (ctx) {
            var deletedPlatforms = ctx.removed_platforms;
            var newPlatforms = ctx.added_platforms;
            var policyId = ctx.policyid;

            var revokePlatforms = {};
            var revokePlatformsArray = new Array();
            var assignPlatforms = {};
            var assignPlatformsArray = new Array();
            revokePlatforms.policyid = policyId;
            assignPlatforms.policyid = policyId;

            for (var i = 0; i < deletedPlatforms.length; i++) {
                if (isResourceExist(policyId, deletedPlatforms[i], constants.POLICY_PLATFORM) == true) {
                    var result = driver.query(sqlscripts.platform_policy_mapping.delete1, policyId,
                        deletedPlatforms[i]);
                    revokePlatformsArray.push(deletedPlatforms[i]);
                }
            }
            revokePlatforms.array = revokePlatformsArray;

            for (var i = 0; i < newPlatforms.length; i++) {
                try {
                    if (isResourceExist(policyId, newPlatforms[i], constants.POLICY_PLATFORM) == false) {
                        var result = driver.query(sqlscripts.platform_policy_mapping.insert1,
                            newPlatforms[i], policyId);
                        assignPlatformsArray.push(newPlatforms[i]);
                    }
                } catch (e) {
                    log.debug(e);
                }
            }
            assignPlatforms.array = assignPlatformsArray;

            this.enforcePlatformsToPolicy(revokePlatforms, assignPlatforms);
        },

        getGroupsByPolicy: function (ctx) {
            var totalGroups = group.getAllGroups({});
            var removeRoles = new Array(constants.INTERNAL_STORE, constants.INTERNAL_PUBLISHER,
                constants.INTERNAL_REVIEWER);
            var allGroups = common.removeNecessaryElements(totalGroups, removeRoles);
            var result = driver.query(sqlscripts.group_policy_mapping.select1, ctx.policyid);

            var array = new Array();
            if (result == undefined || result == null || result[0] == undefined || result[0] == null) {
                for (var i = 0; i < allGroups.length; i++) {
                    var element = {};
                    element.name = allGroups[i];
                    element.available = false;
                    array[i] = element;
                }
            } else {
                for (var i = 0; i < allGroups.length; i++) {
                    var element = {};
                    for (var j = 0; j < result.length; j++) {
                        if (allGroups[i] == result[j].group_id) {
                            element.name = allGroups[i];
                            element.available = true;
                            break;
                        } else {
                            element.name = allGroups[i];
                            element.available = false;
                        }
                    }
                    array[i] = element;
                }
            }
            return array;
        },

        getUsersByPolicy: function (ctx) {
            var allUsers = user.getAllUserNames();
            var result = driver.query(sqlscripts.user_policy_mapping.select1, ctx.policyid);
            var array = new Array();
            if (result == undefined || result == null || result[0] == undefined || result[0] == null) {
                for (var i = 0; i < allUsers.length; i++) {
                    var element = {};
                    element.name = allUsers[i];
                    element.available = false;
                    array.push(element);
                }
            } else {
                for (var i = 0; i < allUsers.length; i++) {
                    var element = {};
                    for (var j = 0; j < result.length; j++) {
                        if (allUsers[i] == result[j].user_id) {
                            element.name = allUsers[i];
                            element.available = true;
                            break;
                        } else {
                            element.name = allUsers[i];
                            element.available = false;
                        }
                    }
                    array.push(element);
                }
            }
            return array;
        },

        getPlatformsByPolicy: function (ctx) {
            var allPlatforms = new Array(constants.ANDROID.toLowerCase(), constants.IOS.toLowerCase());
            var result = driver.query(sqlscripts.platform_policy_mapping.select1, ctx.policyid);

            var array = new Array();
            if (result == undefined || result == null || result[0] == undefined || result[0] == null) {
                for (var i = 0; i < allPlatforms.length; i++) {
                    var element = {};
                    element.name = allPlatforms[i];
                    element.available = false;
                    array[i] = element;
                }
            } else {
                for (var i = 0; i < allPlatforms.length; i++) {
                    var element = {};
                    for (var j = 0; j < result.length; j++) {
                        if (allPlatforms[i] == result[j].platform_id) {
                            element.name = allPlatforms[i];
                            element.available = true;
                            break;
                        } else {
                            element.name = allPlatforms[i];
                            element.available = false;
                        }
                    }
                    array[i] = element;
                }
            }

            return array;
        },

        removeDevicePolicy: function () {
            var policyid = arguments[0];

            //Revoke Group Policy
            var deletedGroups = driver.query(sqlscripts.group_policy_mapping.select3, policyid);
            var revokeGroups = {};
            var revokeGroupsArray = new Array();
            revokeGroups.policyid = policyid;
            for (var i = 0; i < deletedGroups.length; i++) {
                if (isResourceExist(policyid, deletedGroups[i].group_id, constants.POLICY_GROUP) == true) {
                    revokeGroupsArray.push(deletedGroups[i].group_id);
                }
            }
            revokeGroups.array = revokeGroupsArray;
            this.revokeGroupsToPolicy(revokeGroups);

            //Revoke User Policy
            var deletedUsers = driver.query(sqlscripts.user_policy_mapping.select3, policyid);
            var revokeUsers = {};
            var revokeUsersArray = new Array();
            revokeUsers.policyid = policyid;

            for (var i = 0; i < deletedUsers.length; i++) {
                if (isResourceExist(policyid, deletedUsers[i].user_id, constants.POLICY_USER) == true) {
                    revokeUsersArray.push(deletedUsers[i].user_id);
                }
            }
            revokeUsers.array = revokeUsersArray;
            this.revokeUsersToPolicy(revokeUsers);

            //Revoke Platform Policy
            var deletedPlatforms = driver.query(sqlscripts.platform_policy_mapping.select3, policyid);
            var revokePlatforms = {};
            var revokePlatformsArray = new Array();
            revokePlatforms.policyid = policyid;

            for (var i = 0; i < deletedPlatforms.length; i++) {
                if (isResourceExist(policyid, deletedPlatforms[i].platform_id, constants.POLICY_PLATFORM) == true) {
                    revokePlatformsArray.push(deletedPlatforms[i].platform_id);
                }
            }
            revokePlatforms.array = revokePlatformsArray;
            this.revokePlatformsToPolicy(revokePlatforms);

        },

        enforceUsersToPolicy: function () {

            var revokeUsers = arguments[0];
            var assignUsers = arguments[1];
            var tenantid = common.getTenantID();

            this.revokeUsersToPolicy(revokeUsers);

            if (assignUsers.array.length > 0) {
                var policies = driver.query(sqlscripts.policies.select10, assignUsers.policyid, tenantid);
                //var payLoad = parse(policies[0].content);
                var payLoad;
                var mdmPolicy = parse(policies[0].content);
                var mamPolicy = parse(policies[0].mam_content);
                if (mdmPolicy != null && mdmPolicy[0] != null && mamPolicy.length != 0) {
                    var newMamPolicy = device.separateMAMPolicy(mamPolicy);
                    payLoad = mdmPolicy.concat(newMamPolicy);
                } else if (mdmPolicy != null && mdmPolicy[0] != null && mamPolicy.length == 0) {
                    payLoad = mdmPolicy;
                } else if (mdmPolicy == null && mdmPolicy[0] == null && mamPolicy.length != 0) {
                    var newMamPolicy = device.separateMAMPolicy(mamPolicy);
                    payLoad = newMamPolicy;
                }

                //Revoke and Assign policy to users
                for (var i = 0; i < assignUsers.array.length; ++i) {
                    var devices = driver.query(sqlscripts.devices.select26, assignUsers.array[i], tenantid);
                    for (var j = 0; j < devices.length; ++j) {
                        device.sendToDevice({'deviceid': devices[j].id, 'operation': constants.POLICY, 'data': payLoad,
                            'policyid': assignUsers.policyid, 'policypriority': constants.USERS});
                    }
                }
            }
        },

        enforceGroupsToPolicy: function () {

            var revokeGroups = arguments[0];
            var assignGroups = arguments[1];
            var tenantid = common.getTenantID();

            this.revokeGroupsToPolicy(revokeGroups);

            if (assignGroups.array.length > 0) {
                var policies = driver.query(sqlscripts.policies.select10, assignGroups.policyid, tenantid);
                var payLoad;
                var mdmPolicy = parse(policies[0].content);
                var mamPolicy = parse(policies[0].mam_content);
                if (mdmPolicy != null && mdmPolicy[0] != null && mamPolicy.length != 0) {
                    var newMamPolicy = device.separateMAMPolicy(mamPolicy);
                    payLoad = mdmPolicy.concat(newMamPolicy);
                } else if (mdmPolicy != null && mdmPolicy[0] != null && mamPolicy.length == 0) {
                    payLoad = mdmPolicy;
                } else if (mdmPolicy == null && mdmPolicy[0] == null && mamPolicy.length != 0) {
                    var newMamPolicy = device.separateMAMPolicy(mamPolicy);
                    payLoad = newMamPolicy;
                }

                //Revoke and Assign policy to group
                for (var i = 0; i < assignGroups.array.length; ++i) {
                    var users = group.getUsersOfGroup({'groupid': assignGroups.array[i]});
                    for (var j = 0; j < users.length; ++j) {
                        var devices = driver.query(sqlscripts.devices.select26, users[j].username, tenantid);
                        for (var k = 0; k < devices.length; ++k) {
                            device.sendToDevice({'deviceid': devices[k].id, 'operation': constants.POLICY,
                                'data': payLoad, 'policyid': assignGroups.policyid, 'policypriority': constants.ROLES});
                        }
                    }
                }
            }
        },

        enforcePlatformsToPolicy: function () {

            var revokePlatforms = arguments[0];
            var assignPlatforms = arguments[1];
            var tenantid = common.getTenantID();

            this.revokePlatformsToPolicy(revokePlatforms);

            if (assignPlatforms.array.length > 0) {
                var policies = driver.query(sqlscripts.policies.select10, assignPlatforms.policyid, tenantid);
                var payLoad;
                var mdmPolicy = parse(policies[0].content);
                var mamPolicy = parse(policies[0].mam_content);
                if (mdmPolicy != null && mdmPolicy[0] != null && mamPolicy.length != 0) {
                    var newMamPolicy = device.separateMAMPolicy(mamPolicy);
                    payLoad = mdmPolicy.concat(newMamPolicy);
                } else if (mdmPolicy != null && mdmPolicy[0] != null && mamPolicy.length == 0) {
                    payLoad = mdmPolicy;
                } else if (mdmPolicy == null && mdmPolicy[0] == null && mamPolicy.length != 0) {
                    var newMamPolicy = device.separateMAMPolicy(mamPolicy);
                    payLoad = newMamPolicy;
                }

                //Revoke and Assign policy to platform
                for (var i = 0; i < assignPlatforms.array.length; ++i) {
                    var devices = driver.query(sqlscripts.devices.select42, assignPlatforms.array[i], tenantid);
                    for (var j = 0; j < devices.length; ++j) {
                        device.sendToDevice({'deviceid': devices[j].id, 'operation': constants.POLICY, 'data': payLoad,
                            'policyid': assignPlatforms.policyid, 'policypriority': constants.PLATFORMS});
                    }
                }
            }
        },

        revokeUsersToPolicy: function () {
            var revokeUsers = arguments[0];
            var tenantid = common.getTenantID();

            //Revoke policy to users
            for (var i = 0; i < revokeUsers.array.length; ++i) {
                var devices = driver.query(sqlscripts.devices.select26, revokeUsers.array[i], tenantid);
                for (var j = 0; j < devices.length; ++j) {
                    device.removeDevicePolicy({'deviceid': devices[j].id, 'revokepolicyid': revokeUsers.policyid,
                        'policypriority': constants.USERS});
                }
            }
        },

        revokeGroupsToPolicy: function () {
            var revokeGroups = arguments[0];
            var tenantid = common.getTenantID();

            //Revoke policy to groups
            for (var i = 0; i < revokeGroups.array.length; ++i) {
                var users = group.getUsersOfGroup({'groupid': revokeGroups.array[i]});
                for (var j = 0; j < users.length; ++j) {
                    var devices = driver.query(sqlscripts.devices.select26, users[j].username, tenantid);
                    for (var k = 0; k < devices.length; ++k) {
                        device.removeDevicePolicy({'deviceid': devices[k].id, 'revokepolicyid': revokeGroups.policyid,
                            'policypriority': constants.ROLES});
                    }
                }
            }
        },

        revokePlatformsToPolicy: function () {

            var revokePlatforms = arguments[0];
            var tenantid = common.getTenantID();

            //Revoke policy to platforms
            for (var i = 0; i < revokePlatforms.array.length; ++i) {
                var devices = driver.query(sqlscripts.devices.select42, revokePlatforms.array[i], tenantid);
                for (var j = 0; j < devices.length; ++j) {
                    device.removeDevicePolicy({'deviceid': devices[j].id, 'revokepolicyid': revokePlatforms.policyid,
                        'policypriority': constants.PLATFORMS});
                }
            }
        },

        enforcePolicy: function (ctx) {
            var policyId = ctx.policyid;
            var policies = driver.query(sqlscripts.policies.select10, String(policyId), common.getTenantID());

            if (policies != null && policies != undefined && policies[0] != null && policies[0] != undefined) {
                var payLoad;
                var mdmPolicy = parse(policies[0].content);
                var mamPolicy = parse(policies[0].mam_content);
                if (mdmPolicy != null && mdmPolicy[0] != null && mamPolicy.length != 0) {
                    var newMamPolicy = device.separateMAMPolicy(mamPolicy);
                    payLoad = mdmPolicy.concat(newMamPolicy);
                } else if (mdmPolicy != null && mdmPolicy[0] != null && mamPolicy.length == 0) {
                    payLoad = mdmPolicy;
                } else if (mdmPolicy == null && mdmPolicy[0] == null && mamPolicy.length != 0) {
                    var newMamPolicy = device.separateMAMPolicy(mamPolicy);
                    payLoad = newMamPolicy;
                }

                log.debug("Payload : " + stringify(payLoad));
                var users1 = driver.query(sqlscripts.user_policy_mapping.select1, String(policyId));
                for (var i = 0; i < users1.length; i++) {
                    var devices1 = driver.query(sqlscripts.devices.select26, users1[i].user_id, common.getTenantID());
                    for (var j = 0; j < devices1.length; j++) {
                        device.sendToDevice({'deviceid': devices1[j].id, 'operation': constants.POLICY, 'data': payLoad,
                            'policyid': ctx.policyid, 'policypriority': constants.USERS});
                    }
                }

                var platforms = driver.query(sqlscripts.platform_policy_mapping.select1, String(policyId));
                for (var i = 0; i < platforms.length; i++) {
                    if (platforms[i].platform_id == constants.ANDROID.toLowerCase) {

                        var devices2 = driver.query(sqlscripts.devices.select47, common.getTenantID());
                        for (var j = 0; j < devices2.length; j++) {
                            device.sendToDevice({'deviceid': devices2[j].id, 'operation': constants.POLICY,
                                'data': payLoad, 'policyid': ctx.policyid, 'policypriority': constants.PLATFORMS});
                        }
                    } else {
                        var devices3 = driver.query(sqlscripts.devices.select37);
                        for (var j = 0; j < devices3.length; j++) {
                            device.sendToDevice({'deviceid': devices3[j].id, 'operation': constants.POLICY,
                                'data': payLoad, 'policyid': ctx.policyid, 'policypriority': constants.PLATFORMS});
                        }
                    }

                }

                var groups = driver.query(sqlscripts.group_policy_mapping.select1, String(policyId));
                for (var i = 0; i < groups.length; i++) {
                    var users2 = group.getUsersOfGroup({'groupid': groups[i].group_id});
                    for (var j = 0; j < users2.length; j++) {
                        var devices4 = driver.query(sqlscripts.devices.select26, users2[j].username,
                            common.getTenantID());
                        for (var k = 0; k < devices4.length; k++) {
                            device.sendToDevice({'deviceid': devices4[k].id, 'operation': constants.POLICY,
                                'data': payLoad, 'policyid': ctx.policyid, 'policypriority': constants.ROLES});
                        }
                    }
                }
            }
        },

        getPolicyPayLoad: function (deviceId, category) {
            var devices = driver.query(sqlscripts.devices.select1, deviceId);
            var username = devices[0].user_id;//username for pull policy payLoad

            var platforms = driver.query(sqlscripts.devices.select5, deviceId);
            var platformName = platforms[0].type_name;//platform name for pull policy payLoad

            var roleList = user.getUserRoles({'username': username});
            var removeRoles = new Array(constants.INTERNAL_EVERYONE, constants.PORTAL, constants.ANONYMOUS_ROLE,
                INTERNAL_REVIEWER);
            var roles = common.removeNecessaryElements(roleList, removeRoles);
            var role = roles[0];//role name for pull policy payLoad

            var upresult = driver.query(sqlscripts.policies.select11, category, String(username));

            if (upresult != undefined && upresult != null && upresult[0] != undefined && upresult[0] != null) {
                var policyPayLoad = parse(upresult[0].data);
                return policyPayLoad;
            }

            var ppresult = driver.query(sqlscripts.policies.select12, category, platformName);
            if (ppresult != undefined && ppresult != null && ppresult[0] != undefined && ppresult[0] != null) {
                var policyPayLoad = parse(ppresult[0].data);
                return policyPayLoad;
            }

            var gpresult = driver.query(sqlscripts.policies.select13, category, role);
            if (gpresult != undefined && gpresult != null && gpresult[0] != undefined && gpresult[0] != null) {
                var policyPayLoad = parse(gpresult[0].data);
                return policyPayLoad;
            }
            return null;
        },

        monitoring: function (ctx) {
            var monitor_interval = require("/config/config.json").monitor_interval;
            monitor_interval = monitor_interval * 60 * 1000;

            setInterval(
                function (ctx) {
                    device.monitor(ctx);
                }
                , monitor_interval);
        }
    };
    return module;
})();
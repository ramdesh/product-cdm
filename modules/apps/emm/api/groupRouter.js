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

var group = (function () {
	
    var module = function (db,router) {
		var groupModule = require('modules/group.js').group;
		var group = new groupModule(db);
        var userModule = require('modules/user.js').user;
        var user = new userModule(db);

		router.get('groups/', function(ctx){
			var groups= group.getAllGroups(ctx);
		    if(groups[0]!=null){
		        response.content = groups;
		        response.status = 200;
		    }else{
		        response.status = 404;
		    }
		});
        router.put('groups/invite', function(ctx){
            if(user.isEmailConfigured()) {
                var users= group.getUsersOfGroup(ctx);
                for (var i = users.length - 1; i >= 0; i--){
                    user.sendEmail(user.getUser({email: users[i].email+"@"+ user.getTenantDomainFromID(common.getTenantID())}));
                };
                response.status = 200;
            } else {
                response.status = 403;
            }
        });
		router.delete('groups/{groupid}', function(ctx){
            log.debug("Test Delete Router");
			group.deleteGroup(ctx);
		    response.status = 201;
		});
		router.get('groups/{groupid}/users/device_count', function(ctx){
			var users = group.getUsers(ctx);
		    response.content =  users;
		    response.status = 200;
		});
        router.get('groups/{groupid}/users', function(ctx){
            log.debug("Test Router");
            var allUsers = group.getUsersOfGroup(ctx);
            response.content =  allUsers;
            response.status = 200;
        });
        router.put('groups/{groupid}/users', function(ctx){
            log.debug("Test Request "+stringify(ctx));
             var result = group.updateUserListOfRole(ctx);
             response.content = result;
             response.status = 200;
        });
		router.post('groups', function(ctx){
			var returnMsg = group.addGroup(ctx);
            if(returnMsg.status == 'ALLREADY_EXIST'){
                response.status = 409;
                response.content = "Already Exist";
            }else if(returnMsg.status == 'SUCCESSFULL' ){
                response.status = 201;
                response.content = "Successfull";
            }else if(returnMsg.status == 'BAD_REQUEST'){
                response.status = 400;
                response.content = "Name not According to Policy";
            }else if(returnMsg.status == 'SERVER_ERROR'){
                response.status = 500;
                response.content = "Session Expired";
            }else{
                response.status = 400;
            }
		});
		router.put('groups/{groupid}', function(ctx){
			var result = group.editGroup(ctx.groupid, ctx.name);
            response.content = result;
            response.status = 200;
		});
		router.post('groups/{groupid}/operations/{operation}', function(ctx){
                response.status = 200;
                response.content = "success";
                var result = group.sendMsgToGroupDevices(ctx);

		});
		
    };
    // prototype
    module.prototype = {
        constructor: module
    };
    // return module
    return module;
})();
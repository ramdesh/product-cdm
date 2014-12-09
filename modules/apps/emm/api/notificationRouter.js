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

var notification = (function () {

    var constants = require('/modules/constants.js');
	
    var module = function (db,router) {
		var notificationModule = require('modules/notification.js').notification;
		var notification = new notificationModule(db);
		router.get('notifications/devices/{deviceid}', function(ctx){
		    var result = notification.getNotifications(ctx);
		    if(result!= null && result != undefined && result[0] != null && result[0] != undefined) {
		        print(result);
		        response.status = 200;
		    }else{
		        response.status = 404;
		    }
		});

		router.post('notifications/ios', function(ctx){
		    var result = notification.addIosNotification(ctx);
		});

		router.post('notifications/1.0.0', function(ctx){
		    var result = notification.addNotification(ctx);
		});

		router.get('refresh/devices/{deviceid}/{operation}', function(ctx){
		    var result = notification.getLastRecord(ctx);
		    if(result!= null && result != undefined){
                log.debug("Refresh:- occured");
		        print(result);
		        response.status = 200;
		    }else{
		        response.status = 404;
		    }
		});

        /*
         Device contacts this api to get and update the pending operations
         */
        router.post('notifications/pendingOperations/1.0.0', function(ctx) {
            var operations = notification.getAndroidOperations(ctx);
            log.debug("Pending >>>>> " + stringify(operations));
            if(operations == constants.ENTERPRISEWIPE) {
                //Unregister the android agent
                response.status = 200;
                response.content = constants.ENTERPRISEWIPE;
            } else {
                response.status = 200;
                print(operations);
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
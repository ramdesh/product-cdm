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

var permission = (function () {

    var log = new Log();
    var module = function (db,router) {
        var permissionModule = require('modules/permission.js').permission;
        var permission = new permissionModule(db);

        router.put('permissions/', function(ctx){
            log.debug("check policy router add permission group PUT");
            log.debug(ctx);
            var result = permission.assignPermissionToGroup(ctx);
           /* log.info("Status :"+result.status);
            response.status = result.status;*/
        });

        router.get('permission/groups/features', function(ctx){
            log.debug("check policy router GET");
            log.debug(ctx);
            var result = permission.getPermission(ctx);
            response.status = result.status;
            print(result.content);
        });

    };
    // prototype
    module.prototype = {
        constructor: module
    };
    // return module
    return module;
})();
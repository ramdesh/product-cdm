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

var reports = (function () {

    var module = function (db,router) {
        var reportModule = require('modules/mdm_reports.js').mdm_reports;
        var report = new reportModule(db);
        router.get('mdm_reports/devices/registration', function(ctx){
          //  var reports = report.getDevicesByRegisteredDate(ctx);
            var reports = report.getComplianceStatus(ctx);
          //  var reports = report.getDevicesByComplianceState(ctx);
            if(typeof reports !== 'undefined' && reports !== null && typeof reports[0]!== 'undefined' && reports[0]!== null){
                response.content = reports;
                response.status = 200;
            }else{
                response.status = 404;
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

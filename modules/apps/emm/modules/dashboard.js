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

var dashboard = (function() {
    var configs = {
        CONTEXT: "/"
    };
    var common = require("/modules/common.js");
    var sqlscripts = require('/sqlscripts/db.js');
    var driver;
    var routes = new Array();
    var log = new Log();
    var db;
    var module = function(dbs) {
        db = dbs;
        driver = require('driver').driver(db);
    };
    // prototype
    module.prototype = {
        constructor: module,
        getDeviceCountByOS: function(ctx) {
            var tenantID = common.getTenantID();
            var finalResult = driver.query(sqlscripts.devices.select2, tenantID);
            if (finalResult.length == 0) {
                finalResult = [{
                    "label": "No Data",
                    "devices": 0,
                    "data": 100
                }];
            } else {
                for(var i=0;i<finalResult.length;++i){
                    finalResult[i].data = parseInt(parseFloat(finalResult[i].devices/finalResult[i].data)*100);
                }
            }
            return finalResult;
        },
        getDeviceCountByOwnership: function(ctx) {
            var tenantID = common.getTenantID();
            var allDeviceCount = driver.query(sqlscripts.devices.select3, tenantID);
            var allByodCount = driver.query(sqlscripts.devices.select4, tenantID);
            var finalResult = [{
                "label": "Personal",
                "devices": allByodCount[0].count,
                "data": allByodCount[0].count
            }, {
                "label": "Corporate",
                "devices": allDeviceCount[0].count - allByodCount[0].count,
                "data": allDeviceCount[0].count - allByodCount[0].count
            }];
            if (allDeviceCount[0].count == 0) {
                finalResult = [{
                    "label": "No Data",
                    "devices": 0,
                    "data": 100
                }];
            }
            return finalResult;
        },
        getAndroidDeviceCountByOwnership: function(ctx) {
            var tenantID = common.getTenantID();
            var allDeviceCount = driver.query(sqlscripts.devices.select3, tenantID);
            var allByodCount = driver.query(sqlscripts.devices.select4, tenantID);
            var finalResult = [{
                "label": "Personal",
                "data": allByodCount[0].count
            }, {
                "label": "Corporate",
                "data": allDeviceCount[0].count - allByodCount[0].count
            }];
            if (allDeviceCount[0].count == 0) {
                finalResult = [{
                    "label": "No Data",
                    "devices": 0,
                    "data": 100
                }];
            }
            return finalResult;
        }
    };
    return module;
})();
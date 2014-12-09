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

var platform = (function () {

    var routes = new Array();
	var log = new Log();
	var db;
    var driver;
    var sqlscripts = require('/sqlscripts/db.js');

	
    var module = function (dbs) {
		db = dbs;
        driver = require('driver').driver(db);
    };

   
    // prototype
    module.prototype = {
        constructor: module,
        getPlatforms: function(ctx){
            var platforms = driver.query(sqlscripts.platform.select1);
            return platforms;
        }
    };

    // return module
    return module;
})();
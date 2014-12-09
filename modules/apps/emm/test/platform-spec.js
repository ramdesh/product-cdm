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

describe('Platform Module',function(){

    describe('Get platforms operation - Platform Module', function () {
        var db, platform;
        var platform_module = require('/modules/platform.js').platform;

        function initModule() {
            try {
                db = new Database("WSO2_EMM_DB");
                platform = new platform_module(db);
            } catch (e) {
                log.error(e);
            }
        }

        function closeDB() {
            db.close();
        }

        it('Test getPlatforms is not returning null', function () {
            initModule();
            var platforms = platform.getPlatforms();
            expect(platforms).not.toBe(null);
            closeDB();
        });

        it('Test getPlatforms is returning a valid array', function () {
            initModule();
            var platforms = platform.getPlatforms();
            expect(platforms.length).not.toBe(0);
            closeDB();
        });
    });
});

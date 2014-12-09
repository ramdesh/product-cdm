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


var apiConfig = getApiConfig();

var uiConfig = getUIConfig('default');
var currentUserInfo = null;

$.get('/emm/console/info').done(function(data) {
    currentUserInfo = JSON.parse(data);
    tenantDomain = JSON.parse(data).tenantDomain;
    var uiConfig = getUIConfig('default');
}).fail(function() {

});




function getUIConfig(tenantDomain){
   // var uiConfig = loadTextFileAjaxSync("/emm/config/ui.json", "application/json");
    var uiConfig = loadTextFileAjaxSync("/emm/config/tenants/" + tenantDomain +  "/ui.json", "application/json");
    return JSON.parse(uiConfig);
}

function getApiConfig(){	
    var apiConfig = loadTextFileAjaxSync("/emm/config/apis.json", "application/json");
    return JSON.parse(apiConfig);
}


context = function() {	

    var appDefault = {				
        resourcePath: uiConfig.EMM_UI_URI + "themes/" + uiConfig.EMM_THEME + "/img/",
        serverURL: uiConfig.EMM_API_URI,
        appsImageService: uiConfig.APPS_IMAGE_SERVICE,
        devicesImageService: uiConfig.DEVICES_IMAGE_SERVICE
    };
    return appDefault;
};




function loadTextFileAjaxSync(filePath, mimeType)
{
    var xmlhttp=new XMLHttpRequest();
    xmlhttp.open("GET",filePath,false);
    if (mimeType != null) {
        if (xmlhttp.overrideMimeType) {
            xmlhttp.overrideMimeType(mimeType);
        }
    }
    xmlhttp.send();
    if (xmlhttp.status==200)
    {
        return xmlhttp.responseText;
    }
    else {
        // TODO Throw exception
        return null;
    }
}



getServiceURLs = function(item){	

    var tenantId = null;
    $.get('/emm/console/info').done(function(data) {
        tenantId = JSON.parse(data).tenantId;

    }).fail(function() {

    });

    var serverURL = context().serverURL;	
    var urls = apiConfig.APIS;		
    arguments[0] = urls[item];	
    if(tenantId){
        return serverURL + String.format.apply(this, arguments) + "&tenantId=" + tenantId;
    }else{
        return serverURL + String.format.apply(this, arguments);
    }	

};



String.format = function() {
    var s = arguments[0]; 
    for (var i = 0; i < arguments.length - 1; i++) {       
        var reg = new RegExp("\\{" + i + "\\}", "gm");             
        s = s.replace(reg, arguments[i + 1]);
    }

    return s;
}


$('.datepicker').datepickerbt({
    format: 'yyyy-mm-dd'
});


$.noty.defaults = {
    layout: 'center',
    theme: 'defaultTheme',
    type: 'alert',
    text: '',	
    dismissQueue: true, // If you want to use queue feature set this true
    template: '<div class="noty_message"><span class="noty_text"></span><div class="noty_close"></div></div>',
    animation: {
        open: {height: 'toggle'},
        close: {height: 'toggle'},
        easing: 'swing',
        speed: 100 // opening & closing animation speed
    },
    timeout: 1000, // delay for closing event. Set false for sticky notifications
    force: false, // adds notification to the beginning of queue when set to true
    modal: true,
    closeWith: ['click'], // ['click', 'button', 'hover']
    callback: {
        onShow: function() {},
        afterShow: function() {},
        onClose: function() {},
        afterClose: function() {}
    },
    buttons: false // an array of buttons
};





$('.selectpicker').selectpicker();
$('.duallistbox').bootstrapDualListbox();
$('.duallistboxapps').bootstrapDualListbox();
$(".dropdownimage").msDropDown();
$('.multiselect').multiselect({
    includeSelectAllOption: true,
    enableFiltering: 1,
    maxHeight: 150,
    buttonWidth: "215px"
});


$('.nav-tabs a').click(function(e) {
    e.preventDefault();
    $(this).tab('show');
});

$(".jtootip").tooltip();


$(".als-container").als({
    visible_items: 20,
    scrolling_items: 2,
    orientation: "horizontal",
    circular: "yes",
    autoscroll: "yes",
    interval: 6000,
    direction: "right",
    start_from: 1
});


Handlebars.registerHelper('elipsis', function(maxLength, context, options) {
    if(context.length > maxLength) {
        context = context.substring(0, maxLength)+"...";
    }

    return context;
});


Handlebars.registerHelper('batteryLevel', function(level, options) {
    if(level < 20){
        return "20";
    }else if(level < 40){
        return "40";
    }else if(level < 60){
        return "60";
    }else if(level < 80){
        return "80";
    }else if(level <= 100){
        return "100";
    }

});


Handlebars.registerHelper('compare', function(lvalue, rvalue, options) {

    if (arguments.length < 3)
        throw new Error("Handlerbars Helper 'compare' needs 2 parameters");

    operator = options.hash.operator || "==";

    var operators = {
        '==':       function(l,r) { return l == r; },
        '===':      function(l,r) { return l === r; },
        '!=':       function(l,r) { return l != r; },
        '<':        function(l,r) { return l < r; },
        '>':        function(l,r) { return l > r; },
        '<=':       function(l,r) { return l <= r; },
        '>=':       function(l,r) { return l >= r; },
        'typeof':   function(l,r) { return typeof l == r; }
    }

    if (!operators[operator])
        throw new Error("Handlerbars Helper 'compare' doesn't know the operator "+operator);

    var result = operators[operator](lvalue,rvalue);

    if( result ) {
        return options.fn(this);
    } else {
        return options.inverse(this);
    }

});




//DATA TABLES

function createFilter(oTable, rowId, selectId, heading){
    $('.tabel-filter-group').append('<span class="select-filter">' + fnCreateSelect( oTable.fnGetColumnData(rowId), selectId, heading) + '</span>');	
}



(function($) {
    /*
	 * Function: fnGetColumnData
	 * Purpose:  Return an array of table values from a particular column.
	 * Returns:  array string: 1d data array
	 * Inputs:   object:oSettings - dataTable settings object. This is always the last argument past to the function
	 *           int:iColumn - the id of the column to extract the data from
	 *           bool:bUnique - optional - if set to false duplicated values are not filtered out
	 *           bool:bFiltered - optional - if set to false all the table data is used (not only the filtered)
	 *           bool:bIgnoreEmpty - optional - if set to false empty values are not filtered from the result array
	 * Author:   Benedikt Forchhammer <b.forchhammer /AT\ mind2.de>
	 */
    $.fn.dataTableExt.oApi.fnGetColumnData = function ( oSettings, iColumn, bUnique, bFiltered, bIgnoreEmpty ) {
        // check that we have a column id
        if ( typeof iColumn == "undefined" ) return new Array();

        // by default we only want unique data
        if ( typeof bUnique == "undefined" ) bUnique = true;

        // by default we do want to only look at filtered data
        if ( typeof bFiltered == "undefined" ) bFiltered = true;

        // by default we do not want to include empty values
        if ( typeof bIgnoreEmpty == "undefined" ) bIgnoreEmpty = true;

        // list of rows which we're going to loop through
        var aiRows;

        // use only filtered rows
        if (bFiltered == true) aiRows = oSettings.aiDisplay;
        // use all rows
        else aiRows = oSettings.aiDisplayMaster; // all row numbers

        // set up data array   
        var asResultData = new Array();

        for (var i=0,c=aiRows.length; i<c; i++) {
            iRow = aiRows[i];
            var aData = this.fnGetData(iRow);
            var sValue = aData[iColumn];

            // ignore empty values?
            if (bIgnoreEmpty == true && sValue.length == 0) continue;

            // ignore unique values?
            else if (bUnique == true && jQuery.inArray(sValue, asResultData) > -1) continue;

            // else push the value onto the result data array
            else asResultData.push(sValue);
        }

        return asResultData;
    }}(jQuery));


function fnCreateSelect( aData, selectId, heading )
{
    var r= heading+ ': <select id="'+selectId+'" class="selectpicker"><option value="">-- All --</option>', i, iLen=aData.length;
    for ( i=0 ; i<iLen ; i++ )
    {
        r += '<option value="'+aData[i]+'">'+aData[i]+'</option>';
    }
    return r+'</optgroup>';
    return r+'</select>';
}


function urlExists(url)
{
    var http = new XMLHttpRequest();
    http.open('HEAD', url, false);
    http.send();
    return http.status!=404;
}


function getURLParameter(name) {
    return decodeURI(
        (RegExp(name + '=' + '(.+?)(&|$)').exec(location.search)||[,null])[1]
    );
}





$("#modalChangePasswordButton").click(function() {
    var oldPassword = $("#modalChangePasswordTxtOldPassword").val();
    var password = $("#modalChangePasswordTxtPassword").val();
    var passwordConfirm = $("#modalChangePasswordTxtPasswordConf").val();
    
    if(password.length < 6){
        var n = noty({
            text : 'Password should be at least 6 characters long',
            'layout' : 'center',
            type: 'error'

        });
        return;
    }

    if(password != passwordConfirm){
        var n = noty({
            text : 'Password and confirm password fields must be identical',
            'layout' : 'center',
            type: 'error'

        });
    }
    
    else{
        //usersChangePassword
        $("#modalChangePassword").modal('hide');
         var n = noty({	});
        
        $("#modalChangePasswordTxtOldPassword").val("");
        $("#modalChangePasswordTxtPassword").val("");
        $("#modalChangePasswordTxtPasswordConf").val("");

        jQuery.ajax({
            url : getServiceURLs("usersChangePassword", ""),
            type : "POST",		
            data : JSON.stringify({old_password: oldPassword, new_password: password}),		
            contentType : "application/json",
            dataType : "json",
            statusCode: {
                400: function() {				
                    n.setText('Error occured while changing the password! old password mismatch');	
                    n.setType('error');
                    n.setTimeout(1000);			
                },
                404: function() {				
                    n.setText('API not found!');	
                    n.setType('error');	
                    n.setTimeout(1000);		
                },
                500: function() {				
                    n.setText('Fatal error occured!');	
                    n.setType('error');
                    n.setTimeout(1000);			
                },
                200: function() {				
                    n.setText('Password changed successfully!');	
                    n.setTimeout(1000);		
                }
            }				
        });

    }


});    

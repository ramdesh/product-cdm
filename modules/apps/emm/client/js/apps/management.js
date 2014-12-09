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

    $(document).ready(function() {
	// HELPER: #key_value
	//
	// Usage: {{#key_value obj}} Key: {{key}} // Value: {{value}} {{/key_value}}
	//
	// Iterate over an object, setting 'key' and 'value' for each property in
	// the object.
        
        
     $("#app-list-bar").owlCarousel({
         navigation : true,
         items: 10
     });  
	
	
	
	$.ajaxSetup({
	  contentType : "application/json"
	});
	Handlebars.registerHelper("key_value", function(obj, options) {
	    var buffer = "",
	        key;
		
	    for (key in obj) {
	        if (obj.hasOwnProperty(key)) {
	            buffer += options.fn({ 
				                key: key, 
				                value: obj[ key ] 
				            });
	        }
	    }
	    return buffer;
	});
	$(".alert").hide();
	$('.app_box a').click(function(){
		$('.app_box a').removeClass('selected');
		$(this).addClass('selected');
		$(this).parent().css("cursor", "default");
		$(this).fadeTo("slow", 1);
		changeState();
		var packageId = $(this).data('packageid');
		var platform = $(this).data('platform');
		var url = $(this).data('url');
		var type = $(this).data('type');
		var id = $(this).data('id');
		$('.stakes > .nav li').each(function(){
			if($(this).hasClass('active')){
				var currentTab = $(this).children('a').attr('href');
                if (currentTab == '#roles') {
                    viewRoles(packageId, platform, type, url, id);
                } else if (currentTab == '#users') {
                    $(currentTab + ' .nav li').each(function () {
                        if ($(this).hasClass('active')) {
                            if ($(this).children('a').hasClass('install')) {
                                viewUsersInstalled(packageId, platform);
                            } else if ($(this).children('a').hasClass('not-installed')) {
                                viewUsersNotInstalled(packageId, platform, type, url, id);
                            }
                        }
                    });
                }
            }
        });
		
	});
	$('#users .nav li a').off('click').click(function(){
		var selectedApp = $('.app_box .selected');
		var packageId = selectedApp.data('packageid');
		var platform = selectedApp.data('platform');
		var url = selectedApp.data('url');
		var type = selectedApp.data('type');
		var id = selectedApp.data('id');
        if (packageId) {
            if ($(this).hasClass('install')) {
                viewUsersInstalled(packageId, platform);
            } else if ($(this).hasClass('not-installed')) {
                viewUsersNotInstalled(packageId, platform, type, url, id);
            }
        }
    });
    $('.stakes > .nav li').off('click').click(function () {
        if ($(this).hasClass('active')) {
            var currentTab = $(this).children('a').attr('href');
            if (currentTab == '#roles') {
                var selectedApp = $('.app_box .selected');
                var packageId = selectedApp.data('packageid');
                var platform = selectedApp.data('platform');
                var url = selectedApp.data('url');
                var type = selectedApp.data('type');
                var id = selectedApp.data('id');
                if (packageId) {
                    viewRoles(packageId, platform, type, url, id);
                }
            }
        }
    });

    //Handlers for check and uncheck all checkboxes in the users tables when all checkbox is changed
    $(document).on("click", "#checkAllUserInstalled", function () {
        if ($(this).prop('checked')) {
            $('#users_installed .main-table input[type=checkbox]').each(function () {
                $(this).prop('checked', true);
            });
        } else {
            $('#users_installed .main-table input[type=checkbox]').each(function () {
                $(this).prop('checked', false);
            });
        }
    });

    $(document).on("click", "#checkAllUserNotInstalled", function () {
        if ($(this).prop('checked')) {
            $('#users_not-installed .main-table input[type=checkbox]').each(function () {
                $(this).prop('checked', true);
            });
        } else {
            $('#users_not-installed .main-table input[type=checkbox]').each(function () {
                $(this).prop('checked', false);
            });
        }
    });

    $(document).on("click", "#checkAllRoles", function () {
        if ($(this).prop('checked')) {
            $('#roles_app_status .main-table input[type=checkbox]').each(function () {
                $(this).prop('checked', true);
            });
        } else {
            $('#roles_app_status .main-table input[type=checkbox]').each(function () {
                $(this).prop('checked', false);
            });
        }
    });
    var viewRoles = function (packageId, platform, type, url, id) {
        $.get('/emm/api/apps/roles/', {
            'platform': platform,
            'packageid': packageId
        },function (data) {
            compileHandlbarsTemplate('roles_table', data, function (tempGen) {
                $('#roles_app_status').html(tempGen);
                var roles_app_status = $('#roles_app_status .main-table').dataTable({
                    "sDom": "<'row-fluid'<'tabel-filter-group span8'T><'span4'f>r>t<'row-fluid'<'span6'i><'span6'p>>",
                    "iDisplayLength": 20,
                    "bStateSave": false,
                    "aoColumnDefs": [
                        {
                            bSortable: false,
                            aTargets: [ 0 ]
                        }
                    ],
                    "oTableTools": {
                        "aButtons": ["copy", "print", {
                            "sExtends": "collection",
                            "sButtonText": 'Save <span class="caret" />',
                            "aButtons": ["csv", "xls", "pdf"]
                        }]
                    }
                });
                var getSelectedRoles = function () {
                    var roles = [];
                    $('#roles_app_status .main-table input[type=checkbox]:checked').each(function () {
                        roles.push($(this).data('role'));
                    });

                    if ($('#checkAllRoles').prop('checked') && (roles.length > 1)) {
                        roles = [];
                        roles.push('Internal/everyone');
                    }
                    return roles;
                };
                $('#roles .btn-orange').off('click').click(function () {
                    var roles = getSelectedRoles();
                    if (roles.length) {
                        $.post('/emm/api/apps/roles/install', JSON.stringify({
                            'roles': roles,
                            'platform': platform,
                            'packageid': packageId,
                            'url': url,
                            'type': type,
                            'id': id
                        }),function () {
                            noty({
                                text: 'App will be installed to selected roles',
                                'layout': 'center'
                            });
                        }).fail(function (err) {
                                switch (err.status) {
                                    case 403:
                                        noty({
                                            text: 'Unauthorized Access. Please login.',
                                            'layout': 'center'
                                        });
                                        break;
                                    default :
                                        noty({
                                            text: 'Operation unsuccessful. Please try again.',
                                            'layout': 'center'
                                        });
                                        break;
                                }
                            });
                    } else {
                        noty({
                            text: 'Please select one or more roles to install the app',
                            'layout': 'center'
                        });
                    }
                });

                $('#roles .btn-danger').off('click').click(function () {
                    var roles = getSelectedRoles();
                    if (roles.length) {
                        $.post('/emm/api/apps/roles/uninstall', JSON.stringify({
                            'roles': roles,
                            'platform': platform,
                            'packageid': packageId
                        }),function () {
                            noty({
                                text: 'App will be uninstalled from the selected roles',
                                'layout': 'center'
                            });
                        }).fail(function (err) {
                                switch (err.status) {
                                    case 403:
                                        noty({
                                            text: 'Unauthorized Access. Please login.',
                                            'layout': 'center'
                                        });
                                        break;
                                    default :
                                        noty({
                                            text: 'Operation unsuccessful. Please try again.',
                                            'layout': 'center'
                                        });
                                        break;
                                }
                            });
                    } else {
                        noty({
                            text: 'Please select one or more roles to uninstall the app',
                            'layout': 'center'
                        });
                    }
                });
            });
        }).fail(function (err) {
                switch (err.status) {
                    case 403:
                        noty({
                            text: 'Unauthorized Access. Please login.',
                            'layout': 'center'
                        });
                        break;
                    default :
                        noty({
                            text: 'Operation unsuccessful. Please try again.',
                            'layout': 'center'
                        });
                        break;
                }
            });
    };

    var viewUsersInstalled = function (packageId, platform) {
        $.get('/emm/api/apps/users/installed', {
            'platform': platform,
            'packageid': packageId
        },function (data) {
            compileHandlbarsTemplate('users_table_installed', data, function (tempGen) {
                $('#users_installed').html(tempGen);
                var roles_table_installed = $('#users_installed .main-table').dataTable({
                    "sDom": "<'row-fluid'<'tabel-filter-group span8'T><'span4'f>r>t<'row-fluid'<'span6'i><'span6'p>>",
                    "iDisplayLength": 20,
                    "bStateSave": false,
                    "aoColumnDefs": [
                        {
                            bSortable: false,
                            aTargets: [ 0 ]
                        }
                    ],
                    "oTableTools": {
                        "aButtons": ["copy", "print", {
                            "sExtends": "collection",
                            "sButtonText": 'Save <span class="caret" />',
                            "aButtons": ["csv", "xls", "pdf"]
                        }]
                    }
                });
                $('#users button').removeClass('btn-success');
                $('#users button').removeClass('btn-orange');
                $('#users button').addClass('btn-danger').text("Uninstall");
                $('#users button').off('click').click(function () {
                    var users = [];
                    var selectAll = false;
                    $('#users_installed .main-table input[type=checkbox]:checked').each(function () {
                        users.push($(this).data('user'));
                    });

                    if ($('#checkAllUserInstalled').prop('checked')) {
                        selectAll = true;
                    }

                    if (selectAll && (users.length > 1)) {
                        var roles = [];
                        roles.push('Internal/everyone');
                        $.post('/emm/api/apps/roles/uninstall', JSON.stringify({
                            'roles': roles,
                            'platform': platform,
                            'packageid': packageId
                        }),function () {
                            noty({
                                text: 'App will be uninstalled from all users',
                                'layout': 'center'
                            });
                        }).fail(function (err) {
                                switch (err.status) {
                                    case 403:
                                        noty({
                                            text: 'Unauthorized Access. Please login.',
                                            'layout': 'center'
                                        });
                                        break;
                                    default :
                                        noty({
                                            text: 'Operation unsuccessful. Please try again.',
                                            'layout': 'center'
                                        });
                                        break;
                                }
                            });
                        return;
                    }

                    if (users.length && !selectAll) {
                        $.post('/emm/api/apps/users/uninstall', JSON.stringify({
                            'users': users,
                            'platform': platform,
                            'packageid': packageId
                        }),function () {
                            noty({
                                text: 'App will be uninstalled from the selected users',
                                'layout': 'center'
                            });
                        }).fail(function (err) {
                                switch (err.status) {
                                    case 403:
                                        noty({
                                            text: 'Unauthorized Access. Please login.',
                                            'layout': 'center'
                                        });
                                        break;
                                    default :
                                        noty({
                                            text: 'Operation unsuccessful. Please try again.',
                                            'layout': 'center'
                                        });
                                        break;
                                }
                            });
                    } else {
                        noty({
                            text: 'Please select one or more users to uninstall the app',
                            'layout': 'center'
                        });
                    }

                });
            });
        }).fail(function (err) {
                switch (err.status) {
                    case 403:
                        noty({
                            text: 'Unauthorized Access. Please login.',
                            'layout': 'center'
                        });
                        break;
                    default :
                        noty({
                            text: 'Operation unsuccessful. Please try again.',
                            'layout': 'center'
                        });
                        break;
                }
            });
    };
    var viewUsersNotInstalled = function (packageId, platform, type, url, id) {

        $.get('/emm/api/apps/users/not-installed', {
            'platform': platform,
            'packageid': packageId
        },function (data) {
            compileHandlbarsTemplate('users_table_notinstalled', data, function (tempGen) {
                $('#users_not-installed').html(tempGen);
                var roles_table_installed = $('#users_not-installed .main-table').dataTable({
                    "sDom": "<'row-fluid'<'tabel-filter-group span8'T><'span4'f>r>t<'row-fluid'<'span6'i><'span6'p>>",
                    "iDisplayLength": 20,
                    "bStateSave": false,
                    "aoColumnDefs": [
                        {
                            bSortable: false,
                            aTargets: [ 0 ]
                        }
                    ],
                    "oTableTools": {
                        "aButtons": ["copy", "print", {
                            "sExtends": "collection",
                            "sButtonText": 'Save <span class="caret" />',
                            "aButtons": ["csv", "xls", "pdf"]
                        }]
                    }
                });
                $('#users button').removeClass('btn-danger');
                $('#users button').addClass('btn-success').text("Install");
                $('#users button').addClass('btn-orange');
                $('#users button').off('click').click(function () {
                    var users = [];
                    var selectAll = false;
                    $('#users_not-installed .main-table input[type=checkbox]:checked').each(function () {
                        users.push($(this).data('user'));
                    });

                    if ($('#checkAllUserNotInstalled').prop('checked')) {
                        selectAll = true;
                    }

                    if (selectAll && (users.length > 1)) {
                        var roles = [];
                        roles.push('Internal/everyone');

                        $.post('/emm/api/apps/roles/install', JSON.stringify({
                            'roles': roles,
                            'platform': platform,
                            'packageid': packageId
                        }),function () {
                            noty({
                                text: 'App will be installed to the all users',
                                'layout': 'center'
                            });
                        }).fail(function (err) {
                                switch (err.status) {
                                    case 403:
                                        noty({
                                            text: 'Unauthorized Access. Please login.',
                                            'layout': 'center'
                                        });
                                        break;
                                    default :
                                        noty({
                                            text: 'Operation unsuccessful. Please try again.',
                                            'layout': 'center'
                                        });
                                        break;
                                }
                            });
                        return;
                    }

                    if (users.length && !selectAll) {
                        $.post('/emm/api/apps/users/install', JSON.stringify({
                            'users': users,
                            'platform': platform,
                            'packageid': packageId,
                            'url': url,
                            'type': type,
                            'id': id
                        }),function () {
                            noty({
                                text: 'App will be installed to selected users',
                                'layout': 'center'
                            });
                        }).fail(function (err) {
                                switch (err.status) {
                                    case 403:
                                        noty({
                                            text: 'Unauthorized Access. Please login.',
                                            'layout': 'center'
                                        });
                                        break;
                                    default :
                                        noty({
                                            text: 'Operation unsuccessful. Please try again.',
                                            'layout': 'center'
                                        });
                                        break;
                                }
                            });
                    } else {
                        noty({
                            text: 'Please select one or more users to install the app',
                            'layout': 'center'
                        });
                    }
                });
            });
        }).fail(function (err) {
                switch (err.status) {
                    case 403:
                        noty({
                            text: 'Unauthorized Access. Please login.',
                            'layout': 'center'
                        });
                        break;
                    default :
                        noty({
                            text: 'Operation unsuccessful. Please try again.',
                            'layout': 'center'
                        });
                        break;
                }
            });
    }

    //Utilities
	var changeState = function(){
		$('.app_box a').not('.selected').each(function(){
			$(this).fadeTo("slow", 0.1);
		});
	};
	var compileHandlbarsTemplate = function(template_name, data, callback){
		$.get('/emm/client/partials/apps/'+template_name+'.hbs', function(template){
			var tempInstance = Handlebars.compile(template);
			var tempGen = tempInstance(JSON.parse(data));
			callback(tempGen);
		});
	};
});


$( "#mobile-apps-platform-filter" ).change(function() {
    var platform = $(this).val();
    if(platform == 'all'){
        return;   
    }
    $( ".app_box" ).each(function( index ) {
        if($(this).data('platform') == platform){
            $(this).show();
        }else{
            $(this).hide()
        }
    });
});


$( "#mobile-apps-search" ).keyup(function() {
    var search = $(this).val().toLowerCase();
    $( ".app_box" ).each(function( index ) {
      
        if($(this).data('name').toLowerCase().indexOf(search) > -1){
            $(this).show();
        }else{
            $(this).hide();
        }
    });
});

function wordInString(s, word){
  return new RegExp( '\\b' + word + '\\b', 'i').test(s);
}
    
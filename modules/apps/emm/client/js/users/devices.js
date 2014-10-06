var url = document.location.toString();
if (url.match('#')) {
    $('.nav-tabs a[href=#'+url.split('#')[1]+']').tab('show') ;
} 

var selectedTab = null;
var selectedDevice = null;



$(document).ready(function() {
	var countSec=1;
	var previousY=0;
	var str="";//string can keep  growing and cause issues
	var maxX=20;
	var dataY=0
	var data = [],
	totalPoints = 100;
	var ws = new WebSocket("ws://localhost:9763/outputwebsocket/socket_pool/sensor");
	ws.onopen = function(){
	};
	ws.onmessage = function (evt){ 
		var received_msg = JSON.parse(evt.data);
		dataY = received_msg.event.payloadData.temperature;
	};
	ws.onclose = function(){ 
	};
	function getRandomData() {
		y =  dataY;//current point, replace this with point 1
		var prevX=countSec-1;
		if(str==""){
			str="["+prevX+", "+previousY+"],["+countSec+","+y+"]";
		}else{
			str=str+",["+prevX+", "+previousY+"],["+countSec+","+y+"]";
		}
		var array = JSON.parse("["+str+"]");
		previousY=y;
		return array;
	}

	var updateInterval = 1000;
	var maxX=20;
	var initial=JSON.stringify(JSON.parse("[[0,0],[0.0]]"));

	var plot = $.plot("#placeholder", [ initial ], {
		series: {
			shadowSize: 0	// Drawing is faster without shadows
		},
		yaxis: {
			min: 0,
			max: 100
		},
		xaxis: {
			min: 0,
			max: maxX
		}
	});

	
	function update() {
		
		plot.setData([getRandomData()]);
		countSec++;
		// Since the axes change, we need to call plot.setupGrid()
		if(countSec>maxX){
					 plot.getOptions().xaxes[0].min = plot.getOptions().xaxes[0].min+1;
		            plot.getOptions().xaxes[0].max = plot.getOptions().xaxes[0].max+1;
		            plot.setupGrid();
		            plot.draw();
		}else{
		            
					plot.draw();
		}
		setTimeout(update, updateInterval);
	}

	update();
	
	var selDevice = window.location.hash;
	if(selDevice == ""){
		selDevice = "#device-tab-" + $("#devicesTab li").children( "a" ).data('deviceId');
	}
	selDevice = selDevice.replace("#device-tab-",""); 
	
	
	$("#devicesTab li").each(function(idx, li) {
    	
    	if($(this).children( "a" ).data('deviceId') == selDevice){
    		var selTab = $(this).children( "a" ).data('tabId');
    		tabId = $('#device-tab-heading-' + selTab).data("tabId");
			deviceId = $('#device-tab-heading-' + selTab).data("deviceId");
    	}
	});
	
	
	if(selDevice){
		deviceId = selDevice;
	}
	

	selectedTab = tabId;
	selectedDevice = deviceId;

	loadGeneralInformation(tabId, deviceId);
	loadAppList(tabId, deviceId);
	
	
	$( ".device-image" ).each(function( index ) {
		var srcImage = $(this).attr("src");
		if(!urlExists(srcImage)){			
			 $(this).attr("src", context().resourcePath + "none.png");			
		}
	});		
	

});

$('#devicesTab a').click(function(e) {
	var tabId = $(this).data("tabId");
	var deviceId = $(this).data("deviceId");

	selectedTab = tabId;
	selectedDevice = deviceId;

	loadGeneralInformation(tabId, deviceId);
	loadAppList(tabId, deviceId);

});


$('#devicesTab a').on('shown', function (e) {
    window.location.hash = e.target.hash;
    loadGeneralInformation(selectedTab, selectedDevice);
});

$(".btn-refresh").click(function() {
	var command = $(this).data("command");
	if (command == "genInfo") {
		loadGeneralInformation(selectedTab, selectedDevice);
	} else if (command == "appList") {
		loadAppList(selectedTab, selectedDevice);
	}

});

var selectedFeatureText = null;
var selectedFeature = null;
var selectedFeatureTemplate = null;
var selectedDevice = null;


/*drag and drop feature
$('.features-device').draggable({
	revert : true,
	cursor : 'move',
	start : function(ev, ui) {
		selectedFeatureText = $(this).html();
		selectedFeature = $(this).data('feature');
		selectedFeatureTemplate = $(this).data('template');
	},
	stop : function() {

	}
});


$('.device-image').droppable({
	tolerance : "pointer",
	drop : function() {
		selectedDevice = $(this).data('deviceId');
		prePerformOperation(selectedDevice, selectedFeature, selectedFeatureTemplate);
	}
});

*/


$(".features-device").click(function() {
	selectedFeatureText = $(this).html();
	selectedFeature = $(this).data('feature');
	selectedFeatureTemplate = $(this).data('template');	
	prePerformOperation(selectedDevice, selectedFeature, selectedFeatureTemplate);
});



function prePerformOperation(deviceId, feature, featureTemplate) {

	if (featureTemplate != "") {
		$.get('../client/templates/feature_templates/' + featureTemplate + '.hbs').done(function(templateData) {
			var template = Handlebars.compile(templateData);
			$("#featureModal").html(template({
				feature : feature,
				resourcePath : context().resourcePath
			}));
			$('#featureModal').modal('show');

		}).fail(function() {

		});

	} else {
		performOperation(deviceId, feature, {
			data : {}
		});
	}

}


$('#featureModal').on('click', '.feature-command', function(e) {
	var params = {};

	var value = $(this).data('value');
	if (value != "") {
		params['function'] = value;
	}

	var validationFailed = false;

	$(".feature-input").each(function(index) {
		if($(this).attr('type') == 'checkbox'){			
			params[$(this).attr("id")] = $(this).is(':checked');
		}else{
			params[$(this).attr("id")] = $(this).val();
			
			if($(this).data("required") == true && $(this).val() == ""){
				validationFailed = true;
				
				
				
			}
		}
	});
	
	if(validationFailed){
			noty({
					text : 'Please fill required fileds',
					'layout' : 'center',
					'modal': false,
					'type': 'error'
				});
			return;	
	}else{
		$('#featureModal').modal('hide');
	}

	performOperation(selectedDevice, selectedFeature, {
		data : params
	});

});

function performOperation(deviceId, feature, params) {

	noty({
		text : 'Are you sure you want to perform this operation?',
		buttons : [{
			addClass : 'btn btn-cancel',
			text : 'Cancel',
			onClick : function($noty) {
				$noty.close();

			}
			
			
		}, {
			
			addClass : 'btn btn-orange',
			text : 'Ok',
			onClick : function($noty) {

				jQuery.ajax({
							url : getServiceURLs("performDeviceOperation",
									deviceId, feature),
							type : "POST",
							async : "false",
							data : JSON.stringify(params),
							contentType : "application/json",
							dataType : "json",

							error : function(datas) {

								if (datas.status == 200) {

									noty({
										text : 'Operation queued successfully!',
										'layout' : 'center',
										'modal' : false
									});

									$noty.close();

								} else if (datas.status == 404) {

									noty({
										text : 'Operation not allowed!',
										'layout' : 'center',
										'modal' : false,
										type : 'error'
									});

									$noty.close();

								} else {
									noty({
										text : 'Operation failed!',
										'layout' : 'center',
										'modal' : false,
										type : 'error'
									});

									$noty.close();

								}

							}

						});

			}
			
		}]
	});

}

function loadAppList(tabId, deviceId) {

	$("#app-list-" + tabId).html("");

	jQuery.ajax({
		url : getServiceURLs("getDeviceAppList", deviceId),
		type : "GET",
		dataType : "json",
		success : function(appList) {
			//appList.received_data = JSON.parse(appList.received_data)[0].data;
			appList.received_data = JSON.parse(appList.received_data);
			
			//limit to 100
			if(appList.received_data.length > 50){
				appList.received_data.splice(50, appList.received_data.length - 50);
			}
			
			for(var i = 0; i < appList.received_data.length; i++){
				if(urlExists(context().appsImageService  + "/" +  appList.received_data[i].package + ".png")){
					appList.received_data[i].image = context().appsImageService + "/" + appList.received_data[i].package + ".png";
				}else{
					appList.received_data[i].image = context().resourcePath + "os/default-icon.png";
				}
				
				
				//patch for ios
					if(appList.received_data[i].Identifier){
						appList.received_data[i].package = appList.received_data[i].Identifier;
					}					
					if(appList.received_data[i].Name){
						appList.received_data[i].name = appList.received_data[i].Name;
					}				
				//patch for ios
				
				
				
			}

			$.get('../client/partials/users/applist.hbs', function(templateData) {
				var template = Handlebars.compile(templateData);
				$("#app-list-" + tabId).html(template({
					appList : appList,
					resourcePath : context().resourcePath,
					appsImageService: context().appsImageService
			}));
				

				$('.bxslider').bxSlider({
					minSlides : 5,
					maxSlides : 40,
					slideWidth : 300,
					slideMargin : 10
				});

			});

		},
		error : function(jqXHR, textStatus, errorThrown) {

		}
	});

}

function loadGeneralInformation(tabId, deviceId) {

	jQuery.ajax({
		url : getServiceURLs("getDeviceGenInfo", deviceId),
		type : "GET",
		dataType : "json",
		success : function(genInfo) {

			//genInfo.received_data = JSON.parse(genInfo.received_data).data;
            genInfo.received_data = JSON.parse(genInfo.received_data);
            
			$.get('../client/partials/users/geninfo.hbs', function(templateData) {
				var template = Handlebars.compile(templateData);
				$("#gen-info-" + tabId).html(template({
					genInfo : genInfo,
					resourcePath : context().resourcePath
				}));
				// $('.gen-info').tooltip();
				var receivedData = genInfo.received_data;
                
				//var receivedData = {};
				//receivedData.location_obj = {};
				//receivedData.location_obj.latitude = "6.9123661";
				//receivedData.location_obj.longitude = "79.8525739";
				
				//$('#map_canvas').gmap({'zoom':17, 'center': receivedData.location_obj.latitude + "," + receivedData.location_obj.longitude }).bind('init', function(ev, map) {
	           //     $('#map_canvas').gmap('addMarker', { 'position': map.getCenter(), 'bounds': false}).click(function() {
	                   // $('#map_canvas').gmap('openInfoWindow', { 'content': 'Location' }, this);
	           //     	$('#map_canvas').gmap('openInfoWindow', { }, this);
	           //     });
	          // });
	          //alert(receivedData.location_obj.latitude);
	         	            
	        
	         
	         if(receivedData.location_obj){	
	         	
	         	$('#location-map-canvas-block-'+ deviceId).css("display", "block");
			    $('#location-map-canvas-'+ deviceId).gmap({'zoom':17, 'center': receivedData.location_obj.latitude + "," + receivedData.location_obj.longitude }).bind('init', function(ev, map) {
						$('#location-map-canvas-'+ deviceId).gmap('addMarker', { 'position': map.getCenter(), 'bounds': false}).click(function() {
							$('#location-map-canvas-'+ deviceId).gmap('openInfoWindow', {'content': 'Location of the device'}, this);
					});				
				});    
	         	
	         	
	         }
	         
	            
	            
			
	            
	            
				
				
				
				$("#locationModalLink").click(function() {
					$('#locationModal').removeClass("invisible");					
				});
				
				
				
			});

		},
		error : function(jqXHR, textStatus, errorThrown) {

		}
	});

	loadNotifications(tabId, deviceId);

}

$(".btn-enroll").click(function() {
    var enrollURL = $(this).data('url') +  "/emm/api/device_enroll";
         
	noty({
                       text : '<u>Enroll URL</u>:<br><span style="word-wrap:break-word;">' + enrollURL + '</span><div  id="qrcode" style="width:200px; padding-left:45px"></div>',
                        buttons : [{
                            addClass : 'btn btn-orange',
                            text : 'OK',
                            onClick : function($noty) {
                                $noty.close();
                            }


                        }]
                    });
     updateQRCode(enrollURL);    

}); 

function updateQRCode(text) {

    var element = document.getElementById("qrcode");



    var bodyElement = document.body;
    if(element.lastChild)
        element.replaceChild(showQRCode(text), element.lastChild);
    else
        element.appendChild(showQRCode(text));

}

function loadNotifications(tabId, deviceId) {

	jQuery.ajax({
		url : getServiceURLs("getDeviceNotifications", deviceId),
		type : "GET",
		dataType : "json",
		success : function(notifications) {

			for ( i = 0; i < notifications.length; i++) {
				if(notifications[i].received_data){
					if(notifications[i].received_data.length>0){
						notifications[i].received_data = JSON.parse(notifications[i].received_data);
					}
				}
			}

			$.get('../client/partials/users/notifications.hbs', function(teplateData) {
				var template = Handlebars.compile(teplateData);
				$("#notifications-" + tabId).html(template({
					notifications : notifications
				}));
			});

		},
		error : function(jqXHR, textStatus, errorThrown) {

		}
	});

}
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

var selectedGroup = null;

treeData = null;




$("#btn-add-features").click(function() {
	
	 selNodes = null
	 
	 $("#tree3").dynatree("getRoot").visit(function (node) {
        selNodes = node.tree.getSelectedNodes();        
     });
     
    
     featureList = Array();     
     var selKeys = $.map(selNodes, function(node1){            
            if(!node1.data.isFolder){
            	featureList.push(node1.data.value);
            }
            
     });	
     
    	
	var bundleName = $('#inputBundleName').val();
		
	
	jQuery.ajax({
		url : getServiceURLs("permissionsCRUD", ""),
		type : "PUT",
		async : "false",
		data: JSON.stringify({selectedGroup: selectedGroup, featureList: featureList}),		
		contentType : "application/json",
		dataType : "json",
		error: function(datas){
	       	
	       					if (datas.status == 200) {

									noty({
										text : 'Permission Added success!',
										'layout' : 'center',
										'modal' : false
									});
									

								} else {

									noty({
										text : 'Permission assigning failed!',
										'layout' : 'center',
										'modal' : false,
										type : 'error'
									});

									
								}  	
	       	
	    }		
	});
	
	
	
});



$(".add-permission-link").click(function() {	
	selectedGroup = $(this).data("group");	
	$('#assign-permission-heading').html("Assign permissions to " + selectedGroup);

});



$(".btn-item-remove").click(function() {
	var item = $(this).data("item");
		
	noty({
		text : 'Are you sure you want delete this role?',
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
					url : getServiceURLs("groupsCRUD", item),
					type : "DELETE",					
					contentType : "text/plain"
			
				}).done(function() {
					$noty.close();
					window.location.reload(true);
				});
			}
			
		}]
	});	


});


$(".btn-invite").click(function() {
    
   
	
	var item = $(this).data("item");
		
	noty({
		text : 'Are you sure you want invite this group?',
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
				$noty.close();
               
				
				jQuery.ajax({
					url : getServiceURLs("groupsInvite"),
					type : "PUT",					
					data : JSON.stringify({'groupid': item}),		
					contentType : "application/json",
			     	dataType : "json",
			     	statusCode: {
						400: function() {
							
							enrollWithoutEmail();
						},
						404: function() {
							
							enrollWithoutEmail();
						},
                        403: function() {
							
							enrollWithoutEmail();
						},
						500: function() {
							noty({
								text : 'Fatal error occured!',
								'layout' : 'center',
								'type': 'error'
							});
						},
						200: function() {
							noty({
								text : 'Group is invited successfully!',
								'layout' : 'center'
							});
							window.location.assign("configuration");
						}
					}
			
				});
				
				
				
				
				
			}
			
		}]
	});
	
	
});




$(document).ready( function () {
        
       
        
                
        
        oTable = $('#main-table').dataTable( {
                "sDom": "<'row-fluid'<'tabel-filter-group span8'T><'span4'f>r>t<'row-fluid'<'span6'i><'span6'p>>",
                "iDisplayLength": 10,
                 "bStateSave": false,
                 "aoColumnDefs": [
					  {
					     bSortable: false,
					     aTargets: [ -1 ]
					  }
				],
                "oTableTools": {
                        "aButtons": [
                                "copy",
                                "print",
                                {
                                        "sExtends": "collection",
                                        "sButtonText": 'Save <span class="caret" />',
                                        "aButtons": [ "csv", "xls", "pdf" ]
                                }
                        ]
                }
        } );
        
        
$(".tabel-filter-group").html("Type: " + fnCreateSelect( oTable.fnGetColumnData(1)));
        
        $('.tabel-filter-group select').change( function () {
            oTable.fnFilter( $(this).val(), 1 );
     } );
                
        

        
} );


function fnCreateSelect( aData ){
    var r='<select><option value="">--All--</option>', i, iLen=aData.length;
    for ( i=0 ; i<iLen ; i++ )
    {
        r += '<option value="'+aData[i]+'">'+aData[i]+'</option>';
    }
    return r+'</select>';
}



function updateQRCode(text) {

    var element = document.getElementById("qrcode");



    var bodyElement = document.body;
    if(element.lastChild)
        element.replaceChild(showQRCode(text), element.lastChild);
    else
        element.appendChild(showQRCode(text));

}


function enrollWithoutEmail() {
    var enrollURL = location.protocol+'//'+location.hostname+(location.port ? ':'+location.port: '') + "/emm/api/device_enroll";
         
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

}     

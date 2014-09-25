$(".claim").click(function(e){
	var id = $(this).data("button");
	jQuery.ajax({
        url : getServiceURLs("devicesIoTClaim", id),
        type : "POST",
        statusCode: {
            201: function(data) {
                noty({
                    text : 'Device claimed successfully',
                    'layout' : 'center' ,
                    buttons : [{
                            addClass : 'btn btn-orange',
                            text : 'OK',
                            onClick : function($noty) {
                                $noty.close();
                                location.reload();
                            }
                        }]
                });
            }
        },
        data : JSON.stringify({}),
		contentType : "application/json"
    });
});
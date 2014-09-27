$(".btn-generate").click(function(){
	jQuery.ajax({
        url : getServiceURLs("usersToken", ""),
        type : "GET",
        statusCode: {
            201: function(data) {
                data = JSON.parse(data);
                noty({
                    text : 'Token was generated successfully. <div style="width:200px; padding-left:45px"> TOKEN = '+data.token+'</div>',
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
        }				
    });
});
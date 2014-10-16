

/*
 Tenant configuration function
 */

configuration = function(appController) {
    var context = appController.context();
	context.title = context.title + " | Configuration";
	context.page = "configuration";
	context.jsFile = "tenant/configuration.js";
	context.data = {
		configOption : "tenant"
	};
	return context;
};

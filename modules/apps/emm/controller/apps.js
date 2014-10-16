
/*
Application Management function
*/
management = function (appController) {
    var context = appController.context();
    context.title = context.title + " |  Devices Management";
    context.page = "appmanagement";
    context.jsFile = "apps/management.js";
    var storeModule = require('/modules/store.js').store;
    var store = new storeModule();

    context.data = {
        apps: store.getAppsFromStore(),
        store_location : context.config.mam.store_location,
        publisher_location : context.config.mam.archieve_location_general
    };
    return context;
};
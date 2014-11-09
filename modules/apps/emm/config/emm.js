var config = function () {
    var conf = application.get("PINCH_CONFIG");
    if (!conf) {
        var pinch = require('/modules/pinch.min.js').pinch,
            server = require('carbon').server;
        config = require('/config/config.json'),
            pinch(config, /^/, function (path, key, value) {
                if ((typeof value === 'string') && value.indexOf('%https.ip%') > -1) {
                    return value.replace('%https.ip%', server.address("https"));
                } else if ((typeof value === 'string') && value.indexOf('%http.ip%') > -1) {
                    return value.replace('%http.ip%', server.address("http"));
                }
                return  value;
            });
        application.put("PINCH_CONFIG", config);
        conf = config;
    }
    return conf;
};
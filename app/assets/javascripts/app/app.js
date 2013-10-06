// Filename: app.js
define([ 'jquery', 'underscore', 'backbone', 'Bootstrap','app/view' ], function($, _, Backbone, Bootstrap, View) {

    var initialize = function() {
        if (!window.console) {
            window.console = {
                log : function() {}
            };
        }
        $.ajaxSetup({
            cache : false
        });
        if (!this.view) {
            this.view = new View();
        }
    };
    return {
        initialize : initialize
    };
});

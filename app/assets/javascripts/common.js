require.config({
    shim : {
        underscore : {
            exports : '_'
        },
        backbone : {
            deps : ['jquery','underscore'],
            exports : 'Backbone'
        },
        Bootstrap : {
            deps : ['jquery']
        }
    },
    paths : {
        app:'/assets/javascripts/app',
        jquery : '/assets/javascripts/jquery-1.10.2.min',
        underscore : '/assets/javascripts/underscore-min',
        backbone : '/assets/javascripts/backbone-min',
        json2 : '/webjars/json2/20110223/json2.min',
        Bootstrap: '/assets/bootstrap/js/bootstrap.min'
    }
});
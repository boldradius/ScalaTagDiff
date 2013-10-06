// filename map.js
define([ 'jquery', 'underscore', 'backbone', 'Bootstrap', 'app/renderHtml' ], function($, _, Backbone, Bootstrap, RenderHtml) {
    var view = Backbone.View.extend({
        el : '#body',
        initialize : function(options) {
            this.stateId = 0;
            this.serverRequest('GET',{event:'INIT',id:null});
        },

        events : {
            "click .clickable" : "linkClick",
            "change .selectable" : "selectChange",
            "keyup .searchable" : "search"
        },


        linkClick : function(event) {
            event.preventDefault();
            this.sendClick(event)
        },

        sendClick : function(event) {
//            var formAction = this.getTagData(event.target, "formaction");
//            if (formAction !== undefined) {
//                this.postActionWS([ 'PostEvent', {
//                    formAction : JSON.parse(formAction),
//                    payload : $('#' + this.getTagData(event.target, "formid")).serialize()
//                } ]);
//            }
            var getAction = this.getTagData(event.target, "getaction");
            if (getAction !== undefined) {
                this.serverRequest("Get", JSON.parse(getAction));
            }
        },

        getTagData : function(element, data) {
            if (element.dataset[data] !== undefined) return element.dataset[data];
            else {
                if (element.parentElement != undefined) return this.getTagData(element.parentElement, data);
                else {
                    return undefined;
                }
            }
        },


        serverRequest : function(type,event,formString){
            var self = this;

            if(type == "POST"){
                $.ajax({
                    data:[this.stateId, data ],
                    type: "POST",
                    contentType : 'application/json',
                    url:"/page",
                    success:function(result){
                        self.handleServerResponse(result)
                        }
                    });
            }else{

                var route = {
                    clientStateId: this.stateId++,
                    event:event
                }
                 $.ajax({
                    type: "GET",
                    contentType : 'application/json',
                    url:"/browserGetEvent/"+encodeURIComponent(JSON.stringify(route)),
                    success:function(result){
                        self.handleServerResponse(result)
                        }
                    });
            }

        },

        handleServerResponse : function(data) {
            var self = this;
            this.stateId = data.responseId
            console.log("handleServerResponse " + data)
            if (data.page != null) {
                if (data.page.left !== undefined) {
                    $('#main').html(data.page.left.main)
                    if(data.page.left.modal !== null){
                       $('#main').append(data.page.left.modal.contents);
                        $('#'+data.page.left.modal.id).modal();
                    }
                    if( data.page.left.focus !== undefined){
                        $('#'+data.page.left.focus).focus().val($('#'+data.page.left.focus).val());
                    }
                } else {
                    RenderHtml.renderWindowDiff(data.page.right)
                    if( data.page.right.focus !== undefined){
                      if($('input:focus').size() == 0){
                        $('#'+data.page.right.focus).focus().val($('#'+data.page.right.focus).val());
                      }
                    }
                }
            }
        }
    });
    return view;
});

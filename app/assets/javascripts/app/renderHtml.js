/**
 * Library for rendering Html based on Element and ElementDiff in scala codebase
 */

function makeElement(markup) {
    var div = document.createElement('div');
    div.innerHTML = markup.substring(markup.indexOf('<')); // Skip preceeding text (like a newline)
    var node = div.firstChild
    div.removeChild(node)
    return node
}
function makeDiv(children) {
    var div = document.createElement('div');
    _.each(children, function(c) {
       div.appendChild(c) 
    });
    return div
}
function replaceElement(old, nw) {
    var parent = old.getParent()
    parent.insertBefore(nw, old)
    parent.removeChild(old)
}
// Adds when old is null
function replaceOrAddElement(parent, old, nw) {
    parent.insertBefore(nw, old)
    if (old != null) parent.removeChild(old)
}

define([ 'jquery', 'underscore'], function($, _ ) {
    return {
        renderWindowDiff : function(windowDiff) {
            if (windowDiff.main.left) $('#main').html(windowDiff.main.left)
            else if(windowDiff.main.right != null){
            var firstChild = document.getElementById("main")
                this.renderTagDiff(firstChild.childNodes[0], windowDiff.main.right)
            }
            if(windowDiff.modal.left !== null){
               $('#modal').append(windowDiff.modal.left.contents);
                $('#modal').modal('show');
            }else {
            $('#modal').modal('hide');
            $('#modal').empty();
            }
        },
        
        renderModal : function(modal) {
            var markup = this.renderMarkup(modal.contents)
            var m = makeDiv([markup])
            $(m).modal({
                backdrop : 'static', // set to true to allow backdrop and dismiss when clicking on it
                keyboard : false, // set to true to support Escape to dismiss modal window
                show : true
            }).css({
                // make width 90% of screen
                'width' : function() {
                    // return ($(document).width() * (self.width/100)) + 'px';
                    return modal.width + 'px';
                },
                'z-index' : 1500,
                // center model
                'margin-left' : "auto"
            });
            return m
        },
        
        // case class ModalDiff(contents: tagDiff)
        modalDiff : function(modal, diff) {
            this.rendertagDiff(modal.children[0], diff.contents)
            /*
                    this.dialogForm.hide();
                    var t = setTimeout(function() {
                        self.dialogForm.clear();
                    }, 1000);
                    */
        },
        
        renderMarkup : function(markup) {
            var node = makeElement(markup.tag)
            _.each(this.renderElement(markup), function(c) {
                node.appendChild(c)
            })
            return node
        },
        
        
        // At the moment returns a sequence of children instead of a complete element
        renderElement : function(element) {
            var self = this;
            return _.map(element.children, function(child) {
                var c = self.renderMarkup(child[1])
                c.id = child[0]
                return c;
            })
        },

        updateAttrs : function(element, attrs) {
            _.each(element.attributes, function(item,index) {
               if (item !== "id" && item !== "src" && item !== "href" && item !== "data-getaction")  element.removeAttribute(item);
            });

            _.each(attrs, function(e) {
                element.setAttribute(e[0],e[1])
            });
        },


       updateAttrsInPlace : function(element, attrs) {
            $('#' + element).each(function() {
                var attributes = $.map(this.attributes, function(item) {
                    return item.name;
                });
                var elem = $(this);
                $.each(attributes, function(i, item) {
                    if (item !== "id" && item !== "src" && item !== "href"  && item !== "data-getaction") elem.removeAttr(item);
                });
            });
            var obj = {}
            _.each(attrs, function(e, index) {
                obj[e[0]] = e[1];
            });
            $('#' + element).attr(obj);
        },





        arrayToObject : function(arr) {
            var rv = {};
            for ( var i = 0; i < arr.length; ++i)
                if (arr[i] !== undefined) rv[i] = arr[i];
            return rv;
        },



//  case class TagDiff(
//                    id: String,
//                    classesDiff: Option[List[String]] = None,
//                    cssDiff: Option[List[(String, String)]] = None,
//                    diffChildren: List[Diff] = Nil)
//  case class Diff(diff: Either[STag, Option[TagDiff]])
        renderTagDiff : function(element, tagDiff) {
            if (tagDiff == null) return
            else {
                var self = this
                var initialChildren = element.childNodes;
            if(tagDiff.a != null){

            var attributes = $.map(element.attributes, function(item) {
                console.log(item.name);
                if(item.name !== "class" && item.name !== "style" && item.name !== "type")
                return item.name;
              });

               _.each(attributes, function(attr){
                  $(element).removeAttr(attr)
               });
                _.each(tagDiff.a, function(attrDiff){
                    $(element).attr(attrDiff[0],attrDiff[1])
                })
            }
            if(tagDiff.c != null){
                $(element).attr('class', '');
                _.each(tagDiff.c, function(classesDiff){
                    $(element).addClass(classesDiff)
                });
            }
            if(tagDiff.s != null){
                $(element).attr('style', '');
                _.each(tagDiff.s, function(cssDiff){
                    $(element).css(cssDiff[0],cssDiff[1])
                })
            }
            _.each(tagDiff.ch, function(childDiff, index) {
                var actingOn = initialChildren[index]
                var nodeType = 1
                if(initialChildren[index] !== undefined){
                    nodeType = initialChildren[index].nodeType
                }
                if( childDiff.right !== undefined  ){
                    if(childDiff.right !== null){
                       self.renderTagDiff(actingOn,childDiff.right);
                    }
                }else{
                    var html =$.parseHTML(childDiff.left)
                   if( actingOn !== undefined){
                        $(actingOn).replaceWith(html);
                   }else $(element).append(html);
                }
            })
            while (element.childNodes[tagDiff.ch.length] != null){
                element.removeChild(element.childNodes[tagDiff.ch.length])
            }
          }
        }
    };
})

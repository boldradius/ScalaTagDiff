require([ 'app/app', 'json2' ], function(app, json) {
    window.Option = function(value) {
        var self = this;
        var obj = {

            optionValue : value,
            map : function(f) {
                return this.isNone() ? obj : Option(f(this.optionValue))
            },
            flatMap : function(f) {
                if (this.isNone()) return obj;
                else {
                    var newOption = f(this.optionValue);
                    if (newOption === null || typeof (newOption.isSome) == 'undefined')
                        throw new Error("not a flatMappable function");
                    return newOption;
                }

            },
            getOrElse : function(n) {
                return this.isNone() ? n : value
            },
            fold : function(emptyValue, nonEmptyFunction) {
                return this.isNone() ? emptyValue : nonEmptyFunction(this.optionValue)
            },
            isNone : function() {
                return this.optionValue === undefined || this.optionValue === null
            },
            isSome : function() {
                return !this.isNone()
            }
        }
        return obj;
    }
    app.initialize();
});

var MacroSubView = function() {
    function MacroSubView(macroSub) {
        var self = this;
        var _model = macroSub;
		var _container = $("#macro"+macroSub.macroId).find("ul");
		var _element;
        self.getModel = function() {
            return _model;
        };

		self.getElement = function() {
			return _element;
		};
		
        var init = function() {
           	var subli = HTMLBuilder.macroLiBtnBuilder(macroSub);
            subli.appendTo(_container);
			_element = subli;
        };
        init();

        self.deleteView = function() {
            self.getElement().remove();
        };

        self.updateView = function(model) {
			self.getElement().interceptStr({
				max:8,
				text:model.label,
				setText: function(str){
					$(this).text(str);
				}
			});
        };
    }
    return MacroSubView;
} ();
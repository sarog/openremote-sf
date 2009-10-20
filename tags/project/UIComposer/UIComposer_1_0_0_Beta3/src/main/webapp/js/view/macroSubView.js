var MacroSubView = function() {
    function MacroSubView(model,container) {
        var self = this;
        var _model = model;
		var _container = container;
		var _element;
        self.getModel = function() {
            return _model;
        };

		self.getElement = function() {
			return _element;
		};
		
        var init = function() {
           	var subli = HTMLBuilder.macroLiBtnBuilder(model);
            subli.appendTo(_container);
			_element = subli;
        };
        init();

        self.deleteView = function() {
            self.getElement().remove();
        };

        self.updateView = function() {
			self.getElement().interceptStr({
				max:8,
				text:self.getModel().label,
				setText: function(str){
					$(this).text(str);
				}
			});
        };
    }
    return MacroSubView;
} ();
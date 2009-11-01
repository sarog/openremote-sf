var MacroView = function() {
    function MacroView(macro) {
        var self = this;
        var _model = macro;
        self.getModel = function() {
            return _model;
        };
		
		/**
		 * Gets entire macro element, have macro button and macro sub li
		 * @public
		 * @returns macro element
		 * @type Object JQuery DOM element
		 */
        self.getElement = function() {
            return $("#" + self.getModel().getElementId());
        };

        var init = function() {
            var macroBtn = HTMLBuilder.macroBtnBuilder(self.getModel());
            var info = $("#macro .item_container p");
            if (info.size() != 0) {
                info.remove();
            }
            $(macroBtn).prependTo($("#macro .item_container"));
        };
        init();

		/**
		 * get macro button
		 * @public
		 * @returns macro button element
		 * @type Object JQuery dom element of macro button
		 */
        self.getMacroBtn = function() {
            return self.getElement().find(".macro_btn");
        };

		/**
		 * get macro sub ul element
		 * @public
		 * @returns ul jquery dom element
		 * @type Object JQuery dom element of macro sub ul
		 */
        self.getSubList = function() {
            return self.getElement().find(".macro_detail");
        };

		self.updateView = function() {
			self.getMacroBtn().interceptStr({
				text:self.getModel().label,
				maxLength:14
			});
		};
		
		self.deleteView = function() {
			self.getElement().remove();
		};
    }
    return MacroView;
} ();
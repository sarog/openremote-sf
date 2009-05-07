var InfraredView = function() {
    function InfraredView (infrerad) {
		self = this;
		var _model = infrerad;

		self.getModel = function() {
			return _model;
		};

		self.getElement = function() {
			return $("#"+self.getModel().getElementId());
		};

		var init = function () {
			HTMLBuilder.infraredBtnBuilder(self.getModel()).appendTo($("#command_container"));
		};
		init();
	}

	return InfraredView;
}();
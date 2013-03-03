var InfraredView = function() {
    function InfraredView (infrerad) {
		var self = this;
		var _model = infrerad;

		self.getModel = function() {
			return _model;
		};

		self.getElement = function() {
			return $("#"+self.getModel().getElementId());
		};

		(function init () {
			HTMLBuilder.infraredBtnBuilder(self.getModel()).appendTo($("#command_container"));
		})();
	}

	return InfraredView;
}();
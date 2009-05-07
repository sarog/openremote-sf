var InfraredView = function() {
    function InfraredView (infrerad) {
		self = this;
		self.model = infrerad;
		
		self.getModel = function() {
			return self.model;
		};
		
		self.getElement = function() {
			return $("#"+self.model.getElementId());
		};
		
		var init = function () {
			HTMLBuilder.infraredBtnBuilder(self.model).appendTo($("#command_container"));
		}();
	}
	
	return InfraredView;
}();
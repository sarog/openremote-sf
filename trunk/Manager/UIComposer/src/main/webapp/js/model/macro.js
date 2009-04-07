var Macro = function() {
	function Macro () {
		var self = this;
		self.id = -1;
		self.label = "";
		self.buttons = {};		
		
		this.elementId = function() {
			return "macro"+self.id;
		};
		
	}

	return Macro;
}();
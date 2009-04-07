var X10 = function() {
	function X10() {
		var self = this;
		self.id = -1;
		self.label = "";
		self.address = "";
		self.command = "";
		
		this.elementId = function() {
			return "x10"+self.id;
		};
	}

	return X10;
}();
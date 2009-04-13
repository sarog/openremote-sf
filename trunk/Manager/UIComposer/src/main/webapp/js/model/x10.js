var X10 = function() {
	function X10() {
		var self = this;
		self.id = -1;
		self.label = "";
		self.address = "";
		self.command = "";
		// convenient way to get the Class name.
		self.className = "X10";
		
		this.elementId = function() {
			return "x10"+self.id;
		};
	}
	
	X10.init = function(model) {
		var x10 = new X10();
		x10.id      = model.id     ;
		x10.label   = model.label  ;
		x10.address = model.address;
		x10.command = model.command;
		return x10;
	};
	

	return X10;
}();
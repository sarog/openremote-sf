var KNX = function() {
	KNX = function() {
		var self = this;
		self.id = -1;
		self.label = "";
		self.groupAddress = "";
		
		this.elementId = function() {
			return "knx"+self.id;
		};
	};

	return KNX;
}();
var KNX = function() {
	function KNX () {
		var self = this;
		self.id = -1;
		self.label = "";
		self.groupAddress = "";
		// convenient way to get the Class name.
		self.className = "KNX";
		
		this.elementId = function() {
			return "knx"+self.id;
		};	
		
	}
	
	KNX.init = function(model) {
		var knx = new KNX();
		knx.id           = model.id          ;
		knx.label        = model.label       ;
		knx.groupAddress = model.groupAddress;
		return knx;
	};

	return KNX;
}();
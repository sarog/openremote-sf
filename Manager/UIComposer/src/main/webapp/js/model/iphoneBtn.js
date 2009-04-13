var IphoneBtn = function() {
	function IphoneBtn() {
		var self = this;
		self.id = -1;
		//Original model 
		self.oModel = null;
		self.x = -1;
		self.y = -1;
		self.height = -1;
		self.width = -1;
		// convenient way to get the Class name.
		self.className = "IphoneBtn";
	}
	
	IphoneBtn.init = function(model) {
		var iphoneBtn = new IphoneBtn();
		iphoneBtn.id     = model.id        ; 
		iphoneBtn.oModel = model.oModel    ;
		iphoneBtn.x      = model.x         ;
		iphoneBtn.y      = model.y         ;
		iphoneBtn.height = model.height    ;
		iphoneBtn.width  = model.width     ;
		return iphoneBtn;
	};
	
	return IphoneBtn;
}();
var Infrared = function() {
	function Infrared(){
		var self = this;
		self.id = -1;
		self.name = "";
		self.command = "";
		self.label = "";
		self.sectionId = -1;
		//for indetificate each infrared item
		self.codeId = -1;
		// convenient way to get the Class name.
		self.className = "Infrared";
	}
	//private method
	function createInfraredWithButton (btn) {
		var infrared = new Infrared();
		infrared.id = BUTTONID++;
		infrared.name = btn.data("remoteName");
		infrared.command = btn.data("command");
		infrared.label = btn.data("command");
		infrared.sectionId = btn.data("sectionId");
		infrared.codeId = btn.data("codeId");
		InfraredCollection[btn.data("codeId")] = infrared;
		return infrared;
	};
	
	//static method
	Infrared.getInfraredModelWithDraggable = function(btn)  {
		if (InfraredCollection[btn.data("codeId")] === undefined) {
            model = createInfraredWithButton(btn);
        } else {
			model = InfraredCollection[btn.data("codeId")];
		}
		return model;
	};
	
	Infrared.init = function(model) {
		var infrared = new Infrared();
		infrared.id        = model.id       ;
		infrared.name      = model.name     ;
		infrared.command   = model.command  ;
		infrared.label     = model.label    ;
		infrared.sectionId = model.sectionId;
		return infrared;
	};
	return Infrared; 
}();
var Infrared = function() {
	function Infrared(){
		var self = this;
		self.id = -1;
		self.name = "";
		self.command = "";
		self.label = "";
		self.sectionId = -1;
	}
	//private method
	function createInfraredWithButton (btn) {
		var infrared = new Infrared();
		infrared.id = BUTTONID++;
		infrared.name = btn.data("remoteName");
		infrared.command = btn.data("command");
		infrared.label = btn.data("command");
		infrared.sectionId = btn.data("sectionId");
		InfraredCollection[btn.data("codeId")] = infrared;
		return infrared;
	};
	
	//static method
	Infrared.getInfraredModelWithDraggable = function(btn)  {
		if (typeof(InfraredCollection[btn.data("codeId")]) == "undefined") {
            model = createInfraredWithButton(btn);
        } else {
			model = InfraredCollection[btn.data("codeId")];
		}
		return model;
	};
	return Infrared; 
}();
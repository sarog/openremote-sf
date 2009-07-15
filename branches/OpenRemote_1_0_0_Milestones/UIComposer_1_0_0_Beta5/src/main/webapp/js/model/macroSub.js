var MacroSub = function() {
	function MacroSub(macroId,model){
		var self = this;
		
		Model.call(self);
		
		self.oModel = model;
		self.label = model.label;
		self.macroId = macroId;
		self.delay = 0;

		self.inspectViewTemplate = "template/_macroSubInspect.ejs ";
	}
	/**
     * Create new instance from flat model (which have no private method).
     * @param model flat model (which have no private method).
     * @returns created new instance.
     */
	MacroSub.init = function(model) {
		var macroSub = new MacroSub(model.macroId,model.oModel);
		macroSub.id    = model.id;
		macroSub.label = model.label;
		macroSub.oModel = model.oModel;
		macroSub.macroId = model.macroId;
		macroSub.delay = model.delay;
		return macroSub;
	};
	return MacroSub;
}();
var Macro = function() {
	function Macro () {
		var self = this;
		self.id = -1;
		self.label = "";
		// convenient way to get the Class name.
		self.className = "Macro";
		
		
		this.elementId = function() {
			return "macro"+self.id;
		};
		
		
		this.getSubModels = function() {
			var models = new Array();
			findSubLi(self).each(function() {
				var m = $(this).data("model");
				
				// // for solving json stringfy problem
				// 			if (m instanceof Macro) {
				// 				var tmp = new Macro();
				// 				tmp.id = m.id;
				// 				tmp.label = m.label;
				// 				m = tmp;
				// 			}
				models.push(m);
				
			});
			return models;
		};
		
		this.getSubModelsRecursively = function() {
			var models = new Array();
			findSubLi(self).each(function() {
				var m = $(this).data("model");
				foreach(m,models);
			});
			return models;
		};
	
		
		function findSubLi (macro) {
			return $("#" + macro.elementId()).next("ul").find("li");
		}
		
		function foreach (model,models) {
			if(model.className == "Macro") {
				if (model == self) {
					return;
				}
				findSubLi(model).each(function() {
					foreach($(this).data("model"),models);
				});
			} else {
				models.push(model);
			}
		}
	}
	
	Macro.init = function(model) {
		var macro = new Macro();
		macro.id    = model.id;
		macro.label = model.label;
		return macro;
	};
	
	return Macro;
}();
/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
var Macro = function() {
	function Macro () {
		var self = this;
		Model.call(self);
		
        //text ui interface display
		self.label = "";
		// convenient way to get the Class name.
		self.className = getClassName(self);

         //public methods
		/**
         * Get HTML getElementId
         */
		self.getElementId = function() {
			return "macro"+self.id;
		};
       
        /**
         * Gets direct models of current Macro
         */
		self.getSubModels = function() {
			var models = new Array();
			findSubLi(self).each(function() {
				var m = $(this).data("model");	
				models.push(m);
			});
			return models;
		};

        /**
         * Get all models recursively of current Macro, look up deeply into sub macro 
         */
		self.getSubModelsRecursively = function() {
			var models = new Array();
			findSubLi(self).each(function() {
				var m = $(this).data("model");
				foreach(m,models);
				
			});
			return models;
		};
		
		self.inspectViewTemplate = "template/_macroInspect.ejs";

		

        //private methods
        /**
         * find sub
         * @param macro
         */
		function findSubLi (macro) {
			return $("#" + macro.getElementId()).find("ul").find("li");
		}

       
		function foreach (model,models) {
			if(model.oModel.className == "Macro") {
				if (model.oModel == self) {
					return;
				}
				findSubLi(model.oModel).each(function() {
					foreach($(this).data("model"),models);
				});
			} else {
				model.oModel.delay = model.delay;
				models.push(model.oModel);
			}
		}
	}


    /**
     * Create new instance from flat model (which have no private method).
     * @param model flat model (which have no private method).
     * @returns created new instance.
     */
	Macro.init = function(model) {
		var macro = new Macro();
		macro.id    = model.id;
		macro.label = model.label;
		return macro;
	};
	
	return Macro;
}();
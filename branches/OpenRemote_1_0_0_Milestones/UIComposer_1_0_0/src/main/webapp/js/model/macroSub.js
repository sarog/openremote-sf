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
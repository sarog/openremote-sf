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

Screen = function() {
	function Screen () {
		var self = this;
		Model.call(self);
		self.name = "";
		self.label = self.name;
		self.row = -1;
		self.col = -1;
		
		self.buttons = new Array();
	}
	
	/**
     * Create new instance from flat model (which have no private method).
     * @param model flat model (which have no private method).
     * @returns created new instance.
     */
	Screen.init = function (model) {
		var screen = new Screen();
		screen.id = model.id;
		screen.name = model.name;
		screen.row = model.row;
		screen.col = model.col;

		return screen;
	};
	
	
	return Screen;
}();
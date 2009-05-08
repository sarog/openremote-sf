/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU General Public License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF site:
 * http://www.fsf.org.
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
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

var X10 = function() {
	function X10() {
		var self = this;
		self.id = -1;
        //text ui interface display
		self.label = "";
		self.address = "";
		self.command = "";
		// convenient way to get the Class name.
		self.className = "X10";

        /**
         * Get HTML elementId
         */
		this.elementId = function() {
			return "x10"+self.id;
		};
	}

    /**
     * Create new instance from flat model (which have no private method).
     * @param model flat model (which have no private method).
     * @returns created new instance.
     */
	X10.init = function(model) {
		var x10 = new X10();
		x10.id      = model.id     ;
		x10.label   = model.label  ;
		x10.address = model.address;
		x10.command = model.command;
		return x10;
	};
    
	return X10;
}();
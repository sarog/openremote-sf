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
		self.className = getClassName(self);
	}

    /**
     * Create new instance from flat model (which have no private method).
     * @param model flat model (which have no private method).
     * @returns created new instance.
     */
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
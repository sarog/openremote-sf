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
var Infrared = function() {

	function Infrared(){
		var self = this;
		
		Model.call(self);
		
		self.name = "";
		self.command = "";
        //text ui interface display
		self.label = "";
		self.vendorName = "";
		self.modelName = "";
		self.sectionId = -1;
		//for indetificate each infrared item
		self.codeId = -1;
		
		self.getElementId = function () {
			return "infrared"+self.id;
		};
	}

    /**
     * Create new instance from flat model (which have no private method).
     * @param model flat model (which have no private method).
     * @returns created new instance.
     */
	Infrared.init = function(model) {
		var infrared = new Infrared();
		infrared.id        = model.id       ;
		infrared.name      = model.name     ;
		infrared.command   = model.command  ;
		infrared.label     = model.label    ;
		infrared.vendorName = model.vendorName;
		infrared.modelName = model.modelName;
		infrared.sectionId = model.sectionId;
		infrared.codeId    = model.codeId   ;
		return infrared;
	};
	return Infrared; 
}();
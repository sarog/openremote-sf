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

var IphoneBtn = function() {
	function IphoneBtn() {
		var self = this;
		
		Model.call(self);
		
		//Original model 
		self.oModel = null;
		self.label = "";
		self.x = -1;
		self.y = -1;
		self.height = -1;
		self.width = -1;
		self.icon = "";

		
		self.getElementId = function() {
			return "iphoneBtn"+self.id;
		};
		
		self.fillArea = function() {
			for (var i=0; i < self.width; i++) {
				var x = self.x + i;
				for (var j=0; j < self.height; j++) {
					var y = self.y + j;
					ScreenView.btnInArea[x][y] = true;
				};
			};
		};
		
		self.clearArea = function() {
			for (var i=0; i < self.width; i++) {
				var x = self.x + i;
				for (var j=0; j < self.height; j++) {
					var y = self.y + j;
					ScreenView.btnInArea[x][y] = false;
				};
			};
		};
		
		self.inspectViewTemplate = "template/_iphoneBtnInspect.ejs";
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
		iphoneBtn.label  = model.label     ;
		iphoneBtn.label  = model.label     ;
		iphoneBtn.icon   = model.icon      ;
		
		return iphoneBtn;
	};
	
	return IphoneBtn;
}();
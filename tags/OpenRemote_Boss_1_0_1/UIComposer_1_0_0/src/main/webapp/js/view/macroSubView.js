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
var MacroSubView = function() {
    function MacroSubView(macroSub) {
        var self = this;
        var _model = macroSub;
		var _container = $("#macro"+macroSub.macroId).find("ul");
		var _element;
        self.getModel = function() {
            return _model;
        };

		self.getElement = function() {
			return _element;
		};
		
        var init = function() {
           	var subli = HTMLBuilder.macroLiBtnBuilder(macroSub);
            subli.appendTo(_container);
			_element = subli;
        };
        init();

        self.deleteView = function() {
            self.getElement().remove();
        };

        self.updateView = function(model) {
			self.getElement().interceptStr({
				max:8,
				text:model.label,
				setText: function(str){
					$(this).text(str);
				}
			});
        };
    }
    return MacroSubView;
} ();
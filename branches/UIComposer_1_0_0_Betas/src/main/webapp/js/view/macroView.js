/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2009, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 3.0 of the
 * License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU General Public License along with this software; if
 * not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston,
 * MA 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
var MacroView = function() {
    function MacroView(macro) {
        var self = this;
        var _model = macro;
        self.getModel = function() {
            return _model;
        };
		
		/**
		 * Gets entire macro element, have macro button and macro sub li
		 * @public
		 * @returns macro element
		 * @type Object JQuery DOM element
		 */
        self.getElement = function() {
            return $("#" + self.getModel().getElementId());
        };

        var init = function() {
            var macroBtn = HTMLBuilder.macroBtnBuilder(self.getModel());
            var info = $("#macro .item_container p");
            if (info.size() != 0) {
                info.remove();
            }
            $(macroBtn).prependTo($("#macro .item_container"));
        };
        init();

		/**
		 * get macro button
		 * @public
		 * @returns macro button element
		 * @type Object JQuery dom element of macro button
		 */
        self.getMacroBtn = function() {
            return self.getElement().find(".macro_btn");
        };

		/**
		 * get macro sub ul element
		 * @public
		 * @returns ul jquery dom element
		 * @type Object JQuery dom element of macro sub ul
		 */
        self.getSubList = function() {
            return self.getElement().find(".macro_detail");
        };

		self.updateView = function() {
			self.getMacroBtn().interceptStr({
				text:self.getModel().label,
				maxLength:14
			});
		};
		
		self.deleteView = function() {
			self.getElement().remove();
		};
    }
	
	MacroView.getContainer = function(macro) {
		return $("#" + macro.getElementId()).find(".macro_detail");
	};
	
    return MacroView;
} ();
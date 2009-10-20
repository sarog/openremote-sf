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

/*
 * TODO
 *
 * @author allen.wei@finalist.cn
 */
InspectView = function() {
	return {
		updateView: function(options){
			EJSHelper.updateView(options.template,'inspect_detail',options.model);
			var inspectWindow = $("#inspect_tool_bar");
			$("#inspect_button").show();
			inspectWindow.data("model",options.model);
			var left = 0;
			var top = 0;
			if ((options.y + inspectWindow.outerHeight()) > document.body.clientHeight) {
				top = options.y - inspectWindow.outerHeight();
			} else {
				top = options.y;
			}
			
			if ((options.x + inspectWindow.outerWidth()) > document.body.clientWidth) {
				left = options.x - inspectWindow.outerWidth();
			} else {
				left = options.x;
			}
			
			inspectWindow.css("left",left);
			inspectWindow.css("top",top);
			inspectWindow.css("position","absolute");
			inspectWindow.show();			
		},
		//this method will be overload by ejs template
		getModel:function () {
			return $("#inspect_tool_bar").data("model");
		},
		getElement: function(){
			return $("#inspect_tool_bar");
		},
		hideView: function(){
			InspectView.getElement().hide();
			$(".highlightInspected").removeClass("highlightInspected");
		}
	};
}();


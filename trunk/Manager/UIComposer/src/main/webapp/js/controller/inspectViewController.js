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
InspectViewController = function() {
	return {
		init : function() {
			
		},

		/**
		 * Update Inspect view 
		 * @static
		 * @param {Object} options.model model need to inspect.
		 * @param {String} options.template ejs template to render inspect window
		 * @param {Function} options.after  (optional) call after render inspect window
		 */
		updateView : function(options) {

			InspectView.updateView(options);
			$("#inspect_body").clearTips();
			
	        if (options.after !== undefined) {
	             options.after.call(InspectView.getElement());
	        }
			
			
			$("#close_inspect_btn").unbind().hover(function() {
				$(this).addClass("ui-state-hover");
			},function() {
				$(this).removeClass("ui-state-hover");
			}).click(function() {
				InspectView.hideView();
			});
			$("#inspect_ok_btn").unbind().click(function() {
				if (options.check!== undefined && !options.check()) {
					return;
				} else {
					InspectView.getModel().updateModel();
					InspectView.hideView();
				}
				
			});
			$("#inspect_delete_btn").unbind().click(function() {
				var model = InspectView.getModel();
				model.deleteModel();
				InspectView.hideView();
			});
			
			InspectView.getElement().draggable({
				handle:$("#inspect_header"),
				cursor:"move"
			});
		}
	};
}();
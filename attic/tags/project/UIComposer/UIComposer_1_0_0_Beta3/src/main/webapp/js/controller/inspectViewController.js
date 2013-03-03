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
			
			
	        if (options.after !== undefined) {
	             options.after.call(this);
	        }
			
			
			$("#close_inspect_btn").unbind().hover(function() {
				$(this).addClass("ui-state-hover");
			},function() {
				$(this).removeClass("ui-state-hover");
			}).click(function() {
				InspectView.hideView();
			});
			$("#inspect_ok_btn").unbind().click(function() {
				InspectView.getModel().updateModel();
				InspectView.hideView();
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
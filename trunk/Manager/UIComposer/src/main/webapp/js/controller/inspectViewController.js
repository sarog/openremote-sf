InspectViewController = function() {
	return {
		defaultText : "Click an element to inspect it.",
		init : function() {
			$("#inspect_detail").html(InspectViewController.defaultText);
		},

		/**
		 * Update Inspect view 
		 * @static
		 * @param {Object} options.model model need to inspect.
		 * @param {String} options.template ejs template to render inspect window
		 * @param {Function} options.after  (optional) call after render inspect window
		 */
		updateView : function(options) {
			InspectView.updateView(options.model,options.template);
			$("#inspect_button").show();
	        if (options.after !== undefined) {
	             options.after();
	        }

			$("#inspect_ok_btn").unbind().click(function() {
				InspectView.getModel().updateModel();
				InspectViewController.showDefaultView();
			});
			$("#inspect_delete_btn").unbind().click(function() {
				var model = InspectView.getModel();
				model.deleteModel();
			});
		},

		showDefaultView : function(){
			$("#inspect_detail").html(InspectViewController.defaultText);
	        $("#inspect_button").hide();
			$(".highlightInspected").removeClass("highlightInspected");
			$("#inspect_tool_bar").removeData("model");
		}
	};
}();
InspectViewController = function() {
	function InspectViewController () {
		
	}
	
	InspectViewController.defaultText = "Click an element to inspect it.";
	InspectViewController.init = function() {
		$("#inspect_detail").html(InspectViewController.defaultText);
	};
	
	InspectViewController.updateView = function(model) {
		InspectView.updateView(model);
		$("#inspect_button").show();
        if (model.afterShowInspect !== undefined) {
             model.afterShowInspect();
        }
		
		$("#inspect_ok_btn").unbind().click(function() {
			InspectView.getModel().updateModel();
			InspectViewController.defaultView();
		});
		$("#inspect_delete_btn").unbind().click(function() {
			var model = InspectView.getModel();
			
			if (model.deleteModel !== undefined) {
				model.deleteModel();
			} else {
				$("#"+model.elementId()).remove();
			}
			
			// If there are some clean stuff should do, add a method afterDelete in model.
			if (model.afterDelete !== undefined) {
				model.afterDelete();
			}
            InspectViewController.defaultView();
		});
	};
	
	InspectViewController.defaultView = function(){
		$("#inspect_detail").html(InspectViewController.defaultText);
        $("#inspect_button").hide();
		$(".highlightInspected").removeClass("highlightInspected");
		$("#inspect_tool_bar").data("model",null);
	};
	return InspectViewController;
}();
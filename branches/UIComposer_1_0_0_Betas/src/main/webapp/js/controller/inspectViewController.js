InspectViewController = function() {
	function InspectViewController () {
		
	}
	
	InspectViewController.init = function() {
		$("#inspect_ok_btn").unbind().click(function() {
			var model = InspectView.getModel();
			model.label = InspectView.getLabelValue();
			IPhoneController.updateIphoneBtn(model);
		});
		$("#inspect_delete_btn").unbind().click(function() {
			IPhoneController.deleteIphoneBtn(InspectView.getModel());
		});
		
	};
	return InspectViewController;
}();
InspectView = function() {
	return {
		updateView: function(model){
			EJSHelper.updateView(model.inspectViewTemplate,'inspect_detail',model);
			$("#inspect_tool_bar").data("model",model);
		},
		getModel:function (model) {
			return $("#inspect_tool_bar").data("model");
		}
	};
}();






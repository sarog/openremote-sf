InspectView = function() {
	return {
		updateView: function(model,template){
			EJSHelper.updateView(template,'inspect_detail',model);
			$("#inspect_tool_bar").data("model",model);
		},
		getModel:function () {
			return $("#inspect_tool_bar").data("model");
		}
	};
}();






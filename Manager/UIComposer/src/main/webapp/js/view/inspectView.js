InspectView = function() {
	return {
		updateView: function(model){
			new EJS({url:model.inspectViewTemplate}).update('inspect_detail',model);
			$("#inspect_tool_bar").data("model",model);
		},
		getModel:function (model) {
			return $("#inspect_tool_bar").data("model");
		}
	};
}();






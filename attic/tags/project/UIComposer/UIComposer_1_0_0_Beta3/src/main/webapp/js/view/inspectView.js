InspectView = function() {
	return {
		updateView: function(options){
			EJSHelper.updateView(options.template,'inspect_detail',options.model);
			var inspectWindow = $("#inspect_tool_bar");
			$("#inspect_button").show();
			var inspectWindow = $("#inspect_tool_bar");
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






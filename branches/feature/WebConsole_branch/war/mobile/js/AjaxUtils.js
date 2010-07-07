/**
 * This class is responsible for ajax requests.
 */
AjaxUtils = function() {

	var AjaxUtils = {
		
		sendAjax: function(postUrl, successCallback, errorCallback) {
			$.ajaxSetup({contentType:'application/x-www-form-urlencoded; charset=utf-8'});
			$.ajax({
				type: "POST",
				url: postUrl, 
			   	cache: false,
			   	success: successCallback,
			   	error: errorCallback || function(){}
			});
		},
		
		/**
		 * send ajax request with the feedback json data type
		 */
		sendAjaxJson: function(postUrl, successCallback, errorCallback) {
			$.ajaxSetup({contentType:'application/x-www-form-urlencoded; charset=utf-8'});
			$.ajax({
				type: "POST",
				url: postUrl, 
			   	cache: false, 
			   	dataType: "json",
			   	success: successCallback,
			   	error: errorCallback || function(){}
			});
		}
	};
	return AjaxUtils;
}();
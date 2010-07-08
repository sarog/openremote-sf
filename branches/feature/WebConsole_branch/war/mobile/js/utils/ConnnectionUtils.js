/**
 * This class is responsible for ajax requests.
 *
 * auther: handy.wang 2010-07-07
 */
ConnnectionUtils = function() {

	var ConnnectionUtils = {
		/**
		 * send ajax request with the feedback json data type.
		 * Used jquery plugin named jquery-jsonp.
		 */
		sendAjaxJson: function(requestURL, successCallback, errorCallback) {
		  $.jsonp({
          url: requestURL,
          beforeSend: function(xOptions) {
            
          },
          success: successCallback,
          error: errorCallback
      });
		}
	};
	return ConnnectionUtils;
}();
/**
 * This class is responsible for ajax requests.
 *
 * auther: handy.wang 2010-07-07
 */
ConnectionUtils = function() {

	var ConnectionUtils = {
	  /**
		 * Get URL value for control action.
		 */
	  getControlURL: function(componentID, commandValue) {
	    var currentControllerServerURL = CookieUtils.getCookie(Constants.CURRENT_SERVER).url;
      var controlURL = currentControllerServerURL + "/rest/control/" + componentID + "/" + commandValue;
      return controlURL;
	  },
	  
	  /**
		 * Get URL value for resource(mainly images).
		 */
	  getResourceURL: function(resourceName) {
	    var currentController = CookieUtils.getCookie(Constants.CURRENT_SERVER);
      return currentController.url + "/resources/" + resourceName;
	  },
	  
		/**
		 * Send ajax request with the feedback json data type.
		 * Used jquery plugin named jquery-jsonp.
		 * NOTE: this method can get error call back from response.
		 */
	  sendRequest: function(requestURL, delegate) {
      $.jsonp({
          url: requestURL,
          success: delegate.didRequestSuccess,
          error: delegate.didRequestError
      });
	  },
		
		/**
		 * This method is used to requests which need http basic authentication.
		 */
		sendRequestWithAuthen: function(requestURL, successCallback, errorCallback) {
		  var userInfo = UserInfo.getInstance();
		  userInfo.setUsername("handy");
		  userInfo.setPassword("handy");
      if (userInfo.getUsername() && userInfo.getUsername() != "" && userInfo.getPassword() && userInfo.getPassword != "") {
        $.ajax({
            type: "GET",
            url: requestURL, 
            dataType: "jsonp",
            beforeSend : function(xmlHttpRequest) {
              SecurityUtils.getHTTPBasicAuthRequest(xmlHttpRequest);
            },
            success: successCallback,
            error: errorCallback || function(){}
          }
        );
      } else {
        alert("requireUsernamePassword");
        // delegate.requireUsernamePassword();
      }
    }
	};
	return ConnectionUtils;
}();
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
      var controlURL = currentControllerServerURL + "/rest/control/" + componentID + "/" + commandValue + "?callback=?";
      return controlURL;
	  },
	  
	  getStatusURL: function(sensorIDs) {
	    var currentControllerServerURL = CookieUtils.getCookie(Constants.CURRENT_SERVER).url;
	    if(sensorIDs != null && sensorIDs != undefined && sensorIDs.length > 0) {
	      var pollingURL = currentControllerServerURL + "/rest/status/" + sensorIDs.join(",");
	      return pollingURL;
	    } else {
	      return null;
	    }
	  },
	  
	  getPollingURL: function(sensorIDs) {
	    var currentControllerServerURL = CookieUtils.getCookie(Constants.CURRENT_SERVER).url;
	    if(sensorIDs != null && sensorIDs != undefined && sensorIDs.length > 0) {
	      var webConsoleID = AppBoot.getInstance().webConsoleID;
	      var pollingURL = currentControllerServerURL + "/rest/polling/" + webConsoleID + "/" + sensorIDs.join(",");
	      return pollingURL;
	    } else {
	      return null;
	    }
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
		 *
		 * didFeedBackWithRequest method's structure is function(data, textStatus)
		 * didRequestError methods's structure is function(xOptions, textStatus)
		 *
		 * NOTE: this method can get error call back from response.
		 * error recovery in case of network failure or ill-formed JSON responses
		 */
	  sendJSONPRequest: function(requestURL, delegate) {
      $.jsonp({
          url: requestURL,
          callbackParameter: "callback",
          success: delegate.didRequestSuccess,
          //Error recovery in case of network failure or ill-formed JSON responses
          error: delegate.didRequestError
      });
	  },
		
		/**
		 * This method is used to requests which need http basic authentication.
		 */
		sendJSONPRequestWithAuthen: function(requestURL, successCallback, errorCallback) {
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
    },
    
    /**
		 * Send ajax request without json data feedback.
		 * The delegate didFeedBackWithRequest method's structure is function(data, textStatus, XMLHttpRequest).
		 */
		sendNormalRequest: function(requestURL, delegate) {
      $.ajax({
        type: "GET",
        url: requestURL,
        dataType: "jsonp",
        success: delegate.didFeedBackWithRequest
      });
 	  }
	};
	return ConnectionUtils;
}();
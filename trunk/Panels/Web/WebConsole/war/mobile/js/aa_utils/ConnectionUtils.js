/**
 * This class is responsible for ajax requests to controller server 
 * and providing some function about composing specific URL to controller server.
 *
 * author: handy.wang 2010-07-07
 */
ConnectionUtils = function() {

 /**
  * Constructor
  */
	var ConnectionUtils = {
	  /**
	   * Compose the url for roundrobin service of controller server.
	   */
	  getRoundRobinURL: function() {
	    var currentControllerServerURL = CookieUtils.getCookie(Constants.CURRENT_SERVER).url;
      var roundRobinURL = currentControllerServerURL + "/rest/servers?callback=?";
      return roundRobinURL;
	  },
	  
	  /**
	   * Compose the url for logout which controller server provides.
	   */
	  getLogoutURL: function() {
	    var currentControllerServerURL = CookieUtils.getCookie(Constants.CURRENT_SERVER).url;
      var logoutURL = currentControllerServerURL + "/logout?callback=?";
      return logoutURL;
	  },
	  
	  /**
		 * Get URL for control action provied by controller server.
		 */
	  getControlURL: function(componentID, commandValue) {
	    var currentControllerServerURL = CookieUtils.getCookie(Constants.CURRENT_SERVER).url;
      var controlURL = currentControllerServerURL + "/rest/control/" + componentID + "/" + commandValue + "?callback=?";
      return controlURL;
	  },
	  
	  /**
	   * Get URL of status query about devices provided by controller server.
	   */
	  getStatusURL: function(sensorIDs) {
	    var currentControllerServerURL = CookieUtils.getCookie(Constants.CURRENT_SERVER).url;
	    if(sensorIDs != null && sensorIDs != undefined && sensorIDs.length > 0) {
	      var pollingURL = currentControllerServerURL + "/rest/status/" + sensorIDs.join(",");
	      return pollingURL;
	    } else {
	      return null;
	    }
	  },
	  
	  /**
	   * Get URL of status polling about devices provided by controller server.
	   */
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
		 * Get qualified URL of resources(mainly images) where is in controller server side.
		 */
	  getResourceURL: function(resourceName) {
	    var currentController = CookieUtils.getCookie(Constants.CURRENT_SERVER);
      return currentController.url + "/resources/" + resourceName;
	  },
	  
		/**
		 * Send ajax request with feedback of json data using jquery plugin named jquery-jsonp.
		 * The parameter "requestURL" is where the current method sends request to.
		 *
		 * The structure of delegate method didRequestSuccess is "this.didRequestSuccess(data, textStatus)".
		 * The structure of delegate method didRequestError is "this.didRequestError(xOptions, textStatus)".
		 *
		 * NOTE: this method can get error call back from response in case of network failure or ill-formed JSON responses.
		 *
		 * The details about JQueryJSONP please check it's website.
		 */
	  sendJSONPRequest: function(requestURL, delegate) {
      $.jsonp({
          url: requestURL,
          callbackParameter: "callback",
          success: delegate.didRequestSuccess,
          error: delegate.didRequestError
      });
	  },
		
		/**
		 * This method is used to requests which need http basic authentication.
		 */
    // sendJSONPRequestWithAuthen: function(requestURL, successCallback, errorCallback) {
    //   var userInfo = UserInfo.getInstance();
    //   userInfo.setUsername("handy");
    //   userInfo.setPassword("handy");
    //       if (userInfo.getUsername() && userInfo.getUsername() != "" && userInfo.getPassword() && userInfo.getPassword != "") {
    //         $.ajax({
    //             type: "GET",
    //             url: requestURL, 
    //             dataType: "jsonp",
    //             beforeSend : function(xmlHttpRequest) {
    //               SecurityUtils.getHTTPBasicAuthRequest(xmlHttpRequest);
    //             },
    //             success: successCallback,
    //             error: errorCallback || function(){}
    //           }
    //         );
    //       } else {
    //         alert("requireUsernamePassword");
    //         // delegate.requireUsernamePassword();
    //       }
    //     },
    
    /**
		 * Send ajax request without feedback of json data.
		 * The parameter "requestURL" is where the current method sends request to.
		 *
		 * The structure of delegate method didFeedBackWithRequest is "this.didFeedBackWithRequest(data, textStatus, XMLHttpRequest)".
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
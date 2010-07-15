/**
 * This class is responsible for ajax requests.
 *
 * auther: handy.wang 2010-07-07
 */
ConnnectionUtils = function() {

	var ConnnectionUtils = {
		/**
		 * Send ajax request with the feedback json data type.
		 * Used jquery plugin named jquery-jsonp.
		 * NOTE: this method can get error call back from response.
		 */
		getJson: function(requestURL, successCallback, errorCallback) {
      $.jsonp({
          url: requestURL,
          success: successCallback,
          error: errorCallback
        }
      );
		},
		
		/**
		 * This method is used to requests which need http basic authentication.
		 */
		getJsonWithHTTPBasicAuth: function(requestURL, successCallback, errorCallback) {
		  var userInfo = UserInfo.getInstance();
		  userInfo.setUsername("handy");
		  userInfo.setPassword("handy");
      if (userInfo.getUsername() && userInfo.getUsername() != "" && userInfo.getPassword() && userInfo.getPassword != "") {
        // $.ajax({
        //     type: "GET",
        //     url: requestURL, 
        //     dataType: "jsonp",
        //     beforeSend : function(xmlHttpRequest) {
        //      xmlHttpRequest.setRequestHeader("Authorization", "Basic " + Base64.encode("handy:handy"));
        //      alert("before send.");
        //       //SecurityUtils.getHTTPBasicAuthRequest(xmlHttpRequest);
        //     },
        //     withCredentials:"true",
        //     username: "handy",
        //     password: "handy",
        //     success: successCallback,
        //     error: errorCallback || function(){}
        //   }
        // );
        
        
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
	return ConnnectionUtils;
}();
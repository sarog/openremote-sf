/**
 * This class is responsible for dealing some about security, such as HTTP Basic Authentication and so on.
 * author: handy.wang 2010-07-09
 */
SecurityUtils = (function(){
  return {
    /**
     * Append authorization header for HTTP Basic Authentication.
     */
    getHTTPBasicAuthRequest: function(xmlHttpRequest) {
      var userInfo = UserInfo.getInstance();
      var token = userInfo.getUsername() + ":" + userInfo.getPassword();
      var encodedToken = Base64.encode(token);
      xmlHttpRequest.setRequestHeader("Authorization", "Basic " + encodedToken);
    }
  };
})();
/**
 * This class is responsible for storing info about user.
 * author: handy.wang 2010-07-09
 */
 UserInfo = (function(){
   
   // private static variable
   var userInfo = null;
   
   // Constructor
   function UserInfo (usernameParam, passwordParam) {
      var username="";
      var password="";

      this.setUsername = function(usernameParam) {
        username = usernameParam || "";
      };

      this.getUsername = function() {
        return username;
      };

      this.setPassword = function(passwordParam) {
        password = passwordParam || "";
      };

      this.getPassword = function() {
        return password;
      };

      // Init jobs
      this.setUsername(usernameParam);
      this.setPassword(passwordParam);
    }
   
   return {
     getInstance: function() {
       if(!userInfo) {
         userInfo = new UserInfo();
       }
       return userInfo;
     }
   };
 })();
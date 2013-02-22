/**
 * This class is responsible for dealing with round robin.
 * Incldes request group members, store group members in cookie and switch to a available controller server.
 *
 * author: handy.wang 2010-08-09
 */
RoundRobinUtils = (function() {
  var roundRobinUtils = null;
  
  function RoundRobinUtils() {
    var self = this;
    
    /**
	   * Switch to a available controller server.
	   */
	  this.switchControllerServer = function() {
	    MessageUtils.showLoading("Switching controller server...");
	    var servers = CookieUtils.getCookie(Constants.GROUP_MEMBERS);
	    if (servers == null || servers == undefined || servers.length == 0) {
	      MessageUtils.hideLoading();
	      MessageUtils.showMessageDialogWithSettings("RoundRobin fail", "There's no server available. Leave this problem?");
	      return;
	    }
      var serverURL = servers[0][Constants.GROUP_MEMBER_URL];
      
      var oldCurrentServer = CookieUtils.getCookie(Constants.CURRENT_SERVER);
      var currentServer = new ControllerServer(serverURL);
      currentServer.selectedPanelIdentity = oldCurrentServer.selectedPanelIdentity;
      currentServer.panelIdentities = oldCurrentServer.panelIdentities;
      
      var controllerServers = CookieUtils.getCookie(Constants.CONTROLLER_SERVERS);
      controllerServers[controllerServers.length] = currentServer;

      CookieUtils.setCookie(Constants.CURRENT_SERVER, currentServer);
      CookieUtils.setCookie(Constants.CONTROLLER_SERVERS, controllerServers);
      NotificationCenter.getInstance().postNotification(Constants.REFRESH_VIEW_NOTIFICATION, null);
      MessageUtils.hideLoading();
	  },
    
    /** 
     * Cache group members into cookie and use it while polling and control fail.
     */
    this.cacheGroupMembers = function(delegate) {
      var roundRobinURL = ConnectionUtils.getRoundRobinURL();
      ConnectionUtils.sendJSONPRequest(roundRobinURL, delegate);
    };
    
    /**
     * Store the group members into cookie.
     */
    this.storeGroupMembers = function(data) {
      var currentServer = CookieUtils.getCookie(Constants.CURRENT_SERVER);
      var currentServerURL = currentServer.url;
      
      var servers = data.servers.server;
      if (Object.prototype.toString.apply(servers) === '[object Array]' && servers.length > 0) {
        var tempServers = [];
        for (var i = 0; i < servers.length; i++) {
          var tempServer = servers[i];
          if (tempServer[Constants.GROUP_MEMBER_URL] == currentServerURL) {
            continue;
          }
          tempServers[tempServers.length] = tempServer;
        }
        CookieUtils.setCookie(Constants.GROUP_MEMBERS, tempServers);
      } else if (servers != null) {
        var controllerURL = servers[Constants.GROUP_MEMBER_URL];
        if (currentServerURL == controllerURL) {
          // MessageUtils.showMessageDialog("RoudRobin", "No group member was found, but it doesn't matter for now.");
          return;
        }
        CookieUtils.setCookie(Constants.GROUP_MEMBERS, [servers]);
      } else {
        // MessageUtils.showMessageDialog("RoudRobin", "No group member was found, but it doesn't matter for now.");
      }
    }
    
  }
  
  return {
    getInstance : function() {
      if (roundRobinUtils == null) {
        roundRobinUtils = new RoundRobinUtils();
      }
      return roundRobinUtils;
    }
  }
})();




/**
 * It's responsible for loading panel data, parsing it and notify the AppBoot to call rootViewController to render groups, screens and so on.
 * author: handy.wang 2010-07-13
 */
UpdateController = (function() {
  var MSG_OF_NO_CONTROLLER_CONFIG = "Please enter a controller url in Settings panel, or leave it ?";
  
  return function(delegateParam) {
    var self = this;
    var delegate = delegateParam;
    
    /**
     * Begin update with downloading panel json-formed data.
     */
    this.update = function() {
      downloadJSONDataWithURL();
    };
    
    /**
     * It's responsible for downloading json data from controller server.
     */
    function downloadJSONDataWithURL() {
      MessageUtils.updateLoadingMessage("Checking settings ...");
      var currentServer = CookieUtils.getCookie(Constants.CURRENT_SERVER);
      
      // User don't set the controller url and panel identity.
      if (currentServer == null) {
        delegate.didUpdateFail(MSG_OF_NO_CONTROLLER_CONFIG);        
      } else {
        var currentServerURL = currentServer.url;
        var selectedPanelIdentity = currentServer.selectedPanelIdentity;
        
        if (currentServerURL == null || currentServerURL == "" || currentServerURL == undefined || 
          selectedPanelIdentity == null || selectedPanelIdentity =="" || selectedPanelIdentity == undefined) {
          delegate.didUpdateFail(MSG_OF_NO_CONTROLLER_CONFIG);
        } else {
          
          var panelRequestURL = currentServerURL+ "/rest/panel/" + selectedPanelIdentity + "?callback=?";
          MessageUtils.updateLoadingMessage("Downloading panel ...");

          ConnectionUtils.sendJSONPRequest(panelRequestURL, self);
        }
      }
    }
    
    // Fowllings two are delegate methods should be defined in ConnectionUtils.js .
    this.didRequestSuccess = function(jsonData, textStatus) {
      if (jsonData != null && jsonData != undefined) {
        var error = jsonData.error;
        if (error != null && error != undefined && error.code != Constants.HTTP_SUCCESS_CODE) {
          delegate.didUpdateFail("Download panel data fail with " + error.message + ", settings or leave it ?");
        } else {
          MessageUtils.updateLoadingMessage("Downloading panel success ...");
          parseJSONData(jsonData);
        }
      } else {
        delegate.didUpdateFail("Download panel data fail with unknown reason, settings or leave it ?");
      }
    };
    
    this.didRequestError = function(xOptions, textStatus) {
      delegate.didUpdateFail("Download panel data fail, settings or leave it ?");
    };
    
    /**
     * Parse json-formed data downloaded from controller server.
     */
    function parseJSONData(jsonData) {
      MessageUtils.updateLoadingMessage("Parsing panel ...");
      var jsonParser = new JSONParser(jsonData, self);
      jsonParser.startParse();
    }
    
    // Following two methods are delegate methods of JSONParser    
    this.didParse = function(jsonParser, nodeName, properties) {
      if (nodeName == Constants.SCREEN) {
        RenderDataDB.getInstance().addScreen(new Screen(jsonParser, properties));
      } else if (nodeName == Constants.GROUP) {
        RenderDataDB.getInstance().addGroup(new Group(jsonParser, properties));
      } else if (nodeName == Constants.TAB_BAR) {
        RenderDataDB.getInstance().globalTabBar = new TabBar(jsonParser, properties);
      }
    };
    
    this.didParseFinished = function() {
      MessageUtils.hideLoading();
      // Cache group members into cookie and use it while polling and control fail.
      var cacheGroupMembersCallbackDelegate = {
        // The following two methods "didRequestSuccess" and "didRequestError" are delegate methods of ConnectionUtils.
        /**
         * This method will be invoked when request successfully.
         */
        didRequestSuccess : function(data, textStatus) {
          if (data != null && data != undefined) {
            var error = data.error;
            if (error != null && error != undefined && error.code != Constants.HTTP_SUCCESS_CODE) {
              MessageUtils.showMessageDialogWithSettings("RoundRobin fail", error.message);
            } else {
              RoundRobinUtils.getInstance().storeGroupMembers(data);
            }
          } else {
            MessageUtils.showMessageDialogWithSettings("RoundRobin fail", Constants.UNKNOWN_ERROR_MESSAGE);
          }
          // Render the views with panel data of current controller, authough request servers was failed.
          delegate.didUpdateSuccess();
        },

        /**
         * This method will be called when illed json data come back and network exceptions occured.
         */
        didRequestError : function(xOptions, textStatus) {
          // Render the views with panel data of current controller, authough request servers was failed.
          delegate.didUpdateSuccess();
          MessageUtils.showMessageDialogWithSettings("RoundRobin fail", "No group member was found with network connection error or some unknown exceptions occured.");
        }
      };
      
      RoundRobinUtils.getInstance().cacheGroupMembers(cacheGroupMembersCallbackDelegate);
    };
    
  }
})();
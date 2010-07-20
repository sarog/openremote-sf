/**
 * It's responsible for loading panel data, parsing it and notify the AppBoot to call rootViewController to render groups, screens and so on.
 * auther: handy.wang 2010-07-13
 */
UpdateController = (function() {
  var MSG_OF_NO_CONTROLLER_CONFIG = "Please enter a controller firstly in Settings panel, or leave it ?";
  
  return function(delegateParam) {
    var self = this;
    var delegate = delegateParam;
    
    this.update = function() {
      downloadJSONDataWithURL();
    };
    
    // It's responsible for downloading json data from controller server.
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
          
          var successCallback = function(jsonData, textStatus) {
            MessageUtils.updateLoadingMessage("Downloading panel success ...");
            parseJSONData(jsonData);
          };
          
          var errorCallback = function(xOptions, textStatus) {
            delegate.didUpdateFail("Download panel data fail, settings or leave it ?");
          };
          ConnectionUtils.getJson(panelRequestURL, successCallback, errorCallback);
        }
      }
    }
    
    function parseJSONData(jsonData) {
      MessageUtils.updateLoadingMessage("Parsing panel ...");
      var jsonParser = new JSONParser(jsonData, self);
      jsonParser.startParse();
    }
    
    // Delegate methods of JSONParser    
    this.didParse = function(jsonParser, nodeName, properties) {
      if (nodeName == Constants.SCREEN) {
        RenderDataDB.getInstance().addScreen(new Screen(jsonParser, properties));
      } else if (nodeName == Constants.GROUP) {
        RenderDataDB.getInstance().addGroup(new Group(jsonParser, properties));
      }
    };
    
    this.didParseFinished = function() {
      MessageUtils.hideLoading();
      delegate.didUpdateSuccess();
    };
    
  }
})();
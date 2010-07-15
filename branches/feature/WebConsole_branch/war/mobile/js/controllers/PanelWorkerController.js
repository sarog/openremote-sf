/**
 * It's responsible for download panel data and parsing it.
 * auther: handy.wang 2010-07-14
 */
PanelWorkerController = (function() {
  
  return function(delegateParam) {
    
    var delegate = delegateParam;
    
    this.downloadJSONDataWithURL = function() {
      MessageUtils.updateLoadingMessage("Downloading panel ...");
      var currentServer = CookieUtils.getCookie(Constants.CURRENT_SERVER);
      
      // User don't set the controller url and panel identity.
      if (currentServer == null || currentServer == undefined) {
        delegate.didUpdateFail("Please enter a controller firstly in Settings panel, or leave it ?");
      } else {
        
      }
    }
  };
  
})();
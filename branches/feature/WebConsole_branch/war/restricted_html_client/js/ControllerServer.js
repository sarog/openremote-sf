/**
 * This class is responsible for storing info about controller whitch will be used in client.
 * 
 * auther: handy.wang 2010-07-07
 */
 
ControllerServer = (function(){
  
  // Constructor
  return function(urlParam) {
    var url = "";
    var panelIdentities = [];
    
    // Instance methods
    this.setUrl(urlParam) {
      url = urlParam;
    }
    
    // Private methods
    function loadPanelIdentities() {
      // TODO : add panel identities with controller url.
      panelIdentities.push("");
    }
    
    // Init jobs
    this.setUrl(urlParam);
    loadPanelIdentities();
    
  }
  
})();
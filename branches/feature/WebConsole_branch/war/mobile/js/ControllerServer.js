/**
 * This class is responsible for storing info about controller whitch will be used in client.
 * 
 * auther: handy.wang 2010-07-07
 */
 
ControllerServer = (function(){
  
  // Constructor
  return function(urlParam) {
    var id = Math.uuid();
    var url = "";
    var panelIdentities = [];
    var selectedPanelIdentity = "";
    var panelXML = "";
    
    // Instance methods
    this.setUrl = function(urlParam) {
      if (urlParam == "" || urlParam == undefined) {
        throw new Error("ControllerServer constructor requires an url.");
      }
      url = urlParam;
    };
    
    this.getUrl = function() {
      return url;
    };
    
    this.setSelectedPanelIdentity = function(selectedPanelIdentityParam) {
      selectedPanelIdentity = selectedPanelIdentityParam;
    };
    
    this.getSelectedPanelIdentity = function() {
      return selectedPanelIdentity;
    };
    
    this.getID = function() {
      return id;
    };
    
    this.loadPanelIdentities = function(successCallback, errorCallback) {
      panelIdentities = [];
      ConnnectionUtils.sendAjaxJson(url+"/rest/panels?callback=?", successCallback, errorCallback);
    };
    
    // Init jobs
    this.setUrl(urlParam);
  }
  
})();

// Public static methods
// Find ControllerServer model by id.
ControllerServer.findByID = function(id) {
  if (id == "" || id == undefined) {
    return null;
  }
  var controllerServers = AppSettings.getInstance().getControllerServers();
  for (var i = 0; i < controllerServers.length; i++) {
    if (id == controllerServers[i].getID()) {
      return controllerServers[i];
    }
  }
  return null;
};

// Remove ControllerServer model by id.
ControllerServer.removeByID = function(id) {
  if (id == "" || id == undefined) {
    return false;
  }
  var controllerServers = AppSettings.getInstance().getControllerServers();
  for (var i = 0; i < controllerServers.length; i++) {
    if (id == controllerServers[i].getID()) {
      // splice is a good method for adding and removing element into and from array.
      // add: [].splice(startAtIndex, 0, element1, element2,...);
      // remove: [].splice(indexInArray, howManyElementsTobeRemoved);
      controllerServers.splice(controllerServers.indexOf(controllerServers[i]),1);
      return true;
    }
  }
  return false;
};
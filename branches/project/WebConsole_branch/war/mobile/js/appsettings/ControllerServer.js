/**
 * This class is responsible for storing info about controller whitch will be used in client.
 * 
 * author: handy.wang 2010-07-07
 */
 
ControllerServer = (function(){
  
  // Constructor
  return function(urlParam) {
    this.id = Math.uuid();
    this.url = "";
    this.panelIdentities = [];
    this.selectedPanelIdentity = "";
    
    // Instance methods
    this.setUrl = function(urlParam) {
      if (urlParam == "" || urlParam == undefined) {
        throw new Error("ControllerServer constructor requires an url.");
      }
      this.url = urlParam;
    };
    
    this.getUrl = function() {
      return this.url;
    };
    
    this.addPanelIdentities = function(panelIdentity) {
      this.panelIdentities.push(panelIdentity);
    };
    
    this.setPanelIdentities = function(panelIdentitiesArray) {
      this.panelIdentities = panelIdentitiesArray || [];
    };
    
    this.getPanelIdentities = function() {
      return this.panelIdentities;
    };
    
    this.setSelectedPanelIdentity = function(selectedPanelIdentityParam) {
      this.selectedPanelIdentity = selectedPanelIdentityParam;
    };
    
    this.getSelectedPanelIdentity = function() {
      return this.selectedPanelIdentity;
    };
    
    this.getID = function() {
      return this.id;
    };
    
    // Init jobs
    this.setUrl(urlParam);
  }
  
})();
/**
 * This class is for storing button data.
 * auther: handy.wang 2010-07-28
 */
Navigate = (function() {
  
  return function(jsonParser, properties) {
    Navigate.superClass.constructor.call(this, jsonParser, properties);
    var self = this;
    
    // Delegate method of JSONParser.
    this.didParse = function(jsonParser, nodeName, properties) {
    };
    
    function init() {
      self.node_name = Constants.NAVIGATE;
      
      self.toGroup = (properties[Constants.TO_GROUP] == null || properties[Constants.TO_GROUP] == undefined) ? "" : properties[Constants.TO_GROUP] ;
      self.toScreen = (properties[Constants.TO_SCREEN] == null || properties[Constants.TO_SCREEN] == undefined) ? "" : properties[Constants.TO_SCREEN];
      
      var to = properties[Constants.TO];
      to = (to != null && to != undefined && to != "") ? to.toLowerCase() : "";
      self.isToPreviousScreen = (to == Constants.PREVIOUS_SCREEN) ? true : false;
      self.isToNextScreen = (to == Constants.NEXT_SCREEN) ? true : false;
      self.isToSetting = (to == Constants.SETTING) ? true : false;
      self.isToBack = (to == Constants.BACK) ? true : false;
      self.isToLogin = (to == Constants.LOGIN) ? true : false;
      self.isToLogout = (to == Constants.LOGOUT) ? true : false;
      
      jsonParser.setDelegate(self);
    }
    
    init();
  }
})();

ClassUtils.extend(Navigate, BaseModel);
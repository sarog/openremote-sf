/**
 * This class is for storing screen data.
 * auther: handy.wang 2010-07-16
 */
Screen = (function() {
  
  return function(jsonParser, properties) {
    // For extend
    Screen.superClass.constructor.call(this, jsonParser, properties);
    var self = this;
    
    // Delegate method of JSONParser.
    this.didParse = function(jsonParser, nodeName, properties) {
      if (nodeName == "background") {
        this.background = new Background(jsonParser, properties);
      } else if (nodeName == "absolute") {
      }
    };
    
    // Private methods
    function init(jsonParser, properties) {
      self.node_name = Constants.SCREEN;
      self.screenID = properties[Constants.ID];
      self.screenName = properties[Constants.NAME];
      self.layouts = [];
      self.background = null;
      
      jsonParser.setDelegate(self);
    }
    
    // Init jobs
    init(jsonParser, properties);
    
  }
  
})();

ClassUtils.extend(Screen, BaseModel);
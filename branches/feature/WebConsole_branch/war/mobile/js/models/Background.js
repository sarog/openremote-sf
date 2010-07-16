/**
 * This class is for storing background data.
 * auther: handy.wang 2010-07-16
 */
Background = (function() {
  
  // Constructor
  return function(jsonParser, properties) {
    // For extend
    Background.superClass.constructor.call(this, jsonParser, properties);
    var self = this;
    
    this.didParse = function(jsonParser, nodeName, properties) {
      
    }
    
    // Private methods
    function init(jsonParser, properties) {
      jsonParser.setDelegate(self);
      self.node_name = Constants.BACKGROUND;
      
      
    }
    
    // Init jobs
    init(jsonParser, properties);
    
  };
})();

ClassUtils.extend(Background, BaseModel);
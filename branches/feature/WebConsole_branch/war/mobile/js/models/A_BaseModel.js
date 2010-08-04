/**
 * This class is super class of all model classes for storing data.
 * auther: handy.wang 2010-07-16
 */
BaseModel = (function(){
  
  return function(jsonParser, properties) {
    var self = this;

    // Public methods
    this.toString = function() {
      return this.node_name + "_" + Math.uuid();
    };
    
    // Delegate method of JSONParser.
    this.didParse = function(jsonParser, nodeName, properties) {
    };
    
    // Private mehthods
    function init(jsonParser, properties) {
      self.node_name = Constants.BASE_MODEL;
      jsonParser.setDelegate(self);
    }
    
    // Init jobs
    init(jsonParser, properties);
  };
  
})();
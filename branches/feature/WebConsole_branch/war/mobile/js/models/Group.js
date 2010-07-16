/**
 * This class is for storing group data.
 * auther: handy.wang 2010-07-16
 */
Group = (function(){
  
  return function(jsonParser, properties) {
    // For extend
    Group.superClass.constructor.call(this, jsonParser, properties);
    var self = this;
    
    this.didParse = function(jsonParser, nodeName, properties) {
      
    }
    
    // Private methods
    function init(jsonParser, properties) {
      jsonParser.setDelegate(self);
      self.node_name = Constants.GROUP;
    }
    
    // Init jobs
    init(jsonParser, properties);
    
  };
  
})();

ClassUtils.extend(Group, BaseModel);
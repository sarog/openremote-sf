/**
 * This class is for storing image data.
 * auther: handy.wang 2010-07-19
 */
Image = (function() {
  
  return function(jsonParser, properties) {
    // For extend
    Image.superClass.constructor.call(this, jsonParser, properties);
    var self = this;
    
    // Delegate method of JSONParser(Should be defined in JSONParser.js).
    this.didParse = function(jsonParser, nodeName, properties) {
      // TODO for sensor
    };
    
    function init() {
      self.node_name = Constants.IMAGE;
      self.id = properties[Constants.ID];
      self.src = properties[Constants.SRC];
      
      jsonParser.setDelegate(self);
    }
    
    init();
  };
})();

// For extend
ClassUtils.extend(Image, BaseModel);
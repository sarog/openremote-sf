/**
 * This class is for storing absolute layout data.
 * author: handy.wang 2010-07-21
 */
AbsoluteLayoutModel = (function() {
  
  return function(jsonParser, properties) {
    // For extend
    AbsoluteLayoutModel.superClass.constructor.call(this, jsonParser, properties);
    var self = this;
    self.componentModel = null;
    
    // Delegate method of JSONParser.
    this.didParse = function(jsonParser, nodeName, properties) {
      self.componentModel = ComponentModel.build(jsonParser, nodeName, properties);
    };
    
    /**
     * Get all the sensor id of absolute layout contains.
     */
    this.getPollingSensorIDs = function() {
      var pollingSensorIDs = [];
      var sensor = this.componentModel.sensor;
      if (sensor != null && sensor != undefined) {
        pollingSensorIDs[pollingSensorIDs.length] = sensor.id;
      }
      return pollingSensorIDs;
    };
    
    // Private methods
    /**
     * Initializing jobs.
     */
    function init() {
      self.node_name = Constants.ABSOLUTE;
      self.left = properties[Constants.LEFT];
      self.top = properties[Constants.TOP];
      self.width = properties[Constants.WIDTH];
      self.height = properties[Constants.HEIGHT];
      
      jsonParser.setDelegate(self);
    }
    
    // Init jobs
    init();
  }
  
})();

ClassUtils.extend(AbsoluteLayoutModel, BaseModel);
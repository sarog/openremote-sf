/**
 * This class is for storing sensorState data.
 * author: handy.wang 2010-07-26
 */
SensorState = (function() {
  
  return function(jsonParser, properties) {
    SensorState.superClass.constructor.call(this, jsonParser, properties);
    var self = this;
    
    // Delegate method of JSONParser.
    // this.didParse = function(jsonParser, nodeName, properties) {
    // };
    // 
    
    /**
     * Initializing jobs.
     */
    function init() {
      self.node_name = Constants.SENSOR_STATE;
      self.name = properties[Constants.NAME];
      self.value = properties[Constants.VALUE];
      
      // jsonParser.setDelegate(self);
    }
    
    init();
    
  }
})();

ClassUtils.extend(SensorState, BaseModel);
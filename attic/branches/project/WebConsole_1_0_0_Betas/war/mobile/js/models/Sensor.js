/**
 * This class is for storing sensor data.
 * author: handy.wang 2010-07-26
 */
Sensor = (function() {
  
  return function(jsonParser, properties) {
    Sensor.superClass.constructor.call(this, jsonParser, properties);
    var self = this;
    
    // Delegate method of JSONParser.
    this.didParse = function(jsonParser, nodeName, properties) {
      if (nodeName == Constants.STATE) {
        var sensorState = new SensorState(jsonParser, properties);
        self.states[self.states.length] = sensorState;
      }
    };
    
    /**
     * Initializing jobs.
     */
    function init() {
      self.node_name = Constants.LINK;
      self.id = properties[Constants.REF];
      self.states = [];
      
      jsonParser.setDelegate(self);
    }
    
    init();
  }
})();

ClassUtils.extend(Sensor, BaseModel);
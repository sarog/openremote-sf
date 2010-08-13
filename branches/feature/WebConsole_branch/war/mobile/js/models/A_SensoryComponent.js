/**
 * This class is for storing sensory component's data.
 *
 * author: handy.wang 2010-07-26
 */
SensoryComponent = (function() {
  
  return function(jsonParser, properties) {
    var self = this;
    this.sensor = null;
    
    // Delegate method of JSONParser.
    this.didParse = this.didParse || function(jsonParser, nodeName, properties) {
      if(nodeName == Constants.LINK && Constants.SENSOR == properties[Constants.TYPE]) {
        this.sensor = new Sensor(jsonParser, properties);
      }
    };
    
    SensoryComponent.superClass.constructor.call(this, jsonParser, properties);
  }
})();

ClassUtils.extend(SensoryComponent, BaseModel);
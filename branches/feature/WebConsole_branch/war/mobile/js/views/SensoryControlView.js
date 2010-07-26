/**
 * It's view for sensory Conrols.
 * auther: handy.wang 2010-07-26
 */
SensoryControlView = (function() {
  
  return function(controlParam, sizeParam) {
    SensoryControlView.superClass.constructor.call(this, controlParam, sizeParam);
    var self = this;
    self.component = controlParam;
    self.size = sizeParam;
    
    this.addPollingListener = function() {
      var sensorID = 0;
      if(self.component.node_name == Constants.SWITCH) {
        self.sensorID = self.component.sensor.id;
      } else if(self.component.node_name == Constants.SLIDER) {
        self.sensorID = self.component.sensor.id;
      }
      if(sensorID > 0) {
        // TODO: add listener for sensor id.
      }
    };
    
    self.addPollingListener();
  }
})();

ClassUtils.extend(SensoryControlView, ControlView);
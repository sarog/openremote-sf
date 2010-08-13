/**
 * It's view for sensory Conrols.
 * author: handy.wang 2010-07-26
 */
SensoryControlView = (function() {
  
  return function(controlParam, sizeParam) {
    SensoryControlView.superClass.constructor.call(this, controlParam, sizeParam);
    var self = this;
    
    /**
     * This method must be overwritten in subclasses.
     */
    this.initView = function() {
      self.component = controlParam;
      self.size = sizeParam;
    }
    
    /**
     * Add polling observer for sensory view.
     */
    this.addPollingListener = function() {
      var sensorID = 0;
      var sensor = self.component.sensor;
      if (sensor == null || sensor == undefined) {
        return;
      }
      if(self.component.node_name == Constants.SWITCH) {
        self.sensorID = self.component.sensor.id;
      } else if(self.component.node_name == Constants.SLIDER) {
        self.sensorID = self.component.sensor.id;
      }
      if(self.sensorID > 0) {
        var notificationType = Constants.STATUS_CHANGE_NOTIFICATION + self.component.sensor.id;
        NotificationCenter.getInstance().addObserver(notificationType, self.dealPollingStatus);
      }
    };
    
    /**
     * This method must be overwrite in subclasses.
     */
    this.dealPollingStatus = this.dealPollingStatus || function(statusMapParam) {
      throw new Error("The method dealPollingStatus defined in SensoryControlView must be overwrited in subclasses.");
    };
    
    self.initView();
    self.addPollingListener();
  }
})();

ClassUtils.extend(SensoryControlView, ControlView);
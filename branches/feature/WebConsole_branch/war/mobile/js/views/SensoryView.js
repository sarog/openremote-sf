/**
 * It's view for sensory components.
 * auther: handy.wang 2010-07-27
 */
SensoryView = (function() {
  
  return function(componentModelParam, sizeParam) {
    var self = this;
    
    // This method must be overwritten in subclasses.
    this.initView = this.initView || function() {
      self.component = componentModelParam;
      self.size = sizeParam;
    }
    
    // This method must be overwrite in subclasses.
    this.dealPollingStatus = this.dealPollingStatus || function(statusMapParam) {
      throw new Error("The method dealPollingStatus defined in SensoryControlView must be overwrited in subclasses.");
    };
    
    SensoryView.superClass.constructor.call(this, componentModelParam, sizeParam);
    
    this.addPollingListener = function() {
      var sensorID = 0;
      var sensor = self.component.sensor;
      
      if((self.component.node_name == Constants.IMAGE || self.component.node_name == Constants.LABEL) 
      && sensor != null && sensor != undefined) {
        self.sensorID = sensor.id;
      }
      if(self.sensorID > 0) {
        var notificationType = Constants.STATUS_CHANGE_NOTIFICATION + self.component.sensor.id;
        NotificationCenter.getInstance().addObserver(notificationType, self.dealPollingStatus);
      }
    };
    

    
    self.initView();
    self.addPollingListener();
  }
})();

ClassUtils.extend(SensoryView, ComponentView);
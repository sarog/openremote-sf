/**
 * It's view for sensory components.
 * auther: handy.wang 2010-07-27
 */
SensoryView = (function() {
  
  return function(componentModelParam, sizeParam) {
    SensoryView.superClass.constructor.call(this, componentModelParam, sizeParam);
    var self = this;
    
    // This method must be overwritten in subclasses.
    this.initView = function() {
      self.component = controlParam;
      self.size = sizeParam;
    }
    
    this.addPollingListener = function() {
      var sensorID = 0;
      if(self.component.node_name == Constants.IMAGE) {
        self.sensorID = self.component.sensor.id;
      } else if(self.component.node_name == Constants.LABEL) {
        self.sensorID = self.component.sensor.id;
      }
      if(self.sensorID > 0) {
        window.statusChangeEvent.subscribe(function(type, args) {
          self.dealPollingStatus(args[0]);
        });
      }
    };
    
    // This method must be overwrite in subclasses.
    this.dealPollingStatus = function(statusMapParam) {
      throw new Error("The method dealPollingStatus defined in SensoryControlView must be overwrited in subclasses.");
    };
    
    self.initView();
    self.addPollingListener();
  }
})();

ClassUtils.extend(SensoryControlView, ComponentView);
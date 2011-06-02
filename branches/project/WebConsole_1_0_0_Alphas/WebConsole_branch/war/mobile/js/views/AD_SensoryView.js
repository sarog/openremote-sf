/**
 * It's view for sensory components.
 * author: handy.wang 2010-07-27
 */
SensoryView = (function() {
  
  return function(componentModelParam, sizeParam) {
    var self = this;
    
    /**
     * This method must be overwritten in subclasses.
     */
    this.initView = this.initView || function() {
      self.component = componentModelParam;
      self.size = sizeParam;
      self.sensorID = null;
    }
    
    /**
     * This method must be overwrite in subclasses.
     */
    this.dealPollingStatus = this.dealPollingStatus || function(statusMapParam) {
      throw new Error("The method dealPollingStatus defined in SensoryControlView must be overwrited in subclasses.");
    };
    
    SensoryView.superClass.constructor.call(this, componentModelParam, sizeParam);
    
    /**
     * Add polling observer for sensory view.
     */
    this.addPollingListener = function() {
      var sensorID = 0;
      var sensor = self.component.sensor;
      
      if (sensor != null && sensor != undefined) {
        if(self.component.node_name == Constants.LABEL) {
          self.sensorID = sensor.id;
        } else if (self.component.node_name == Constants.IMAGE) {
          self.sensorID = sensor.id;
          if (self.sensorID <= 0) {
            var labelID = self.component.labelID;
            if (labelID != null) {
              var labelOfImageInclude = RenderDataDB.getInstance().findLabelByID(labelID);
              self.sensorID = labelOfImageInclude.sensor.id;
            }
          }
        }
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
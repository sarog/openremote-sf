/**
 * This class is for building control views depending on component model data and size.
 * author: handy.wang 2010-07-23
 */
ControlView = (function() {
  
  return function(componentModelParam, sizeParam) {
    var self = this;
    
    /** 
     * This method must be overwritten in subclasses.
     * This method must be defined before calling superClass's construtor whatever in current class or sub classes.
     */
    this.initView = this.initView || function() {
      // throw new Error("The method initView defined in ControlView must be override in subclasses.");
    }
    
    /**
     * Super class's constructor calling
     */
    ControlView.superClass.constructor.call(this, componentModelParam, sizeParam);
    
    self.component = componentModelParam;
    self.size = sizeParam;
    
    /**
     * Send control command to controller server with value.
     */
    this.sendCommandRequest = function(commandValue) {
      var controlURL = ConnectionUtils.getControlURL(self.component.id, commandValue);
      ConnectionUtils.sendJSONPRequest(controlURL, self);
    };
    
    // Delegate methods should be defined in ConnectionUtils.
    this.didRequestSuccess = function(data, textStatus) {
      if (data != null && data != undefined) {
        var error = data.error;
        if (error != null && error != undefined) {
          self.handleServerError(error);
        }
      } else {
        // MessageUtils.showMessageDialogWithSettings("Send request error", Constants.UNKNOWN_ERROR_MESSAGE);
        RoundRobinUtils.getInstance().switchControllerServer();
      }
    };
    
    /**
     * Handle errors from controller server.
     */
    this.handleServerError = function(error) {
      var statusCode = error.code;
      if (statusCode != Constants.HTTP_SUCCESS_CODE) {
        switch (statusCode) {
          case Constants.TIME_OUT:
            return;
          case Constants.CONTROLLER_CONFIG_CHANGED:
            return;
          case Constants.UNAUTHORIZED:
            MessageUtils.showMessageDialogWithSettings("Send request error", error.message);
            return;
        }
        // MessageUtils.showMessageDialogWithSettings("Send request error", error.message);
        RoundRobinUtils.getInstance().switchControllerServer();
      }
    };
    
    /** 
     * For dealing network error and illed json data.
     */
    this.didRequestError = function(xOptions, textStatus) {
      // MessageUtils.showMessageDialogWithSettings("Send request error", "Failed to send control request.");
      RoundRobinUtils.getInstance().switchControllerServer();
    };
  }
})();

ClassUtils.extend(ControlView, ComponentView);

/**
 * Factory method for build all kinds of control view with model data and size the control view should be.
 */
ControlView.build = function(componentModelParam, sizeParam) {
  switch(componentModelParam.node_name) {
    case Constants.BUTTON:
      return new ButtonView(componentModelParam, sizeParam);
    case Constants.SWITCH:
      return new SwitchView(componentModelParam, sizeParam);
    case Constants.SLIDER:
      return new SliderView(componentModelParam, sizeParam);
    default:
      return null;
  }
};


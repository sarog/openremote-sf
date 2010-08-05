/**
 * This class is for building control views depending on component model data and size.
 * auther: handy.wang 2010-07-23
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
    
    // Super class's constructor calling
    ControlView.superClass.constructor.call(this, componentModelParam, sizeParam);
    
    self.component = componentModelParam;
    self.size = sizeParam;
    
    this.sendCommandRequest = function(commandValue) {
      var controlURL = ConnectionUtils.getControlURL(self.component.id, commandValue);
      ConnectionUtils.sendNormalRequest(controlURL, self);
    };
    
    // It is delegate methods should be defined in ConnectionUtils for sendNormalRequest.
    this.didFeedBackWithRequest = function(data, textStatus, XMLHttpRequest) {
      if (data != null && data != undefined) {
        var error = data.error;
        if (error != null && error != undefined && error.code != Constants.HTTP_SUCCESS_CODE) {
          MessageUtils.showMessageDialogWithSettings("Send request error ", error.message);
        }
      } else {
        MessageUtils.showMessageDialogWithSettings("Send request error ", Constants.UNKNOWN_ERROR_MESSAGE);
      }
    };
  }
})();

ClassUtils.extend(ControlView, ComponentView);

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


/**
 * This class is for building control views depending on component model data and size.
 * auther: handy.wang 2010-07-23
 */
ControlView = (function() {
  
  return function(componentModelParam, sizeParam) {
    ControlView.superClass.constructor.call(this, componentModelParam, sizeParam);
    var self = this;
    self.component = componentModelParam;
    self.size = sizeParam;
    
    this.sendCommandRequest = function(commandValue) {
      var controlURL = ConnectionUtils.getControlURL(self.component.id, commandValue);
      ConnectionUtils.sendNormalRequest(controlURL, self);
    };
    
    // This method must be overwritten in subclasses.
    this.initView = function() {
    }
    
    // It is delegate methods should be defined in ConnectionUtils for sendNormalRequest.
    this.didFeedBackWithRequest = function(data, textStatus, XMLHttpRequest) {
      if (data != null && data != undefined) {
        var error = data.error;
        if (error != null && error != undefined) {
          MessageUtils.showMessageDialogWithSettings("Send request error ", error.message);
        }
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
      return new ControlView(componentModelParam, sizeParam);// return new SliderView(componentModelParam, sizeParam);
    default:
      return null;
  }
};


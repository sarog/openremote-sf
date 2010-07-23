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
      ConnectionUtils.sendRequest(controlURL, self);
    };
    
    this.initView = function() {
    }
    
    // Followings two are delegate methods should be defined in ConnectionUtils.
    this.didRequestSuccess = function(data, textStatus) {
      
    };
    
    this.didRequestError = function(xOptions, textStatus) {
      handleErrorWithStatusCode(textStatus);
    };
    
    function handleErrorWithStatusCode(statusCode) {
      // if (statusCode != 200) {
      //   MessageUtils.showMessageDialogWithSettings("Send request error", MessageUtils.getExceptionMessage(statusCode) + statusCode);
      // }
    };
  }
})();

ClassUtils.extend(ControlView, ComponentView);

ControlView.build = function(componentModelParam, sizeParam) {
  switch(componentModelParam.node_name) {
    case Constants.BUTTON:
      return new ButtonView(componentModelParam, sizeParam);
    case Constants.SWITCH:
      return new ControlView(componentModelParam, sizeParam);// return new SwitchView(componentModelParam, sizeParam);
    case Constants.SLIDER:
      return new ControlView(componentModelParam, sizeParam);// return new SliderView(componentModelParam, sizeParam);
    default:
      return null;
  }
};


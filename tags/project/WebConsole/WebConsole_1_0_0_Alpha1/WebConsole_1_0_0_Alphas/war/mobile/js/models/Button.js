/**
 * This class is for storing button data.
 * author: handy.wang 2010-07-22
 */
Button = (function() {
  
  return function(jsonParser, properties) {
    Button.superClass.constructor.call(this, jsonParser, properties);
    var self = this;
    
    // Delegate method of JSONParser.
    this.didParse = function(jsonParser, nodeName, properties) {
      switch (nodeName) {
        case Constants.DEFAULT:
          self.tempStatusImageNode = Constants.DEFAULT;
          break;
        case Constants.PRESSED:
          self.tempStatusImageNode = Constants.PRESSED;
          break;
        case Constants.IMAGE:
          if (self.tempStatusImageNode == Constants.DEFAULT) {
            self.defaultImage = new Image(jsonParser, properties);
          } else if (self.tempStatusImageNode == Constants.PRESSED) {
            self.pressedImage = new Image(jsonParser, properties);
          }
          break;
        case Constants.NAVIGATE:
          self.navigate = new Navigate(jsonParser, properties);
          break;
      }
    };
    
    /**
     * Initializing jobs.
     */
    function init() {
      self.node_name = Constants.BUTTON;
      self.id = properties[Constants.ID];
      self.name = properties[Constants.NAME];
      
      var hadControlCommandValue = properties[Constants.HAS_CONTROL_COMMAND];
      self.hasControlCommand = (hadControlCommandValue == true) ? true : false;
      
      var isCommandRepeatedValue = properties[Constants.IS_COMMAND_REPEATED];
      self.isCommandRepeated = (isCommandRepeatedValue == true) ? true : false;
      
      jsonParser.setDelegate(self);
    }
    
    init();
  }
})();

ClassUtils.extend(Button, BaseModel);
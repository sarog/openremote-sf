/**
 * This class is for storing background data.
 * author: handy.wang 2010-07-16
 */
Background = (function() {
  
  // Constructor
  return function(jsonParser, properties) {
    // For extend
    Background.superClass.constructor.call(this, jsonParser, properties);
    var self = this;
    
    this.didParse = function(jsonParser, nodeName, properties) {
      if (nodeName == Constants.IMAGE) {
      	self.image = new Image(jsonParser, properties);
      }
    };
    
    // Private methods
    /**
     * Initializing jobs.
     */
    function init(jsonParser, properties) {
      self.node_name = Constants.BACKGROUND;
      self.image = null;
      self.isBGImageAbsolutePosition = false;
            
      var bgImageRelativePositionTemp = properties[Constants.BG_IMAGE_RELATIVE];
      var bgImageAbsolutePositionTemp = properties[Constants.BG_IMAGE_ABSOLUTE];
      var fillScreenTemp = properties[Constants.FILL_SCREEN];
      
      // Is relative position for background image。
      if (bgImageRelativePositionTemp != undefined && bgImageRelativePositionTemp != null && bgImageRelativePositionTemp != "") {
        self.bgImageRelativePosition = bgImageRelativePositionTemp
        self.isBGImageAbsolutePosition = false;
      }
      // Is absolute position for background image。
      if (bgImageAbsolutePositionTemp != undefined && bgImageAbsolutePositionTemp != null && bgImageAbsolutePositionTemp != "") {
        var indexOfComma = bgImageAbsolutePositionTemp.indexOf(",");
        self.bgImageAbsolutePositionLeft = bgImageAbsolutePositionTemp.substring(0, indexOfComma);
        self.bgImageAbsolutePositionTop = bgImageAbsolutePositionTemp.substring(indexOfComma+1, bgImageAbsolutePositionTemp.length);
        self.isBGImageAbsolutePosition = true;
      }
      // Is background-image filled screen.
      if (fillScreenTemp != undefined && fillScreenTemp != null && fillScreenTemp != "") {
        self.fillScreen = (fillScreenTemp == true) ? true : false;
      } else {
        self.fillScreen = false;
      }
      
      jsonParser.setDelegate(self);
    }
    
    // Init jobs
    init(jsonParser, properties);
    
  };
})();

ClassUtils.extend(Background, BaseModel);
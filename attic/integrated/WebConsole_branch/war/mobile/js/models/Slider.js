/**
 * This class is for storing slider data.
 * author: handy.wang 2010-08-05
 */
Slider = (function() {
  
  return function(jsonParser, properties) {
    var self = this;
    
    // Override didParse method of SensoryComponent
    this.didParse = function(jsonParser, nodeName, properties) {
      if(nodeName == Constants.LINK && Constants.SENSOR == properties[Constants.TYPE]) {
        self.sensor = new Sensor(jsonParser, properties);
      } else if (nodeName == Constants.MAX) {
        var maxValueStr = properties[Constants.VALUE];
        self.maxValue = (maxValueStr != null && maxValueStr != undefined) ? maxValueStr : "0";
        
        var maxImageStr = properties[Constants.SLIDER_IMAGE];
        self.maxImageSRC = (maxImageStr != null && maxImageStr != undefined) ? maxImageStr : "";
        
        var maxTrackImageStr = properties[Constants.SLIDER_TRACK_IMAGE];
        self.maxTrackImageSRC = (maxTrackImageStr != null && maxTrackImageStr != undefined) ? maxTrackImageStr : "";
        
      } else if (nodeName == Constants.MIN) {
        var minValueStr = properties[Constants.VALUE];
        self.minValue = (minValueStr != null && minValueStr != undefined) ? minValueStr : "0";
        
        var minImageStr = properties[Constants.SLIDER_IMAGE];
        self.minImageSRC = (minImageStr != null && minImageStr != undefined) ? minImageStr : "";
        
        var minTrackImageStr = properties[Constants.SLIDER_TRACK_IMAGE];
        self.minTrackImageSRC = (minTrackImageStr != null && minTrackImageStr != undefined) ? minTrackImageStr : "";
        
      }
    };
    
    Slider.superClass.constructor.call(this, jsonParser, properties);
    
    /**
     * Initializing jobs.
     */
    function init() {
      self.node_name = Constants.SLIDER;
      self.id = properties[Constants.ID];
      
      var isVerticalStr = properties[Constants.IS_VERTICAL];
      self.isVertical = (isVerticalStr != null && isVerticalStr != undefined && isVerticalStr == true) ? true : false;
      
      var isPassiveStr = properties[Constants.IS_PASSIVE];
      self.isPassive = (isPassiveStr != null && isPassiveStr != undefined && isPassiveStr == true) ? true : false;
      
     var thumbImage = properties[Constants.THUMB_IMAGE];
     self.thumbImageSRC = (thumbImage != null && thumbImage != undefined) ? thumbImage : "";
      
      jsonParser.setDelegate(self);
    }
    
    init();
  }
})();

ClassUtils.extend(Slider, SensoryComponent);
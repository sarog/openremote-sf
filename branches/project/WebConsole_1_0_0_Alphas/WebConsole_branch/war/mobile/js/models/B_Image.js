/**
 * This class is for storing image data.
 *
 * author: handy.wang 2010-07-19
 */
Image = (function() {
  
  return function(jsonParser, properties) {
    var self = this;
    
    /**
     * Override didParse method of SensoryComponent
     */
    this.didParse = function(jsonParser, nodeName, properties) {
      if(nodeName == Constants.LINK && Constants.SENSOR == properties[Constants.TYPE]) {
        this.sensor = new Sensor(jsonParser, properties);
      } else if (nodeName == Constants.INCLUDE && Constants.LABEL == properties[Constants.TYPE]) {
        this.labelID = properties[Constants.REF];
      }
    };
    
    Image.superClass.constructor.call(this, jsonParser, properties);
    
    /**
     * Initializing jobs.
     */
    function init() {
      self.node_name = Constants.IMAGE;
      self.id = properties[Constants.ID];
      self.src = properties[Constants.SRC];
      self.labelID = null;
      
      jsonParser.setDelegate(self);
    }
    
    init();
  };
})();

// For extend
ClassUtils.extend(Image, SensoryComponent);
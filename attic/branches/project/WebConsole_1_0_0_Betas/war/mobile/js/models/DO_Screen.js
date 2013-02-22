/**
 * This class is for storing screen data.
 * author: handy.wang 2010-07-16
 */
Screen = (function() {
  
  return function(jsonParser, properties) {
    // For extend
    Screen.superClass.constructor.call(this, jsonParser, properties);
    var self = this;
    
    // Delegate method of JSONParser.
    this.didParse = function(jsonParser, nodeName, properties) {
      switch (nodeName) {
        case Constants.BACKGROUND :
          this.background = new Background(jsonParser, properties);
          break;
        case Constants.ABSOLUTE:
          var absoluteLayoutContainer = new AbsoluteLayoutModel(jsonParser, properties, self);
          this.layouts[self.layouts.length] = absoluteLayoutContainer;
          break;
        case  Constants.GRID:
          var gridLayoutContainer = new GridLayoutModel(jsonParser, properties);
          this.layouts[self.layouts.length] = gridLayoutContainer;
          break;
      }
    };
    
    /**
     * Get all the sensor id of screen contains.
     */
    this.getPollingSensorIDs = function() {
      self.sensorIDs = [];
      for (var i = 0; i < this.layouts.length; i++) {
        self.sensorIDs = self.sensorIDs.concat(this.layouts[i].getPollingSensorIDs());
      }
      return self.sensorIDs;
    };
    
    // Private methods
    /**
     * Initializing jobs.
     */
    function init() {
      self.node_name = Constants.SCREEN;
      self.id = properties[Constants.ID];
      self.name = properties[Constants.NAME];
      self.layouts = [];
      self.background = null;
      
      jsonParser.setDelegate(self);
    }
    
    // Init jobs
    init();
    
  }
  
})();

ClassUtils.extend(Screen, BaseModel);
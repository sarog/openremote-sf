/**
 * This class is for storing screen data.
 * auther: handy.wang 2010-07-16
 */
Screen = (function() {
  
  return function(jsonParser, properties) {
    // For extend
    Screen.superClass.constructor.call(this, jsonParser, properties);
    var self = this;
    
    // Delegate method of JSONParser.
    this.didParse = function(jsonParser, nodeName, properties) {
      if (nodeName == Constants.BACKGROUND) {
        this.background = new Background(jsonParser, properties);
      } else if (nodeName == Constants.ABSOLUTE) {
        var absoluteLayoutContainer = new AbsoluteLayoutModel(jsonParser, properties);
        self.layouts[self.layouts.length] = absoluteLayoutContainer;
      } else if (nodeName == Constants.GRID) {
        var gridLayoutContainer = new GridLayoutModel(jsonParser, properties);
        self.layouts[self.layouts.length] = gridLayoutContainer;
      }
    };
    
    // Private methods
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
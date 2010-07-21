/**
 * This class is for storing grid layout data.
 * auther: handy.wang 2010-07-21
 */
GridLayoutModel = (function() {
  
  return function(jsonParser, properties) {
    // For extend
    GridLayoutModel.superClass.constructor.call(this, jsonParser, properties);
    var self = this;
    
    // Delegate method of JSONParser.
    this.didParse = function(jsonParser, nodeName, properties) {
    };
    
    // Private methods
    function init() {
      self.node_name = Constants.GRID;
      self.left = properties[Constants.LEFT];
      self.top = properties[Constants.TOP];
      self.width = properties[Constants.WIDTH];
      self.height = properties[Constants.HEIGHT];
      self.rows = properties[Constants.ROWS];
      self.cols = properties[Constants.COLS];
      
      self.cells = [];
      
      jsonParser.setDelegate(self);
    }
    
    init();
  }
  
})();

ClassUtils.extend(GridLayoutModel, LayoutModel);
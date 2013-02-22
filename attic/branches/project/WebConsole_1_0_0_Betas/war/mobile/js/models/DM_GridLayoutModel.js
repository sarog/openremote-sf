/**
 * This class is for storing grid layout data.
 * author: handy.wang 2010-07-21
 */
GridLayoutModel = (function() {
  
  return function(jsonParser, properties) {
    // For extend
    GridLayoutModel.superClass.constructor.call(this, jsonParser, properties);
    var self = this;
    
    // Delegate method of JSONParser.
    this.didParse = function(jsonParser, nodeName, properties) {
      if (nodeName == Constants.GRID_CELL) {
        var gridCell = new GridCell(jsonParser, properties, self);
        this.cells[this.cells.length] = gridCell;
      }
    };
    
    /**
     * Get all the sensor id of grid layout contains.
     */
    this.getPollingSensorIDs = function() {
      var pollingSensorIDs = [];
      for (var i = 0; i < this.cells.length; i++) {
        var cell = this.cells[i];
        if (cell.componentModel != null && cell.componentModel != undefined && cell.componentModel.sensor != null && cell.componentModel.sensor != undefined) {
          pollingSensorIDs[pollingSensorIDs.length] = cell.componentModel.sensor.id;
        }
      }
      return pollingSensorIDs;
    };
    
    // Private methods
    /**
     * Initializing jobs.
     */
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

ClassUtils.extend(GridLayoutModel, BaseModel);
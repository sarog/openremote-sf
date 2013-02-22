/**
 * This class is for storing gridcell data.
 *
 * author: handy.wang 2010-07-22
 */
GridCell = (function() {
  
  return function(jsonParser, properties, parentDelegateParam) {
    GridCell.superClass.constructor.call(this, jsonParser, properties);
    var self = this;
    self.parentDelegate = parentDelegateParam;
    
    // Delegate method of JSONParser.
    this.didParse = function(jsonParser, nodeName, properties) {
      self.componentModel = ComponentModel.build(jsonParser, nodeName, properties);
      if (self.componentModel == null) {
         jsonParser.setDelegate(self.parentDelegate);
      }
    };
    
    /**
     * Initializing jobs.
     */
    function init() {
      self.node_name = Constants.GRID_CELL;
      self.x = properties[Constants.GRID_CELL_X];
      self.y = properties[Constants.GRID_CELL_Y];
      
      var temprowspan = properties[Constants.GRID_CELL_ROWSPAN];
      var tempcolspan = properties[Constants.GRID_CELL_COLSPAN];
      self.rowspan = (parseInt(temprowspan) < 1) ? "1" : temprowspan ;
      self.colspan = (parseInt(tempcolspan) < 1) ? "1" : tempcolspan;
      
      jsonParser.setDelegate(self);
    }
    
    init();
  }
})();

ClassUtils.extend(GridCell, BaseModel);
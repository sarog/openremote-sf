/**
 * It's view for grid layout.
 * author: handy.wang 2010-07-22
 */
GridLayoutView = (function() {
  var ID = "gridLayoutView";
  var DEFAULT_CSS_STYLE = {
     "width":"100%",
     "height":"100%",
     "position":"absolute"
   };
  
  return function(screenID, layoutModelParam) {
    GridLayoutView.superClass.constructor.call(this);
    var self = this;
    self.gridCellViews = [];
    
    function init() {
      self.gridLayoutModel = layoutModelParam;
      self.setID(ID + screenID);
      var canvas = $("<div />", {
        id : self.getID()
      })
      self.setCanvas(canvas);
      DEFAULT_CSS_STYLE.top = self.gridLayoutModel.top;
      DEFAULT_CSS_STYLE.left = self.gridLayoutModel.left;
      DEFAULT_CSS_STYLE.width = self.gridLayoutModel.width;
      DEFAULT_CSS_STYLE.height = self.gridLayoutModel.height;
      self.setCss(DEFAULT_CSS_STYLE);
    }
    
    /**
     * Render all the cell views of included in grid layout.
     */
    function renderGridviews() {
      var size = new Size(self.gridLayoutModel.width, self.gridLayoutModel.height);
      var widthPerGridCell = parseInt(self.gridLayoutModel.width)/parseInt(self.gridLayoutModel.cols);
      var heightPerGridCell = parseInt(self.gridLayoutModel.height)/parseInt(self.gridLayoutModel.rows);
      var gridCells = self.gridLayoutModel.cells;
      for (var index = 0; index < gridCells.length; index++) {
        var gridCell = gridCells[index];
        var left = parseInt(gridCell.x) * widthPerGridCell;
        var top = parseInt(gridCell.y) * heightPerGridCell;
        var width = widthPerGridCell * parseInt(gridCell.colspan);
        var height = heightPerGridCell * parseInt(gridCell.rowspan);
        var gridCellView = new GridCellView(gridCells[index], new Frame(left, top, width, height));
        self.gridCellViews[self.gridCellViews.length] = gridCellView;
        self.addSubView(gridCellView);
      }
    }
    
    init();
    renderGridviews();
  }
})();

ClassUtils.extend(GridLayoutView, BaseView);
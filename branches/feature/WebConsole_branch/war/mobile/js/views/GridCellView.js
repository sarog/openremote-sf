/**
 * It's view for grid cell.
 * auther: handy.wang 2010-07-22
 */
GridCellView = (function() {
  var ID = "gridCellView";
  var DEFAULT_CSS_STYLE = {
     "border" : "#CCCCCC dotted 1px",
     // "background-color" : "red",
     "color":"#FF0000",
     "width":"100%",
     "height":"100%",
     "position":"absolute"
   };
  
  return function(gridCellParam, frameParam) {
    GridCellView.superClass.constructor.call(this);
    var self = this;
    
    function init() {
      self.gridCell = gridCellParam;
      self.frame = frameParam;
      self.setID(ID + Math.uuid());
      var canvas = $("<div />", {
        id : self.getID()
      })
      self.setCanvas(canvas);
      DEFAULT_CSS_STYLE.top = self.frame.origin.top;
      DEFAULT_CSS_STYLE.left = self.frame.origin.left;
      DEFAULT_CSS_STYLE.width = self.frame.size.width;
      DEFAULT_CSS_STYLE.height = self.frame.size.height;
      self.setCss(DEFAULT_CSS_STYLE);
    }
    
    function renderSubviews() {
      if(self.gridCell.componentModel.node_name == Constants.SLIDER || self.gridCell.componentModel.node_name == Constants.BASE_MODEL) {
        return;
      }
      self.componentView = ComponentView.build(self.gridCell.componentModel, self.size);
      self.addSubView(self.componentView);
    }
    
    init();
    renderSubviews();
  }
})();

ClassUtils.extend(GridCellView, BaseView);
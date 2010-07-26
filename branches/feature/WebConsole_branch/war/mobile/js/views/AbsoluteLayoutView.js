/**
 * It's view for absolute layout.
 * auther: handy.wang 2010-07-22
 */
AbsoluteLayoutView = (function() {
  var ID = "absoluteLayoutView";
  var DEFAULT_CSS_STYLE = {
     "border" : "#CCCCCC dashed 1px",
     "width":"100%",
     "height":"100%",
     "position":"absolute"
   };
  
  return function(screenID, layoutModelParam) {
    // For extend
    AbsoluteLayoutView.superClass.constructor.call(this);
    var self = this;
    
    function init() {
      self.absoluteLayoutModel = layoutModelParam;
      self.setID(ID+screenID);
      var canvas = $("<div />", {
        id : self.getID()
      })
      self.setCanvas(canvas);
      DEFAULT_CSS_STYLE.top = self.absoluteLayoutModel.top;
      DEFAULT_CSS_STYLE.left = self.absoluteLayoutModel.left;
      DEFAULT_CSS_STYLE.width = self.absoluteLayoutModel.width;
      DEFAULT_CSS_STYLE.height = self.absoluteLayoutModel.height;
      self.setCss(DEFAULT_CSS_STYLE);
    }
    
    function renderSubviews() {
      var size = new Size(self.absoluteLayoutModel.width, self.absoluteLayoutModel.height);
      if(self.absoluteLayoutModel.componentModel.node_name != Constants.BUTTON && self.absoluteLayoutModel.componentModel.node_name != Constants.SWITCH) {
        return;
      }
      self.componentView = ComponentView.build(self.absoluteLayoutModel.componentModel, size);
      self.addSubView(self.componentView);
    }
    
    init();
    renderSubviews();
    
  }
  
})();

// For extend
ClassUtils.extend(AbsoluteLayoutView, BaseView);
/**
 * It's view for screen view controller.
 * author: handy.wang 2010-07-19
 */
ScreenView = (function() {
  
  var ID = "screenView";
  var DEFAULT_CSS_STYLE = {
     // "background-color":"black",
     "width":"100%",
     "height":"100%",
     "position":"relative"
   };
  
  return function(screenParam) {
    // For extend
    ScreenView.superClass.constructor.call(this);
    var self = this;
    
    /**
     * Render absolute and grid layout view
     */
    function createCustomizedLayoutviews() {
      for (var index = 0; index < self.screen.layouts.length; index++) {
        var layoutModel = self.screen.layouts[index];
        if (layoutModel.node_name == Constants.ABSOLUTE) {
          var absoluteLayoutView = new AbsoluteLayoutView(self.screen.id, layoutModel);
          self.addSubView(absoluteLayoutView);
        } else if (layoutModel.node_name == Constants.GRID) {
          var gridLayoutView = new GridLayoutView(self.screen.id, layoutModel);
          self.addSubView(gridLayoutView);
        }
      }
    }
  
    function init() {
      self.screen = screenParam;
      
      self.setID(ID+self.screen.id);
      var canvas = $("<div />", {
        "id" : self.getID()
      });
      self.setCanvas(canvas);
      self.setCss(DEFAULT_CSS_STYLE);
      
      self.addSubView(new BackgroundView(self.screen));
      createCustomizedLayoutviews();
    }
  
    init();
    
  };
  
})();

ClassUtils.extend(ScreenView, BaseView);
/**
 * It's container of all views.
 * author: handy.wang 2010-07-13
 */
RootView = (function() {
  
  var ID = "rootView";
  var DEFAULT_CSS_STYLE = {
     "background-color":"white",
     "width":"100%",
     "height":"100%",
     "position":"absolute"
   };
  // var rootView = null;
  
  // Constructor
  var RootView = function(id) {
    // For extends
    RootView.superClass.constructor.call(this);
    
    var self = this;

    // Private instance methods
    function initView() {
      self.setID(id || ID);
      var canvas = $("<div />", {
        "id" : ID
      });
      self.setCanvas(canvas);
      self.setCss(DEFAULT_CSS_STYLE);
    }
    
    // init jobs
    initView();
    
  }
  
  return RootView;
  
})();

ClassUtils.extend(RootView, BaseView);
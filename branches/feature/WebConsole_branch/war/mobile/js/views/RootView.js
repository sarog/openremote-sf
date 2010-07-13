/**
 * It's root view for all views.
 * auther: handy.wang 2010-07-13
 */
RootView = (function() {
  
  var rootView = null;
  
  // Constructor
  function RootView() {
    this.id = "#rootView"; // This id format is in order to identify to others.
    var canvas = null;
    
    // Pubilc instance methods
    this.getCanvas = function() {
      return canvas;
    };
    
    // The parameter's format as {'background-color' : '#0000FF', 'color' : 'FF000000' }
    // So, the background color is black and text colorr is red.
    this.setCss = function(cssParam) {
      $(canvas).css(cssParam);
    };
    
    this.addSubView = function() {
    };
    
    // Private instance methods
    function initView() {
      canvas = $("<div />", {
        "id" : this.id,
        "text" : "handy is here."
      });
    }
    
    // init jobs
    initView();
    
  }
  
  return {
    getInstance:function() {
      if (rootView == null) {
        rootView = new RootView();
      }
      return rootView;
    }
  };
  
})();
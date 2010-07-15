/**
 * It's super base view of all views.
 * auther: handy.wang 2010-07-14
 */
BaseView = (function() {
  
  // Constructor
  return function() {
    // Private instance variables
    var id = "";
    var canvas = null;
    
    this.setID = function(idParam) {
      id = idParam;
    };
    
    this.getID = function() {
      return id;
    };
    
    this.setCanvas = function(canvasParam) {
      canvas = canvasParam;
    };
    
    this.getCanvas = function() {
      return canvas;
    };
    
    // The parameter's format as {'background-color' : '#0000FF', 'color' : 'FF000000' }
    // So, the background color is black and text colorr is red.
    this.setCss = function(cssParam) {
      if (canvas == null) {
        throw new Error("Canvas is null.");
      }
      $(canvas).css(cssParam);
    };
    
    this.addSubView = function(subView) {
      $(canvas).append(subView.getCanvas());
    };

  };
  
})();
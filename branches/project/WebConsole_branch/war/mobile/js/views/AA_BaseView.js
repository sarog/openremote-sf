/**
 * It's super base view of all views.
 * author: handy.wang 2010-07-14
 */
BaseView = (function() {
  
  // Constructor
  return function() {
    var self = this;
    // Private instance variables
    var id = "";
    var canvas = null;
    
    this.setID = function(idParam) {
      id = idParam;
    };
    
    this.getID = function() {
      return id;
    };
    
    /**
     * The canvas is for attaching subviews' canvas.
     */
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
      self.css = cssParam;
      $(canvas).css(cssParam);
    };
    
    this.getCss = function() {
      return self.css;
    };
    
    this.addSubView = function(subView) {
      $(canvas).append(subView.getCanvas());
    };
    
    this.removeSubView = function(subView) {
      $("#"+subView.getID()).detach();
    };
    
    this.removeSubViews = function() {
      $(canvas).children().detach();
    };

  };
  
})();
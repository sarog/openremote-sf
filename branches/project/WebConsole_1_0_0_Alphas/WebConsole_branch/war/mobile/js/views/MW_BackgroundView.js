/**
 * It's view for background.
 * author: handy.wang 2010-07-22
 */
BackgroundView = (function() {
  var ID = "backgroundView";
  var DEFAULT_CSS_STYLE = {
     "background-color":"black",
     "color":"#FF0000",
     "width":"100%",
     "height":"100%"
   };
  
  return function(screenParam) {
    BackgroundView.superClass.constructor.call(this);
    var self = this;
    
    function init() {
      self.background = screenParam.background;
      self.setID(ID+screenParam.id);
      var canvas = $("<div />", {
        "id" : self.getID()
      });
      self.setCanvas(canvas);
      self.setCss(DEFAULT_CSS_STYLE);
      
      layoutView();
    }
    
    /**
     * Render background view
     */
    function layoutView() {
      if(!isImageNull()) {
        var imageQualifiedURL = ConnectionUtils.getResourceURL(self.background.image.src);
        
        var customizedCss = {};
        
        // Fill screen is false.
        if (self.background.fillScreen === false) {
          // Ablosute position of screen background.
          if (self.background.isBGImageAbsolutePosition === true) {
            var left = self.background.bgImageAbsolutePositionLeft;
            var top = self.background.bgImageAbsolutePositionTop;
            customizedCss = {
              "background" : "url('" + imageQualifiedURL +"') no-repeat scroll " + left + "px " + top + "px",
              "width" : "100%",
              "height" : "100%"
            };
          }
          // Relative position of screen background
          else {
            var bgRelativePosition = self.background.bgImageRelativePosition;
            var bgRelativePositionCSS = "left top";
            switch(bgRelativePosition) {
              case "TOP":
                bgRelativePositionCSS = "center top";
                break;
              case "BOTTOM":
                bgRelativePositionCSS = "center bottom";
                break;
              case "LEFT":
                bgRelativePositionCSS = "left center";
                break;
              case "RIGHT":
                bgRelativePositionCSS = "right center";
                break;
              case "CENTER":
                bgRelativePositionCSS = "center center";
                break;
              case "TOP_LEFT":
                bgRelativePositionCSS = "left top";
                break;
              case "TOP_RIGHT":
                bgRelativePositionCSS = "right top";
                break;
              case "BOTTOM_LEFT":
                bgRelativePositionCSS = "left bottom";
                break;
              case "BOTTOM_RIGHT":
                bgRelativePositionCSS = "right bottom";
                break;
            }
            customizedCss = {
              "background" : "url('" + imageQualifiedURL +"') no-repeat scroll " + bgRelativePositionCSS,
              "width" : "100%",
              "height" : "100%"
            };
          }
        } 
        // Fill screen is true.
        else {
          customizedCss = {
            "background" : "url('" + imageQualifiedURL +"') no-repeat",
            "width" : "100%",
            "height" : "100%"
          };
        }
        self.setCss(customizedCss);
      }
    }
    
    function isImageNull() {
      return !(screenParam != null && screenParam != undefined 
        && self.background != null && self.background != undefined
        && self.background.image != null && self.background.image != undefined);
    }
    
    init();
  }
})();

ClassUtils.extend(BackgroundView, BaseView);
/**
 * It's view for screen view controller.
 * auther: handy.wang 2010-07-19
 */
ScreenView = (function() {
  
  var ID = "screenView";
  var DEFAULT_CSS_STYLE = {
     "background-color":"black",
     "color":"#FF0000",
     "width":"100%",
     "height":"100%",
     "position":"relative"
   };
  
  return function(screenParam) {
    // For extend
    ScreenView.superClass.constructor.call(this);
    var self = this;
    
    function layoutBackground() {
      if(!isImageNull()) {
        var currentController = CookieUtils.getCookie(Constants.CURRENT_SERVER);
        var imageQualifiedURL = currentController.url + "/resources/" + self.screen.background.image.src;
        
        var customizedCss = {};
        
        // Fill screen is false.
        if (self.screen.background.fillScreen === false) {
          // Ablosute position of screen background.
          if (self.screen.background.isBGImageAbsolutePosition === true) {
            var left = self.screen.background.bgImageAbsolutePositionLeft;
            var top = self.screen.background.bgImageAbsolutePositionTop;
            customizedCss = {
              "background" : "url('" + imageQualifiedURL +"') no-repeat scroll " + left + "px " + top + "px",
              "width" : "100%",
              "height" : "100%"
            };
          } 
          // Relative position of screen background
          else {
            var bgRelativePosition = self.screen.background.bgImageRelativePosition;
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
        
        var background = $("<div />", {
          "id" : "screenViewBackground" + self.screen.id,
          css : customizedCss
        });
        $(self.getCanvas()).append(background);
      }
    }
    
    function isImageNull() {
      return !(self.screen != null && self.screen != undefined 
        && self.screen.background != null && self.screen.background != undefined
        && self.screen.background.image != null && self.screen.background.image != undefined);
    }
  
    function init() {
      self.screen = screenParam;
      
      self.setID(ID+self.screen.id);
      var canvas = $("<div />", {
        "id" : (ID+self.screen.id)
      });
      self.setCanvas(canvas);
      self.setCss(DEFAULT_CSS_STYLE);
      
      layoutBackground();
    }
  
    init();
    
  };
  
})();

ClassUtils.extend(ScreenView, BaseView);
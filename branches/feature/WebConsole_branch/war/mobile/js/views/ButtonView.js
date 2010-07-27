/**
 * It's view for button.
 * auther: handy.wang 2010-07-22
 */
ButtonView = (function() {
  var ID = "buttonView";
  var DEFAULT_CSS_STYLE = {
     "background-color":"#CCCCCC",
     "background":"url('./mobile/css/jquery/images/ui-bg_glass_75_e6e6e6_1x400.png') repeat-x scroll 50% 50% #E6E6E6",
     "color":"#000000",
     "text-shadow":"0px -1px #bbb,0 2px #fff",
     "width":"100%",
     "height":"100%",
     "border":"solid 1px #D3D3D3",
     "font-family":"Verdana,Arial,sans-serif",
     // for vertical align and the text div css
     "position":"static",
     "display":"table"
   };
  
  return function(buttonParam, sizeParam) {
    ButtonView.superClass.constructor.call(this);
    var self = this;
    
    this.initView = function() {
      initCanvasAndCSS();
      self.customizedCss = {};
      renderImages();
      renderButtonName();
      self.setCss(self.customizedCss);
    }
    
    function initCanvasAndCSS() {
      self.component = buttonParam;
      self.size = sizeParam;
      self.setID(ID+Math.uuid());
      var canvas = $("<div />", {
        "id" : self.getID()
      });
      self.setCanvas(canvas);
      registerListenersForBtn();
      
      self.setCss(DEFAULT_CSS_STYLE);
      
      // register default mouseover and mouseout events.
      if (!(self.component.defaultImage !=null && self.component.defaultImage != undefined)) {
        self.getCanvas().mouseover(function() {
          $(self.getCanvas()).css("background", "url('./mobile/css/jquery/images/ui-bg_glass_75_dadada_1x400.png') 50% 50% repeat-x");
        });
        self.getCanvas().mouseout(function() {
          $(self.getCanvas()).css("background", "url('./mobile/css/jquery/images/ui-bg_glass_75_e6e6e6_1x400.png') 50% 50% repeat-x");
        });
      }
    }
    
    function registerListenersForBtn() {
      // Mousedown event
      $(self.getCanvas()).mousedown(function() {
        if(self.component.hasControlCommand == true) {
          self.sendCommandRequest("click");
          if(self.component.isCommandRepeated) {
            self.timerID = window.setInterval(function() {self.sendCommandRequest("click");}, Constants.REPEAT_CMD_INTERVAL);
          }
        }
      });
      
      // Mouseup event
      $(self.getCanvas()).mouseup(function() {
        window.clearInterval(self.timerID)
      });
      
      // Mouseup event
      $(self.getCanvas()).mouseout(function() {
        window.clearInterval(self.timerID)
      });
    }
    
    function renderImages() {
      var defaultImage = self.component.defaultImage;
      if (defaultImage != null && defaultImage != undefined) {
        self.customizedCss.background = "url('" + ConnectionUtils.getResourceURL(defaultImage.src) + "') no-repeat left top";
        var pressedImage = self.component.pressedImage;
        if (pressedImage != null && pressedImage != undefined) {
          self.getCanvas().mousedown(function() {
            $(self.getCanvas()).css("background", "url('" + ConnectionUtils.getResourceURL(pressedImage.src) + "') no-repeat left top");
          });
          self.getCanvas().mouseup(function() {
            $(self.getCanvas()).css("background", "url('" + ConnectionUtils.getResourceURL(defaultImage.src) + "') no-repeat left top");
          });
          self.getCanvas().mouseout(function() {
            $(self.getCanvas()).css("background", "url('" + ConnectionUtils.getResourceURL(defaultImage.src) + "') no-repeat left top");
          });
        }
      }
    }
    
    function renderButtonName() {
      var name = self.component.name;
      if (name != null && name != undefined) {
        $(self.getCanvas()).html("<div style='position:static;display:table-cell;vertical-align:middle;top:50%'>" + 
                        "<div style='position:relative;top:-50%;width:100%;text-align:center'>" + 
                          name + 
                        "</div>" + 
                       "</div>");
      }
    }

    self.initView();
  }
})();

ClassUtils.extend(ButtonView, ControlView);
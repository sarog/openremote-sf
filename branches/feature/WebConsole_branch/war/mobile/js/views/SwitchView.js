/**
 * It's view for switch.
 * auther: handy.wang 2010-07-26
 */
SwitchView = (function() {
  var ID = "switchView";
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
  
  return function(switchParam, sizeParam) {
    SwitchView.superClass.constructor.call(this, switchParam, sizeParam);
    var self = this;
    
    this.initView = function() {
      initCanvasAndCSS();
      self.customizedCss = {};
      renderImages();
      renderButtonName();
      self.setCss(self.customizedCss);
    }
    
    function initCanvasAndCSS() {
      self.component = switchParam;
      self.size = sizeParam;
      self.setID(ID+self.component.id);
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
      $(self.getCanvas()).click(function() {
        if (self.currentStatus.toUpperCase() == "ON") {
          self.sendCommandRequest("OFF");
        } else if (self.currentStatus.toUpperCase() == "OFF") {
          self.sendCommandRequest("ON");
        }
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
        }
      }
    }
    
    function renderButtonName() {
        $(self.getCanvas()).html("<div style='position:static;display:table-cell;vertical-align:middle;top:50%'>" + 
                        "<div style='position:relative;top:-50%;width:100%;text-align:center'>" + 
                          (Constants.OFF).toUpperCase() + 
                        "</div>" + 
                       "</div>");
        self.currentStatus = Constants.OFF.toUpperCase();
    }
    
    self.initView();
    
  }
})();

ClassUtils.extend(SwitchView, SensoryControlView);
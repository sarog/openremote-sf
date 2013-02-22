/**
 * It's view for switch.
 * author: handy.wang 2010-07-26
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
    var self = this;
    
    /**
     * Override method
     * This method must be defined before calling superClass's construtor whatever in current class or sub classes.
     */
    this.initView = function() {
      self.onImageName = "";
      self.offImageName = "";
      
      initCanvasAndCSS();
      self.customizedCss = {};
      // renderImages();
      renderSwithValue();
      self.setCss(self.customizedCss);
    }
    
    /**
     * Deal with changed status for render in switch view.
     */
    this.dealPollingStatus = function(statusMapParam) {
      var sensor = self.component.sensor;
      if (sensor == null || sensor == undefined) {
        return;
      }
      var sensorStates = sensor.states;
      if (sensorStates.length > 0) {
        if (self.onImageName == "" || self.offImageName == "") {
          for (var i = 0; i < sensorStates.length; i++) {
            var sensorState = sensorStates[i];
            if (sensorState.name.toUpperCase() == Constants.ON.toUpperCase()) {
              self.onImageName = sensorState.value;
            } else if (sensorState.name.toUpperCase() == Constants.OFF.toUpperCase()) {
              self.offImageName = sensorState.value;
            }
          }
        }
      }
      updateViewWithStatus(statusMapParam[sensor.id]);
    };
    
    // Super class's constructor calling
    SwitchView.superClass.constructor.call(this, switchParam, sizeParam);
    
    /**
     * Initializing the style of switch view.
     */
    function initCanvasAndCSS() {
      self.component = switchParam;
      self.size = sizeParam;
      self.setID(ID+Math.uuid());
      var canvas = $("<div />", {
        "id" : self.getID()
      });
      self.setCanvas(canvas);
      registerListeners();
      
      self.setCss(DEFAULT_CSS_STYLE);
      
      var sensor = self.component.sensor;
      if (sensor != null && sensor != undefined) {
        var sensorStates = sensor.states;
      }
      
      if (sensor == null || sensor == undefined || sensorStates == null || sensorStates.length == 0 || sensorStates == undefined) {
        // register default mouseover and mouseout events.
        self.getCanvas().mouseover(function() {
          $(self.getCanvas()).css("background", "url('./mobile/css/jquery/images/ui-bg_glass_75_dadada_1x400.png') 50% 50% repeat-x");
        });
        self.getCanvas().mouseout(function() {
          $(self.getCanvas()).css("background", "url('./mobile/css/jquery/images/ui-bg_glass_75_e6e6e6_1x400.png') 50% 50% repeat-x");
        });
      }
    }
    
    /**
     * Binding events about switch.
     */
    function registerListeners() {
      $(self.getCanvas()).click(function() {
        if (self.currentStatus.toUpperCase() == Constants.ON.toUpperCase()) {
          self.sendCommandRequest(Constants.OFF.toUpperCase());
        } else if (self.currentStatus.toUpperCase() == Constants.OFF.toUpperCase()) {
          self.sendCommandRequest(Constants.ON.toUpperCase());
        }
      });
    }
    
    /**
     * Render changed status value if there is no images for changed status render.
     */
    function renderSwithValue() {
        $(self.getCanvas()).html(
                        "<img id='switchImageSRC" + self.getID() + "' src='' style='display:none;' />" +
                        "<div style='position:static;display:table-cell;vertical-align:middle;top:50%'>" + 
                            "<div id='switchBtnName" + self.getID() + "' style='position:relative;top:-50%;width:100%;text-align:center'>" +
                                Constants.OFF.toUpperCase() + "</div>" + 
                       "</div>");
        updateViewWithStatus(Constants.OFF.toUpperCase());
    }
    
    /**
     * Update the render of switch view with changed status.
     */
    function updateViewWithStatus(statusParam) {
      if (statusParam.toUpperCase() != Constants.ON.toUpperCase() && statusParam.toUpperCase() != Constants.OFF.toUpperCase()) {
        MessageUtils.showMessageDialogWithSettings("Error info", "Invalid status " + statusParam + " with switchview's component id " + self.component.id);
        return;
      }
      self.currentStatus = statusParam.toUpperCase();
      if (canUseImage()) {
        $("#switchBtnName"+self.getID()).text("");
        var imageSRC = "";
        if (self.currentStatus == Constants.ON.toUpperCase()) {
          imageSRC = self.onImageName;
        } else {
          imageSRC = self.offImageName;
        }
        $(self.getCanvas()).css("background", "url('')");
        $("#switchImageSRC"+self.getID()).attr("src", ConnectionUtils.getResourceURL(imageSRC));
        $("#switchImageSRC"+self.getID()).css("display", "inline");
      } else {
        $("#switchImageSRC"+self.getID()).css("display", "none");
        $(self.getCanvas()).css("background", "url('./mobile/css/jquery/images/ui-bg_glass_75_e6e6e6_1x400.png') repeat-x scroll 50% 50% #E6E6E6");
        $("#switchBtnName"+self.getID()).text(self.currentStatus);
      }
    }
    
    /**
     * Judge if switch can use image to render changed status.
     */
    function canUseImage() {
      return self.onImageName != "" && self.offImageName != "";
    }
    
  }
})();

ClassUtils.extend(SwitchView, SensoryControlView);
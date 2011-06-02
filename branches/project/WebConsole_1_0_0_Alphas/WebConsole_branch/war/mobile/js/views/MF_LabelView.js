/**
 * It's view for label.
 * author: handy.wang 2010-08-03
 */
LabelView = (function() {
  var ID = "labelView";
  var DEFAULT_CSS_STYLE = {
     "text-shadow":"0px -1px #bbb,0 1px #fff",
     "font-family":"Verdana,Arial,sans-serif",
     // for vertical align and the text div css
     "position":"static",
     "display":"table"
   };
  
  return function(LabelModelParam, sizeParam) {
    var self = this;
    
    /**
     * Override method
     * This method must be defined before calling superClass's construtor whatever in current class or sub classes.
     */
    this.initView = function() {
      self.component = LabelModelParam;
      self.size = sizeParam;
      self.setID(ID + Math.uuid());
      var canvas = $("<div />", {
        "id" : self.getID(),
        "html" : "<div style='position:static;display:table-cell;vertical-align:middle;top:50%'>" + 
                        "<div id='labelName" + self.getID() + "' style='position:relative;top:-50%;width:100%;text-align:center'>" +
                        self.component.text +
                        "</div>" + 
                       "</div>",
        css : {
          "color" : self.component.color,
          "width" : self.size.width+"px",
          "height" : self.size.height+"px",
          "font-size" : self.component.fontSize
        }
      });
      self.setCanvas(canvas);
      self.setCss(DEFAULT_CSS_STYLE);
    }
    
    /**
     * Deal with the changed status for render in lalel view.
     */
    this.dealPollingStatus = function(statusMapParam) {
      var sensorStates = self.component.sensor.states;
      var newStatus = statusMapParam[self.sensorID];
      var changeText = false; 
      if (sensorStates.length > 0) {
        for (var i = 0; i < sensorStates.length; i++) {
          var state = sensorStates[i];
          if (state.name.toLowerCase() == newStatus.toLowerCase()) {
            $("#labelName" + self.getID()).text(state.value);
            changeText = true;
            break;
          }
        }
      }
      
      if (!changeText && newStatus != null && newStatus != "") {
        $("#labelName" + self.getID()).text(newStatus);
        changeText = true;
      }
      
      if (!changeText) {
        $("#labelName" + self.getID()).text(self.component.text);
      }

    };
    
    // Super class's constructor calling
    LabelView.superClass.constructor.call(this, LabelModelParam, sizeParam);
    
  }
})();

ClassUtils.extend(LabelView, SensoryView);
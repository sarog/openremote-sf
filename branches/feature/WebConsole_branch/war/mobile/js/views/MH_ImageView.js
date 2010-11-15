/**
 * It's view for image.
 * author: handy.wang 2010-08-04
 */
ImageView = (function() {
  var ID = "imageView";
  var DEFAULT_CSS_STYLE = {
     "text-shadow":"0px -1px #bbb,0 1px #fff",
     "font-family":"Verdana,Arial,sans-serif",
     // for vertical align and the text div css
     "position":"static",
     "display":"table"
   };
  
  return function(imageModelParam, sizeParam) {
    var self = this;
    
    /**
     * Override method
     * This method must be defined before calling superClass's construtor whatever in current class or sub classes.
     */
    this.initView = function() {
      self.component = imageModelParam;
      self.size = sizeParam;
      self.setID(ID + Math.uuid());
      var defaultImageSRC = ConnectionUtils.getResourceURL(self.component.src);
      var canvas = $("<div />", {
        "id" : self.getID(),
        "html" : "<img id='imageSRC" + self.getID() + "' src='" + defaultImageSRC + "' />" +
                "<div style='position:static;display:table-cell;vertical-align:middle;top:50%'>" + 
                    "<div id='textSRC" + self.getID() + "' style='position:relative;top:-50%;width:100%;text-align:center'>" +
                    "</div>" + 
                 "</div>",
        css : {
          "width" : self.size.width+"px",
          "height" : self.size.height+"px"
        }
      });
      self.setCanvas(canvas);
      
      self.setCss(DEFAULT_CSS_STYLE);
    }
    
    /**
     * Deal with changed status for render in image view.
     */
    this.dealPollingStatus = function(statusMapParam) {
      var newStatus = statusMapParam[self.sensorID];
      var updateView = false;
      var sensorStates = self.component.sensor.states;
      
	    // Render image-sensor's state image
      for (var i = 0; i < sensorStates.length; i++) {
        var sensorState = sensorStates[i];
        if (sensorState.name == newStatus) {
          var imageSRC = ConnectionUtils.getResourceURL(sensorState.value);
          $("#imageSRC"+self.getID()).css({"display" : "inline"});
          $("#imageSRC"+self.getID()).attr({"src" : imageSRC});
          updateView = true;
          break;
        }
      }
      
    	// Render included label
    	var labelID = self.component.labelID;
    	if (labelID == null) {
    	  return;
    	}
    	var includedLabelOfImage = RenderDataDB.getInstance().findLabelByID(labelID);
      if (updateView == false && includedLabelOfImage != null) {
        var labelSensorStates = includedLabelOfImage.sensor.states;
        for (var i = 0; i < labelSensorStates.length; i++) {
          var labelSensorState = labelSensorStates[i];
          if (labelSensorState.name == newStatus) {
            var css = {
              "color" : includedLabelOfImage.color,
              "font-size" : includedLabelOfImage.fontSize
            };
            $("#imageSRC"+self.getID()).css({"display" : "none"});
            $("#textSRC"+self.getID()).text(labelSensorState.value);
            $("#textSRC"+self.getID()).css(css);
          }
        }
      }
      
    }
    
    ImageView.superClass.constructor.call(this, imageModelParam, sizeParam);
    
  }
})();

ClassUtils.extend(ImageView, SensoryView);
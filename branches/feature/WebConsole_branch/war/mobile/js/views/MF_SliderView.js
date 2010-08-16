/**
 * It's view for slider.
 * author: handy.wang 2010-08-05
 */
SliderView = (function() {
  var ID = "sliderView";
  var DEFAULT_CSS_STYLE = {};
   
   return function(sliderModelParam, sizeParam) {
     var self = this;
     
     /**
      * Override method
      * This method must be defined before calling superClass's construtor whatever in current class or sub classes.
      */
     this.initView = function() {
       self.component = sliderModelParam;
       self.size = sizeParam;
       self.currentValue = null;

       self.UUID = Math.uuid();       
       makeRootCanvas(self.UUID);
       makeMiddleCanvas(self.UUID);
       
       $(self.rootCanvas).append(self.middleCanvas);
       self.setCanvas(self.rootCanvas);
       self.setCss(DEFAULT_CSS_STYLE);
     }

     /**
      * Deal with the polling status.
      */
     this.dealPollingStatus = function(statusMapParam) {
       var newStatus = statusMapParam[self.sensorID];
       // self.sliderRange.children("a:last-child").text(newStatus);
       self.sliderRange.slider({"value":parseInt(newStatus)});
       self.currentValue = newStatus;
     }
     
     /** 
      * Compose root canvas for appending middle canvas.
      */
     function makeRootCanvas(UUID) {
       self.setID(ID + UUID);
       
       self.rootCanvas = $("<div />", {
         "id" : self.getID(),
         css : {
           "width" : self.size.width,
           "height" : self.size.height,
           "position":"static",
           "display":"table"
         }
       });
     }
     
     /**
      * Compose middle canvas for appending slider buttions and slider range.
      */
      function makeMiddleCanvas(UUID) {
        self.middleCanvas = $("<div />", {
          "id" : "midleView" + UUID,
          css : {
            "display":"table-cell",
            "vertical-align":"middle",
            "text-align":"center",
            "position":"relative",
            "width":"100%",
            "height":"100%"
          }
        });

        // Append left/top button to middleCanvas
        $(self.middleCanvas).append(makeBtn(UUID, true));
        
        // Append slider range to middleCanvas
        if (self.component.isVertical == true) {
          $(self.middleCanvas).append(makeSliderRange(UUID));
        }
        
        // Append right/bottom button to middleCanvas
        $(self.middleCanvas).append(makeBtn(UUID, false));

        // Append slider range to middleCanvas
        if (self.component.isVertical == false) {
          $(self.middleCanvas).append(makeSliderRange(UUID));
        }
      }
     
     /**
      * Compose slider buttons around slider range.
      */
     function makeBtn(UUID, isFirstBtn) {
       var maxImageSRC = self.component.maxImageSRC;
       var canMaxImageUse = (maxImageSRC != null && maxImageSRC != "");
       var qualifiedMaxImageSRC = ConnectionUtils.getResourceURL(maxImageSRC);
       
       var minImageSRC = self.component.minImageSRC;
       var canMinImageUse = (minImageSRC != null && minImageSRC != "");
       var qualifiedMinImageSRC = ConnectionUtils.getResourceURL(minImageSRC);
       
       var imageSRC = "";
       if (self.component.isVertical) {
         if (isFirstBtn) {
           imageSRC = canMaxImageUse ? qualifiedMaxImageSRC : "./mobile/images/plus.png";
         } else {
           imageSRC = canMinImageUse ? qualifiedMinImageSRC : "./mobile/images/minus.png";
         }
       } else {
         if (isFirstBtn) {
           imageSRC = canMinImageUse ? qualifiedMinImageSRC : "./mobile/images/minus.png";
         } else {
           imageSRC = canMaxImageUse ? qualifiedMaxImageSRC : "./mobile/images/plus.png";
         }
       }
       
       btn = $("<div />", {
         "id" : "sliderBtn" + (isFirstBtn ? (self.component.isVertical ? "Top" : "Left") : (self.component.isVertical ? "Bottom" : "Right")) + UUID,
         css : {
           "cursor":"default",
           "height":"1.4em",
           "width":"1.4em",
           "z-index":"2",
           "float":(self.component.isVertical == true) ? "none" : (isFirstBtn == true) ? "left" : "right",
           "-moz-border-radius":"4px 4px 4px 4px",
           "background" : "url(" + imageSRC + ") no-repeat scroll 50% 50%",
           "margin-left": (self.component.isVertical) ? "auto" : (isFirstBtn == true) ? "0px" : "7px",
           "margin-right":"auto",
           "margin-top":self.component.isVertical ? ((isFirstBtn == true) ? "0px" : "7px") : "auto"
         },
         click : function() {
           if (self.component.isPassive == true) {
             return;
           }
           if (self.currentValue == null) {
             return;
           }
           if (self.component.isVertical) {
             if (isFirstBtn) {
               var num = parseInt(self.currentValue)+1;
               self.sendCommandRequest((num > self.component.maxValue) ? self.component.maxValue : num);
             } else {
               var num = parseInt(self.currentValue)-1;
               self.sendCommandRequest((num < self.component.minValue) ? self.component.minValue : num);
             }
           } else {
             if (isFirstBtn) {
               var num = parseInt(self.currentValue)-1;
               self.sendCommandRequest((num < self.component.minValue) ? self.component.minValue : num);
             } else {
               var num = parseInt(self.currentValue)+1;
               self.sendCommandRequest((num > self.component.maxValue) ? self.component.maxValue : num);
             }
           }
         }
       });
       return btn;
     }
     
     /**
      * Compose slider range.
      */
     function makeSliderRange(UUID) {
       var sliderRangeCanvas = $("<div />", {
         "id" : "sliderRange" + UUID,
         css : {
           "top": (self.component.isVertical == true) ? "0px" : "0.2em",
           "margin-top" : (self.component.isVertical == true) ? "7px" : "auto",
           "margin-left" : "auto",
           "margin-right" : "auto",
           "position":"relative",
           "text-align":"center"
         }
       });
       
       self.sliderRange = $(sliderRangeCanvas).slider({
         orientation: (self.component.isVertical) ? "vertical" : "horizontal",
         range: "min",
         min: parseInt(self.component.minValue),
         max: parseInt(self.component.maxValue),
         step: 1,
         value: 0,
         slide: function(event, ui) {
           self.sendCommandRequest(ui.value);
         }
       });
       
       var thumb = self.sliderRange.children("a:last-child");
       
       // Hack the slider of JQuery UI providing.
       // Default trackImages and thumbImages.
       if (self.component.isVertical == false) {
         sliderRangeCanvas.css({"width":"70%"});
         self.sliderRange.children("div:first-child").removeClass("ui-widget-header").addClass("horizontal-slider-min-track-image");
         thumb.removeClass("ui-state-default").addClass("slider-thumb-image");
       } else {
         sliderRangeCanvas.css({"height":"70%"});
         self.sliderRange.children("div:first-child").removeClass("ui-widget-header").addClass("vertical-slider-min-track-image");
         thumb.removeClass("ui-state-default").addClass("slider-thumb-image");
       }
       
       // Customized sliderRange if the custom data exists(such as thumb image, track image, etc.)
       // Customize thumb image.
       // thumb.css({"font-size":"14px"});
       var thumbImageSRC = self.component.thumbImageSRC;
       var canThumbImageUse = (thumbImageSRC != null && thumbImageSRC != "" && thumbImageSRC != undefined);
       var qualifiedThumbImageSRC = ConnectionUtils.getResourceURL(thumbImageSRC);
       if (canThumbImageUse) {
         thumb.css({"background":"url("+ qualifiedThumbImageSRC +") no-repeat scroll 50% 50%"});
         thumb.css({"border":"0px"});
         thumb.mouseover(function() {
           thumb.css({"border":"0px solid #D3D3D3"});
         });
       }
       
       // Begin customize track image.
       // Customizes min trackImage.
       var minTrackImageSRC = self.component.minTrackImageSRC;
       var canMinTrackImageUse = (minTrackImageSRC != null && minTrackImageSRC != "");
       var qualifiedMinTrackImageSRC = ConnectionUtils.getResourceURL(minTrackImageSRC);
       if (canMinTrackImageUse) {
         if (self.component.isVertical) {
           self.sliderRange.children("div:first-child").css({"background" : "url(" + qualifiedMinTrackImageSRC +") repeat-y scroll 50% 50%"});
         } else {
           self.sliderRange.children("div:first-child").css({"background" : "url(" + qualifiedMinTrackImageSRC +") repeat-x scroll 50% 50%"});
         }
       }
       
       // Customizes max trackImage
       var maxTrackImageSRC = self.component.maxTrackImageSRC;
       var canMaxTrackImageUse = (maxTrackImageSRC != null && maxTrackImageSRC != "");
       var qualifiedMaxTrackImageSRC = ConnectionUtils.getResourceURL(maxTrackImageSRC);
       if (canMaxTrackImageUse) {
         if (self.component.isVertical) {
           self.sliderRange.css({"background" : "url(" + qualifiedMaxTrackImageSRC + ") repeat-y scroll 50% 50%"});
         } else {
           self.sliderRange.css({"background" : "url(" + qualifiedMaxTrackImageSRC + ") repeat-x scroll 50% 50%"});
         }
       }
       if (canMinTrackImageUse && canMaxTrackImageUse) {
         self.sliderRange.css({"border" : "0px"});
       }
       // End custom track image
       
       if (self.component.isPassive == true) {
         self.sliderRange.slider({disabled: true});
       }
       
       return self.sliderRange;
     }

     SliderView.superClass.constructor.call(this, sliderModelParam, sizeParam);
     
   }
  
})();

ClassUtils.extend(SliderView, SensoryControlView);
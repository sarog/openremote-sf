/**
 * It's view for slider.
 * auther: handy.wang 2010-08-05
 */
SliderView = (function() {
  var ID = "sliderView";
  var DEFAULT_CSS_STYLE = {
   };
   
   return function(sliderModelParam, sizeParam) {
     var self = this;
     
     /**
      * Override method
      * This method must be defined before calling superClass's construtor whatever in current class or sub classes.
      */
     this.initView = function() {
       self.component = sliderModelParam;
       self.size = sizeParam;
       var UUID = Math.uuid();
       self.setID(ID + UUID);
       
       // Begin make slider view root canvas
       var rootCanvas = $("<div />", {
         "id" : self.getID(),
         css : {
           "width" : self.size.width,
           "height" : self.size.height,
           "position":"static",
           "display":"table"
         }
       });
       // End make slider view root canvas
       
       var middleCanvas = $("<div />", {
         "id" : "midleView" + UUID,
         css : {
           "display":"table-cell",
           "vertical-align":(self.component.isVertical == true) ? "top" : "middle",
           "text-align":"center",
           "position":"relative",
           "top":"-50%",
           "top":(self.component.isVertical == true) ? "0px" : "50%",
           "width":"100%",
           "height":"100%"
         }
       });
       
       // Append left/top button to middleCanvas
       if (self.component.isVertical == true) {
         $(middleCanvas).append(makeBtn(UUID, "./mobile/images/plus.png", true));
       } else {
         $(middleCanvas).append(makeBtn(UUID, "./mobile/images/minus.png", true));
       }
       // Append slider range to middleCanvas
       $(middleCanvas).append(makeSliderRange(UUID));
       // Append right/bottom button to middleCanvas
       if (self.component.isVertical == true) {
         $(middleCanvas).append(makeBtn(UUID, "./mobile/images/minus.png", false));
       } else {
         $(middleCanvas).append(makeBtn(UUID, "./mobile/images/plus.png", false));
       }
       
       $(rootCanvas).append(middleCanvas);
       self.setCanvas(rootCanvas);
       self.setCss(DEFAULT_CSS_STYLE);
     }

     this.dealPollingStatus = function(statusMapParam) {
     }
     
     function makeBtn(UUID, imageSRC, isFirstBtn) {
       self.leftBtn = $("<div />", {
         "id" : "sliderLeftBtn" + UUID,
         css : {
           "cursor":"default",
           "height":"1.2em",
           "width":"1.2em",
           "z-index":"2",
           "float":(self.component.isVertical == true) ? "none" : "left",
           "-moz-border-radius":"4px 4px 4px 4px",
           "background" : "url(" + imageSRC + ") no-repeat scroll 50% 50%",
           "margin-left": (self.component.isVertical) ? "auto" : (isFirstBtn == true) ? "0px" : "7px",
           "margin-right":"auto",
           "margin-top":self.component.isVertical ? ((isFirstBtn == true) ? "0px" : "7px") : "auto"
         }
       });
       return self.leftBtn;
     }
     
     function makeSliderRange(UUID) {
       var sliderRangeCanvas = $("<div />", {
         "id" : "sliderRange" + UUID,
         css : {
           "float" : (self.component.isVertical == true) ? "none" : "left",
           "top": (self.component.isVertical == true) ? "0px" : "0.2em",
           "margin-top" : (self.component.isVertical == true) ? "7px" : "auto",
           "margin-left":(self.component.isVertical) ? "auto" : "7px",
           "margin-right":"auto"
         }
       });
       
       self.sliderRange = $(sliderRangeCanvas).slider({
         orientation: (self.component.isVertical) ? "vertical" : "horizontal",
         range: "min",
         min: 0,
         max: 200,
         step: 1,
         value: 100,
         slide: function(event, ui) {
         }
       });

       if (self.component.isVertical == false) {
         sliderRangeCanvas.css({"width":"70%"});
         self.sliderRange.children("div:first-child").removeClass("ui-widget-header").addClass("horizontal-slider-left-track-image");
         self.sliderRange.children("a:last-child").removeClass("ui-state-default").addClass("slider-thumb-image");
       } else {
         sliderRangeCanvas.css({"height":"70%"});
         self.sliderRange.children("div:first-child").removeClass("ui-widget-header").addClass("vertical-slider-left-track-image");
         self.sliderRange.children("a:last-child").removeClass("ui-state-default").addClass("slider-thumb-image");
       }
       return self.sliderRange;
     }

     SliderView.superClass.constructor.call(this, sliderModelParam, sizeParam);
     
   }
  
})();

ClassUtils.extend(SliderView, SensoryControlView);
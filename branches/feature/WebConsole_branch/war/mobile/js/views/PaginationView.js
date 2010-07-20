/**
 * It's view for pagination controller.
 * auther: handy.wang 2010-07-19
 */
PaginationView = (function() {
  
  var UUID = Math.uuid();
  var ID = "paginationView" + UUID;
  var DEFAULT_CSS_STYLE = {
     "background-color":"gray",
     "color":"#FF0000",
     "width":"100%",
     "height":"100%",
     "position":"absolute"
   };
  
  return function(screenViewParam, delegateParam) {
    // For extend
    PaginationView.superClass.constructor.call(this);
    var self = this;
    self.delegate = delegateParam;
    
    this.updateView = function(screenViewParam) {
      $(self.screenViewContainer).children().remove();
      $(self.screenViewContainer).append(screenViewParam.getCanvas());
    };
    
    function init() {
      self.setID(ID);
      
      self.screenViewContainer = $("<div />", {
        "id" : "screenViewContainer" + UUID,
        css : {
          "backgroundColor" : "black",
          "position" : "relative",
          "width" : "100%",
          "height" : "90%"
        }
      });
      if (screenViewParam != null) {
        $(self.screenViewContainer).append(screenViewParam.getCanvas());
      }
            
      var canvas = $("<div />", {
        "id" : ID
      });
      
      $(canvas).append(self.screenViewContainer);
      $(canvas).append(constructPageControl());
      self.setCanvas(canvas);
      self.setCss(DEFAULT_CSS_STYLE);
    }
    
    function constructPageControl() {
      var previousScreenBtn = $("<div />", {
        "id" : "previousScreenBtn" + UUID,
        css : {
          "float" : "left",
          "marginLeft" : "10px",
          "height" : "100%",
          "width" : "24px",
          "background" : "url('./mobile/images/previous_next_screen.png') no-repeat scroll 0 50%"
        },
        click : function() {
          self.delegate.previousScreen();
        }
      });
      
      var nextScreenBtn = $("<div />", {
        "id" : "nextScreenBtn" + UUID,
        css : {
          "float" : "right",
          "marginRight" : "10px",
          "height" : "100%",
          "width" : "24px",
          "background" : "url('./mobile/images/previous_next_screen.png') no-repeat scroll -24px 50%"
        },
        click : function() {
          self.delegate.nextScreen();
        }
      });
      
      var pageControl = $("<div />", {
        "id" : "pager" + UUID,
        css : {
          // "background" : "url(./mobile/images/error_title_bg.jpg) repeat-x",
          "backgroundColor" : "#477db6",
          "position" : "relative",
          "width" : "100%",
          "height" : "10%"
        }
      });
      $(pageControl).append(previousScreenBtn);
      $(pageControl).append(nextScreenBtn);
      return pageControl;
    }
    
    init();
  };
})();

// For extend
ClassUtils.extend(PaginationView, BaseView);
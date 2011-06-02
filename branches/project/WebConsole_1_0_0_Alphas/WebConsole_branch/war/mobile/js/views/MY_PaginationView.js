/**
 * It's view for pagination controller.
 * author: handy.wang 2010-07-19
 */
PaginationView = (function() {
  
  var ID = "paginationView";
  var DEFAULT_CSS_STYLE = {
     // "background-color":"gray",
     "width":"100%",
     "height":"100%"
   };
  
  return function(screenViewParam, delegateParam) {
    // For extend
    PaginationView.superClass.constructor.call(this);
    var self = this;
    self.UUID = Math.uuid();
    self.delegate = delegateParam;
    
    /**
     * Switch screen view.
     */
    this.updateView = function(screenViewParam) {
      $(self.screenViewContainer).children().detach();
      $(self.screenViewContainer).append(screenViewParam.getCanvas());
    };
    
    /**
     * Render the view of menuItemList.
     */
    this.renderMenuItemListView = function(groupParam) {
      self.menuItemListView = new MenuItemListView(groupParam.tabBar);
      self.addSubView(self.menuItemListView);
    }
    
    function init() {
      self.setID(ID+self.UUID);
      
      self.screenViewContainer = $("<div />", {
        "id" : "screenViewContainer" + self.UUID,
        css : {
          // "backgroundColor" : "black",
          "width" : "100%",
          "height" : "90%"
        }
      });
      if (screenViewParam != null) {
        $(self.screenViewContainer).append(screenViewParam.getCanvas());
      }
            
      var canvas = $("<div />", {
        "id" : self.getID()
      });
      
      $(canvas).append(constructPageControl());
      $(canvas).append(self.screenViewContainer);
      
      self.setCanvas(canvas);
      self.setCss(DEFAULT_CSS_STYLE);
    }
    
    /**
     * Make page control view.
     */
    function constructPageControl() {
      // PreviousScreenButton
      var previousScreenBtn = $("<div />", {
        "id" : "previousScreenBtn" + self.UUID,
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
      
      // NextScreenButton
      var nextScreenBtn = $("<div />", {
        "id" : "nextScreenBtn" + self.UUID,
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
      
      // MenuButton
      var menuBtn = $("<div />", {
        "id" : "menuBtn" + self.UUID,
        "html" : "<div style='position:static;display:table-cell;vertical-align:middle;top:50%'>" +
                    // "<div style='position:relative;top:-50%;width:100%;text-align:center;'>" +
                    //   "<image src = './mobile/images/menu.png' />" +
                    // "</div>" +
                    "<div style='position:relative;top:-50%;width:100%;text-align:center'>Menu</div>" +
                 "</div>",
        css : {
          "float" : "center",
          "height" : "98%",
          "width" : "25%",
          "margin-left" : "37%",
          "text-align" : "center",
          "font-size" : "11px",
          "color":"#000000",
          "text-shadow":"0px -1px #bbb,0 2px #fff",
          "font-family":"Verdana,Arial,sans-serif",
          // For vertical center
          "position":"static",
          "display":"table",
          "background":"url('./mobile/css/jquery/images/ui-bg_glass_75_e6e6e6_1x400.png') repeat-x scroll 50% 50%"
        },
        click : function() {
          self.menuItemListView.trigger();
        }
      });
      
      var pageControl = $("<div />", {
        "id" : "pager" + self.UUID,
        css : {
          // "background" : "url(./mobile/images/error_title_bg.jpg) repeat-x",
          "backgroundColor" : "#E6E6E6",//#477db6
          "width" : "100%",
          "height" : "10%"
        }
      });
      $(pageControl).append(previousScreenBtn);
      $(pageControl).append(nextScreenBtn);
      $(pageControl).append(menuBtn);
      return pageControl;
    }
    
    init();
  };
})();

// For extend
ClassUtils.extend(PaginationView, BaseView);
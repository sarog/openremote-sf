/**
 * It's view for menu item list.
 * auther: handy.wang 2010-08-02
 */
MenuItemListView = (function() {
  var ID = "menuItemListView" + Math.uuid();
  var DEFAULT_CSS_STYLE = {
     "background-color":"#E6E6E6",
     "border-top" : "1px solid orange",
     "border-left" : "1px solid orange",
     "border-right" : "1px solid orange",
     "position" : "absolute",
     "bottom" : "10%",
     "width" : "99%",
     "display" : "none",
     "color" : "black",
     "font-size" : "10px",
     "text-shadow":"0px -1px #bbb,0 2px #fff",
     "font-family":"Verdana,Arial,sans-serif"
   };
  
  return function(tabBarModelParam) {
    MenuItemListView.superClass.constructor.call(this);    
    var self = this;
    self.show = false;
    
    this.trigger = function() {
      if (self.show == false) {
        $(self.getCanvas()).css("display", "block");
        self.show = true;        
      } else {
        $(self.getCanvas()).css("display", "none");
        self.show = false;
      }
    };
    
    function initView() {
      self.tabBarModel = tabBarModelParam;
      self.setID(ID);
      var canvas = $("<div />", {
        "id" : self.getID()
      });
      self.setCanvas(canvas);
      self.setCss(DEFAULT_CSS_STYLE);
      renderMenuItems();
    }
    
    function renderMenuItems() {
      var globalTabBar = RenderDataDB.getInstance().globalTabBar;
      if (globalTabBar != null) {
        var globalTabBarItems = globalTabBar.items;
        if (globalTabBarItems.length > 0) {
          for (var i = 0; i < globalTabBarItems.length; i++) {
            var float = "left";
            if (i % 2 != 0) {
              float = "right";
            }
            var item = globalTabBarItems[i];
            var qualifiedImageSrc = ConnectionUtils.getResourceURL(item.image.src);
            var itemHTML = $("<div />", {
              "html" : "<div style='position:static;display:table-cell;vertical-align:middle;top:50%'>" + 
                              "<div style='position:relative;top:-50%;width:100%;text-align:center;'>" +
                              "<image style='padding-top:10px;' src = '" + qualifiedImageSrc + "' />" +
                              "</div>" +
                              
                              "<div style='position:relative;top:-50%;width:100%;text-align:center;padding-top:5px;padding-bottom:5px;'>" + 
                                item.name + 
                              "</div>" + 
                             "</div>",
              css : {
                "background" : "url('./mobile/css/jquery/images/ui-bg_glass_75_e6e6e6_1x400.png') repeat-x scroll 50% 50% #E6E6E6",
                "border" : "solid 1px #D3D3D3",
                "width" : "49%",
                "height" : "44px",
                "position":"static",
                "display":"table",
                "float" : float
              },
              click : function() {
                NotificationCenter.getInstance().postNotification(Constants.NAVIGATION_NOTIFICATION, item.navigate);
              }
            });
            $(self.getCanvas()).append(itemHTML);
          }
        }
      }
    }
    
    initView();
  }
})();

ClassUtils.extend(MenuItemListView, BaseView);
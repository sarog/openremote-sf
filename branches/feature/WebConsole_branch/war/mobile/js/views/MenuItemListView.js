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
     "font-size" : "12px",
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
      renderMenu();
    }
    
    function renderMenu() {
      var globalTabBar = RenderDataDB.getInstance().globalTabBar;
      if (self.tabBarModel != null) {
        renderMenuItems(self.tabBarModel);
      } else if (globalTabBar != null) {
        renderMenuItems(globalTabBar);
      } else {
        var defaultTabBar = {};
        defaultTabBar.items = [];
        var settingItem = {name : "Settings", image : {src : "!./mobile/images/gear.png"}, navigate : {isToSetting : true}};
        var backItem = {name : "Back", image : {src : "!./mobile/images/go-back.png"}, navigate : {isToBack : true}};
        
        defaultTabBar.items[defaultTabBar.items.length] = settingItem;
        defaultTabBar.items[defaultTabBar.items.length] = backItem;
        renderMenuItems(defaultTabBar);
      }
    }
    
    function renderMenuItems(tabBarModel) {
      var tabBarItems = tabBarModel.items;
      if (tabBarItems.length > 0) {
        for (var i = 0; i < tabBarItems.length; i++) {
          var float = "left";
          if (i % 2 != 0) {
            float = "right";
          }
          var item = tabBarItems[i];
          self.addSubView(new MenuItemView(item, float, self));
        }
      }
    }
    
    initView();
  }
})();

ClassUtils.extend(MenuItemListView, BaseView);
/**
 * It's view for menu item.
 * author: handy.wang 2010-08-03
 */
MenuItemView = (function() {
  var ID = "menuItemView";
  var DEFAULT_CSS_STYLE = {
    "background" : "url('./mobile/css/jquery/images/ui-bg_glass_75_e6e6e6_1x400.png') repeat-x scroll 50% 50% #E6E6E6",
    "border" : "solid 1px #D3D3D3",
    "width" : "33%",
    "height" : "44px",
    "position":"static",
    "display":"table",
    "float" : "left",
    "margin-left" : "1px"
   };
  
  return function(tabBarItemParam, delegateParam) {
    MenuItemView.superClass.constructor.call(this);
    var self = this;
    
    function initView() {
      self.tabBarItem = tabBarItemParam;
      self.delegate = delegateParam;
      self.setID(ID + Math.uuid());
      
      var qualifiedImageSrc = "";
      var imageSrc = (self.tabBarItem.image != null && self.tabBarItem.image != undefined) ? self.tabBarItem.image.src : "";
      // The src of image start with "!" means it's local resource.
      if (imageSrc.indexOf("!") == 0) {
        qualifiedImageSrc = imageSrc.substring(imageSrc.indexOf("!") + 1);
      } else {
        qualifiedImageSrc = ConnectionUtils.getResourceURL(imageSrc);
      }
      
      var tabBarItemName = (self.tabBarItem.name.length > 10) ? self.tabBarItem.name.substring(0,10)+"..." : self.tabBarItem.name;
      
      var canvas = $("<div />", {
        "id" : self.getID(),
        "html" : "<div style='position:static;display:table-cell;vertical-align:middle;top:50%'>" + 
                        "<div style='position:relative;top:-50%;width:100%;text-align:center;'>" +
                          "<image style='padding-top:10px;' src = '" + qualifiedImageSrc + "' />" +
                        "</div>" +
                        
                        "<div style='position:relative;top:-50%;width:100%;text-align:center;padding-top:5px;padding-bottom:5px;'>" + 
                          tabBarItemName + 
                        "</div>" + 
                       "</div>",
        click : function() {
          NotificationCenter.getInstance().postNotification(Constants.NAVIGATION_NOTIFICATION, self.tabBarItem.navigate);
          self.delegate.trigger();
        }
      });
      self.setCanvas(canvas);
      self.setCss(DEFAULT_CSS_STYLE);
    }
    
    initView();
  }
})();

ClassUtils.extend(MenuItemView, BaseView);
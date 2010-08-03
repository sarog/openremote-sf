/**
 * It's view for menu item.
 * auther: handy.wang 2010-08-03
 */
MenuItemView = (function() {
  var ID = "menuItemView" + Math.uuid();
  var DEFAULT_CSS_STYLE = {
    "background" : "url('./mobile/css/jquery/images/ui-bg_glass_75_e6e6e6_1x400.png') repeat-x scroll 50% 50% #E6E6E6",
    "border" : "solid 1px #D3D3D3",
    "width" : "49%",
    "height" : "44px",
    "position":"static",
    "display":"table"
   };
  
  return function(tabBarItemParam, cssFloatValue, delegateParam) {
    MenuItemView.superClass.constructor.call(this);
    var self = this;
    
    function initView() {
      self.tabBarItem = tabBarItemParam;
      self.delegate = delegateParam;
      self.setID(ID);
      
      var qualifiedImageSrc = "";
      var imageSrc = self.tabBarItem.image.src;
      if (imageSrc.indexOf("!") != -1) {
        qualifiedImageSrc = imageSrc.substring(imageSrc.indexOf("!") + 1);
      } else {
        qualifiedImageSrc = ConnectionUtils.getResourceURL(self.tabBarItem.image.src);
      }
      var canvas = $("<div />", {
        "id" : self.getID(),
        "html" : "<div style='position:static;display:table-cell;vertical-align:middle;top:50%'>" + 
                        "<div style='position:relative;top:-50%;width:100%;text-align:center;'>" +
                          "<image style='padding-top:10px;' src = '" + qualifiedImageSrc + "' />" +
                        "</div>" +
                        
                        "<div style='position:relative;top:-50%;width:100%;text-align:center;padding-top:5px;padding-bottom:5px;'>" + 
                          self.tabBarItem.name + 
                        "</div>" + 
                       "</div>",
        css : {
          "float" : cssFloatValue
        },
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
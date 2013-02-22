/**
 * It's init view for app.
 * author: handy.wang 2010-07-14
 */
InitView = (function() {
  
  var ID = "initView";
  var DEFAULT_CSS_STYLE = {
     "background-color":"black",
     "color":"#FF0000",
     "width":"100%",
     "height":"100%",
     "position":"absolute"
   };
  
  return function() {
    InitView.superClass.constructor.call(this);
    var self = this;
    
    function initView() {
      self.setID(ID);
      var canvas = $("<div />", {
        "id" : ID,
        "html" : "<div style='text-align:center; position:relative; width:100%; height:100%;' >" + 
                    "<image style='width:auto;' src='./mobile/images/Default.png' />" + 
                  "</div>"
      });
      self.setCanvas(canvas);
      self.setCss(DEFAULT_CSS_STYLE);
    }
    
    initView();
    
  };
})();

ClassUtils.extend(InitView, BaseView);
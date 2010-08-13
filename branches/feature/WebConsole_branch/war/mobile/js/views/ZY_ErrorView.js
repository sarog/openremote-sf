/**
 * It's error view for error.
 * author: handy.wang 2010-07-14
 */
 
ErrorView = (function(){
  
  var ID = "errorView";
  var DEFAULT_CSS_STYLE = {
     "background-color":"white",
     "color":"black",
     "width":"100%",
     "height":"100%",
     "position":"absolute",
     "top" : "0px"
   };
  
  // Constructor
  return function(titleParam, messageParam) {
    // For extend
    ErrorView.superClass.constructor.call(this);
    
    var self = this;
    
    var title = "Error";
    var message = "Error message.";
    
    function initView () {
      self.setID(ID);
      title = titleParam;
      message = messageParam;
      
      var canvas = $("<div />", {
        "id" : ID,
        "html" :  "<div style='background:url(./mobile/images/error_title_bg.jpg) repeat-x; width:100%; height:44px;'>" + 
                      "<button id='errorViewSettingsBtn' style='margin-top:5px;'>Settings</button>" + 
                  "</div>" + 
                  "<div style='text-align:center; position:relative; margin-top:5%;' >" + 
                    "<image style='width:auto;' src='./mobile/images/repair.png' />" + 
                    "<div class='segmentTitle' style='font-size:160%; padding: 3%;'>" + title + "</div>" + 
                    "<div style='font-size:100%;'>" + message + "</div>" + 
                  "</div>"
                  
      });
      self.setCanvas(canvas);
      self.setCss(DEFAULT_CSS_STYLE);

    }
    
    initView();
    
  }
})();

// For extend.
ClassUtils.extend(ErrorView, BaseView);
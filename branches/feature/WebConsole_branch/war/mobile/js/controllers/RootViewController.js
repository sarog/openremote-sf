/**
 * It's for controlling all views render.
 * auther: handy.wang 2010-07-13
 */
 RootViewController = (function(){
   
   // Constructor
   return function() {
     var self = this;
     
     // For extends
     RootViewController.superClass.constructor.call(this);
     
     // properties
     //var ErrorViewController = new ErrorViewController();

     // Public instance methods
     //TODO....

     // Private instance methods
     function initView() {
       self.setView(RootView.getInstance());
       var cssStyle = {
         "background-color":Constants.COLOR_BLACK,
         "color":"#FF0000",
         "width":"100%",
         "height":"100%",
         "position":"absolute",
         "float":"left"
       };
       self.getView().setCss(cssStyle);
     }
     
     // Init jobs
     initView();
     
     
   };
 })();
 
 // For extends
 ClassUtils.extend(RootViewController, BaseViewController);
/**
 * It's for controlling all views render.
 * auther: handy.wang 2010-07-13
 */
RootViewController = (function(){
 
 // Constructor
 return function() {
   // For extends
   RootViewController.superClass.constructor.call(this);
   
   var self = this;
   var errorViewController = null;
   var initViewController = null;
   
   // properties
   //var ErrorViewController = new ErrorViewController();

   // Public instance methods
   this.renderViews = function() {
     self.getView().removeSubView(errorViewController.getView());
     self.getView().removeSubView(initViewController.getView());
     
     //groups.length > 0
     if (false) {
       
     } else {
       self.getView().addSubView(errorViewController.getView());
       $("#errorViewSettingsBtn").button({icons: {primary: 'ui-icon-gear'}}).click(function() {AppSettings.getInstance(self).show();})
     }
   };

   // Private instance methods
   function initView() {
     self.setView(new RootView());
     errorViewController = new ErrorViewController("No Group Found", "Please check your setting or define a group with screens first.");
     initViewController = new InitViewController();
     self.getView().addSubView(errorViewController.getView());
     self.getView().addSubView(initViewController.getView());
   }
   
   // Init jobs
   initView();
 };
})();
 
// For extends
ClassUtils.extend(RootViewController, BaseViewController);
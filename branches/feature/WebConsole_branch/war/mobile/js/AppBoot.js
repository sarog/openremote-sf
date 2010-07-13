/**
 * This javascript entry of mobile client.
 *
 * auther: handy.wang 2010-07-07
 */
$().ready(function() {
  var appBoot = AppBoot.getInstance();
});

AppBoot = (function() {
  var appBoot = null;
  
  // Constructor
  function AppBoot() {
    var self = this;
    
    // Public instance methods
    this.beginUpdate = function() {
      MessageUtils.showLoading("Rendering......");
      var updateController = new UpdateController(self);
      updateController.update();
    }
    
    // Following methods are delegate methods should defined in UpdateController.
    this.didUpdateSuccess = function() {
      MessageUtils.hideLoading();
    }
    
    this.didUpdateFail = function(error) {
      MessageUtils.hideLoading();
    }
    
    // Private instance method
    function init() {
      // Init root view
      var rootViewController = new RootViewController();
      $(rootViewController.getView().getCanvas()).insertBefore($("body").children().first());
      
      // This is for testing callout AppSettings dialog.
      $("#settings").button({icons: {primary: 'ui-icon-gear'}}).click(function() {AppSettings.getInstance(self).show();})
      
      if (CookieUtils.getCookie(Constants.CURRENT_SERVER) == null) {
        AppSettings.getInstance(self).show();
      } else {
        self.beginUpdate();
      }
      $("#welcome-content-loading").hide();
    }
    
    // Init jobs and the entrance of current client.
    init();
  }
  
  return {
    getInstance : function() {
      if (appBoot == null) {
        appBoot = new AppBoot();
      }
      return appBoot;
    }
  };
  
})();
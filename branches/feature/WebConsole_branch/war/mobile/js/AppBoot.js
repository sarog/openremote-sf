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
  var rootViewController;
  
  // Constructor
  function AppBoot() {
    var self = this;
    this.webConsoleID = Math.uuid();
    
    this.didUpdateFinished = function() {
      if (RenderDataDB.getInstance().getGroups().length > 0) {
        rootViewController.renderViews();
      } else {
        rootViewController.removeInitView();
      }
    };
    
    // Following methods are delegate methods should defined in UpdateController.
    this.didUpdateSuccess = function() {
      MessageUtils.hideLoading();
      this.didUpdateFinished();
    };
    
    this.didUpdateFail = function(error) {
      MessageUtils.hideLoading();
      this.didUpdateFinished();
      MessageUtils.showMessageDialogWithSettings("Update fail", error);
    };
    
    // Private instance method
    function init() {
      // Init root view
      rootViewController = new RootViewController(self);
      $(rootViewController.getView().getCanvas()).insertBefore($("body").children().first());
      
      // This is for callout AppSettings dialog.
      $("#errorViewSettingsBtn").button({icons: {primary: 'ui-icon-gear'}}).click(function() {AppSettings.getInstance(self).show();})
      
      self.beginUpdate();
    }
    
    // Public instance methods
    this.beginUpdate = function() {
      MessageUtils.showLoading("Rendering......");
      var updateController = new UpdateController(self);
      updateController.update();
    };
    
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
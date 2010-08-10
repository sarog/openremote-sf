/**
 * This file is the entry of the whole webconsole application.
 * This webconsole application is Javascript based and JQuery plugin based.
 *
 * This file is responsible for
 * 1) Initializes the root view of webconsole application
 * 2) Downloads panel.xml and parses it into Javascript object then stores in memory(not into cookie) of client OS.
 * 3) Renders views(such as groups, screens, labels, switches and so on).
 *
 * auther: handy.wang 2010-07-07
 */
 
/**
 * This is JQuery's method calling for launching webconsole application
 */
$().ready(function() {
  var appBoot = AppBoot.getInstance();
});

/**
 * This AppBoot class encapsulates the previous mentioned operations of 
 * Initializing root view, downloading and parsing panel.xml and rendering views.
 */
AppBoot = (function() {
  var appBoot = null;
  var rootViewController;
  
  /**
   * Constructor of class AppBoot.
   */
  function AppBoot() {
    var self = this;
    this.webConsoleID = Math.uuid();
    
    /**
     * Instance method
     */
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
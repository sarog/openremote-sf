/**
 * This file is the entry of the whole webconsole application.
 * This webconsole application is Javascript based and JQuery plugin based.
 *
 * This file is responsible for
 * 1) Initializes the root view of webconsole application
 * 2) Downloads panel.xml and parses it into Javascript object then stores in memory(not into cookie) of client OS.
 * 3) Renders views(such as groups, screens, labels, switches and so on).
 *
 * author: handy.wang 2010-07-07
 */
 
/**
 * This is JQuery's method calling for launching webconsole application
 */
$().ready(function() {
  var appBoot = AppBoot.getInstance();
});

/**
 * This class is singleton, that means the entry of current project should and must be only one.
 * This AppBoot class encapsulates the previous mentioned operations of 
 * Initializing root view, downloading and parsing panel.xml and rendering views by RootViewController and UpdateController.
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
     * Public instance method.
     * This method will be called by UpdataController delegate's two delegate methods(didUpdateSuccess and didUpdateFail) is called.
     * This method is responsible for invoking views' render if group model data existes 
     * or alerting user there is no group found with error view.
     */
    this.didUpdateFinished = function() {
      if (RenderDataDB.getInstance().getGroups().length > 0) {
        rootViewController.renderViews();
      } else {
        rootViewController.removeInitView();
      }
    };
    
    /**
     * Public instance method.
     * This is delegate method of UpdateController and called by UpdateController in condition of update successfully.
     * This method is responsible for hiding loading view of updating and then calling method didUpdateFinished of self.
     */
    this.didUpdateSuccess = function() {
      MessageUtils.hideLoading();
      this.didUpdateFinished();
    };
    
    /**
     * Public instance method
     * This is delegate method of UpdateController and called by UpdateController in condition of update fail.
     * This method is responsible for hiding loading view of updating, calling method didUpdateFinished of self
     * and showing failed messages to user.
     */
    this.didUpdateFail = function(error) {
      MessageUtils.hideLoading();
      this.didUpdateFinished();
      MessageUtils.showMessageDialogWithSettings("Update fail", error);
    };
    
    /**
     * Private instance method
     * This method is responsible for creating root view for being prepared for including all views about current webconsole,
     * and then update views with data which is from remote controller server by method beginUpdate of self.
     */
    function init() {
      // Init root view
      rootViewController = new RootViewController(self);
      $(rootViewController.getView().getCanvas()).insertBefore($("body").children().first());
      
      $("#errorViewSettingsBtn").button({icons: {primary: 'ui-icon-gear'}}).click(function() {AppSettings.getInstance(self).show();})
      
      self.beginUpdate();
    }
    
    /**
     * Public instance method
     * This method is responsible for showing updating message to users and updating by UpdateController.
     */
    this.beginUpdate = function() {
      MessageUtils.showLoading("Rendering......");
      var updateController = new UpdateController(self);
      updateController.update();
    };
    
    /**
     * Call initializing method.
     */
    init();
  }
  /**
   * This is for singleton implementation.
   */
  return {
    getInstance : function() {
      if (appBoot == null) {
        appBoot = new AppBoot();
      }
      return appBoot;
    }
  };
  
})();
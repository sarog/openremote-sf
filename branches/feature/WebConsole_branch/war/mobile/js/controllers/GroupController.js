/**
 * It's responsible for rendering group.
 *
 * author: handy.wang 2010-07-19
 */
GroupController = (function() {
  
  return function(groupParam) {
    // For extend
    GroupController.superClass.constructor.call(this);
    var self = this;
    self.paginationController = null;
    
    /**
     * Get current screen's id.
     */
    this.currentScreenID = function() {
      return self.currentScreen().id;
    };
    
    /**
     * Get current screen instance.
     */
    this.currentScreen = function() {
      return self.currentScreenViewController().screen;
    };
    
    /**
     * Get current screenViewController instance.
     */
    this.currentScreenViewController = function() {
      if (self.paginationController != null) {
        return self.paginationController.currentScreenViewController();
      } else {
        return null;
      }
    };
    
    /**
     * Judge if there is no screen view.
     */
    this.hasNoScreenView = function() {
      return (self.group.screens.length == 0);
    };
    
    /**
     * Judge if app can find the specified screen with screen id.
     */
    this.canFindScreenByID = function(screenID) {
      for (var i = 0; i < self.group.screens.length; i++) {
        var screen = self.group.screens[i];
        if (screenID == screen.id) {
          return true;
        }
      }
      return false;
    };
    
    /**
     * Start polling.
     */
    this.startPolling = function() {
      if (this.currentScreenViewController() != null) {
        this.currentScreenViewController().startPolling();
      }
    };
    
    /**
     * Stop polling.
     */
    this.stopPolling = function() {
      if (this.currentScreenViewController() != null) {
        this.currentScreenViewController().stopPolling();
      }      
    };
    
    /**
     * Switch to the specified screen with screen id.
     */
    this.switchToScreen = function(screenID) {
      return this.paginationController.switchToScreen(screenID);
    };
    
    /**
     * Switch to the previous screen view of current screen view.
     */
    this.previousScreen = function() {
      return this.paginationController.previousScreen();
    };
    
    /**
     * Switch to the next screen view of current screen view.
     */
    this.nextScreen = function() {
      return this.paginationController.nextScreen();      
    };
    
    /**
     * Show the error view about no screen found.
     */
    function showErrorView() {
      MessageUtils.hideLoading();
      self.errorViewController = new ErrorViewController("No Screen Found", "Please associate screens with this group.");
      self.setView(self.errorViewController.getView());
    }
    
    /**
     * Initializing jobs.
     */
    function init() {
      self.group = groupParam;
      
      var screens = self.group.screens;
      if (screens.length > 0) {
        self.paginationController = new PaginationController(self.group);
        self.setView(self.paginationController.getView());
        
        var currentScreenViewController = self.currentScreenViewController();
        currentScreenViewController.startPolling();
        
        MessageUtils.hideLoading();
      } else {
        showErrorView();
      }
    }

    init();
    
  };
})();

// For extend
ClassUtils.extend(GroupController, BaseViewController);
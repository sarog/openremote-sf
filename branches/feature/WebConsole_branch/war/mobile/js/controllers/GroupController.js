/**
 * It's responsible for rendering group.
 * auther: handy.wang 2010-07-19
 */
GroupController = (function() {
  
  return function(groupParam) {
    // For extend
    GroupController.superClass.constructor.call(this);
    var self = this;
    self.paginationController = null;
    
    this.currentScreenID = function() {
      return self.currentScreen().id;
    };
    
    this.currentScreen = function() {
      return self.currentScreenViewController().screen;
    };
    
    this.currentScreenViewController = function() {
      if (self.paginationController != null) {
        return self.paginationController.currentScreenViewController();
      } else {
        return null;
      }
    };
    
    this.hasNoScreenView = function() {
      return (self.group.screens.length == 0);
    };
    
    this.canFindScreenByID = function(screenID) {
      for (var i = 0; i < self.group.screens.length; i++) {
        var screen = self.group.screens[i];
        if (screenID == screen.id) {
          return true;
        }
      }
      return false;
    };
    
    this.startPolling = function() {
      if (this.currentScreenViewController() != null) {
        this.currentScreenViewController().startPolling();
      }
    };
    
    this.stopPolling = function() {
      if (this.currentScreenViewController() != null) {
        this.currentScreenViewController().stopPolling();
      }      
    };
    
    this.switchToScreen = function(screenID) {
      return this.paginationController.switchToScreen(screenID);
    };
    
    this.previousScreen = function() {
      return this.paginationController.previousScreen();
    };
    
    this.nextScreen = function() {
      return this.paginationController.nextScreen();      
    };
    
    function showErrorView() {
      MessageUtils.hideLoading();
      self.errorViewController = new ErrorViewController("No Screen Found", "Please associate screens with this group.");
      self.setView(self.errorViewController.getView());
    }
    
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
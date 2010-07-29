/**
 * It's responsible for controlling render screenviews.
 * auther: handy.wang 2010-07-19
 */
PaginationController = (function() {
  
  // Constructor
  return function(screensParam) {
    // For extend
    PaginationController.superClass.constructor.call(this);
    var self = this;
    self.screenViewControllers = [];
    self.currentScreenIndex = 0;
    
    this.previousScreen = function() {
      if (self.currentScreenIndex > 0) {
        self.currentScreenViewController().stopPolling();
        self.currentScreenIndex -= 1;
        self.paginationView.updateView(self.screenViewControllers[self.currentScreenIndex].getView());
        self.currentScreenViewController().startPolling();
      }
    };
    
    this.nextScreen = function() {
      if (self.currentScreenIndex < (self.screenViewControllers.length - 1)) {
        self.currentScreenViewController().stopPolling();
        self.currentScreenIndex += 1;
        self.paginationView.updateView(self.screenViewControllers[self.currentScreenIndex].getView());
        self.currentScreenViewController().startPolling();
      }
    };
    
    this.switchToScreen = function(screenID) {
      var index = -1;
      for (var i = 0; i < self.screenViewControllers.length; i++) {
        var svc = self.screenViewControllers[i];
        if (svc.screen.id == screenID) {
          index = i;
          break;
        }
      }
      
      if (index != -1) {
        self.currentScreenViewController().stopPolling();
        self.currentScreenIndex = index;
        self.paginationView.updateView(self.screenViewControllers[self.currentScreenIndex].getView());
        self.currentScreenViewController().startPolling();
        return true;
      } else {
        return false;
      }
    };
    
    this.currentScreenViewController = function() {
      return self.screenViewControllers[self.currentScreenIndex];
    };
    
    function init() {
      for (var index = 0; index < screensParam.length; index++) {
        var screenViewController = new ScreenViewController(screensParam[index]);
        self.screenViewControllers[self.screenViewControllers.length] = screenViewController;
      }

      self.currentScreenIndex = 0;
      
      var screenIDWhenQuit = null;
      var lastFootPrint = CookieUtils.getCookie(Constants.LAST_FOOT_PRINT);
      if (lastFootPrint != null && lastFootPrint != undefined) {
        screenIDWhenQuit = lastFootPrint.screenID;
      }
      
      if (screenIDWhenQuit != null && screenIDWhenQuit != "0" && screenIDWhenQuit != 0 && screenIDWhenQuit != undefined) {
        for (var i = 0; i < self.screenViewControllers.length; i++) {
          var tempScreenViewController = self.screenViewControllers[i];
          if (tempScreenViewController.screen.id == screenIDWhenQuit) {
            self.currentScreenIndex = i;
            break;
          }
        }
      }
      
      self.paginationView = new PaginationView(self.screenViewControllers[self.currentScreenIndex].getView(), self);
      self.setView(self.paginationView);

    }
    
    init();
  };
})();

// For extend
ClassUtils.extend(PaginationController, BaseViewController);
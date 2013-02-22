/**
 * It's responsible for controlling render screenviews.
 *
 * author: handy.wang 2010-07-19
 */
PaginationController = (function() {
  
  // Constructor
  return function(groupParam) {
    // For extend
    PaginationController.superClass.constructor.call(this);
    var self = this;
    self.group = groupParam;
    self.screens = groupParam.screens;
    self.screenViewControllers = [];
    self.currentScreenIndex = 0;
    
    /**
     * Switch to previous screen view of current view.
     */
    this.previousScreen = function() {
      if (self.currentScreenIndex > 0) {
        self.currentScreenViewController().stopPolling();
        self.currentScreenIndex -= 1;
        self.paginationView.updateView(self.screenViewControllers[self.currentScreenIndex].getView());
        self.currentScreenViewController().startPolling();
        saveLastGroupIdAndScreenId();
      }
    };
    
    /**
     * Switch to next screen view of current view.
     */
    this.nextScreen = function() {
      if (self.currentScreenIndex < (self.screenViewControllers.length - 1)) {
        self.currentScreenViewController().stopPolling();
        self.currentScreenIndex += 1;
        self.paginationView.updateView(self.screenViewControllers[self.currentScreenIndex].getView());
        self.currentScreenViewController().startPolling();
        saveLastGroupIdAndScreenId();
      }
    };
    
    /**
     * Switch to the specified screen view with screen id.
     */
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
        
        /* There will be two pollings running, if don't call window.setTimeout for pausing a moment.
         * This will ocurr in the situation of navigation with a navigation data which contains groupID and screenID.
         * Because method navigateToGroupAndScreen of RootViewController will navigate to a group with groupID firstly,
         * then judges screenID is valid and navigates to screen whose id is screenID. 
         * So, RootViewController stop polling of navigated group before navigating to that screen,
         * then start polling of screen. However, the action of stoping polling wasnt't successful, so main thread must call "window.setTimeout"
         * for catching some time for stop polling of previous navigated group .
         * Obviously, one of two running pollings is started by toGroup in method navigateToGroupAndScreen and
         * the another one is started by switchScreen in method navigateToGroupAndScreen.
         *
         * The time set must be equals or great than 100 milliseconds tested by me.
         */
        // window.setTimeout(function() {
        //   self.currentScreenIndex = index;
        //   self.paginationView.updateView(self.screenViewControllers[self.currentScreenIndex].getView());
        //   self.currentScreenViewController().startPolling();
        // }, 200);
        self.currentScreenIndex = index;
        self.paginationView.updateView(self.screenViewControllers[self.currentScreenIndex].getView());
        self.currentScreenViewController().startPolling();
        return true;
      } else {
        return false;
      }
    };
    
    /**
     * Get current screenViewController instance.
     */
    this.currentScreenViewController = function() {
      return self.screenViewControllers[self.currentScreenIndex];
    };
    
    /**
     * Initializing jobs
     */
    function init() {
      for (var index = 0; index < self.screens.length; index++) {
        var screenViewController = new ScreenViewController(self.screens[index]);
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
      self.paginationView.renderMenuItemListView(self.group);
      
      self.setView(self.paginationView);
      saveLastGroupIdAndScreenId();
    }
    
    /**
     * Save the last group and screen into cookie when switch screen views in pagination view.
     */
    function saveLastGroupIdAndScreenId() {
      var lastFootPrint = {};
      lastFootPrint.groupID = self.group.id;
      lastFootPrint.screenID = self.currentScreenViewController().screen.id;
      CookieUtils.setCookie(Constants.LAST_FOOT_PRINT, lastFootPrint);
    }
    
    init();
  };
})();

// For extend
ClassUtils.extend(PaginationController, BaseViewController);
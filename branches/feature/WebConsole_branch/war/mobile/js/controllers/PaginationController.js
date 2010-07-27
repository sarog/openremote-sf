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
        self.paginationView.updateView(self.screenViewControllers[self.currentScreenIndex - 1].getView());
        self.currentScreenIndex -= 1;
      }
    };
    
    this.nextScreen = function() {
      if (self.currentScreenIndex < (self.screenViewControllers.length - 1)) {
        self.paginationView.updateView(self.screenViewControllers[self.currentScreenIndex + 1].getView());
        self.currentScreenIndex += 1;        
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
      self.paginationView = new PaginationView(self.screenViewControllers[0].getView(), self);
      self.setView(self.paginationView);
      self.currentScreenIndex = 0;
    }
    
    init();
  };
})();

// For extend
ClassUtils.extend(PaginationController, BaseViewController);
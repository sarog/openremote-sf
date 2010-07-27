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
    
    function showErrorView() {
      MessageUtils.hideLoading();
      self.errorViewController = new ErrorViewController("No Screen Found", "Please associate screens with this group.");
      self.setView(self.errorViewController.getView());
    }
    
    function init() {
      self.group = groupParam;
      
      var screens = self.group.screens;
      if (screens.length > 0) {
        self.paginationController = new PaginationController(self.group.screens);
        self.setView(self.paginationController.getView());
        
        var currentScreenViewController = self.paginationController.currentScreenViewController();
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
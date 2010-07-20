/**
 * It's responsible for rendering screen.
 * auther: handy.wang 2010-07-19
 */
ScreenViewController = (function() {
  
  return function(screenParam) {

    // For extend
    ScreenViewController.superClass.constructor.call(this);
    var self = this;
        
    function init() {
      self.screen = screenParam;
      self.setView(new ScreenView(self.screen));
    }
    
    init();
    
  };
})();

ClassUtils.extend(ScreenViewController, BaseViewController);


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
      self.pollingSensorIDs = self.screen.getPollingSensorIDs();
      self.pollingHelper = null;
      if (self.pollingSensorIDs.length > 0) {
        self.pollingHelper = new PollingHelper(self.pollingSensorIDs);
      }
    }
    
    this.startPolling = function() {
      self.pollingHelper.requestCurrentStatusAndStartPolling();
    };
    
    this.stopPolling = function() {
      self.pollingHelper.cancelPolling();
    };
    
    init();
    
  };
})();

ClassUtils.extend(ScreenViewController, BaseViewController);


/**
 * It's responsible for rendering screen.
 *
 * author: handy.wang 2010-07-19
 */
ScreenViewController = (function() {
  
  return function(screenParam) {

    // For extend
    ScreenViewController.superClass.constructor.call(this);
    var self = this;
        
    /**
     * Initializing jobs
     */
    function init() {
      self.screen = screenParam;
      self.setView(new ScreenView(self.screen));
      self.pollingSensorIDs = self.screen.getPollingSensorIDs();
      self.pollingHelper = null;
      if (self.pollingSensorIDs.length > 0) {
        self.pollingHelper = new PollingHelper(self.pollingSensorIDs);
      }
    }
    
    /**
     * Start polling for screenview's sensors.
     */
    this.startPolling = function() {
      if (self.pollingHelper != null) {
        self.pollingHelper.requestCurrentStatusAndStartPolling();
      }
    };
    
    /**
     * Stop polling of current screen view.
     */
    this.stopPolling = function() {
      if (self.pollingHelper != null) {
        self.pollingHelper.cancelPolling();
      }
    };
    
    init();
    
  };
})();

ClassUtils.extend(ScreenViewController, BaseViewController);


/**
 * It's error controller for show some error info.
 * author: handy.wang 2010-07-13
 */
ErrorViewController = (function(){

  // Constructor
  return function(titleParam, messageParam) {
    ErrorViewController.superClass.constructor.call(this);
    
    var self = this;
    
    // Private instance variables
    
    // Private instance methods
    function init() {
      self.setView(new ErrorView(titleParam, messageParam));
    }
    
    // Init jobs
    init();
  }
})();

ClassUtils.extend(ErrorViewController, BaseViewController);
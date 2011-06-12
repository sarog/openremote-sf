/**
 * It's for controlling init view render.
 *
 * author: handy.wang 2010-07-14
 */
InitViewController = (function() {

  // Constructor
  return function() {
    // For extend
    InitViewController.superClass.constructor.call(this);
    var self = this;
    
    function initView() {
      self.setView(new InitView());
    }
    
    initView();
  }
})();

ClassUtils.extend(InitViewController, BaseViewController);
/**
 * It's super class of related view controller.
 *
 * author: handy.wang 2010-07-13
 */
BaseViewController = (function(){
  
  // Constructor
  var BaseViewController = function() {
    var view = new BaseView();
    
    // Public instance methods    
    this.setView = function(viewParam) {
      view = viewParam;
    };
    
    this.getView = function() {
      return view;
    };
    
    // Private instance methods
    function initView() {
    }
    
  };

  return BaseViewController;
  
})();


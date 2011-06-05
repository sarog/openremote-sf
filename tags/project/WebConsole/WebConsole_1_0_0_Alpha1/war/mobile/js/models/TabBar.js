/**
 * This class is for storing tabbar data.
 * author: handy.wang 2010-08-02
 */
TabBar = (function() {
  
  return function(jsonParser, properties) {
    TabBar.superClass.constructor.call(this, jsonParser, properties);
    var self = this;
    self.items = [];
    
    // Delegate method of JSONParser.
    this.didParse = function(jsonParser, nodeName, properties) {
      if (nodeName == Constants.TAB_BAR_ITEM) {
        self.items[self.items.length] = new TabBarItem(jsonParser, properties);
      }
    };
    
    /**
     * Initializing jobs.
     */
    function init() {
      self.node_name = Constants.TAB_BAR;
      jsonParser.setDelegate(self);
    }
    
    init();
  }
})();

ClassUtils.extend(TabBar, BaseModel);
/**
 * This class is for storing tabbar item data.
 * author: handy.wang 2010-08-02
 */
TabBarItem = (function() {
  
  return function(jsonParser, properties) {
    TabBarItem.superClass.constructor.call(this, jsonParser, properties);
    var self = this;
    self.navigate = null;
    self.image = null;
    
    // Delegate method of JSONParser.
    this.didParse = function(jsonParser, nodeName, properties) {
      switch (nodeName) {
        case Constants.NAVIGATE :
          self.navigate = new Navigate(jsonParser, properties); 
          break;
        case Constants.IMAGE:
          self.image = new Image(jsonParser, properties);
          break;
      }
    };
    
    /**
     * Initializing jobs.
     */
    function init() {
      self.node_name = Constants.TAB_BAR_ITEM;
      self.name = properties[Constants.NAME];
      jsonParser.setDelegate(self);
    }
    
    init();
  }
})();

ClassUtils.extend(TabBarItem, BaseModel);
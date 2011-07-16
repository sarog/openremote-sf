/**
 * This class is for storing group data.
 * author: handy.wang 2010-07-16
 */
Group = (function(){
  
  return function(jsonParser, properties) {
    // For extend
    Group.superClass.constructor.call(this, jsonParser, properties);
    var self = this;
        
    self.screens = [];
    self.tabBar = null;
    
    // Delegate method of JSONParser.
    this.didParse = function(jsonParser, nodeName, properties) {
      if (Constants.INCLUDE == nodeName && Constants.SCREEN == properties[Constants.TYPE]) {
        var screenRefID = properties[Constants.REF];
        var cachedScreen = RenderDataDB.getInstance().findScreenByID(screenRefID);
        this.screens[this.screens.length] = cachedScreen;
      } else if (nodeName == Constants.TAB_BAR) {
        self.tabBar = new TabBar(jsonParser, properties);
      }
    }
    
    // Private methods
    /**
     * Initializing jobs.
     */
    function init(jsonParser, properties) {
      self.node_name = Constants.GROUP;
      self.id = properties[Constants.ID];
      self.name = properties[Constants.NAME];
      
      jsonParser.setDelegate(self);
    }
    
    // Init jobs
    init(jsonParser, properties);
    
  };
  
})();

ClassUtils.extend(Group, BaseModel);
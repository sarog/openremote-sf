/**
 * This class is for storing switch data.
 * auther: handy.wang 2010-07-26
 */
Switch = (function() {
  
  return function(jsonParser, properties) {
    Switch.superClass.constructor.call(this, jsonParser, properties);
    var self = this;

    function init() {
      self.node_name = Constants.SWITCH;
      self.id = properties[Constants.ID];
      
      jsonParser.setDelegate(self);
    }
    
    init();
    
  }
})();

ClassUtils.extend(Switch, SensoryComponent);
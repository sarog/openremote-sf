/**
 * This class is for storing label data.
 * author: handy.wang 2010-08-03
 */
Label = (function() {
  
  return function(jsonParser, properties) {
    Label.superClass.constructor.call(this, jsonParser, properties);
    var self = this;
    
    /**
     * Initializing jobs.
     */
    function init() {
      self.node_name = Constants.LABEL;
      self.id = properties[Constants.ID];
      self.fontSize = properties[Constants.FONT_SIZE];
      self.color = properties[Constants.COLOR];
      self.text = properties[Constants.TEXT];
      
      jsonParser.setDelegate(self);
    }
    
    init();
    
  }
})();

ClassUtils.extend(Label, SensoryComponent);
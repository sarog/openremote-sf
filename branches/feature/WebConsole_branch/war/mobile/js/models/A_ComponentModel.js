/**
 * This class is for building component models depending on jsonParser, nodeName and properties.
 * author: handy.wang 2010-07-22
 */
ComponentModel = (function(){
  return function() {}
})();

/**
 * Factory method for build all kinds of model instances related to webconsole identified by node name.
 */
ComponentModel.build = function(jsonParser, nodeName, properties) {
  switch(nodeName) {
    case Constants.LABEL :
      var label = new Label(jsonParser, properties);
      RenderDataDB.getInstance().addLabel(label);
      return label;
    case Constants.IMAGE :
      return new Image(jsonParser, properties);
    default:
      return ControlModel.build(jsonParser, nodeName, properties);
  }
};
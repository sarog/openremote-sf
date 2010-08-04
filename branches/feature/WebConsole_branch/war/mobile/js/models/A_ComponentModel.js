/**
 * This class is for building component models depending on jsonParser, nodeName and properties.
 * auther: handy.wang 2010-07-22
 */
ComponentModel = (function(){
  return function() {}
})();

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
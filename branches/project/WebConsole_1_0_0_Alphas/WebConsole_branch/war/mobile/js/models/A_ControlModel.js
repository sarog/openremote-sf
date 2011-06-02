/**
 * This class is for building control models depending on jsonParser, nodeName and properties.
 * author: handy.wang 2010-07-22
 */
ControlModel = (function() {
  return function() {}
})();

/**
 * Factory method for build all kinds of control model instances related to webconsole identified by node name.
 */
ControlModel.build = function(jsonParser, nodeName, properties) {
  switch(nodeName) {
    case Constants.BUTTON :
      return new Button(jsonParser, properties);
    case Constants.SWITCH :
      return new Switch(jsonParser, properties);
    case Constants.SLIDER :
      return new Slider(jsonParser, properties);
    default:
      return null;
  }
};
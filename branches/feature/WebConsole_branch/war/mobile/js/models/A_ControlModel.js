/**
 * This class is for building control models depending on jsonParser, nodeName and properties.
 * auther: handy.wang 2010-07-22
 */
ControlModel = (function() {
  return function() {}
})();

ControlModel.build = function(jsonParser, nodeName, properties) {
  switch(nodeName) {
    case Constants.BUTTON :
      return new Button(jsonParser, properties);
    case Constants.SWITCH :
      return new Switch(jsonParser, properties);
    case Constants.SLIDER :
      return new BaseModel(jsonParser, properties);//new Slider(jsonParser, properties);
    default:
      return new BaseModel(jsonParser, properties);
  }
};
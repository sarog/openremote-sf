/**
 * This class is for building component views depending on component model data and size.
 * auther: handy.wang 2010-07-22
 */
ComponentView = (function() {
  
  return function(componentModelParam, sizeParam) {
    ComponentView.superClass.constructor.call(this);
    var self = this;
    self.component = componentModelParam;
    self.size = sizeParam;
    
    this.initView = function() {
      // throw new Error("The method initView defined in ComponentView must be rewrited in subclasses.");
    }
    
    self.initView();
  }

})();

ClassUtils.extend(ComponentView, BaseView);

ComponentView.build = function(componentModelParam, sizeParam) {
  switch(componentModelParam.node_name) {
    case Constants.LABEL :
      return new ComponentView(componentModelParam, sizeParam);//new LabelView(componentModelParam, sizeParam);
    case Constants.IMAGE :
      return new ComponentView(componentModelParam, sizeParam);//new ImageView(componentModelParam, sizeParam);
    default:
      return ControlView.build(componentModelParam, sizeParam);//ControlView.build(componentModelParam, sizeParam);
  }
};
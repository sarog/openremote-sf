/**
 * This class is for building component views depending on component model data and size.
 * author: handy.wang 2010-07-22
 */
ComponentView = (function() {
  
  return function(componentModelParam, sizeParam) {
    var self = this;
    
    /**
     * This method must be overwritten in subclasses.
     * This method must be defined before calling superClass's construtor whatever in current class or sub classes.
     */
    this.initView = this.initView || function() {
      // throw new Error("The method initView defined in ComponentView must be override in subclasses.");
    }
    
    // Super class's constructor calling
    ComponentView.superClass.constructor.call(this);
    
    self.component = componentModelParam;
    self.size = sizeParam;

    self.initView();
  }

})();

ClassUtils.extend(ComponentView, BaseView);

ComponentView.build = function(componentModelParam, sizeParam) {
  switch(componentModelParam.node_name) {
    case Constants.LABEL :
      return new LabelView(componentModelParam, sizeParam);
    case Constants.IMAGE :
      return new ImageView(componentModelParam, sizeParam);
    default:
      return ControlView.build(componentModelParam, sizeParam);//ControlView.build(componentModelParam, sizeParam);
  }
};
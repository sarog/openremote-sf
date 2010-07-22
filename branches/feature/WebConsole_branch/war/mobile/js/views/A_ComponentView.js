/**
 * This class is for building component views depending on component model data.
 * auther: handy.wang 2010-07-22
 */
ComponentView = (function() {
  return {
    build : function(componentModelParam, sizeParam) {
      switch(componentModelParam.node_name) {
        case Constants.LABEL :
          return new BaseView();//new LabelView(componentModelParam, sizeParam);
        case Constants.IMAGE :
          return new BaseView();//new ImageView(componentModelParam, sizeParam);
        default:
          return new BaseView();//ControlView.build(componentModelParam, sizeParam);
      }
    }
  }
})();
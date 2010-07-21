/**
 * This class is super class of gridLayoutContainer and absoluteLayoutContainer.
 * auther: handy.wang 2010-07-21
 */
LayoutModel = (function() {
 
 return function(jsonParser, properties) {
   // For extend
   LayoutModel.superClass.constructor.call(this, jsonParser, properties);
   var self = this;
   
   // Private methods
   function init() {
     self.node_name = Constants.LAYOUT_MODEL;
     self.left = properties[Constants.LEFT];
     self.top = properties[Constants.TOP];
     self.width = properties[Constants.WIDTH];
     self.height = properties[Constants.HEIGHT];
   }

   // Init jobs
   init();
 }
})();

ClassUtils.extend(LayoutModel, BaseModel);
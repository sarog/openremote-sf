/**
 * It's responsible for JS OO.
 * auther: handy.wang 2010-07-13
 */
ClassUtils = (function() {
  
  return {
    // For JS OO extend function.
    extend : function (SubClass, SuperClass) {
        var F = function(){};
        F.prototype = SuperClass.prototype;
        SubClass.prototype = new F();
        SubClass.prototype.constructor = SubClass;

        SubClass.superClass = SuperClass.prototype;
        if (SuperClass.prototype.constructor == Object.prototype.constructor) {
            SuperClass.prototype.constructor = SuperClass;
        }
    }
  };
  
})();
 

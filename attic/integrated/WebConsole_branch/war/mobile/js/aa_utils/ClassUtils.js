/**
 * This class is for Class style extends of OOP like JAVA.
 * This implementation referenced Book named "Pro Javascript Design Patterns".
 *
 * author: handy.wang 2010-07-13
 */
ClassUtils = (function() {
  
  return {
    /**
     * This static method with two parameters.
     * The first parameter SubClass is the class which want to extend from the second parameter named SuperClass,
     * so, first parameter class object extends from the second parameter class object after calling this method.
     * However, Please note that this method must use with another code segment in Subclass.
     * The code segment is something like "SubClass.superClass.constructor.call(this, [superClassParameter1, ...])".
     *
     * So, if you are confused now how to use it, I will give some steps with followings:
     * 1) Call the code segment like "SubClass.superClass.constructor.call(this, [superClassParameter1, ...])" in the 
     *    first line of inner subclass's constructor. However, You must write the methods to be overrided
     *    before the code segment if the subclass must override some methods of super class's. You can refrence class ButtonView,
     *    the method initView is overridden by ButtonView.
     * 2) It's not enough to only call that code segement, you must need the method extend of current class ClassUtil provided.
     *    So, It's the point. Develoers write the calling "extend" method at the bottom of class file and out of class definition.
     *    The calling code is like this: ClassUtils.extend(SubClassName, SuperClassName);
     *
     * Finally, the detail of method "extend" isn't important and is a little complex, 
     * so, I won't explain it in detail and you only need know is how to use it.
     */
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
 

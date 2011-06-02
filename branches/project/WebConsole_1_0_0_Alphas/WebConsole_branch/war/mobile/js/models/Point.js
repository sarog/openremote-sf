/**
 * This class is for storing point data including left and top.
 *
 * author: handy.wang 2010-07-22
 */
Point = (function() {
  
  return function(leftParam, topParam) {
    this.left = leftParam;
    this.top = topParam;
  }
})();
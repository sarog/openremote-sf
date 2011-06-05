/**
 * This class is for storing size data including width and height.
 * author: handy.wang 2010-07-22
 */
Size = (function() {
  
  return function(widthParam, heightParam) {
    this.width = widthParam;
    this.height = heightParam;
  }
})();
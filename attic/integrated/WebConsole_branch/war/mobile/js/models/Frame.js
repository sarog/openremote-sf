/**
 * This class is for storing frame data including left, top, width and height.
 *
 * author: handy.wang 2010-07-22
 */
Frame = (function() {
  
  return function(leftParam, topParam, widthParam, heightParam) {
    this.origin = new Point(leftParam, topParam);
    this.size = new Size(widthParam, heightParam);
  }
})();
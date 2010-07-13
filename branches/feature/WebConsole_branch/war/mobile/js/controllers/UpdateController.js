/**
 * It's responsible for loading panel data and parsing it.
 * auther: handy.wang 2010-07-13
 */
UpdateController = (function() {
  
  return function(delegateParam) {
    var delegate = delegateParam;
    
    this.update = function() {      
      delegate.didUpdateSuccess();
      // var panelJSONData = downloadPanelJSONData();
      // PanelParser.parse(panelJSONData);
      // // It's responsible for downloading panel json data.
      // function downloadPanelJSONData() {
      // 
      // }
    };
  }
})();
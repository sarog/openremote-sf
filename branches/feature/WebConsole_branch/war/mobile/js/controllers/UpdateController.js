/**
 * It's responsible for loading panel data, parsing it and notify the AppBoot to call rootViewController to render groups, screens and so on.
 * auther: handy.wang 2010-07-13
 */
UpdateController = (function() {
  
  return function(delegateParam) {
    
    var delegate = delegateParam;
    var panelWorkerController = new PanelWorkerController(this);
    
    this.update = function() {
      panelWorkerController.downloadJSONDataWithURL();
      
      //delegate.didUpdateSuccess();
      
      // var panelJSONData = downloadPanelJSONData();
      // PanelParser.parse(panelJSONData);
      // // It's responsible for downloading panel json data.
      // function downloadPanelJSONData() {
      // 
      // }
    };
    
    this.didUpdateSuccess = function() {
      delegate.didUpdateSuccess();
    };
    
    this.didUpdateFail = function(error) {
      delegate.didUpdateFail(error);
    };
    
  }
})();
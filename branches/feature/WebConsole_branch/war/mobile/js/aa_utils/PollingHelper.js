/**
 * This class is for storing sensor data.
 * auther: handy.wang 2010-07-27
 */
PollingHelper = (function() {
  var STATUS_ERROR_MSG_TITLE = "Status query fail";
  var POLLING_ERROR_MSG_TITLE = "Polling fail";
  
  return function(sensorIDsParam) {
    var self = this;
    
    this.requestCurrentStatusAndStartPolling = function() {
      if (this.isPollingRunning == false) {
        var statusURL = ConnectionUtils.getStatusURL(this.sensorIDs);
        if (statusURL == null || statusURL == undefined) {
          MessageUtils.showMessageDialogWithSettings(STATUS_ERROR_MSG_TITLE, "Wrong status request URL.");
        }
        this.isPollingRunning = true;
        ConnectionUtils.sendNormalRequest(statusURL, self);
      }
    };
    
    function doPolling() {
      if (this.isPollingRunning == false) {
        var pollingURL = ConnectionUtils.getPollingURL(this.sensorIDs);
        if (pollingURL == null || pollingURL == undefined) {
          MessageUtils.showMessageDialogWithSettings(POLLING_ERROR_MSG_TITLE, "Wrong polling URL.");
        }
        this.isPollingRunning = true;
        ConnectionUtils.sendNormalRequest(pollingURL, self);
      }
    }
    
    // Delegate methods should be defined in ConnectionUtils.
    this.didFeedBackWithRequest = function(data, textStatus, XMLHttpRequest) {
      if (data != null && data != undefined) {
        var error = data.error;
        if (error != null && error != undefined && error.code != Constants.HTTP_SUCCESS_CODE) {
          MessageUtils.showMessageDialogWithSettings(STATUS_ERROR_MSG_TITLE, error.message);
        } else {
          // TODO: parse polling data and polling.
          var jsonParser = new JSONParser(data, self);
          self.statusMap = {};
          jsonParser.startParse();
        }
      } else {
        MessageUtils.showMessageDialogWithSettings(STATUS_ERROR_MSG_TITLE, Constants.UNKNOWN_ERROR_MESSAGE);
      }
    };
    
    // Delegate methods of JSONParser    
    this.didParse = function(jsonParser, nodeName, properties) {
      if (nodeName == Constants.STATUS) {
        var id = properties[Constants.ID];
        var value = properties[Constants.STATUS_VALUE];
        self.statusMap[id] = value;
      }
    };
    
    this.didParseFinished = function() {
      var isGotStatus = false;
      for (var key in self.statusMap) {
        isGotStatus = true;
        break;
      }
      if (isGotStatus == false) {
        MessageUtils.showMessageDialogWithSettings(STATUS_ERROR_MSG_TITLE, "No status was parsed.");
      } else {
        window.statusChangeEvent.fire(self.statusMap);
      }
    };
    
    function init() {
      self.isPollingRunning = false;
      self.sensorIDs = sensorIDsParam;
    }
    
    init();
  }
})();
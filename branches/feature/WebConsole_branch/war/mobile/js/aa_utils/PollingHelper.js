/**
 * This class is for storing sensor data.
 * auther: handy.wang 2010-07-27
 */
PollingHelper = (function() {
  var STATUS_ERROR_MSG_TITLE = "Status query fail";
  var POLLING_ERROR_MSG_TITLE = "Polling fail";
  
  return function(sensorIDsParam) {
    var self = this;
    
    this.cancelPolling = function() {
      self.isPollingRunning = false;
      if(navigator.appName == "Microsoft Internet Explorer") {
        window.document.execCommand('Stop');
      } else {
        window.stop();
      }
    };
    
    this.requestCurrentStatusAndStartPolling = function() {
      if (this.isPollingRunning == false) {
        var statusURL = ConnectionUtils.getStatusURL(this.sensorIDs);
        if (statusURL == null || statusURL == undefined) {
          MessageUtils.showMessageDialogWithSettings(STATUS_ERROR_MSG_TITLE, "Wrong status request URL.");
        }
        self.isPollingRunning = true;
        self.queryStatusStep = true;
        ConnectionUtils.sendJSONPRequest(statusURL, self);
      }
    };
    
    function doPolling() {
      if (self.isPollingRunning == false) {
        return;
      }
      self.queryStatusStep = false;
      var pollingURL = ConnectionUtils.getPollingURL(self.sensorIDs);
      if (pollingURL == null || pollingURL == undefined) {
        MessageUtils.showMessageDialogWithSettings(POLLING_ERROR_MSG_TITLE, "Wrong polling URL.");
      }
      ConnectionUtils.sendJSONPRequest(pollingURL, self);
    }
    
    // Delegate methods should be defined in ConnectionUtils.
    this.didRequestSuccess = function(data, textStatus) {
      if (data != null && data != undefined) {
        var error = data.error;
        if (error != null && error != undefined) {
          self.handleServerError(error);
        } else {
          var jsonParser = new JSONParser(data, self);
          self.statusMap = {};
          jsonParser.startParse();
          doPolling();
        }
      } else {
        MessageUtils.showMessageDialogWithSettings(STATUS_ERROR_MSG_TITLE, Constants.UNKNOWN_ERROR_MESSAGE);
      }
    };
    
    this.handleServerError = function(error) {
      var statusCode = error.code;
      if (statusCode != Constants.HTTP_SUCCESS_CODE) {
        switch (statusCode) {
          case Constants.TIME_OUT:
            doPolling();
            return;
          case Constants.CONTROLLER_CONFIG_CHANGED:
            return;
        }
        self.isPollingRunning = false;
        if (self.queryStatusStep == true) {
          MessageUtils.showMessageDialogWithSettings(STATUS_ERROR_MSG_TITLE, error.message);
        } else {
          MessageUtils.showMessageDialogWithSettings(POLLING_ERROR_MSG_TITLE, error.message);
        }
      }
    };
    
    // For dealing network error and illed json data.
    this.didRequestError = function(xOptions, textStatus) {
      if (self.isPollingRunning == false) {
        return;
      }
      if (self.queryStatusStep == true) {
        MessageUtils.showMessageDialogWithSettings(STATUS_ERROR_MSG_TITLE, "Failed to query status.");
      } else {
        MessageUtils.showMessageDialogWithSettings(POLLING_ERROR_MSG_TITLE, "Failed to polling status.");
      }
    };
    
    // Delegate methods of JSONParser    
    this.didParse = function(jsonParser, nodeName, properties) {
      if (nodeName == Constants.STATUS) {
        self.lastID = properties[Constants.ID];
        var value = properties[Constants.STATUS_VALUE];
        self.statusMap[self.lastID] = value;
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
        var eventType = Constants.STATUS_CHANGE_EVENT + self.lastID;
        NotificationCenter.getInstance().postNotification(eventType, self.statusMap);
      }
    };
    
    function init() {
      self.isPollingRunning = false;
      self.sensorIDs = sensorIDsParam;
    }
    
    init();
  }
})();
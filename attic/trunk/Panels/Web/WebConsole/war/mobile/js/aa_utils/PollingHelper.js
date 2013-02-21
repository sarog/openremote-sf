/**
 * This class is for polling with sensor data and posting notification of status change.
 *
 * author: handy.wang 2010-07-27
 */
PollingHelper = (function() {
  var STATUS_ERROR_MSG_TITLE = "Status query fail";
  var POLLING_ERROR_MSG_TITLE = "Polling fail";
  
  /**
   * Constructor
   */
  return function(sensorIDsParam) {
    var self = this;
    
    /**
     * Cancle polling request. So the polling request which is waiting for status change will be cancled
     * and webconsole won't deal the status although some status changes after polling is cancled.
     */
    this.cancelPolling = function() {
      self.isPollingRunning = false;
      if(navigator.appName == "Microsoft Internet Explorer") {
        window.document.execCommand('Stop');
      } else {
        window.stop();
      }
    };
    
    /**
     * Query the status of devices directly for initializing the status of sensory components in screen view.
     */
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
    
    /**
     * Send polling request to controller server with polling url which contains sensor ids.
     */
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
    
    /**
     * This is delegate method of ConnectionUtils and invoked by ConnectionUtils on condition of ConnectionUtils goes well.
     */
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
        // MessageUtils.showMessageDialogWithSettings(STATUS_ERROR_MSG_TITLE, Constants.UNKNOWN_ERROR_MESSAGE);
        RoundRobinUtils.getInstance().switchControllerServer();
      }
    };
    
    /**
     * This is responsible for handle errors from controller server passed by previous delegate method didRequestSuccess.
     */
    this.handleServerError = function(error) {
      var statusCode = error.code;
      if (statusCode != Constants.HTTP_SUCCESS_CODE) {
        switch (statusCode) {
          case Constants.TIME_OUT:
            doPolling();
            return;
          case Constants.CONTROLLER_CONFIG_CHANGED:
            NotificationCenter.getInstance().postNotification(Constants.REFRESH_VIEW_NOTIFICATION, null);
            return;
          case Constants.UNAUTHORIZED:
            self.isPollingRunning = false;
            if (self.queryStatusStep == true) {
              MessageUtils.showMessageDialogWithSettings(STATUS_ERROR_MSG_TITLE, error.message);
            } else {
              MessageUtils.showMessageDialogWithSettings(POLLING_ERROR_MSG_TITLE, error.message);
            }
            return;
        }
        self.isPollingRunning = false;
        // if (self.queryStatusStep == true) {
        //   MessageUtils.showMessageDialogWithSettings(STATUS_ERROR_MSG_TITLE, error.message);
        // } else {
        //   MessageUtils.showMessageDialogWithSettings(POLLING_ERROR_MSG_TITLE, error.message);
        // }
        RoundRobinUtils.getInstance().switchControllerServer();
      }
    };
    
    /**
     * This is delegate method of ConnectionUtils and invoked by ConnectionUtils on condition of error occurs .
     */
    this.didRequestError = function(xOptions, textStatus) {
      if (self.isPollingRunning == false) {
        return;
      }
      // if (self.queryStatusStep == true) {
      //   MessageUtils.showMessageDialogWithSettings(STATUS_ERROR_MSG_TITLE, "Failed to query status.");
      // } else {
      //   MessageUtils.showMessageDialogWithSettings(POLLING_ERROR_MSG_TITLE, "Failed to polling status.");
      // }
      RoundRobinUtils.getInstance().switchControllerServer();
    };
    
   /**
    * This is delegate method of JSONParser and will be called during parsing process.
    */
    this.didParse = function(jsonParser, nodeName, properties) {
      if (nodeName == Constants.STATUS) {
        self.lastID = properties[Constants.ID];
        var value = properties[Constants.STATUS_VALUE];
        self.statusMap[self.lastID] = value;
      }
    };
    
    /**
     * This is delegate method of JSONParser and is called after parsing process finish.
     */
    this.didParseFinished = function() {
      var isGotStatus = false;
      for (var key in self.statusMap) {
        isGotStatus = true;
        break;
      }
      if (isGotStatus == false) {
        MessageUtils.showMessageDialogWithSettings(STATUS_ERROR_MSG_TITLE, "No status was parsed.");
      } else {
        var notificationType = Constants.STATUS_CHANGE_NOTIFICATION + self.lastID;
        NotificationCenter.getInstance().postNotification(notificationType, self.statusMap);
      }
    };
    
    /**
     * Initializing method.
     */
    function init() {
      self.isPollingRunning = false;
      self.sensorIDs = sensorIDsParam;
    }
    
    /**
     * Call initializing method.
     */
    init();
  }
})();
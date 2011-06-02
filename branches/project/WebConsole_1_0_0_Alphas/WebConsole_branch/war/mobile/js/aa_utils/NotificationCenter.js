/**
 * This class is responsible for dealing with notification.
 * Notification mechanism is a important and useful mechanism like event model.
 * 
 * The design of the NotificationCenter references Notification mechanism of cocoa touch of apple.
 * It's responsible for registering observers with notification type and dealing function, posting notifications
 * resetting NotificationCenter.
 *
 * This class is singleton.
 * 
 * author: handy.wang 2010-07-28
 */
NotificationCenter = (function() {
  var notificationCenter = null;
  
  /**
   * Constructor
   */
  var NotificationCenter = function() {
    this.notificationHandleFunctionsMap = [];
    
    /**
     * Add obesever into notification center with notification type and dealing function of observer's.
     *
     * Parameter "notificationType" is string type.
     * Parameter "observerFunction" is a javascript function.
     */
    this.addObserver = function(notificationType, observerFunction) {
      var notificationHandleFunctions = this.notificationHandleFunctionsMap[notificationType];
      if (notificationHandleFunctions == null || notificationHandleFunctions == undefined) {
        notificationHandleFunctions = [];
      }
      notificationHandleFunctions[notificationHandleFunctions.length] = observerFunction;
      this.notificationHandleFunctionsMap[notificationType] = notificationHandleFunctions;
    };
    
    /**
     * Post notification to observer identified by notification type with some data.
     */
    this.postNotification = function(notificationType, data) {
      var notificationHandleFunctions = this.notificationHandleFunctionsMap[notificationType];
      if (notificationHandleFunctions == null || notificationHandleFunctions == undefined || notificationHandleFunctions.length == 0) {
        return;
      }
      for (var i = 0; i < notificationHandleFunctions.length; i++) {
        var notificationHandleFunction = notificationHandleFunctions[i];
        notificationHandleFunction(data);
      }
    };
    
    /**
     * Rest Notication center with clearing the all data stored in notification center.
     */
    this.reset = function() {
      this.notificationHandleFunctionsMap = [];
    };
    
  };
  
  /**
   * Singleton implementation.
   */
  return {
    getInstance : function() {
      if (notificationCenter == null) {
        notificationCenter = new NotificationCenter();
      }
      return notificationCenter;
    }
  };
})();
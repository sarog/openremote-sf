/**
 * This class is responsible for dealing with string.
 * auther: handy.wang 2010-07-28
 */
NotificationCenter = (function() {
  var notificationCenter = null;
  
  var NotificationCenter = function() {
    this.notificationHandleFunctionsMap = [];
    
    this.addObserver = function(notificationType, observerFunction) {
      var notificationHandleFunctions = this.notificationHandleFunctionsMap[notificationType];
      if (notificationHandleFunctions == null || notificationHandleFunctions == undefined) {
        notificationHandleFunctions = [];
      }
      notificationHandleFunctions[notificationHandleFunctions.length] = observerFunction;
      this.notificationHandleFunctionsMap[notificationType] = notificationHandleFunctions;
    };
    
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
    
  };
  
  return {
    getInstance : function() {
      if (notificationCenter == null) {
        notificationCenter = new NotificationCenter();
      }
      return notificationCenter;
    }
  };
})();
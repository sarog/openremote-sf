/**
 * This class is responsible for dealing with string.
 * auther: handy.wang 2010-07-28
 */
NotificationCenter = (function() {
  var notificationCenter = null;
  
  var NotificationCenter = function() {
    this.eventListenersMap = [];
    
    this.addEventListener = function(eventType, observer) {
      var eventListeners = this.eventListenersMap[eventType];
      if (eventListeners == null || eventListeners == undefined) {
        eventListeners = [];
      }
      eventListeners[eventListeners.length] = observer;
      this.eventListenersMap[eventType] = eventListeners;
    };
    
    this.removeEventListener = function(eventType, observer) {
      var eventListeners = this.eventListenersMap[eventType];
      if (eventListeners != null && eventListeners != undefined && eventListeners.length > 0) {
        eventListeners.splice(eventListeners.indexOf(observer), 1);
      }
    };
    
    this.fireEvent = function(eventType, data) {
      var eventListeners = this.eventListenersMap[eventType];
      if (eventListeners == null || eventListeners == undefined || eventListeners.length == 0) {
        return;
      }
      for (var i = 0; i < eventListeners.length; i++) {
        var eventListener = eventListeners[i];
        eventListener.handleEvent(data);
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
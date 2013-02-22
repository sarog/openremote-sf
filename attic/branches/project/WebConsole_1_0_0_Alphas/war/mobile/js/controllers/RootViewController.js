/**
 * It's for controlling all views render.
 * author: handy.wang 2010-07-13
 */
RootViewController = (function(){
 
 // Constructor
 return function(delegateParam) {
   // For extends
   RootViewController.superClass.constructor.call(this);
   var self = this;
   
   this.delegate = delegateParam;
   this.groupControllers = [];
   this.groupIDViewMap = {};
   this.currentGroupController = null;
   this.lastSubView = null;
   this.navigateHistory = [];

   var errorViewController = null;
   var initViewController = null;

   // Public instance methods
   /**
    * It's responsible for subviews of root view including group views, pagination views, screen views and all kinds of component views.
    */
   this.renderViews = function() {
     self.getView().removeSubView(errorViewController.getView());
     self.getView().removeSubView(initViewController.getView());
     
     var groups = RenderDataDB.getInstance().getGroups();
     if (groups.length > 0) {
       MessageUtils.showLoading("Rendering views ...");
       
       var groupController = null;
       
       var lastFootPrint = CookieUtils.getCookie(Constants.LAST_FOOT_PRINT);
       if (lastFootPrint != null && lastFootPrint != undefined) {
         var lastGroupIDWhenQuit = lastFootPrint.groupID;
         // This situation is for recover the last-group in web browser while refreshing manually or reopening a browser tab.
         // Or render the first group in condition of access app at first time.
         if (lastGroupIDWhenQuit != undefined && lastGroupIDWhenQuit != null && lastGroupIDWhenQuit != "") {
           var lastGroup = RenderDataDB.getInstance().findGroupByID(lastGroupIDWhenQuit);
           if (lastGroup != null) {
             groupController = new GroupController(lastGroup);
           } else {
             groupController = new GroupController(groups[0]);
           }
         } 
       } else {
         groupController = new GroupController(groups[0]);
       }
       
       // The following way with "array[array.length] = xxx" is better than "array.push(xxx);" while considering performance.
       self.groupControllers[this.groupControllers.length] = groupController;
       self.groupIDViewMap[groupController.group.id] = groupController.getView();
       self.currentGroupController = groupController;
       
       var currentGroupControllerView = self.currentGroupController.getView();
       self.getView().addSubView(currentGroupControllerView);
       self.lastSubView = currentGroupControllerView;
       saveLastGroupIdAndScreenId();
     } else {
       self.getView().addSubView(errorViewController.getView());
     }
     $("#errorViewSettingsBtn").button({icons: {primary: 'ui-icon-gear'}}).click(function() {AppSettings.getInstance(self).show();})
   };
   
   /**
    * Remove the initializing view of current webconsole.
    */
   this.removeInitView = function() {
     self.getView().removeSubView(initViewController.getView());
   };

   // Private instance methods
   /**
    * Initializing jobs.
    */
   function initView() {
     
     self.setView(new RootView());
     errorViewController = new ErrorViewController("No Group Found", "Please check your setting or define a group with screens first.");
     initViewController = new InitViewController();
     self.getView().addSubView(errorViewController.getView());
     self.getView().addSubView(initViewController.getView());
     addObservers();
   }
   
   /**
    * Add obsevers into Notification center for navigate and action of refreshing all view.
    */
   function addObservers() {
     // Add navigation event listener.
     NotificationCenter.getInstance().addObserver(Constants.NAVIGATION_NOTIFICATION, self.navigateFromNotification);
     // Register notification of refreshing view.
     NotificationCenter.getInstance().addObserver(Constants.REFRESH_VIEW_NOTIFICATION, self.refreshView);
   }
   
   /**
    * Do navigate action with model data navigate which is transfered by notification.
    */
   this.navigateFromNotification = function(data) {
     if (data != null && data != undefined) {
       var navigate = data;
       navigateToWithHistory(navigate);
     }
   };
   
   /**
    * Refresh webconsole views.
    */
   this.refreshView = function() {
     MessageUtils.hideLoading();
     RenderDataDB.getInstance().clearAll();
     NotificationCenter.getInstance().reset();
     self.getView().removeSubViews();
     self.getView().addSubView(errorViewController.getView());
     self.getView().addSubView(initViewController.getView());
     self.groupControllers = [];
     self.groupIDViewMap = {};
     
     if (self.currentGroupController != null && self.currentGroupController != undefined) {
       self.currentGroupController.stopPolling();
     }
     self.currentGroupController = null;
     addObservers();
     self.delegate.beginUpdate();
   };
   
   /**
    * Do navigate action and save the group and screen information.
    */
   function navigateToWithHistory(navigate) {
     var historyNavigate = {};
     var currentGroup = self.currentGroupController.group;
     if (currentGroup != null && currentGroup != undefined) {
       historyNavigate.fromGroup = currentGroup.id;
       historyNavigate.fromScreen = self.currentGroupController.currentScreenID();
     } else {
       return;
     }
     
     if (navigateTo(navigate)) {
       saveLastGroupIdAndScreenId();
       self.navigateHistory[self.navigateHistory.length] = historyNavigate;
     }
   }
   
   /**
    * Save the last group and screen which users browse.
    */
   function saveLastGroupIdAndScreenId() {
     var lastFootPrint = {};
     lastFootPrint.groupID = self.currentGroupController.group.id;
     lastFootPrint.screenID = self.currentGroupController.currentScreenID();
     CookieUtils.setCookie(Constants.LAST_FOOT_PRINT, lastFootPrint);
   }
   
   /**
    * Do navigate action with navigate model data.
    */
   function navigateTo(navigate) {
     // To group and screen
     if (parseInt(navigate.toGroup) > 0) {
       return navigateToGroupAndScreen(parseInt(navigate.toGroup), parseInt(navigate.toScreen));
     } 
     // To previous screen if it exits in same group.
     else if (navigate.isToPreviousScreen) {
       return navigateToPreviousScreen();
     }
     // To next screen if it exits in same group.
     else if (navigate.isToNextScreen) {
       return navigateToNextScreen();
     }
     // To back in histories.
     else if (navigate.isToBack) {
       navigateBackwardInHistory();
       return false;
     }
     // To setting dialog
     else if (navigate.isToSetting) {
       AppSettings.getInstance(self).show();
       return false;
     }
     // To logout
     else if (navigate.isToLogout) {
       navigateToLogout();
       return false;
     }
   }
   
   /**
    * Do navigate action to the specified group and screen with group id and screen id.
    */
   function navigateToGroupAndScreen(groupID, screenID) {
     var targetGroupController = null;
     var isNotToSelf = (groupID != self.currentGroupController.group.id);
     
     // To certain group
     if (groupID > 0 && isNotToSelf) {
       // Find if targetGroupController was cached in self.groupControllers
       for (var i = 0; i < self.groupControllers.length; i++) {
         var tempGroupController = self.groupControllers[i];
         if (groupID == tempGroupController.group.id) {
           targetGroupController = tempGroupController;
           break;
         }
       }
       
       // If didn't find means the groupcontroller where is to go wasn't visited, 
       // so create a target groupcontroller instance and cache it.
       if (targetGroupController == null) {
         var group = RenderDataDB.getInstance().findGroupByID(groupID);
         if (group != null && group != undefined) {
           targetGroupController = new GroupController(group);
           self.groupControllers[self.groupControllers.length] = targetGroupController;
           self.groupIDViewMap[targetGroupController.group.id] = targetGroupController.getView();
         } else {
           return false;
         }
       }
       
       if (targetGroupController.hasNoScreenView()) {
         MessageUtils.showMessageDialog("Message", "No screen is in that group is to go.");
         return NO;
       }
       
       if (screenID > 0 && !targetGroupController.canFindScreenByID(screenID)) {
         MessageUtils.showMessageDialog("Message", "The screen where is to go isn't in that group where is to go.");
         return NO;
       }
       
       self.currentGroupController.stopPolling();
       self.getView().removeSubView(self.lastSubView);
       
       self.currentGroupController = targetGroupController;
       var currentGroupControllerView = self.groupIDViewMap[groupID];
       self.getView().addSubView(currentGroupControllerView);
       self.lastSubView = currentGroupControllerView;
       self.currentGroupController.startPolling();
     }
     
     // To certain screen
     if (screenID > 0) {
       if (!self.currentGroupController.canFindScreenByID(screenID)) {
         MessageUtils.showMessageDialog("Message", "The screen where is to go isn't in current group.");
         return false;
       } else {
         // self.currentGroupController().stopPolling();
         return self.currentGroupController.switchToScreen(screenID);
       }
     }
     return true;
   }
   
   /**
    * Do navigate action to previous screen view.
    */
   function navigateToPreviousScreen() {
     self.currentGroupController.previousScreen();
   }
   
   /**
    * Do navigate action to next screen view.
    */
   function navigateToNextScreen() {
     self.currentGroupController.nextScreen();
   }
   
   /**
    * Do navigate action for back with history.
    */
   function navigateBackwardInHistory() {
     if (self.navigateHistory.length > 0) {
       var backNavigate = self.navigateHistory[self.navigateHistory.length-1];
       var fromGroup = parseInt(backNavigate.fromGroup);
       var fromScreen = parseInt(backNavigate.fromScreen);
       if (fromGroup > 0 && fromScreen > 0) {
         navigateToGroupAndScreen(fromGroup, fromScreen);
       } else {
         navigateTo(backNavigate);
       }
       
       self.navigateHistory.splice(self.navigateHistory.length-1 ,1);
     }
   }
   
   /**
    * Do logout action for user.
    */
   function navigateToLogout() {
     ConnectionUtils.sendJSONPRequest(ConnectionUtils.getLogoutURL(), self);
   }
   
   //The following two methods are delegate methods should be declared in ConnectionUtils and invoked by ConnectionUtils.
   /**
    * This method will be called after request being sent succussfully and 
    * some exception occured in controller server with json-formatted data back.
    */
   this.didRequestSuccess = function(data, textStatus) {
     if (data != null && data != undefined) {
       var error = data.error;
       if (error != null && error != undefined && error.code != Constants.HTTP_SUCCESS_CODE && error.code == Constants.UNAUTHORIZED) {
        MessageUtils.showMessageDialog("Logout info", error.message);
       } else {
         MessageUtils.showMessageDialogWithSettings("Logout fail", error.message);
       }
     } else {
       MessageUtils.showMessageDialogWithSettings("Logout fail", Constants.UNKNOWN_ERROR_MESSAGE);
     }
   };
   
   /**
    * This method will be called in case of network failure or ill-formed JSON responses
    */
   this.didRequestError = function(xOptions, textStatus) {
      // MessageUtils.showMessageDialogWithSettings("Logout fail", "Network connection error or some unknown exceptions occured.");
   }
   
   // Init jobs
   initView();
 };
})();
 
// For extends
ClassUtils.extend(RootViewController, BaseViewController);
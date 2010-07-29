/**
 * It's for controlling all views render.
 * auther: handy.wang 2010-07-13
 */
RootViewController = (function(){
 
 // Constructor
 return function() {
   // For extends
   RootViewController.superClass.constructor.call(this);
   var self = this;
   
   this.groupControllers = [];
   this.groupIDViewMap = {};
   this.currentGroupController = null;
   this.lastSubView = null;

   var errorViewController = null;
   var initViewController = null;
   
   // properties
   //var ErrorViewController = new ErrorViewController();

   // Public instance methods
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
     } else {
       self.getView().addSubView(errorViewController.getView());
     }
     $("#errorViewSettingsBtn").button({icons: {primary: 'ui-icon-gear'}}).click(function() {AppSettings.getInstance(self).show();})
   };
   
   this.removeInitView = function() {
     self.getView().removeSubView(initViewController.getView());
   };

   // Private instance methods
   function initView() {
     self.setView(new RootView());
     errorViewController = new ErrorViewController("No Group Found", "Please check your setting or define a group with screens first.");
     initViewController = new InitViewController();
     self.getView().addSubView(errorViewController.getView());
     self.getView().addSubView(initViewController.getView());
     
     // Add navigation event listener.
     NotificationCenter.getInstance().addObserver(Constants.NAVIGATION, self);
   }
   
   this.handleNotification = function(data) {
     navigateFromNotification(data);
   }
   
   function navigateFromNotification(data) {
     if (data != null && data != undefined) {
       var navigate = data;
       navigateToWithHistory(navigate);
     }
   }
   
   function navigateToWithHistory(navigate) {
     var historyNavigate = {};
     var currentGroup = self.currentGroupController.group;
     if (currentGroup != null && currentGroup != undefined) {
       historyNavigate.fromGroup = currentGroup.id;
       historyNavigate.fromScreen = self.currentGroupController.currentScreenID();
     } else {
       return;
     }
     
     // TODO: navigate to.
     if (navigateTo(navigate)) {
       saveLastGroupIdAndScreenId();
     }
   }
   
   function saveLastGroupIdAndScreenId() {
     var lastFootPrint = {};
     lastFootPrint.groupID = self.currentGroupController.group.id;
     lastFootPrint.screenID = self.currentGroupController.currentScreenID();
     CookieUtils.setCookie(Constants.LAST_FOOT_PRINT, lastFootPrint);
   }
   
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
       // TODO: navigateBackwardInHistory
       return false;
     }
     // To setting dialog
     else if (navigate.isToSetting) {
       // AppSettings.getInstance(null).show();
       return false;
     }
     // TODO: to other certain place.
   }
   
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
       
       // TODO: render tabbar if exists.
       
       self.currentGroupController.stopPolling();
       self.getView().removeSubView(self.lastSubView);
       
       self.currentGroupController = targetGroupController;
       var currentGroupControllerView = self.groupIDViewMap[groupID];
       self.getView().addSubView(currentGroupControllerView);
       self.lastSubView = currentGroupControllerView;
       self.currentGroupController.startPolling();
     }
     
     // To certain screen in current group
     if (screenID > 0) {
       if (!self.currentGroupController.canFindScreenByID(screenID)) {
         MessageUtils.showMessageDialog("Message", "The screen where is to go isn't in current group.");
         return false;
       } else {
         return self.currentGroupController.switchToScreen(screenID);
       }
     }
     return true;
   }
   
   function navigateToPreviousScreen() {
     self.currentGroupController.previousScreen();
   }
   
   function navigateToNextScreen() {
     self.currentGroupController.nextScreen();
   }
   
   // Init jobs
   initView();
 };
})();
 
// For extends
ClassUtils.extend(RootViewController, BaseViewController);
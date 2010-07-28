/**
 * It's for controlling all views render.
 * auther: handy.wang 2010-07-13
 */
RootViewController = (function(){
 
 // Constructor
 return function() {
   // For extends
   RootViewController.superClass.constructor.call(this);
   
   this.groupControllers = [];
   this.groupIDViewMap = {};
   this.currentGroupController = null;
   
   var self = this;
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
       var lastGroupIDWhenQuit = CookieUtils.getCookie(Constants.LAST_GROUP_ID_WHEN_QUIT);
       
       // This situation is for recover the last-group in web browser while refreshing manually or reopening a browser tab.
       // Or render the first group in condition of access app at first time.
       if (lastGroupIDWhenQuit != undefined && lastGroupIDWhenQuit != null && lastGroupIDWhenQuit != "") {
         var lastGroup = RenderDataDB.getInstance().getGroupByID(lastGroupIDWhenQuit);
         if (lastGroup != null) {
           groupController = new GroupController(lastGroup);
         } else {
           groupController = new GroupController(groups[0]);
         }
       } else {
         groupController = new GroupController(groups[0]);
       }
       
       // The following way with "array[array.length] = xxx" is better than "array.push(xxx);" while considering performance.
       self.groupControllers[this.groupControllers.length] = groupController;
       self.groupIDViewMap[groupController.group.id] = groupController.getView();
       self.currentGroupController = groupController;
       
       self.getView().addSubView(self.currentGroupController.getView());
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
     
     // // TODO: navigate to.
     // if (navigateTo(navigate)) {
     //   
     // }
   }
   
   // Init jobs
   initView();
 };
})();
 
// For extends
ClassUtils.extend(RootViewController, BaseViewController);
/**
 * This class is for storing render data only, such as groups, screens, sliders and so on.
 * It just stores data in memory but cookie or some place else.
 * This class is singleton.
 *
 * author: handy.wang 2010-07-15
 */
RenderDataDB = (function() {
  
  var renderDataDB = null;
  
  // Constructor
  function RenderDataDB() {
    var self = this;
    var groups = [];
    var screens = [];
    var labels = [];
    self.globalTabBar = null;
    
    /**
     * Add a screen into RenderDataDB.
     */
    this.addScreen = function(screenParam) {
      if (screenParam != null && screenParam != undefined && screenParam.id != null && screenParam.id != undefined && 
        this.findScreenByID(screenParam.id) == null) {
          
        screens[screens.length] = screenParam;
      }
    };
    
    /**
     * Get all the screens from RenderDataDB.
     */
    this.getScreens = function() {
      return screens;
    };
    
    /**
     * Add a group into RenderDataDB.
     */
    this.addGroup = function(groupParam) {
      if (groupParam != null && groupParam != undefined && groupParam.id != null && groupParam.id != undefined && 
        this.findGroupByID(groupParam.id) == null) {
          
        groups[groups.length] = groupParam;
      }
    };
    
    /**
     * Get all the groups from RenderDataDB.
     */
    this.getGroups = function() {
      return groups;
    };
    
    /**
     * Add a label into RenderDataDB.
     */
    this.addLabel = function(labelParam) {
      if (labelParam != null && labelParam != undefined && labelParam.id != null && labelParam.id != undefined && 
        this.findLabelByID(labelParam.id) == null) {
          
        labels[labels.length] = labelParam;
      }
    };
    
    /**
     * Get all the labels from RenderDataDB.
     */
    this.getLabels = function() {
      return labels;
    };
    
    /**
     * Get the sepecified group with group id from RenderDataDB.
     */
    this.findGroupByID = function(groupID) {
      for (var index = 0; index < groups.length; index++) {
        var tempGroup = groups[index];
        if (tempGroup.id == groupID) {
          return tempGroup;
        }
      }
      return null;
    };
    
    /**
     * Get specified screen with screen id from RenderDataDB.
     */
    this.findScreenByID = function(screenID) {
      for (var index = 0; index < screens.length; index++) {
        var tempScreen = screens[index];
        if (tempScreen.id == screenID) {
          return tempScreen;
        }
      }
      return null;
    };
    
    /**
     * Get specified label with label id from RenderDataDB.
     */
    this.findLabelByID = function(labelID) {
      for (var i = 0; i < labels.length; i++) {
        var tempLabel = labels[i];
        if (tempLabel.id == labelID) {
          return tempLabel;
        }
      }
      return null;
    };
    
    /**
     * Clear all data from RenderDataDB.
     */
    this.clearAll = function() {
      groups = [];
      screens = [];
      labels = [];
      self.globalTabBar = null;
    };
    
  };
  
  return {
    getInstance : function() {
      if(renderDataDB == null) {
        renderDataDB = new RenderDataDB();
      }
      return renderDataDB;
    }
  };
  
})();
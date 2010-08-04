/**
 * This class is for storing render data only, such as groups, screens, sliders and so on.
 * It just stores data in memory but cookie or some place else.
 * auther: handy.wang 2010-07-15
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
    
    this.addScreen = function(screenParam) {
      if (screenParam != null && screenParam != undefined && screenParam.id != null && screenParam.id != undefined && 
        this.findScreenByID(screenParam.id) == null) {
          
        screens[screens.length] = screenParam;
      }
    };
    
    this.getScreens = function() {
      return screens;
    };
    
    this.addGroup = function(groupParam) {
      if (groupParam != null && groupParam != undefined && groupParam.id != null && groupParam.id != undefined && 
        this.findGroupByID(groupParam.id) == null) {
          
        groups[groups.length] = groupParam;
      }
    };
    
    this.getGroups = function() {
      return groups;
    };
    
    this.addLabel = function(labelParam) {
      if (labelParam != null && labelParam != undefined && labelParam.id != null && labelParam.id != undefined && 
        this.findLabelByID(labelParam.id) == null) {
          
        labels[labels.length] = labelParam;
      }
    };
    
    this.getLabels = function() {
      return labels;
    };
    
    this.findGroupByID = function(groupID) {
      for (var index = 0; index < groups.length; index++) {
        var tempGroup = groups[index];
        if (tempGroup.id == groupID) {
          return tempGroup;
        }
      }
      return null;
    };
    
    this.findScreenByID = function(screenID) {
      for (var index = 0; index < screens.length; index++) {
        var tempScreen = screens[index];
        if (tempScreen.id == screenID) {
          return tempScreen;
        }
      }
      return null;
    };
    
    this.findLabelByID = function(labelID) {
      for (var i = 0; i < labels.length; i++) {
        var tempLabel = labels[i];
        if (tempLabel.id == labelID) {
          return tempLabel;
        }
      }
      return null;
    };
    
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
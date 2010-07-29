/**
 * This class is for storing render data only, such as groups, screens, sliders and so on.
 * It just stores data in memory but cookie or some place else.
 * auther: handy.wang 2010-07-15
 */
RenderDataDB = (function() {
  
  var renderDataDB = null;
  
  // Constructor
  function RenderDataDB() {
    
    var groups = [];
    var screens = [];
    
    this.addScreen = function(screenParam) {
      screens.push(screenParam);
    };
    
    this.getScreens = function() {
      return screens;
    };
    
    this.addGroup = function(groupParam) {
      groups.push(groupParam);
    };
    
    this.getGroups = function() {
      return groups;
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
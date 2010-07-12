/**
 * This class is responsible for saving some configurations which is used to create client.
 *
 * auther: handy.wang 2010-07-07
 */

AppSettings = (function(){
  
  // Private static variables
  var appSettings = null;
  
  var DIALOG_WIDHT = "97%";
  var DIALOG_HEIGHT = "auto";
  var URL_REGEX = /(ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/;
  var EMPTY_CONTROLLER_URL_LIST = "<div style='text-align: center; padding: 0.4em; font-size: 100%; height: 18px;'>Currently, there is no controller url.</div>";
   
  // Constructor
  function AppSettings() {
    var controllerServers = [];
    var selectedControllerServer = null;
    var appSettingsDialog = $("#appSettingsDialog");
    
    // Instatnce methods
    
    this.show = function() {
      if (controllerServers.length > 0) {
        renderControllerServers();
      } else {
        $(appSettingsDialog).find("#controllerURLList")
        .html(EMPTY_CONTROLLER_URL_LIST);
      }
      $(appSettingsDialog).dialog("open");
    };
    
    this.getControllerServers  = function() {
      return controllerServers;
    };
    
    this.recoverSettingsFromCookie = function() {
      // Recover cookied controller servers.
  	  var cookiedControllerServersString = CookieUtils.getCookie(Constants.CONTROLLER_SERVERS);
  	  if (cookiedControllerServersString != null) {
  	    MessageUtils.showLoading("Recovering controller servers...");
  	    controllerServers = [];
        var cookiedControllerServersObjs = JSON.parse(cookiedControllerServersString, null);
        for (var index in cookiedControllerServersObjs) {
          var cookiedControllerServer = cookiedControllerServersObjs[index];
          var controllerServer = new ControllerServer(cookiedControllerServer.url);
          controllerServer.id = cookiedControllerServer.id;
          controllerServer.panelIdentities = cookiedControllerServer.panelIdentities;
          controllerServer.selectedPanelIdentity = cookiedControllerServer.selectedPanelIdentity;
          controllerServers.push(controllerServer);
        }
        MessageUtils.hideLoading();
      }
      
      // Recover cookied selected controller server.
      var cookiedSelectedControllerServerString = CookieUtils.getCookie(Constants.CURRENT_SERVER);
      if (cookiedSelectedControllerServerString != null) { 
        var cookiedSelectedControllerServerObj = JSON.parse(cookiedSelectedControllerServerString);
        selectedControllerServer = new ControllerServer(cookiedSelectedControllerServerObj.url);
        selectedControllerServer.id = cookiedSelectedControllerServerObj.id;
        selectedControllerServer.panelIdentities = cookiedSelectedControllerServerObj.panelIdentities;
        selectedControllerServer.selectedPanelIdentity = cookiedSelectedControllerServerObj.selectedPanelIdentity;
      }
  	};
    
    // Private methods
    function init() {
      renderTemplate();
      initProtocolRadioBtns();
      initAddControllerURLBtn();
      initControllerPanelSelect();
    }
    
    // Attatch the template layout to dialog.
    function renderTemplate() {
      var dialogContent = new EJS({url: "./mobile/ejs/AppSettings.ejs"}).render({name:"handy"});
      $(appSettingsDialog).html(dialogContent);
      $(appSettingsDialog).dialog({
    		autoOpen: false,
    		height: DIALOG_HEIGHT,
    		width: DIALOG_WIDHT,
    		position: "top",
    		resizable: true,
    		modal: false,
    		buttons: {
    			'OK': function() {
            if (selectedControllerServer == null) {
              MessageUtils.showMessageDialog("Please select a controller.");
              return;
            }
            
            // Save selected panel identity
            var selectedPanelIdentity = $("#controllerPanelSelect").val();
            if (selectedPanelIdentity != null && selectedPanelIdentity != "" && selectedPanelIdentity != undefined) {
              selectedControllerServer.setSelectedPanelIdentity(selectedPanelIdentity);
            } else {
              MessageUtils.showMessageDialog("The panel identity is illegal.");
              return;
            }
            loadPanelWithName();
    			},
    			Cancel: function() {
            closeAppSettingsDialog();
    			}
    		},
    		close: function() {
    		}
    	});
    }
    
    function closeAppSettingsDialog() {
		  $("#controllerPanelSelectContainer").hide();
      $("#controllerPanelSelect").children().remove();
      $("#controllerPanelSelect").html("<option>none</option>");
			$(appSettingsDialog).dialog('close');
    }
    
    // Init the addControllerURL button.
    function initAddControllerURLBtn() {
      $(appSettingsDialog).find("#addControllerURLBtn").button({icons : {primary : 'ui-icon-plusthick'}})
        .click(function() {
          var controllerURLInput = $(appSettingsDialog).find("#controllerURLInput");
          if(checkRegexp(controllerURLInput, URL_REGEX, "Invalid URL format.")) {
            newControllerServer(controllerURLInput.val());
            renderControllerServers();
            $(appSettingsDialog).find("#controllerURLInput").val("");
          }
        }
      );
    }
    
    function renderControllerServers() {
      $(appSettingsDialog).find("#controllerURLList").html("");
      var controllerServersDivs = "";
      if (controllerServers.length <= 0) {
        $(appSettingsDialog).find("#controllerURLList").html(EMPTY_CONTROLLER_URL_LIST);
        $("#controllerPanelSelectContainer").hide();
        $("#controllerPanelSelect").html("<option>none</option>");
        return;
      }
      for (var i = 0; i < controllerServers.length; i++) {
        if(i % 2 != 0) {
          controllerServersDivs += 
          "<div id='" + controllerServers[i].getID() + "' name='controllerServer' class='ui-state-default'><span name='removeControllerServerBtn' value='" + controllerServers[i].getID() + "' >Remove item</span><span style='font-size: 100%;'>" + controllerServers[i].getUrl() + 
          "</span></div>";
        } else {
          controllerServersDivs += 
          "<div id='" + controllerServers[i].getID() + "' name='controllerServer' ><span name='removeControllerServerBtn' value='" + controllerServers[i].getID() + "' >Remove item</span><span>" + controllerServers[i].getUrl() + 
          "</span></div>";
        }
      }
      $(appSettingsDialog).find("#controllerURLList").html(controllerServersDivs);
      
      $(appSettingsDialog).find("[name = 'controllerServer']").click(function() {
        selectedControllerServer = findControllerServerByID($(this).attr("id"));
        $(appSettingsDialog).find("[name = 'controllerServer']").removeClass("ui-selected");
        $(this).addClass("ui-selected");
        loadPanelIdentities();
      });
      
      $(appSettingsDialog).find("[name = 'removeControllerServerBtn']")
        .button({
          icons: {
            primary: 'ui-icon-closethick'
          },
          text:false
        }).click(function() {
          var controllerServerID = $(this).attr("value");
          removeControllerServerByID(controllerServerID);
          renderControllerServers();
      });
      
      if (selectedControllerServer != null) {
        // click the selected controller item in the list.
        $("#" + selectedControllerServer.getID()).click();
      }
    }
    
    function loadPanelIdentities() {
      MessageUtils.showLoading("Loading panel identities.");
      
      $("#controllerPanelSelect").children().remove();
      $("#controllerPanelSelect").html("<option>none</option>");

      var successCallback = function(data, textStatus) {
        var panelIdentities = [];
        if (data != null) {
          for (var index in data.panel) {
            var panel = data.panel[index];
            var panelID = panel["@id"];
            var panelName = panel["@name"];
            panelIdentities.push(panelName);
          }
        } else {
          MessageUtils.showMessageDialog("There is no panel identities.");
        }
        
        if (panelIdentities.length != 0) {
          selectedControllerServer.setPanelIdentities(panelIdentities);
          $("#controllerPanelSelect").children().remove();
          for (var index in panelIdentities) {
            if (selectedControllerServer.getSelectedPanelIdentity() == panelIdentities[index]) {
              $("#controllerPanelSelect").append("<option selected>" + panelIdentities[index] + "</option>");
            } else {
              $("#controllerPanelSelect").append("<option>" + panelIdentities[index] + "</option>");
            }
          }
        }

        $("#controllerPanelSelectContainer").show();
        MessageUtils.hideLoading();
      };
      
      var errorCallback = function(xOptions, textStatus) {
        $("#controllerPanelSelectContainer").hide();
        MessageUtils.hideLoading();
        MessageUtils.showMessageDialog("Failed to load panels.");
      };
      
      ConnnectionUtils.getJson(selectedControllerServer.getUrl()+"/rest/panels?callback=?", successCallback, errorCallback);
    }
    
    function loadPanelWithName() {
      var successCallback = function(data, textStatus) {
        MessageUtils.hideLoading();
  		  CookieUtils.setCookie(Constants.CURRENT_SERVER, selectedControllerServer);
  		  
  		  closeAppSettingsDialog();
      };
      
      var errorCallback = function(xOptions, textStatus) {
        MessageUtils.hideLoading();
        MessageUtils.showMessageDialog("Failed to load panel.");  
      };
      
      MessageUtils.showLoading("Loading panel.");
      
      // Save selectedPanelIdentity for selectedController server.
      replaceControllerServer(selectedControllerServer.getID(), selectedControllerServer);
      ConnnectionUtils.getJson(selectedControllerServer.getUrl() + "/rest/panel/" + selectedControllerServer.getSelectedPanelIdentity() + "?callback=?", successCallback, errorCallback);
    }
    
    function initProtocolRadioBtns() {
      $("input[name = 'protocol']").click(function() {
        $("#controllerURLInput").val($(this).val());
        $("#controllerURLInput").focus();
      });
    }
    
    function initControllerPanelSelect() {
      $("#controllerPanelSelectContainer").hide();
      $("#controllerPanelSelect").change(function(){
      });
    }

    function updateTips(t) {
      var tips =  $(appSettingsDialog).find("#appSettingsTips");
      var initInfo = tips.html();
      tips.text(t).addClass('ui-state-highlight');
  		setTimeout(function() {
  		  tips.removeClass('ui-state-highlight', 1000);
  		  tips.html(initInfo);
  		  }, 5000
  		);
  	}

    function checkRegexp(o,regexp,n) {
      o.removeClass('ui-state-error');
  		if (!( regexp.test(o.val()))) {
  			o.addClass('ui-state-error');
  			updateTips(n);
  			return false;
  		} else {
  			return true;
  		}
  	}
  	
  	function newControllerServer(urlParam) {
      var controllerServer = new ControllerServer(urlParam);
      controllerServers.push(controllerServer);
      CookieUtils.setCookie(Constants.CONTROLLER_SERVERS, controllerServers);
    }
    
    // function addControllerServer(controllerServerObj) {
    //   controllerServers.push(controllerServerObj);
    //   CookieUtils.setCookie(Constants.CONTROLLER_SERVERS, controllerServers);
    // }
    
    function findControllerServerByID(id) {
      if (id == null || id == "" || id == undefined) {
        return null;
      }
      for (var i = 0; i < controllerServers.length; i++) {
        if (id == controllerServers[i].getID()) {
          return controllerServers[i];
        }
      }
      return null;
    }
    
    function removeControllerServerByID(id) {
      if (id == "" || id == undefined) {
        return false;
      }
      for (var i = 0; i < controllerServers.length; i++) {
        if (id == controllerServers[i].getID()) {
          // splice is a good method for adding and removing element into and from array.
          // add: [].splice(startAtIndex, 0, element1, element2,...);
          // remove: [].splice(indexInArray, howManyElementsTobeRemoved);
          controllerServers.splice(controllerServers.indexOf(controllerServers[i]),1);
          CookieUtils.setCookie(Constants.CONTROLLER_SERVERS, controllerServers);
          return true;
        }
      }
      return false;
    }
    
    function replaceControllerServer(id, controllerServerObj) {
      var controllerServer = findControllerServerByID(id);
      controllerServers.splice(controllerServers.indexOf(controllerServer), 0, controllerServerObj);
      removeControllerServerByID(id);
      CookieUtils.setCookie(Constants.CONTROLLER_SERVERS, controllerServers);
    }
    
    // Call initializing jobs
    init();
    
   }
   
   return {
     getInstance: function() {
       if (!appSettings) {
         appSettings = new AppSettings();
       }
       appSettings.recoverSettingsFromCookie();
       return appSettings;
     }
   };
   
})();

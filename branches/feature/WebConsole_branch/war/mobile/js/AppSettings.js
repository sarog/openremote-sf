/**
 * This class is responsible for saving some configurations which is used to create client.
 *
 * auther: handy.wang 2010-07-07
 */

AppSettings = (function(){
  
  // Private static variables
  var appSettings = null;
  
  var DIALOG_WIDHT = "100%";
  var DIALOG_HEIGHT = "auto";
  var URL_REGEX = /(ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/;
  var EMPTY_CONTROLLER_URL_LIST = "<div style='text-align: center; padding: 0.4em; font-size: 100%; height: 18px;'>Currently, there is no controller url.</div>";
   
  // Constructor
  function AppSettings() {
    var controllerServers = [];
    var selectedControllerServer = null;
    var appSettingsDialog = $("#appSettingsDialog");
    
    // Instatnce methods
    this.addControllerServer = function(controllerServerParam) {
      controllerServers.push(controllerServerParam);
    };
    
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
            closeAppSettingsDialog();
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
      $(appSettingsDialog).find("#addControllerURLBtn").button({
                  icons: {
                      primary: 'ui-icon-plusthick'
                  }
              }).click(function() {
                var controllerURLInput = $(appSettingsDialog).find("#controllerURLInput");
                if(checkRegexp(controllerURLInput, URL_REGEX, "Invalid URL format.")) {
                  var controllerServer = new ControllerServer(controllerURLInput.val());
                  controllerServers.push(controllerServer);
                  renderControllerServers();
                  $(appSettingsDialog).find("#controllerURLInput").val("");
                }
                
              });
    }
    
    function renderControllerServers() {
      $(appSettingsDialog).find("#controllerURLList").html("");
      var controllerServersDivs = "";
      if (controllerServers.length == 0) {
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
        selectedControllerServer = ControllerServer.findByID($(this).attr("id"));
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
          ControllerServer.removeByID(controllerServerID);
          renderControllerServers();
      });
    }
    
    function loadPanelIdentities() {
      MessageUtils.showLoading("Loading panel identities with url <b>" + selectedControllerServer.getUrl() + "</b>");
      
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
          alert("There is no panel identities.");
        }
        
        if (panelIdentities.length != 0) {
          $("#controllerPanelSelect").children().remove();
          for (var index in panelIdentities) {
            $("#controllerPanelSelect").append("<option>" + panelIdentities[index] + "</option>");
          }
        }
        $("#controllerPanelSelectContainer").show();
        MessageUtils.hideLoading();
      };
      
      var errorCallback = function(xOptions, textStatus) {
        $("#controllerPanelSelectContainer").hide();
        MessageUtils.hideLoading();
        MessageUtils.showMessageDialog("Can't load panels with url " + selectedControllerServer.getUrl());
      }
      
      selectedControllerServer.loadPanelIdentities(successCallback, errorCallback);
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
        selectedControllerServer.setSelectedPanelIdentity($(this).val());
        alert(selectedControllerServer.getSelectedPanelIdentity());
        // var panelIdentities = selectedControllerServer.getPanelIdentities();
        // if (panelIdentities.length == 0) {
        //   $(this).html("<option>none</option>");
        //   return;  
        // }
        // $(this).children().remove();
        // for (var index in panelIdentities) {
        //   $(this).append("<option index=" + index + ">" + panelIdentities[index] + "</option>");
        // }
      });
      
      // $("#controllerPanelSelect").click(function(){
      //   var panelIdentities = selectedControllerServer.getPanelIdentities();
      //   if (panelIdentities.length == 0) {
      //     $(this).html("<option>none</option>");
      //     return;  
      //   }
      //   $(this).children().remove();
      //   for (var index in panelIdentities) {
      //     $(this).append("<option index=" + index + ">" + panelIdentities[index] + "</option>");
      //   }
      // });
      
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
  		if ( !( regexp.test( o.val() ) ) ) {
  			o.addClass('ui-state-error');
  			updateTips(n);
  			return false;
  		} else {
  			return true;
  		}
  	}
    
    // Call initializing jobs
    init();
    
   }
   
   return {
     getInstance: function() {
       if (!appSettings) {
         appSettings = new AppSettings();
       }
       return appSettings;
     }
   };
   
})();

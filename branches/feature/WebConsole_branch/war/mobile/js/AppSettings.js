/**
 * This class is responsible for saving some configurations which is used to create client.
 *
 * auther: handy.wang 2010-07-07
 */

AppSettings = (function(){
  
  // Private static variables
  var appSettings = null;
  
  var DIALOG_WIDHT = 500;
  var DIALOG_HEIGHT = 400;
  var URL_REGEX = /(ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/;
  var EMPTY_CONTROLLER_URL_LIST = "<div style='padding: 0.4em; font-size: 1.4em; height: 18px; margin-top: 12%; margin-left:17%'>Currently, there is no controller url.</div>";
   
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
    		modal: true,
    		buttons: {
    			'OK': function() {
    				var bValid = true;
    				allFields.removeClass('ui-state-error');

    				bValid = bValid && checkLength(name,"username",3,16);
    				bValid = bValid && checkLength(email,"email",6,80);
    				bValid = bValid && checkLength(password,"password",5,16);

    				bValid = bValid && checkRegexp(name,/^[a-z]([0-9a-z_])+$/i,"Username may consist of a-z, 0-9, underscores, begin with a letter.");
    				// From jquery.validate.js (by joern), contributed by Scott Gonzalez: http://projects.scottsplayground.com/email_address_validation/
    				bValid = bValid && checkRegexp(email,/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i,"eg. ui@jquery.com");
    				bValid = bValid && checkRegexp(password,/^([0-9a-zA-Z])+$/,"Password field only allow : a-z 0-9");

    				if (bValid) {
    					$('#users tbody').append('<tr>' +
    						'<td>' + name.val() + '</td>' + 
    						'<td>' + email.val() + '</td>' + 
    						'<td>' + password.val() + '</td>' +
    						'</tr>'); 
    					$(appSettingsDialog).dialog('close');
    				}
    			},
    			Cancel: function() {
    				$(this).dialog('close');
    			}
    		},
    		close: function() {
    			allFields.val('').removeClass('ui-state-error');
    		}
    	});
    }
    
    // Init the addControllerURL button.
    function initAddControllerURLBtn() {
      $(appSettingsDialog).find("#addControllerURLBtn").button({
                  icons: {
                      primary: 'ui-icon-plusthick'
                  }
              }).click(function() {
                var contextURLInput = $(appSettingsDialog).find("#contextURLInput");
                if(checkRegexp(contextURLInput, URL_REGEX, "Invalid URL format.")) {
                  var controllerServer = new ControllerServer(contextURLInput.val());
                  controllerServers.push(controllerServer);
                  renderControllerServers();
                  $(appSettingsDialog).find("#contextURLInput").val("");
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
          "<div id='" + controllerServers[i].getID() + "' name='controllerServer' class='ui-state-default'><span name='removeControllerServerBtn' value='" + controllerServers[i].getID() + "' >Remove item</span>&nbsp;&nbsp;<span>" + controllerServers[i].getUrl() + 
          "</span></div>";
        } else {
          controllerServersDivs += 
          "<div id='" + controllerServers[i].getID() + "' name='controllerServer' ><span name='removeControllerServerBtn' value='" + controllerServers[i].getID() + "' >Remove item</span>&nbsp;&nbsp;<span>" + controllerServers[i].getUrl() + 
          "</span></div>";
        }
      }
      $(appSettingsDialog).find("#controllerURLList").html(controllerServersDivs);
      $(appSettingsDialog).find("[name = 'controllerServer']").click(function() {
        selectedControllerServer = ControllerServer.findByID($(this).attr("id"));
        $(appSettingsDialog).find("[name = 'controllerServer']").removeClass("ui-selected");
        $(this).addClass("ui-selected");
        
        
        // var panelIdentities = selectedControllerServer.getPanelIdentities();
        // if (panelIdentities.length == 0) {
        //   $("#controllerPanelSelect").html("<option>none</option>");
        //   return;  
        // }
        // $("#controllerPanelSelect").children().remove();
        // for (var index in panelIdentities) {
        //   $("#controllerPanelSelect").append("<option index=" + index + ">" + panelIdentities[index] + "</option>");
        // }
        
        
        $("#controllerPanelSelectContainer").show();
      });
      
      $(appSettingsDialog).find("[name = 'removeControllerServerBtn']").button({
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
    
    function initControllerPanelSelect() {
      $("#controllerPanelSelectContainer").hide();
      $("#controllerPanelSelect").click(function(){
              var panelIdentities = selectedControllerServer.getPanelIdentities();
              if (panelIdentities.length == 0) {
                $(this).html("<option>none</option>");
                return;  
              }
              $(this).children().remove();
              for (var index in panelIdentities) {
                $(this).append("<option index=" + index + ">" + panelIdentities[index] + "</option>");
              }
            });
    }

    function updateTips(t) {
      var tips =  $(appSettingsDialog).find("#appSettingsTips");
      var initInfo = tips.text();
      tips.text(t).addClass('ui-state-highlight');
  		setTimeout(function() {
  		  tips.removeClass('ui-state-highlight', 1000);
  		  tips.text(initInfo);
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

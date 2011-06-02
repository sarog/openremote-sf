/**
 * This class is responsible for saving some configurations which is used to create client.
 * This class is singleton.
 *
 * author: handy.wang 2010-07-07
 */

AppSettings = (function(){
  
  // Private static variables
  var appSettings = null;
  
  /** Width css style for AppSettings dialog */
  var DIALOG_WIDHT = "97%";
  
  /** Height css style for AppSettings dialog */
  var DIALOG_HEIGHT = "auto";
  
  /** HTTP protocol for qualified url */
  var HTTP_PROTOCOL = "http://";
  
  /** Regular expression for url which doesn't contain protocol part */
  var URL_REGEX = /(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/;
  
  /** Empty html content of url list of controller server. */
  var EMPTY_CONTROLLER_URL_LIST = "<div style='text-align: center; padding: 0.4em; font-size: 100%; height: 18px;'>Currently, there is no controller url.</div>";
   
  /**
   * Constructor
   */
  function AppSettings(delegateParam) {
    var self = this;
    var delegate = delegateParam;
    
    var controllerServers = [];
    var selectedControllerServer = null;
    var appSettingsDialog = $("#appSettingsDialog");
    
    // Instatnce methods
    
    /**
     * Set delegate for change current delegate instance.
     */
    this.setDelegate = function(delegateParam) {
      delegate = delegateParam;
    };
    
    /**
     * Show appsettings's dialog.
     */
    this.show = function() {
      if (controllerServers.length > 0) {
        renderControllerServers();
      } else {
        $(appSettingsDialog).find("#controllerURLList")
        .html(EMPTY_CONTROLLER_URL_LIST);
      }
      $(appSettingsDialog).dialog("open");
    };
    
    /**
     * Get controller servers users customized.
     */
    this.getControllerServers  = function() {
      return controllerServers;
    };
    
    /**
     * Recover AppSettings dialog with current controller server and controller servers stored in cookie.
     */
    this.recoverSettingsFromCookie = function() {
      // Recover cookied controller servers.
  	  var cookiedControllerServersObjs = CookieUtils.getCookie(Constants.CONTROLLER_SERVERS);
  	  if (cookiedControllerServersObjs != null) {
  	    MessageUtils.showLoading("Recovering controller servers...");
  	    controllerServers = [];
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
      var cookiedSelectedControllerServerObj = CookieUtils.getCookie(Constants.CURRENT_SERVER);
      if (cookiedSelectedControllerServerObj != null) {
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
    
    /**
     * Renders the AppSettings dialog's layout.
     */
    function renderTemplate() {
      var dialogContent = new EJS({url: "./mobile/ejs/AppSettings.ejs"}).render({name:"handy"});
      $(appSettingsDialog).html(dialogContent);
      $(appSettingsDialog).dialog({
    		autoOpen: false,
    		height: DIALOG_HEIGHT,
    		width: DIALOG_WIDHT,
    		position: "top",
    		resizable: false,
    		draggable: false,
    		modal: false,
    		buttons: {
    		  Cancel: function() {
            resetControllerPanelSelectContainer();
      			$(appSettingsDialog).dialog('close');
    			},
    			'OK': function() {
            if (selectedControllerServer == null) {
              MessageUtils.showMessageDialog("Message", "Please select a controller.");
              return;
            }
            
            // Save selected panel identity
            var selectedPanelIdentity = $("#controllerPanelSelect").val();
            if (selectedPanelIdentity != null && selectedPanelIdentity != "" && selectedPanelIdentity != "none") {
              selectedControllerServer.setSelectedPanelIdentity(selectedPanelIdentity);
              replaceControllerServer(selectedControllerServer.getID(), selectedControllerServer);
              CookieUtils.setCookie(Constants.CURRENT_SERVER, selectedControllerServer);
            } else if (selectedPanelIdentity == null && selectedPanelIdentity == "") {
              MessageUtils.showMessageDialog("Message", "The panel identity is empty.");
              return;
            } else if (selectedPanelIdentity == "none") {
              MessageUtils.showMessageDialog("Message", "Currently, there is no available panels for selected controller.");
              return;
            } else {
              MessageUtils.showMessageDialog("Message", "The panel identity is illegal.");
              return;
            }
            MessageUtils.hideLoading();
            resetControllerPanelSelectContainer();
      			$(appSettingsDialog).dialog('close');
      			
      			// Update with controller url.
      			MessageUtils.showLoading("Rendering......");
            NotificationCenter.getInstance().postNotification(Constants.REFRESH_VIEW_NOTIFICATION, null);
    			}
    		},
    		close: function() {
    		}
    	});
    }
    
    /**
     * Reset the panel identities select.
     */
    function resetControllerPanelSelectContainer() {
		  $("#controllerPanelSelectContainer").hide();
      $("#controllerPanelSelect").children().remove();
      $("#controllerPanelSelect").html("<option>none</option>");
    }
    
    /** 
     * Init the addControllerURL button.
     */
    function initAddControllerURLBtn() {
      $(appSettingsDialog).find("#addControllerURLBtn").button({icons : {primary : 'ui-icon-plusthick'}})
        .click(function() {
          var controllerURLInput = $(appSettingsDialog).find("#controllerURLInput");
          if(checkRegexp(controllerURLInput, URL_REGEX, "Invalid URL format.")) {
            newControllerServer(HTTP_PROTOCOL + controllerURLInput.val().trim());
            renderControllerServers();
            $(appSettingsDialog).find("#controllerURLInput").val("");
          }
        }
      );
    }
    
    /**
     * Render the controller servers list.
     */
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
        resetControllerPanelSelectContainer();
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
    
    /**
     * Load panel identities from controller server.
     */
    function loadPanelIdentities() {
      MessageUtils.showLoading("Loading panel identities.");
      
      $("#controllerPanelSelect").children().remove();
      $("#controllerPanelSelect").html("<option>none</option>");
      
      ConnectionUtils.sendJSONPRequest(selectedControllerServer.getUrl()+"/rest/panels?callback=?", self);
    }
    
    // Followings two are delegate methods should be defined in ConnectionUtils.js .
    /**
     * Invoked when request is successful.
     */
    this.didRequestSuccess = function(data, textStatus) {
      var panelIdentities = [];
      if (data != null && data != undefined) {
        var error = data.error;
        if (error != null && error != undefined && error.code != Constants.HTTP_SUCCESS_CODE) {
          selectedControllerServer = null;
          MessageUtils.hideLoading();
          MessageUtils.showMessageDialog("Load panel identities", error.message);
          return;
        } else {
          if (Object.prototype.toString.apply(data.panel) === '[object Array]') {
            for (var index in data.panel) {
              var panel = data.panel[index];
              var panelID = panel["id"];
              var panelName = panel["name"];
              panelIdentities[panelIdentities.length] = panelName;
            }
          } else if (data.panel != null && data.panel != undefined) {
            var panel = data.panel;
            var panelID = panel["id"];
            var panelName = panel["name"];
            panelIdentities[panelIdentities.length] = panelName;
          }
        }
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
      } else {
        MessageUtils.hideLoading();
        MessageUtils.showMessageDialog("Message", "No identity load");
        return;
      }

      $("#controllerPanelSelectContainer").show();
      MessageUtils.hideLoading();
    };
    
    /**
     * Invoked when some error occured with request.
     */
    this.didRequestError = function(xOptions, textStatus) {
      $("#controllerPanelSelectContainer").hide();
      selectedControllerServer = null;
      MessageUtils.hideLoading();
      MessageUtils.showMessageDialog("Message", "Failed to load panels.");
    };
    
    /**
     * Binding events to radio buttons for protocol choose.
     */
    function initProtocolRadioBtns() {
      $("input[name = 'protocol']").click(function() {
        $("#controllerURLInput").val($(this).val());
        $("#controllerURLInput").focus();
      });
    }
    
    /**
     * Binding events to panel identity select.
     */
    function initControllerPanelSelect() {
      $("#controllerPanelSelectContainer").hide();
      $("#controllerPanelSelect").change(function(){
      });
    }

    /**
     * Update the tips when user enter wrong formed url.
     */
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

    /**
     * Check the value of o with regular expression.
     */
    function checkRegexp(o,regexp,n) {
      o.removeClass('ui-state-error');
  		if (!( regexp.test(o.val().trim()))) {
  			o.addClass('ui-state-error');
  			updateTips(n);
  			return false;
  		} else {
  			return true;
  		}
  	}
  	
  	/**
  	 * Create a controller server instance and save it to cookie.
  	 */
  	function newControllerServer(urlParam) {
      var controllerServer = new ControllerServer(urlParam);
      controllerServers.push(controllerServer);
      CookieUtils.setCookie(Constants.CONTROLLER_SERVERS, controllerServers);
    }
    
    // function addControllerServer(controllerServerObj) {
    //   controllerServers.push(controllerServerObj);
    //   CookieUtils.setCookie(Constants.CONTROLLER_SERVERS, controllerServers);
    // }
    
    /**
     * Query controller server instance from controller servers.
     */
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
    
    /**
     * Remove controller server with id from cookie.
     */
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
    
    /**
     * Replace controller server instance with id.
     */
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
     getInstance: function(delegateParam) {
       if (!appSettings) {
         appSettings = new AppSettings(delegateParam);
       }
       appSettings.setDelegate(delegateParam);
       appSettings.recoverSettingsFromCookie();
       return appSettings;
     }
   };
   
})();

/**
 * This class is responsible for showing several kinds of message dialog.
 *
 * author: handy.wang 2010-07-08
 */
MessageUtils = (function(){
  
  /** Width css style of message dialog */
  var MSG_DIALOG_WIDTH = "97%";
  
  /** Height css style of message dialog */
  var MSG_DIALOG_HEIGHT = "auto";
  
  /**
   * Static methods.
   */
  return {
    /** 
     * Show loading view with customized messsage, it's useful for the whole webconsole.
     */
    showLoading:function(message) {
      $("#grayMask").show();
      $("#process-loading").show();
      $("#loading-info").html(message);
    },
    
    /**
     * Update the message of loading view with the passing message.
     */
    updateLoadingMessage:function(message) {
      $("#loading-info").html(message);
    },
    
    /**
     * Hide the loading view which is showing.
     */
    hideLoading:function() {
      $("#grayMask").hide();
      $("#process-loading").hide();
      $("#loading-info").html("");
    },
    
    /**
     * Show message dialog with customized title and message content.
     */
    showMessageDialog:function(titleParam, messageParam) {
      $("#messageDialog").dialog("destroy");
      $("#messageDialog").html(messageParam);
		  $("#messageDialog").dialog({
  			modal: true,
  			width: MSG_DIALOG_WIDTH,
  			height: MSG_DIALOG_HEIGHT,
  			title: titleParam,
  			position: "top",
  			buttons: {
  			  OK: function() {
  			    $(this).dialog("close");
  				}
  			}
  		});
  		$("#messageDialog").dialog("open");
    },
    
    /**
     * Show message dialog with customized title and message content with setting button.
     */
    showMessageDialogWithSettings:function(titleParam, messageParam) {
      $("#messageDialog").dialog("destroy");
      $("#messageDialog").html(messageParam);
		  $("#messageDialog").dialog({
  			modal: true,
  			width: MSG_DIALOG_WIDTH,
  			height: MSG_DIALOG_HEIGHT,
  			title: titleParam,
  			position: "top",
  			buttons: {
  			  "Leave it": function() {
  					$(this).dialog('close');
  				},
  			  "Settings": function() {
  			    AppSettings.getInstance(AppBoot.getInstance()).show();
  			    $(this).dialog("close");
  				}
  			}
  		});
  		$("#messageDialog").dialog("open");
    }
  };
})();
/**
 * This class is responsible for showing message.
 *
 * auther: handy.wang 2010-07-08
 */
MessageUtils = (function(){
  
  var MSG_DIALOG_WIDTH = "97%";
  var MSG_DIALOG_HEIGHT = "auto";
  
  return {
    showLoading:function(message) {
      $("#grayMask").show();
      $("#process-loading").show();
      $("#loading-info").html(message);
    },
    updateLoadingMessage:function(message) {
      $("#loading-info").html(message);
    },
    hideLoading:function() {
      $("#grayMask").hide();
      $("#process-loading").hide();
      $("#loading-info").html("");
    },
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
    },
    getExceptionMessage:function(textStatus) {
      var message = "Unknown error!";
      if (textStatus != "200") {
        // switch(statusCode) {
        //   case REQUEST_ERROR://404
        //     break;
        // }
      }
      return message;
    }
  };
})();
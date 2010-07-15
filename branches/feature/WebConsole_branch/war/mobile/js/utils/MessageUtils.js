/**
 * This class is responsible for showing message.
 *
 * auther: handy.wang 2010-07-08
 */
MessageUtils = (function(){
  
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
  			title: titleParam,
  			position: "top",
  			buttons: {
  			  "Settings": function() {
  			    AppSettings.getInstance().show();
  			    $(this).dialog("close");
  				},
  				"Leave it": function() {
  					$(this).dialog('close');
  				}
  			}
  		});
  		$("#messageDialog").dialog("open");
    }
  };
})();
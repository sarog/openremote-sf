/**
 * This class is responsible for showing message.
 *
 * auther: handy.wang 2010-07-08
 */
MessageUtils = (function(){
  {$("#messageDialog").dialog({
		modal: true,
		buttons: {
			Ok: function() {
				$(this).dialog('close');
			}
		}
	});}
  
  return {
    showLoading:function(message) {
      $("#grayMask").show();
      $("#process-loading").show();
      $("#loading-info").html(message);
    },
    hideLoading:function() {
      $("#grayMask").hide();
      $("#process-loading").hide();
      $("#loading-info").html("");
    },
    showMessageDialog:function(message) {
      $("#messageDialog").dialog("destroy");
      $("#messageDialog").html(message);
		  $("#messageDialog").dialog({
  			modal: true,
  			title: "Message",
  			position: "top",
  			buttons: {
  				Ok: function() {
  					$(this).dialog('close');
  				}
  			}
  		});
  		$("#messageDialog").dialog("open");
    }
  };
})();
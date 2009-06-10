<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
   <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">   
   <title>OpenRemote Beehive - Changes From Update</title>
   <script type="text/javascript">
   var timer = 0;
   $(document).ready(function() {	   
	   if($('#commitStatus').val() == "true"){
           $('#message').text(" The committing is running, please update later.");
           $("#updateBtn").attr("disabled","true").addClass("disabled_button");
     }
	   if($('#updateStatus').val() == "true"){
		   setAnimation();
		   refresh();
		   $('#message').text("Updating form http://lirc.sourceforge.net/remotes ......");
		   timer=setInterval("refresh()",5000);
	   }
	   $('#updateBtn').click(function(){
		      setAnimation();
			   $.post("sync.htm?method=update",{});
			   timer=setInterval("refresh()",5000);
			   $('#message').text("Updating from http://lirc.sourceforge.net/remotes ......");
	      });
	 });
   
	function refresh() {
		$.getJSON("sync.htm?method=getSyncProgress",{r:Math.random()}, function(json) {
			setProgress(json);
			if (json.status == "isEnd") {
				clearInterval(timer);
				timer = 0;
				$('#tab_2 img').attr("src","image/update_icon.gif");
	         $('#updateBtn').removeAttr("disabled").removeClass("disabled_button");
	         $('#spinner').hide();
	         $('#message').html("Update completed, you can view and commit the <b><a href='changes.htm' style='text-decoration: underline;'>changes</a></b>");
			}
		});
	}
	function setProgress(progress){
		$('#updateInfo').html("<pre>"+progress.data+"</pre>");
		var infoContainer = $("#infoContainer");
		infoContainer[0].scrollTop = infoContainer[0].scrollHeight;
		var bar = $('#progressbar');
      bar.find('.progress').css("width",progress.percent);
      bar.find('.text').text(progress.percent);
	}	
	function setAnimation(){
		$('#tab_2 img').attr("src","image/update.gif");
      $('#updateBtn').attr("disabled","true").addClass("disabled_button");
      $('#spinner').show();
	}
</script>
</head>
	<body tabId="2">
	<input type="hidden" name="updateStatus" id="updateStatus" value="${sessionScope.isUpdating}"/>
   <input type="hidden" name="commitStatus" id="commitStatus" value="${sessionScope.isCommitting}"/>
	<table class="infopanel" width="100%" border="0" cellpadding="0"
		cellspacing="0">	
			<tr>
				<td width="100%">
				<table class="tabcontent" width="100%" border="0" cellpadding="0"
					cellspacing="0">
						<tr>
							<td class="value" style="padding-left: 20px;" colspan="5"
								width="10%"><b>Progress</b>&nbsp;:</td>
							<td class="value" colspan="5" width="80%">
								<div id="progressbar" style="float:left">
									<div class="text">0%</div>
									<div class="progress" >
									<span class="text" >0%</span>
									</div>
								</div><span id="message" style="margin-left:10px">Please click the update button to sync lirc files with lirc website.</span>
							</td>
							<td class="value" colspan="5" width="10%"><input id="updateBtn" value="Update" class="button" type="button">
							</td>
						</tr>
				</table>
				</td>	
			</tr>			
	</table>
	<div id="infoContainer" class="infoContainer">
	   <div id="updateInfo"></div>
	   <div id="spinner"><img alt="" src="image/spinner.gif" /></div>
	</div>

</body>
</html>
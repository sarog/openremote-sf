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
	   if($('#updateStatus').val() == "true"){
		   setAnimation();
		   refresh();
		   timer=setInterval("refresh()",5000);
	   }
	   $('#updateBtn').click(function(){
		      setAnimation();
			   $.post("sync.html?method=update");
			   timer=setInterval("refresh()",5000);
			   $('#progressInfoSpan').text("Downloading from http://lirc.sourceforge.net/remotes ......");
	      });
	 });

	function refresh() {
		$.getJSON("sync.html?method=getScraperProgress", function(json) {
			setProgress(json);
			if (json.status == "isEnd") {
				clearInterval(timer);
				timer = 0;
				getCopyProgress();
				timer = setInterval("getCopyProgress()",5000);
				$('#progressInfoSpan').text("Checking modified files ......");
			}
		});
	}
	function getCopyProgress() {
		$.getJSON("sync.html?method=getCopyProgress", function(json) {
			setProgress(json);
			   if(json.status == "isEnd"){
				   clearInterval(timer);
				   timer = 0;
				   $('#tab_2 img').attr("src","image/update_icon.gif");
				   $('#updateBtn').removeAttr("disabled").removeClass("disabled_button");
	            $('#spinner').hide();
	            $('#progressInfoSpan').text("Update completed");
				}
		});
	}
	function setProgress(progress){
		$('#updateInfo').html(progress.data);
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
	<input type="hidden" name="updateStatus" id="updateStatus" value="${isUpdating}"/>
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
								</div><span id="progressInfoSpan" style="margin-left:10px"></span>
							</td>
							<td class="value" colspan="5" width="10%"><input id="updateBtn" value="Update" class="button" type="button">
							</td>
						</tr>
				</table>
				</td>	
			</tr>			
	</table>
	<div class="infoContainer">
	   <pre id="updateInfo"></pre>
	   <div id="spinner"><img alt="" src="image/spinner.gif" /></div>
	</div>

</body>
</html>
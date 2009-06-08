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
               $('#message').text(" The updating is running, please commit changes later.");
               $("#commitSubmit").attr("disabled","true").addClass("disabled_button");
           }
           if($('#commitStatus').val() == "true"){
                $('#message').text(" The committing is running...");
                $("#commitSubmit").attr("disabled","true").addClass("disabled_button");
                refresh();
                showBlock();
                timer=setInterval("refresh()",2000);
           }
           $('#submitForm').ajaxForm(function() {
               $('#message').text(" Commit succeeds!");
               $("#commitSubmit").removeAttr("disabled").removeClass("disabled_button");
           });
           $("#commitSubmit").click( function() {
        	      var validator = validateCheck();
        	      if(validator.form()){
        	    	   inputComment();
        	      }
           });
           $('#commit').click(function(){
               $('#comment').val($('#inputComment').val());
               $.unblockUI();
        	      $('#message').text(" The committing is running...");
               $(this).attr("disabled","true").addClass("disabled_button");
               $('#submitForm').submit();
               showBlock();
               timer=setInterval("refresh()",2000);
               });
           $('#cancel').click(function(){
        	      $.unblockUI();
               });
           $("#checkall").click( function() {
               $("input[name='items']").attr("checked",this.checked);
           });
           $('input.changedNode').each(function(){
        	      var action = $(this).attr('action');
        	      if(action=='UNVERSIONED'||action=='ADDED'){
        	    	  $(this).click(checkAdd);
        	    	}else if(action=='DELETEED'){
        	    		$(this).click(checkDelete);
            	}
           });
           $('#commitSuccessBtn').click(function(){
        	     $.unblockUI();
        	     window.location='';
           });
           
       });
       function validateCheck(){
    	   return $("#submitForm").validate({
    		      meta: "validate",
               rules:{
                   items:"required"
               },
               messages:{
                  items:"Please select at least one change!"
               },
               errorPlacement:function(error, element){
                   error.appendTo("#message").css("color","red");
               }
             });
           }
       function showBlock(){
			$.blockUI({
				message: $('#commitView'),
				css: {
	               width: '30%',
	               top: '20%',
	               left: '30%',
	               height: '50%',
	               textAlign: 'left',
	               cursor: 'default'
	            }
			});
         $('#commitSuccessBtn').attr("disabled","true").addClass("disabled_button");
         $('#spinner').show();
       }
       function inputComment(){
           $.blockUI({
              message: $('#commentView'),
              css: {
                    width: '30%',
                    top: '30%',
                    left: '30%',
                    height: '30%',
                    textAlign: 'center',
                    cursor: 'default'
                 }
           });
         }
       
	function refresh() {
		$.getJSON("changes.htm?method=getCommitProgress",{r:Math.random()}, function(json) {
			$("#commitInfo").html("<pre>"+json.data+"</pre>");
			var infoContainer = $("#infoContainer");
			infoContainer[0].scrollTop = infoContainer[0].scrollHeight;
	         if (json.status == "isEnd") {
	            clearInterval(timer);
	            timer = 0;
	            $('#spinner').hide();
	            $("#commitSuccessBtn").removeAttr("disabled").removeClass("disabled_button");
	         }
	      });
	}
	function checkDelete() {
		var delTexts = $(this).attr("value").substring(1).split("/");
		var delText = null;
		if (delTexts.length == 1) {
			delText = $(this).attr("value");
		} else {
			delText = "/" + delTexts[0];
		}
		var parentTR = $(this).parents("tr.first");
		parentTR.prevAll().each( function() {
			var checkbox = $(this).find("input[action='DELETEED']");
			if (checkbox.val() && checkbox.val().indexOf(delText) == 0) {
				checkbox.attr("checked", this.checked);
			}
		});
		parentTR.nextAll().each( function() {
			var checkbox = $(this).find("input[action='DELETEED']");
			if (checkbox.val() && checkbox.val().indexOf(delText) == 0) {
				checkbox.attr("checked", this.checked);
			}
		});
	}
	function checkAdd() {
		var action = $(this).attr('action');
		var addText = $(this).val();
		var addPath = addText.substring(0, addText.indexOf("|"));
		var parentTR = $(this).parents("tr.first");
		if (this.checked) {
			parentTR
					.prevAll()
					.each(
							function() {
								var checkbox = $(this).find(
										"input[action='" + action + "']");
								var checkboxText = checkbox.val();
								if (checkboxText) {
									var checkboxPath = checkboxText.substring(
											0, checkboxText.indexOf("|"));
									if (addPath.indexOf(checkboxPath + "/") == 0) {
										checkbox.attr("checked", true);
										if (checkboxPath.substring(1)
												.split("/").length == 1) {
											return false;
										}
									}
								}
							});
		} else {
			parentTR.nextAll().each(
					function() {
						var checkbox = $(this).find(
								"input[action='" + action + "']");
						var checkboxText = checkbox.val();
						if (checkboxText) {
							var checkboxPath = checkboxText.substring(0,
									checkboxText.indexOf("|"));
							if (checkboxPath.indexOf(addPath + "/") == 0) {
								checkbox.attr("checked", false);
							} else {
								return false;
							}
						}
					});
		}
	}
</script>
</head>
<body tabId="1">
      <input type="hidden" name="updateStatus" id="updateStatus" value="${sessionScope.isUpdating}"/>
      <input type="hidden" name="commitStatus" id="commitStatus" value="${sessionScope.isCommitting}"/>
	   <table class="infopanel" width="100%" border="0" cellpadding="0" cellspacing="0">
	      <tr>
	         <td width="100%">
	         <table class="tabcontent" width="100%" border="0" cellpadding="0"
	            cellspacing="0">
	            <tbody>
	               <tr class="value" nowrap="true">
	                  <td class="value" style="padding-left: 20px;" nowrap="true"><b>Revision:</b>&nbsp;
	                     ${headMessage.revision}[HEAD]</td>
	                  <td class="value" style="padding-left: 20px;" nowrap="true"><b>Author:</b>&nbsp;
	                     ${headMessage.author}</td>
	                  <td class="value" style="padding-left: 20px;" nowrap="true"><b>Total
	                     items:</b>&nbsp; ${fn:length(diffStatus)}</td>
	                  <td style="text-align:center;" width="100%"><span id="message" style="margin-left:10px; font-size:11px;"></span></td>
	               </tr>
	               <tr>
	                  <td class="value" style="padding-left: 20px;" colspan="5"
	                     width="100%"><b>Comment:</b>&nbsp; ${headMessage.comment}</td>
	               </tr>
	         </table>
	         </td>
	         <td>
	         <table class="tabcontent" border="0" cellpadding="0" cellspacing="0">
	            <tr>
	               <td width="23" align="left" style="padding-right: 7px;">
	                  <input id="commitSubmit" type="submit" class="button" value="Submit"/></td>
	            </tr>
	         </table>
	         </td>
	      </tr>
	   </table>
	   <table id="table_list_of_revisions"  class="list" rules="all" width="100%" cellpadding="0" cellspacing="0">
	      <tr class="second">
	         <th align="left" nowrap="true"><input id="checkall" name="checkall" type="checkbox" ><label for="checkall">Changed resources</label></th>
	         <th width="5%" nowrap="true"><a href="#">Revision </a></th>
	      </tr>
	      <c:set var="hasContent" value="false" />
		   <c:if test="${fn:length(diffStatus) eq 0}">
		       <c:set var="hasContent" value="true" />
		   </c:if>
		     <tr id="msg_tr" class="first" style="${hasContent? '' : 'display:none'}">
		          <td>
			          <c:choose>
				          <c:when test="${isBlankSVN eq true}">
				             The repo is blank, please sync to import remote files.
				          </c:when>
				          <c:otherwise>
		                  There is no change.		          
				          </c:otherwise>
			          </c:choose>
		          </td>
		          <td></td>
		     </tr>
            <form id="submitForm" action="changes.htm?method=commit" method="post">
	           <div id="commentView" class="hidden">
	              <h2>Comment</h2>
	              <textarea id="inputComment" rows="4" cols="40"></textarea>
	              <div>
		              <input id="commit" type="button" value="OK"/>
		              <input id="cancel" type="button" value="Cancel"/>
	              </div>
	           </div>
	           <input type="hidden" id="comment" name="comment"/>
	           <c:forEach items="${diffStatus}" var="diffElement">
	            <tr class="first" >
	              <td width="50%"><table width="100%" border="0" cellpadding="0" cellspacing="0"><tr>
	                      <td class="internal" style="padding-right: 5px;"><input name="items" type="checkbox" value="${diffElement.path}|${diffElement.status}" action="${diffElement.status }" class="changedNode"/>
	                      <td class="internal" style="padding-right: 5px;"><a href="changes.htm?method=change&path=${diffElement.path}&action=${diffElement.status }"><span class="image_link ${diffElement.status }"></span></a></td>
	                      <td class="internal" width="100%" nowrap="true"><a href="changes.htm?method=change&path=${diffElement.path}&action=${diffElement.status }">${diffElement.path}</a></td>
	                    </tr>
	                </table></td>
	              <td align="center"><a href="history.htm?method=getRevisions&path=${diffElement.path}"> <img src="image/revision.gif" alt="Revision list" title="Revision list" border="0"> </a> </td>
	            </tr>
	           </c:forEach>
		      </form>
	   </table>
	   <div id="commitView" class="hidden">
	      <div id="infoContainer" style="height:90%; overflow:auto;">
	         <div id="commitInfo"></div>
	         <div id="spinner"><img alt="" src="image/spinner.gif" /></div>	      
	      </div>
         <div style="text-align:right; background-color:#F1FDE9;"><input id="commitSuccessBtn" type="button" value="OK" class="button"/></div>
      </div>
   </div>
<script type="text/javascript" src="jslib/jquery.blockUI.js"></script>
<script type="text/javascript" src="jslib/jquery.validate.min.js"></script>
</body>
</html>
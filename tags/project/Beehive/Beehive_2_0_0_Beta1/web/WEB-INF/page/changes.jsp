<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
   <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
   <title>OpenRemote Beehive - Changes From Update</title>
   <script type="text/javascript">
	   $(function(){ 
	       $("#checkall").click( function() {
	               $("input[name='items']").attr("checked",this.checked);
	       });
	   });

	   $(function(){
	        $("input[action='A']").click(function(){
	            var addText = $(this).val();
	            var parentTR = $(this).parent().parent().parent().parent().parent().parent();
	            if(this.checked){
	            	parentTR.prevAll().each(function(){
	            		  var checkbox = $(this).find("input[action='A']");
	                    if(addText.indexOf(checkbox.val())==0){
	                    	  checkbox.attr("checked",true);
	                    }
	                });
	            }else{
	            	parentTR.nextAll().each(function(){
	            		var checkbox = $(this).find("input[action='A']");
	                    if(checkbox.val() && checkbox.val().indexOf(addText)==0){
	                    	  checkbox.attr("checked",false);
	                    }
	                });
	            }
	        });
	    });
	   $(function(){
	        $("input[action='D']").click(function(){
	            var delTexts = $(this).attr("value").substring(1).split("/");
	            var delText = null;
	            if(delTexts.length==1){
	                delText = $(this).attr("value");
	            }else{
	                delText = "/"+delTexts[0];
	            }
	            var parentTR = $(this).parent().parent().parent().parent().parent().parent();
	            if(this.checked){            
	            	parentTR.prevAll().each(function(){
	            		  var checkbox = $(this).find("input[action='D']");
	                    if(checkbox.val() && checkbox.val().indexOf(delText)==0){
	                    	checkbox.attr("checked",true);
	                    }
	                });
	            	parentTR.nextAll().each(function(){
	            		 var checkbox = $(this).find("input[action='D']");
	                    if(checkbox.val() && checkbox.val().indexOf(delText)==0){
	                    	checkbox.attr("checked",true); 
	                    }
	                });
	            }else{
	            	parentTR.prevAll().each(function(){
	                       var checkbox = $(this).find("input[action='D']");
	                       if(checkbox.val() && checkbox.val().indexOf(delText)==0){
	                        checkbox.attr("checked",false);
	                       }
	                   });
	                  parentTR.nextAll().each(function(){
	                      var checkbox = $(this).find("input[action='D']");
	                       if(checkbox.val() && checkbox.val().indexOf(delText)==0){
	                        checkbox.attr("checked",false); 
	                       }
	                   });
	            }
	        });
	    });
   </script>
</head>
<body tabId="1">
   <form action="changes.html?method=commit" method="post">
	   <table class="infopanel" width="100%" border="0" cellpadding="0"
	      cellspacing="0">
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
	                  <td width="100%"></td>
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
	                  <input type="submit" class="button" value="Submit"/></td>
	            </tr>
	         </table>
	         </td>
	      </tr>
	   </table>
	   <table id="table_list_of_revisions"  class="list" rules="all" width="100%" cellpadding="0" cellspacing="0">
	      <tr class="second">
	         <th align="left" nowrap="true"><input id="checkall" name="checkall" type="checkbox" >Changed resources</th>
	         <th width="5%" nowrap="true"><a href="#">Revision </a></th>
	      </tr>
		   <c:if test="${fn:length(diffStatus) eq 0}">
		     <tr>
		          <td>there is not change</td>
		          <td></td>
		     </tr>
		   </c:if>
	      <c:forEach items="${diffStatus}" var="diffElement">
	            <tr class="first" >
	              <td width="50%"><table width="100%" border="0" cellpadding="0" cellspacing="0">
	                    <tr>
	                      <td class="internal" style="padding-right: 5px;"><input name="items" type="checkbox" value="${diffElement.path}" action="${diffElement.status }">
	                      </td>
	                      <td class="internal" style="padding-right: 5px;"><a href="changes.html?method=change&path=${diffElement.path}&action=${diffElement.status }"><span class="image_link ${diffElement.status }"></span></a></td>
	                      <td class="internal" width="100%" nowrap="true"><a href="changes.html?method=change&path=${diffElement.path}&action=${diffElement.status }">${diffElement.path}</a> </td>
	                    </tr>
	                </table></td>
	              <td align="center"><a href="revisionList.jsp.htm"> <img src="image/revision.gif" alt="Revision list" title="Revision list" border="0"> </a> </td>
	            </tr>
	        </c:forEach>
	   </table>
   </form>
</body>
</html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
   <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
   <title>OpenRemote Beehive - Changes From Update</title>
     <script type="text/javascript">
       $(document).ready(function() {
           if($('#updateStatus').val() == "true"){
               $('#updateInfoTd').text(" The updating is running, please commit changes later.");
               $("#commitSubmit").attr("disabled","true").addClass("disabled_button");
           }
           if($('#commitStatus').val() == "true"){
                $('#updateInfoTd').text(" The committing is running...");
                $("#commitSubmit").attr("disabled","true").addClass("disabled_button");
           }
           $('#submitForm').ajaxForm(function() {
               $('#updateInfoTd').text(" Commit succeeds!");
               window.location='';
           });
           $("#commitSubmit").click( function() {
               $('#updateInfoTd').text(" The committing is running...");
               $("#commitSubmit").attr("disabled","true").addClass("disabled_button");
               $('#submitForm').submit();
           });
           $("#checkall").click( function() {
               $("input[name='items']").attr("checked",this.checked);
           });
       });
       
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
	                  <td id="updateInfoTd" style="text-align:center;font-size:14px" width="100%"></td>
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
		     <tr id="msg_tr" style="${hasContent? '' : 'display:none'}">
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
	           <c:forEach items="${diffStatus}" var="diffElement">
	            <tr class="first" >
	              <td width="50%"><table width="100%" border="0" cellpadding="0" cellspacing="0">
	                    <tr>
	                      <td class="internal" style="padding-right: 5px;"><input name="items" type="checkbox" value="${diffElement.path}|${diffElement.status}" action="${diffElement.status }">
	                      </td>
	                      <td class="internal" style="padding-right: 5px;"><a href="changes.htm?method=change&path=${diffElement.path}&action=${diffElement.status }"><span class="image_link ${diffElement.status }"></span></a></td>
	                      <td class="internal" width="100%" nowrap="true"><a href="changes.htm?method=change&path=${diffElement.path}&action=${diffElement.status }">${diffElement.path}</a> </td>
	                    </tr>
	                </table></td>
	              <td align="center"><a href="history.htm?method=getRevisions&path=${diffElement.path}"> <img src="image/revision.gif" alt="Revision list" title="Revision list" border="0"> </a> </td>
	            </tr>
	           </c:forEach>
		      </form>
	   </table>
</body>
</html>
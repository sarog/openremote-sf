<%@ page language="java" contentType="text/html; charset=UTF8"
    pageEncoding="UTF8"%>
<%@ include file="../../common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF8">
<title>OpenRemote Beehive - Revision List</title>
<script type="text/javascript">
	$(document).ready(function() {
		  	var paths = $("#filePath").val().split("/");
		  	if(paths.length < 3){
		  		$("#compareLink").css("display","none");
		  	}
	});
   function compareFiles(){
	   var rev1 = 0;
	   var rev2 = 0;
	   $('input[name="items"][checked="true"]').each(function(i){
		   if(i==0){
	        rev1 = $(this).val();
		   }
		   if(i==1){
			   rev2 = $(this).val();
		   }
		   if(i==2){
			   alert("Please compare two versions!");
			   return;
		   }
	    });
	    if(rev1 == 0){
		    alert("Please select two versions to compare!");
		    return;
	    }
	    if(rev1!=0 && rev2!=0){
		   var filePath = $("#filePath").val();
		   location.href="history.htm?method=compare&path="+filePath+"&rev1="+rev1+"&rev2="+rev2;
	    }
   }
</script>
</head>
<body tabId="3">
   <input id="filePath" type="hidden" value="${breadcrumbPath}"/>
	<table class="infopanel" width="100%" border="0" cellpadding="0"
		cellspacing="0">
			<tr>
				<td>
				<table width="100%" border="0" cellpadding="0" cellspacing="0">
						<tr class="path_node">
							<td nowrap="true"><span class="pathFormat">
							Path&nbsp;:&nbsp; </span></td>
                     <td width="100%" nowrap="true">
                        <jsp:include page="breadcrumb.jsp" flush="true">
                           <jsp:param name="breadcrumbPath" value="${breadcrumbPath}" />
                        </jsp:include>
                     </td>
						</tr>
				</table>
				</td>
			</tr>
			<tr>
				<td width="100%">
					<table class="tabcontent" width="100%" border="0" cellpadding="0"
						cellspacing="0">
							<tr nowrap="true">
								<td class="value" style="padding-left: 20px;" nowrap="true"><b>Head revision:</b>&nbsp; ${headRevision}</td>
								<td class="value" style="padding-left: 20px;" nowrap="true"><b>Displayed revisions:</b>&nbsp; ${fn:length(logMessages)}</td>
								<td width="100%"></td>
							</tr>
					</table>
		       </td>
				 <td>
				 <table class="tabcontent" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td style="padding-right: 7px;" align="center"><a id="compareLink"
								href="javascript:compareFiles()"> <img src="image/diff.gif"
								alt="Diff" title="Diff"
								style="margin: 0pt; padding: 0pt; cursor: pointer;" border="0">
							</a></td>
						</tr>
				 </table>
				</td>
			</tr>
	</table>
	<table id="table_list_of_revision" class="list" rules="all"
		width="100%" cellpadding="0" cellspacing="0">
		<thead>
		<tr class="second">
			<th width="20%" nowrap="true"><a href="#">Revision</a></th>
			<th width="30%" nowrap="true"><a href="#">Age</a></th>
			<th width="10%" nowrap="true"><a href="#">Author</a></th>
			<th width="40%" nowrap="true"><a href="#">Comment</a></th>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="${logMessages}" var="logMessage">
			<tr class="first">
				<td>
					<table width="100%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td class="internal" style="padding-right: 5px;"><input
								name="items" type="checkbox" value="${logMessage.revision}"></td>
							<td class="internal" width="100%" nowrap="true">
							  <a href="#">${logMessage.revision}</a>
							</td>
						</tr>
					</table>
				</td>
				<td align="center">${logMessage.age}<input type="hidden" value="${logMessage.date }"/></td>
				<td align="center">${logMessage.author}</td>
				<td align="center">${logMessage.comment}</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</body>
</html>
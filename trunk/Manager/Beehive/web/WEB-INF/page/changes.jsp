<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
   <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
   <title>OpenRemote Beehive - Changes From Update</title>
</head>
<body>
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
               <td width="23" align="left" style="padding-right: 7px;"><a
                  href="#"><img src="image/database_go.gif" alt="commit"
                  title="commit" border="0"></a></td>
            </tr>
         </table>
         </td>
      </tr>
   </table>
   <table id="table_list_of_revisions"  class="list" rules="all" width="100%" cellpadding="0" cellspacing="0">
      <tr class="second">
         <th align="left" nowrap="true">Changed resources</th>
         <th width="5%" nowrap="true"><a href="#">Revision </a></th>
      </tr>
      <c:forEach items="${diffStatus}" var="diffElement">
            <tr class="first" >
              <td width="50%"><table width="100%" border="0" cellpadding="0" cellspacing="0">
                    <tr>
                      <td class="internal" style="padding-right: 5px;"><input name="items" type="checkbox" >
                      </td>
                      <td class="internal" style="padding-right: 5px;"><a href="changes.html?method=change&path=${diffElement.path}&action=${diffElement.status }"><span class="image_link ${diffElement.status }"></span></a></td>
                      <td class="internal" width="100%" nowrap="true"><a href="changes.html?method=change&path=${diffElement.path}&action=${diffElement.status }" > ${diffElement.path} </a> </td>
                    </tr>
                </table></td>
              <td align="center"><a href="revisionList.jsp.htm"> <img src="image/revision.gif" alt="Revision list" title="Revision list" border="0"> </a> </td>
            </tr>
        </c:forEach>
   </table>
</body>
</html>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html> 
   <head>
      <meta http-equiv="Content-Type" 
              content="text/html; charset=UTF-8">
      <title><decorator:title default="OpenRemote Beehive" /></title>
      <link href="css/default.css" type="text/css" rel="stylesheet" media="screen">
      <link href="css/table.css" type="text/css" rel="stylesheet" media="screen">
      <script type="text/javascript" src="jslib/jquery-1.3.1.min.js"></script>
      <decorator:head />
   </head>
   <body>
      <table width="100%" cellpadding="0" cellspacing="0" height="100%">
          <tr>
            <td><table width="100%" cellpadding="0" cellspacing="0" height="100%">
                  <tr>
                    <td><table class="actionbar_head" width="100%" cellpadding="0" cellspacing="0">
                          <tr>
                            <td style="padding-left: 8px; padding-right: 7px;"><a href="http://www.openremote.org/"><img src="image/global.logo.gif" alt="openremote.org" title="openremote.org" style="margin: 0pt; padding: 0pt; vertical-align: middle;" border="0"></a> </td>
                            <td class="title" nowrap="nowrap">Beehive Administration Client</td>
                            <td width="100%"></td>
                            <td style="padding-left: 8px; padding-right: 7px;" class="title"><a href="http://192.168.4.100:8080/svnwebclient/logout.jsp"> Logout </a> </td>
                          </tr>
                      </table></td>
                  </tr>
              </table></td>
          </tr>
          <tr>
            <td style="padding: 2px 5px 0pt;"><table width="100%" cellpadding="0" cellspacing="0" height="100%">
                  <tr>
                    <td><table border="0" cellpadding="0" cellspacing="0">
                        <tr>
                          <td colspan="2" class="activetab" onClick="window.location='changes.html'"><img style="vertical-align: middle; margin-right: 3px;" title="Changes" alt="Changes" src="image/changes.gif"/>Changes </td>
                          <td colspan="2" class="inactivetab" onClick="window.location='update.html'"><img style="vertical-align: middle; margin-right: 3px;" title="Update" alt="Update" src="image/update.gif"/>Sync </td>
                          <td colspan="2" class="inactivetab" onClick="window.location='vendor_list.html'"><img style="vertical-align: middle; margin-right: 3px;" title="History" alt="History" src="image/history.gif"/>History </td>
                        </tr>
                    </table></td>
                  </tr>
              </table></td>
          </tr>
          <tr>
             <td style="padding: 0px 5px 0pt;" width="100%" height="100%" valign="top">
                <decorator:body />
             </td>
          </tr>
         <tr>
            <td valign="bottom"><table class="footer" style="border-width: 1px 0pt 0pt; border-top: 1px solid black;" width="100%" cellpadding="0" cellspacing="0">
                  <tr>
                    <td style="padding-left: 10px; padding-right: 5px;" align="left" nowrap="true"><a class="footer" href="http://www.openremote.org/" target="_blank">© OpenRemote Software 2008</a> </td>
                    <td style="padding-left: 5px; padding-right: 5px;" width="100%" align="center" nowrap="true"><a class="footer" href="http://www.openremote.org/" target="_blank">Powered by OpenRemote Beehive</a> </td>
                    <td style="padding-left: 5px; padding-right: 10px;" align="right" nowrap="true"><a class="footer" href="http://www.openremote.org/" target="_blank">Support and Additional Information</a> </td>
                  </tr>
              </table></td>
          </tr>
      </table>
   </body>
</html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<jsp:useBean id="now" class="java.util.Date" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta content="openremote, knx, iphone, insteon, x10, infrared, crestron, zigbee, opensource, gpl, iknx, lirc, beehive, modeler, uicomposer" name="KEYWORDS"/>
<link href="image/OpenRemote.Logo.16x16.png" rel="shortcut icon"/>
<link href="image/OpenRemote.Logo.16x16.png" type="image/png" rel="icon"/>
<title>Access Denied - OpenRemote Boss 2.0 Online</title>
<style type="text/css">
    body{
        line-height:100%;
        background-color:#D2D1D0;
        font-family:'Lucida Grande',Geneva,Verdana,Arial,sans-serif;
    }
   .center-div{
      margin:auto;
      font-size:11px;
      color:#4D4D4D;
      width:500px;
   }
   a img{
        border:none;
    }
    .copyright{
        text-align: center;
    }
    p.title {
      text-align: center; 
      font-weight: bold; 
      font-size: 13px;
    }
    p.fail{
        color:red;
        text-align: center;
    }
    div.inner-boundary {
      border:1px solid #A6A6A6;
   }
   div.inner-border  {
      border:1px solid #FEFEFE;
      background-color:#E0E0E0;  
      padding:20px;
   }
   a, a:hover {
      color:#225E8A;
      text-decoration:none;
   }
</style>
</head>
<body>
	<div class="center-div">
         <div class="inner-boundary">
            <div class="inner-border">
               <a href="http://www.openremote.org" ><img src="image/global.logo.png" /></a>
               <p class="title">Login to OpenRemote Boss 2.0 Online</p>
               <p class="fail">Guest user can't access to OpenRemote Boss 2.0 Online! Maybe you can sync your controller with this account.</p>
               <p class="copyright">Copyright &copy; 2008-<fmt:formatDate value="${now}"pattern="yyyy" /> 
               <a href="http://www.openremote.org">OpenRemote</a>.</p>
            </div>
         </div>
	</div>
</body>
</html>
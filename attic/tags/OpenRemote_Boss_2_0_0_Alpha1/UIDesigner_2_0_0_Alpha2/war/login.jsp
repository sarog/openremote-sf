<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<jsp:useBean id="now" class="java.util.Date" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta content="openremote, knx, iphone, insteon, x10, infrared, crestron, zigbee, opensource, gpl, iknx, lirc, beehive, modeler, uicomposer" name="KEYWORDS"/>
<link href="http://community.openremote.org/download/attachments/295282/OpenRemote+Logo+16x16.png" rel="shortcut icon"/>
<link href="http://community.openremote.org/download/attachments/295282/OpenRemote+Logo+16x16.png" type="image/png" rel="icon"/>
<title>Login - Modeler</title>
<style type="text/css">
    body{
        line-height:100%;
    }
	.form_label{
	    padding-right:10px;
	}
	.form{
	   margin:auto;
	   font-family:Arial,sans-serif;
	   font-size:11px;
	   color:#446689;
	   width:500px;
	   border:2px solid #CCC;
	   background-color:#f7f7f7;
	   padding:20px;
	}
	a img{
        border:none;
    }
    .login_submit{
        width:70px;
        margin-left: 60px;
    }
    .copyright{
        text-align: center;
    }
    p.fail{
        color:red;
    }
    p.pass{
        color:green;
    }
    .register_btn{
    	margin-left: 40px;
    }
</style>
</head>
<body>

<form method="POST" action="j_security_check"/>
      <div class="form">
            <a href="http://www.openremote.org" ><img src="image/global.logo.png" /></a>
            <div style="padding-left:120px">
	            <h3>Login to Modeler</h3>
	            <p><b class="form_label">Username</b><input id="username" style="width:150px" type="text" name="j_username" value="${username}"></p>
	            <c:if test="${success ne null and success}">
	                <p class="pass"><b>${username}</b> has been created.</p>
                </c:if>
	            <p><b class="form_label">Password</b><input id="password" style="width:150px" type="password" name="j_password" value=""></p>
                <c:if test="${param.fail ne null }">
	                <p class="fail">The username or password you entered is incorrect.</p>
                </c:if>
                <p>
                	<input id="rememberme" type="checkbox" name="_spring_security_remember_me"><label for="rememberme">remember me</label>
                	<a class="register_btn" href="register.jsp">Create an account</a>
                </p> 
	            <div><input class="login_submit" type="submit" value="Login"></div>
            </div>                       
	        <p class="copyright">Copyright &copy; 2008-<fmt:formatDate value="${now}"pattern="yyyy" />  <a href="http://www.openremote.org">OpenRemote</a>. All rights reserved.</p>            
      </div>
</form>
</body>
</html>
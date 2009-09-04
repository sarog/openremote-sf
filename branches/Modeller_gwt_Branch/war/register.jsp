<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<jsp:useBean id="now" class="java.util.Date" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
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
    .register_submit{
        width:150px;
        margin-left: 60px;
    }
    .copyright{
        text-align: center;
    }
    p.fail{
        color:red;
    }
    .register_btn{
    	margin-left: 40px;
    }
</style>
</head>
<body>

<form method="POST" action="account.htm?method=create"/>
      <div class="form">
            <a href="http://www.openremote.org" ><img src="image/global.logo.png" /></a>
            <div style="padding-left:120px">
	            <h3>Create a Modeler Account</h3>
	            <p>If you already have a Modeler Account, you can <a href="login.jsp">login here</a>.</p>
	            <p><b class="form_label">Desired username</b><input id="username" style="width:150px" type="text" name="username" value="${username}"></p>
	            <c:if test="${success ne null and not success}">
	                <p class="fail"><b>${username}</b> is not available, choose another.</p>
                </c:if>
                <c:if test="${username_blank ne null}">
	                <p class="fail">Required field cannot be left blank.</p>
                </c:if>
	            <p><b class="form_label">Choose password</b><input id="password" style="width:150px" type="password" name="password" value="${password}"></p>
	            <c:if test="${password_error ne null}">
	                <p class="fail">Passwords do not match.</p>
                </c:if>
                <c:if test="${password_blank ne null}">
	                <p class="fail">Required field cannot be left blank.</p>
                </c:if>
	            <p><b class="form_label">Re-type password</b><input id="r_password" style="width:150px" type="password" name="r_password" value="${r_password}"></p>
                <c:if test="${r_password_blank ne null}">
	                <p class="fail">Required field cannot be left blank.</p>
                </c:if>
                <p>
                	<b class="form_label">Choose your role</b>
                	<input id="role_bm" type="checkbox" name="role" value="role_bm" checked><label for="role_bm">Building Modeler</label>
                	<input id="role_ud" type="checkbox" name="role" value="role_ud" checked><label for="role_ud">UI Designer</label>
                </p>
                <c:if test="${role_blank ne null}">
	                <p class="fail">You must choose at least one role.</p>
                </c:if>
				<div><input class="register_submit" type="submit" value="Create my account"></div>
            </div>                       
	        <p class="copyright">Copyright &copy; 2008-<fmt:formatDate value="${now}"pattern="yyyy" />  <a href="http://www.openremote.org">OpenRemote</a>. All rights reserved.</p>            
      </div>
</form>
</body>
</html>
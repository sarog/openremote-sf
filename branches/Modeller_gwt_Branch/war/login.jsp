<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Login - Modeler</title>
<style type="text/css">
	.form_label{
	    padding-right:10px;
	}
	.form{
	   margin:auto;
	   font-family:Arial,sans-serif;
	   font-size:10px;
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
</style>
</head>
<body>

<form method="POST" action="j_security_check"/>
      <div class="form">
            <a href="http://www.openremote.org" ><img src="image/global.logo.png" /></a>
            <div style="padding-left:120px">
	            <h3>Login to Modeler</h3>
	            <p><b class="form_label">Username</b><input style="width:150px" type="text" name="j_username" value="super"></p>
	            <p><b class="form_label">Password</b><input style="width:150px" type="password" name="j_password" value="123"></p>
	            <input class="login_submit" type="submit" value="Login">
            </div>                       
	        <p class="copyright">Copyright &copy; 2008-2009  <a href="http://www.openremote.org">OpenRemote</a> All rights reserved.</p>            
      </div>
</form>
</body>
</html>
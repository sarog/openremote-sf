<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<jsp:useBean id="now" class="java.util.Date" />
<!DOCTYPE html>
	<!--[if IE]><![endif]-->
 	<!--[if lt IE 9]>  <html lang="en" class="oldie"><![endif]-->
 	<!--[if !IE]><!--> <html lang="en"> <!--<![endif]-->
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0" />
	<link rel="icon" type="image/png" href="image/favicon-32x32.png" sizes="32x32" />
	<link rel="icon" type="image/png" href="image/favicon-16x16.png" sizes="16x16" />
	<!--[if IE]><link rel="shortcut icon" type="image/x-icon" href="image/favicon.ico" /><![endif]-->
	<link rel="stylesheet" type="text/css" href="resources/css/loginPage.css" />
	<style>
		#username {
			background-color: #efefef;
    		border: medium none;
    		font-weight: 400;
    		height: 20px;
    		padding: 7px 8px;
			margin-left:14px;
	  	} 
		p {
		padding:10px;
		}
	</style>
</head>

<body>
	<div style="width:450px;margin-left:auto;margin-right:auto;background-color:white;text-align:center;padding:20px;margin-top:100px;">
		<header>
			<h1 id="logo">
				<a href="http://www.openremote.com">
					<img src="image/or_logo.png" alt="OpenRemote | Open Source for Internet of Things" width="403" height="124"/>
				</a>
			</h1>
		</header>
	<form method="POST" action="account.htm?method=forgetPassword">
		<div class="inner-boundary">
			<div class="inner-border">
            	<p class="title">Forget your password?</p>
            		<c:if test="${needReset eq null}">
	       		     	<div style="padding:10px">
		        	    	<p class="input">Username
		            		<input id="username" style="width:150px" type="text" name="username" value="${username}"></p>
						</div>
			
						<c:if test="${isUserAvailable ne null and not isUserAvailable}">
                			<p class="incorrect">Invalid username, get password failed.</p>
            			</c:if>
				
						<c:if test="${username_blank ne null}">
                      		<p class="incorrect">Username cannot be left blank.</p>
                		</c:if>
                   </c:if>                    
                	
			</div>                  
           	<c:if test="${needReset eq null}">
				<div style="padding:10px">
	            	<input class="send_submit" type="submit" value="Submit">
    	        </div>
			</c:if>
			<c:if test="${needReset ne null}">
                			<p class="pass">We have sent you an email to <b>${email}</b>,
                	 		please follow the instructions in the email to reset your password.</p>
            </c:if>
		</div>
	        <p class="copyright">Copyright &copy; 2008-<fmt:formatDate value="${now}" pattern="yyyy"/> 
	        <a href="http://www.openremote.org">OpenRemote</a>.</p>
		  </div>
		</div>
	</form>
</div>
</body>
</html>
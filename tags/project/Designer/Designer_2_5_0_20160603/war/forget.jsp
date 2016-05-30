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

    <title>OpenRemote Designer</title>
  </head>

<body>
	<div class="forget">
		<article id="login">
			<header>
				<h1 id="logo">
					<a href="http://www.openremote.com">
						<img src="image/or_logo.png" alt="OpenRemote | Open Source for Internet of Things" width="403" height="124"/>
					</a>
				</h1>
				<h2>Forgot your Password?</h2>
			</header>
							
			<form method="POST" action="account.htm?method=forgetPassword">
            		<c:if test="${needReset eq null}">
	       		     	<div id="field">
		        	    	<p>
								<label for="username">Username</label>
		            			<input id="username"  type="text" name="username" value="${username}">
							</p>
						</div>
						<p class="incorrect" style="height:30px;padding-bottom:10px;padding-left:120px;">
							<c:if test="${isUserAvailable ne null and not isUserAvailable}">
                					Invalid username, get password failed.
            				</c:if>
							<c:if test="${username_blank ne null}">
                      					Username cannot be left blank.
							</c:if>
                		</p>
                   	</c:if>                    
                	<div id="accept">
           				<c:if test="${needReset eq null}">
	            			<input class="send_submit" type="submit" value="Submit">
						</c:if>
					</div>
					<c:if test="${needReset ne null}">
                		<p class="pass">We have sent you an email to <b>${email}</b>,
                	 		please follow the instructions in the email to reset your password.</p>
            		</c:if>
			</form>
			<footer>
				<p>Copyright &copy;2008-2016 <span><a href="http://www.openremote.org">OpenRemote</a></span></p>
			</footer>
		</article>
	</div>
	</body>
</html>


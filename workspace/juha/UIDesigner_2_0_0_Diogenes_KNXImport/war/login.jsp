<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:useBean id="now" class="java.util.Date" />

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
  <head>
    <meta http-equiv = "Content-Type" content = "text/html; charset=UTF-8">
    <meta content = "openremote, open source, home automation, iphone, android,
                     knx, insteon, x10, infrared, z-wave, zigbee, isy-99, russound,
                     lutron, domintell"
          name = "KEYWORDS"/>

    <link href = "image/OpenRemote.Logo.16x16.png" rel = "shortcut icon"/>
    <link href = "image/OpenRemote.Logo.16x16.png" type = "image/png" rel = "icon"/>

    <title>OpenRemote Designer</title>

    <style type="text/css">

      BODY
      {
        background-color: rgba(255, 255, 255, 1.0);
        color: rgba(107, 92, 79, 1.0);

        border-width: 0px;
        border-style: none;

        font-family: Verdana, Arial, sans-serif;
      }


      DIV.main
      {

      }

      IMG.watermark
      {
        opacity:     0.20;
        overflow:    visible;

        z-index:     -10;

        position:    relative;
        top:         -350px;
        left:        -400px;
      }


      DIV.warning-notice
      {
        margin:              20px 10px;
        padding:             20px;

        box-shadow:          0px 0px 35px rgba(212, 71, 15, 0.4);
        -moz-box-shadow:     0px 0px 35px rgba(212, 71, 15, 0.4);
        -webkit-box-shadow:  0px 0px 35px rgba(212, 71, 15, 0.4);

        background-color:    rgba(212, 71, 15, 0.6);
        color:               rgba(156, 48, 26, 0.95);

        border-width:        3px;
        border-color:        rgba(156, 48, 26, .8);
        border-style:        solid;

        border-radius:         15px;
        -moz-border-radius:    15px;
        -webkit-border-radius: 15px;

        display:             none;
      }
      
      .form_label
      {
        padding-right:      10px;
      }

      .center-form
      {
        margin:             8em auto;
        font-size:          11px;
        color:              #4D4D4D;
        width:              500px;
      }

      a img
      {
        border:             none;
      }

      .login_submit
      {
        width:              70px;
        margin-left:        80px;
      }

      .copyright
      {
        text-align:         center;
      }

      p.title
      {
        text-align:         center;
        font-weight:        bold;
        font-size:          13px;
      }

      p.fail
      {
        color:              red;
        text-align:         center;
      }

      p.pass
      {
        color:              green;
        text-align:         center;
      }

      .register_btn
      {
        margin-left:        40px;
      }

      div.inner-boundary
      {
        border:             1px solid #A6A6A6;
      }

      div.inner-border
      {
        border:             1px solid #FEFEFE;
        background-color:   rgba(230, 219, 209, .7);
        padding:            20px;
      }

      p.input
      {
        text-align:         right;
        width:              240px;
      }

      a, a:hover
      {
        color:              #225E8A;
        text-decoration:    none;
      }

      .incorrect
      {
        color:              red;
        text-align:         left;
      }

      .forget_btn
      {
        margin-left:         10px;
      }

    </style>
  </head>

  <body>

    <div class = "main">


      <!-- ============ LOGO TITLE ======================================= -->

      <div style = "text-align: center;">
        <p>
          <a href = "http://www.openremote.org">
            <img src = "http://www.openremote.org/download/attachments/11960338/OpenRemote-singleline-full-logo_400x62.png"
                 border = "0"/>
          </a>
        </p>
      </div>

      <div class="center-form">

        <!-- ========== OPTIONAL WARNING NOTICE ========================= -->

        <div class = "warning-notice">

        </div>

        <!-- ========== LOGIN FORM ====================================== -->

        <form method="POST" action="j_security_check">

          <div class="inner-boundary">
            <div class="inner-border">

              <p class="title">Login to OpenRemote Designer Alpha (Voldemort)</p>

              <c:if test = "${isActivated ne null and isActivated}">
                <p class="pass"><b>${username}</b> has been activated, please login.</p>
              </c:if>

              <c:if test = "${isActivated ne null and not isActivated}">
                <p class="fail">Invalid activation credentials, activation failed.</p>
              </c:if>

              <c:if test = "${needActivation ne null}">
                <p class="pass">
                   We have sent an activation email to <b>${email}</b>,
                   please follow the instructions in the email to complete
                   your registration.
                </p>
              </c:if>

              <c:if test = "${isAccepted ne null and isAccepted}">
                <p class = "pass">You have accepted the invitation, please login.</p>
              </c:if>

              <c:if test = "${needActivation eq null}">
                <div style = "padding-left:110px">
                  <p class = "input">
                    <b class = "form_label">Username</b>
                    <input id = "username"
                           style = "width:150px"
                           type = "text"
                           name = "j_username"
                           value = "${username}">
                  </p>

                  <p class = "input">
                    <b class = "form_label">Password</b>
                    <input id = "password"
                           style = "width:150px"
                           type = "password"
                           name = "j_password"
                           value = "">
                  </p>

                  <c:if test = "${param.fail ne null }">
                    <p class = "incorrect">The username or password you entered is incorrect.</p>
                  </c:if>

                  <p>
                    <input id = "rememberme" type = "checkbox" name = "_spring_security_remember_me">
                      <label for = "rememberme">Remember Me</label>

                    <a class = "register_btn" href = "register.jsp">Create a New Account</a>
                  </p>

                  <div>
                    <input class = "login_submit" type = "submit" value = "Login">

                    <a class = "forget_btn" href = "forget.jsp">Forgot password?</a>
                  </div>
                </div>
              </c:if>

            </div>
		  </div>
	    </form>

        <p class = "copyright">
            Copyright &copy; 2008-<fmt:formatDate value = "${now}" pattern = "yyyy"/>
            <a href="http://www.openremote.org">OpenRemote</a> -- SNAPSHOT 20111212
        </p>

        <img class = "watermark" src = "http://www.openremote.org/download/attachments/11468891/OpenRemote iTunes Icon 512x512.png"
             align = "absmiddle" border = "0"/>

      </div>
    </div>
  </body>
</html>


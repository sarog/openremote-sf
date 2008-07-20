<!--
 | OpenRemote, the Internet-enabled Home.
 |
 | This work is licensed under Creative Commons Attribution-Noncommercial-Share Alike 3.0
 | United States, http://creativecommons.org/licenses/by-nc-sa/3.0/us/
 +-->
<html>
  <head>
    <title>OpenRemote - User Registration</title>

    <meta name = "revision" content = "$Id: $"/>
    <meta name = "author"   content = "Juha Lindfors"/>
    <meta name = "license"  content = "Creative Commons BY-NC-SA 3.0 US"/>
    <meta name = "description" content = "OpenRemote User Registration"/>
    <meta name = "keywords" content = "openremote, register"/>
  </head>

  <%@ page pageEncoding = "UTF-8" %>


  <body>

    <form action = "/user/register" method = "POST">

      Login name: <input name = "loginname" type = "text" maxlength = "255"/>

      <br/>

      Password: <input name = "password" type = "password" maxlength = "255"/>

      <br/>

      Verify Password: <input name = "verifypassword" type = "password" maxlength = "255"/>

      <br/>

      Email: <input name = "email" type = "text" maxlength = "255"/>

      <br/>

      Verify Email: <input name = "verifyemail" type = "text" maxlength = "255"/>

      <br/>

      Controller Serial Number: <input name = "serialnumber" type = "text" maxlength = "20"/>

      <br/>

      <input type = "submit" value = "Register"/>

    </form>

  </body>

</html>

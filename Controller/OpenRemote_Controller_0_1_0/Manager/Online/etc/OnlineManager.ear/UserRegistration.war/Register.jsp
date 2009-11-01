<!--
OpenRemote, the Home of the Digital Home.
Copyright 2008, OpenRemote Inc.

See the contributors.txt file in the distribution for a
full listing of individual contributors.

This is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3.0 of
the License, or (at your option) any later version.

This software is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

You should have received a copy of the GNU General Public
License along with this software; if not, write to the Free
Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
02110-1301 USA, or see the FSF site: http://www.fsf.org.
-->
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

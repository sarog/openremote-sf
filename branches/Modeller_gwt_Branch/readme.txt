
================================================================================
===                                                                          ===
===                 MODELER DEPLOYMENT GUIDE                                 ===
===                                                                          ===
=== version 1.0.0                                http://www.openremote.org   ===
================================================================================

I. Introduction 
===============
The modeler allows home-owners and professional installers to configure devices and appliances of a building visually against a floor plan. 
Device configurations can be retrieved from Beehive database and scenes and event controls can be modeled visually.

II. Requirements
================
The "Beehive" requires this 3rd party software:
1) GWT 1.6.4 or above
2) JBoss 4.2.3 GA or above, Tomcat 5.5.26 or above
3) Java 1.5 or above
4) MySQL 5.0 or above
5) Ant 1.7.1 or above

III. "Modeler" site deployment
==============================================
1) create a database named 'modeler' in MySQL.
2) modify following configuration variables in "%PROJECT_ROOT%/config/config.properties"
    file (see comments inside this file for details):
    a) set "jdbc.url" parameter value to MySQL URL 
    b) set "jdbc.username" parameter value to username to MySQL (for example: scott)
    c) set "jdbc.password" parameter value to password to MySQL (for example: tiger)
    d) set "beehive.REST.Url" parameter value to your Beehive REST URL
    
3) modify following configuration variables in "%PROJECT_ROOT%/build.properties"
    a) set parameter 'gwt.sdk' in build.properties to your GWT SDK home. This is required by GWT build.
    b) set parameter 'deploy.dir' in build.properties to your webapps folder of Tomcat.
     
4) run 'ant deploy' in command line. 

   
IV. Supported functions
=======================
In the current iteration we are focusing on infrared (LIRC), KNX, X10, HTTP, TCP/IP, Telnet protocol.

Building Modeler:
    1) Device
    2) DeviceCommnad
    3) DeviceMacro

V. "Modeler" logging
====================================
You can modify the log4j configuration in "%PROJECT_ROOT%/config/log4j.properties".
All the logs will be written in "%WEBAPP_ROOT%/logs" 

VI. "Modeler" javadoc
====================================
You can generate javadoc by using "ant javadoc"
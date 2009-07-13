
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
1) JBoss 4.2.3 GA or above, Tomcat 5.5.26 or above
2) Java 1.5 or above
3) MySQL 5.0 or above
4) Ant 1.7.1 or above

III. "Modeler" site deployment
==============================================
1) run "ant war" to get the war file in "%PROJECT_ROOT%/output" directory;
2) deploy the war into your web server;
3) modify Beehive REST_API_URL in "%WEBAPP_ROOT%/js/constant.js",
   "beehive/rest" should be the default value at the end of REST_API_URL.
4) To speed up the site, we use Jawr as a tunable packaging solution for Javascript and CSS.
    modify jawr configure in %WEBAPP_ROOT%/WEB-INF/classes/jawr.properties :
   a)jawr.debug.on = false
   b)jawr.gzip.on  = true
   
IV. Supported functions
=======================
In the current iteration we are focusing on infrared (LIRC), KNX, X10 device.
We have set several goals for users:

1) Multi screen
2) Resize button on panel
3) Macro
4) Customize icon

V. "Modeler" logging
====================================
You can modify the log4j configuration in "%PROJECT_ROOT%/config/log4j.properties".
All the logs will be written in "%WEBAPP_ROOT%/logs" 

VI. "Modeler" javadoc
====================================
You can generate javadoc by using "ant javadoc"
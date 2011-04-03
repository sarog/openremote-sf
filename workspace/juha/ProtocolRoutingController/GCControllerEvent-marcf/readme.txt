
================================================================================
===                                                                          ===
===                 CONTROLLER DEPLOYMENT GUIDE                              ===
===                                                                          ===
=== version 1.0.0                                http://www.openremote.org   ===
================================================================================

I. Introduction 
===============
http://community.openremote.org/display/orb/OpenRemote+Controller+Software

II. Requirements
================
The "Controller" requires this 3rd party software:
1) JBoss 4.2.3 GA or above, Tomcat 5.5.26 or above
2) Java 1.5 or above
3) Ant 1.7.1 or above
4) openremote.zip, downloaded from Modeler(UI composer), containing following 3 files:

    a)lircd.conf: configuration file for lircd deamon, see http://www.lirc.org/html/lircd.html;
    b)iphone.xml: layout descriptor for iPhone;
    c)controller.xml: remote action descriptor for buttons in layout;

III. "Controller" deployment
==============================================

1) run "ant war" to get the war file in "%PROJECT_ROOT%/output" directory;

2) deploy the war into your web server;

3) put "iphone.xml" and "controller.xml" in "/webapps/controller" directory, DON'T rename the files.

IV. Supported functions
=======================
1) Serve up UI interfaces to remotes, either as web UI or as a client-server setup for native user interfaces.
2) Receive commands from the remotes (e.g act as a daemon server listening to incoming commands)
3) Translate and route commands to integrated runtimes (e.g issue a KNX or X10 command)

V. "Controller" logging
====================================
You can modify the log4j configuration in "%PROJECT_ROOT%/config/log4j.properties".
All the logs will be written in "/webapps/controller/logs" 

VI. "Controller" javadoc
====================================
You can generate javadoc by using "ant javadoc"
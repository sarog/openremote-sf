
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
4) openremote.zip, downloaded from Modeler(UI composer), containing following files:

    a)lircd.conf: configuration file for lircd deamon, see http://www.lirc.org/html/lircd.html;
    b)iphone.xml: layout descriptor for iPhone;
    c)controller.xml: remote action descriptor for buttons in layout;
    d)image: icon for button
    e)panel.irb: layout descriptor for Modeler(UI composer) which is useless here;

III. "Controller" deployment
==============================================
1) modify following configuration variables in "%PROJECT_ROOT%/config/config.properties"
    file (Normally you can run Controller without modifying these parameters):
    a) set "Macro.Execution.Delay" parameter value to a common delay, default value is 500 millisecond;
    b) set "lircd.conf.path" parameter value to the path where lircd.conf should be, default value is "/etc/lircd.conf";
    c) set "copy.lircd.conf.on" parameter value to "on" if you want Controller to copy lircd.conf to %lircd.conf.path% after you upload openremtoe.zip;
    d) set "irsend.path" parameter value to irsend installation path if you move it, default value is "/usr/local/bin/irsend"
    e) set "resource.path" parameter value to the folder where you want to store all the resources unzipped from openremote.zip
    f) set "webapp.port" parameter value to the webapp port of Controller, default value is "8080";
    g) set "multicast.address" parameter value to the address used for IP auto-discovery, default value is "224.0.1.100";
    h) set "multicast.port" parameter value to the port used for IP auto-discovery, default value is "3333";
    
2) run "ant war" to get the war file in "%PROJECT_ROOT%/output" directory;

3) deploy the war into your web server;

4) login 'localhost:8080/controller'in brower, upload the openremote.zip, check if the files are in right place:
    
    a)lircd.conf: should be in %lircd.conf.path%
    b)iphone.xml: should be in %resource.path%
    c)controller.xml:should be in %resource.path%
    d)image:should be in %resource.path%

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
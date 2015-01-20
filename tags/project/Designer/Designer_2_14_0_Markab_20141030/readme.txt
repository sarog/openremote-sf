

I. Build and Deployment Requirements
====================================

To build, you will need to install GWT SDK from Google first. Currently GWT version 2.4.0
is required. Modify the build.properties file to target your GWT 2.4.0 SDK install location.

Java SDK 6 is required for build. 

Apache ANT 1.7 or higher is required for the build scripts.


II. "Modeler" site deployment
==============================================
1) share a database named 'beehive' with Beehive in MySQL.
2) modify following configuration variables in "%PROJECT_ROOT%/config/config.properties"
    file (see comments inside this file for details):
    a) set "jdbc.url" parameter value to MySQL URL 
    b) set "jdbc.username" parameter value to username to MySQL (for example: scott)
    c) set "jdbc.password" parameter value to password to MySQL (for example: tiger)
    d) set "beehive.REST.Root.Url" parameter value to your Beehive REST Root URL
    e) set "beehive.lircdconf.REST.Url" parameter value to your Beehive REST lirc.conf URL
    f) set "beehive.RESR.Icon.Url" parameter value to your Beehive REST icons URL
    g) set "webapp.server.root" parameter value to your web server root (eg: http://localhost:8080)
    h) set "controller.config.path" parameter value to your controller config xml descriptor file path
    
    i) set "mail.sender.host" to your mail host (eg: smtp.163.com)
    j) set "mail.sender.port" to your mail port (eg: 25)
    k) set "mail.sender.username" to your mail username (eg: openremote@163.com)
    l) set "mail.sender.password" to your mail password 
    m) set "mail.smtp.auth.on" to true if your mail service needs auth.
    n) set "mail.smtp.timeout" to your mail timeout in millisecond. (eg: 25000)

    
3) modify following configuration variables in "%PROJECT_ROOT%/build.properties"
    a) set parameter 'gwt.sdk' in build.properties to your GWT SDK home. This is required by GWT build.
     

   
III. Logging
====================================
You can modify the log4j configuration in "%PROJECT_ROOT%/config/log4j.properties".


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!-- The HTML 4.01 Transitional DOCTYPE declaration-->
<!-- above set at the top of the file will set     -->
<!-- the browser's rendering engine into           -->
<!-- "Quirks Mode". Replacing this declaration     -->
<!-- with a "Standards Mode" doctype is supported, -->
<!-- but may lead to some differences in layout.   -->

<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    
    <link href="resources/images/OpenRemote.Logo.16x16.png" rel="shortcut icon"/>
    <link href="resources/images/OpenRemote.Logo.16x16.png" type="image/png" rel="icon"/>
    <link type="text/css" rel="stylesheet" href="resources/css/gxt-all.css" />
    <link type="text/css" rel="stylesheet" href="resources/css/webconsole.css" />
    <link type="text/css" rel="stylesheet" href="resources/css/image-slider.css" />

    <title>OpenRemote Boss 2.0 WebConsole</title>
    
    <script type="text/javascript" language="javascript" src="webconsole/webconsole.nocache.js"></script>
    <style type="text/css">
      #welcome-content {
         border: 1px solid #CCCCCC;
         height: auto;
         left: 50%;
         margin-left: -147px;
         padding: 2px;
         position: absolute;
         top: 40%;
         z-index: 20001;
      }
      
      #welcome-content .loading-indicator-img {
         background: none repeat scroll 0 0 white;
         color: #444444;
         font: bold 13px tahoma, arial, helvetica;
         height: auto;
         margin: 0;
         padding: 10px;
      }
      
      #welcome-content .loading-indicator-img img {
         float: left;
         margin-right: 8px;
         vertical-align: top;
      }
      
      #welcome-content #loading-msg {
         font: 10px arial, tahoma, sans-serif;
      }
      
      #error-content {
         left: 40%;
         position: absolute;
         top: 30%;
         width: 200;
         text-align: center;
         display: none;
      }
    </style>
  </head>

  <body>

    <!-- OPTIONAL: include this if you want history support -->
    <iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>
      
    <div id="welcome-content">
      <div class="loading-indicator-img">
          <img width="32" height="32" src="resources/images/large-loading.gif" />OpenRemote Boss 2.0 Web Console<br>
          <span id="loading-msg">Loading resources, please wait...</span>
       </div>
    </div>
    <div id="error-content">
      <img width="100" height="100" src="resources/images/repair_welcome.png" />
    </div>
  </body>
</html>

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

    <!--                                                               -->
    <!-- Consider inlining CSS to reduce the number of requested files -->
    <!--                                                               -->
	<link rel="stylesheet" type="text/css" href="resources/css/gxt-all.css" />
	<link rel="stylesheet" type="text/css" href="resources/css/main.css" />
	
    <!--                                           -->
    <!-- Any title is fine                         -->
    <!--                                           -->
    <title>OpenRemote Modeler</title>
    
    <!--                                           -->
    <!-- This script loads your compiled module.   -->
    <!-- If you add any GWT meta tags, they must   -->
    <!-- be added before this line.                -->
    <!--                                           -->
    <script type="text/javascript" language="javascript" src="modeler/modeler.nocache.js"></script>
    <style type="text/css">
		#loading-cont {
			border: 1px solid #CCCCCC;
			height: auto;
			left: 45%;
			margin-left: -45px;
			padding: 2px;
			position: absolute;
			top: 40%;
			z-index: 20001;
		}
		
		#loading-cont .loading-indicator-img {
			background: none repeat scroll 0 0 white;
			color: #444444;
			font: bold 13px tahoma, arial, helvetica;
			height: auto;
			margin: 0;
			padding: 10px;
		}
		
		#loading-cont .loading-indicator-img img {
			float: left;
			margin-right: 8px;
			vertical-align: top;
		}
		
		#loading-cont #loading-msg {
			font: 10px arial, tahoma, sans-serif;
		}
    
    </style>

  </head>

  <!--                                           -->
  <!-- The body can have arbitrary html, or      -->
  <!-- you can leave the body empty if you want  -->
  <!-- to create a completely dynamic UI.        -->
  <!--                                           -->
  <body>

    <!-- OPTIONAL: include this if you want history support -->
    <iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>

	<!-- The loading message container div -->
	<div id="loading-cont">
	    <div class="loading-indicator-img">
		    <img width="32" height="32" src="resources/images/large-loading.gif" />OpenRemote Modeler 2.0<br>
		    <span id="loading-msg">Loading application, please wait...</span>
	    </div>
	</div>
	
	<div id="main"></div>
	
  </body>
<head>
	<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
	<META HTTP-EQUIV="CACHE-CONTROL" CONTENT="NO-CACHE">
    <META HTTP-EQUIV="EXPIRES" CONTENT="0">
    <meta content="openremote, knx, iphone, insteon, x10, infrared, crestron, zigbee, opensource, gpl, iknx, lirc, beehive, modeler, uicomposer" name="KEYWORDS"/>
	<link href="http://community.openremote.org/download/attachments/295282/OpenRemote+Logo+16x16.png" rel="shortcut icon"/>
	<link href="http://community.openremote.org/download/attachments/295282/OpenRemote+Logo+16x16.png" type="image/png" rel="icon"/>
</head>
</html>

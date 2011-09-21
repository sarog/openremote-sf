THIS DOCUMENT OUTLINES CURRENT ADOPTION OF HTML5/CSS3/JS FUNCTIONALITY AS WELL AS DISCUSSING FUNCTIONALITY WHICH COULD BE
INTEGRATED AT A LATTER STAGE.

THE MAIN ISSUE WITH ADOPTION OF THE LATEST FUNCTIONALITY IS BROWSER SUPPORT. A GREAT WEBSITE FOR COMPARING BROWSER ADOPTION
OF THE LATEST FUNCTIONALITY IS: -

http://caniuse.com/

THE BELOW FUNCTIONALITY IS REQUIRED IN THE CURRENT WEB CONSOLE VERSION AND COMPATIBILITY ONLY LOOKS UP TO 3 VERSIONS BACK IN EACH BROWSER: -

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
CATEGORY	|	FUNCTIONALITY	|	COMPATIBILITY ISSUES																													|	NOTES
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
HTML5		|					|																																			|	
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
CSS3		|	TRANSFORMS		|	NOT SUPPORTED IN IE (PRE V9.0 - BUT CAN BE REPLICATED USING IE PROPRIETRY ZOOM EXTENSION), OPERA MINI (CURRENT), OPERA MOBILE (PRE V11)	|	USED FOR ROTATING THE CONSOLE UNIT AND CONSOLE DISPLAY (ESSENTIAL REQUIREMENT)
CSS3		|	BORDER RADIUS	|	NOT SUPPORTED IN IE (PRE V9.0), OPERA MINI (CURRENT), OPERA MOBILE (PRE V11)															|	USED FOR CONSOLE UNIT, SLIDER WIDGET (WON'T LOOK NICE BUT WILL FUNCTION WITHOUT THIS)
CSS3		|	GRADIENTS		|	NOT SUPPORTED IN IE (PRE V10), SAFARI (PRE V4), OPERA (PRE V11.1), OPERA MINI (CURRENT), OPERA MOBILE (PRE V11.1)						|	USED FOR CONSOLE UNIT FRAME (WON'T LOOK AS GOOD BUT WILL FUNCTION WITHOUT THIS)
CSS3		| 	POSITION: FIXED	|	NOT SUPPORTED IN IE (PRE V7), MOBILE SAFARI, OPERA MINI, ANDROID (PRE V2.2)																|	USED TO FIX TAB BAR TO BOTTOM OF WINDOW, WORKAROUND AVAILABLE FOR MOBILE SAFARI
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
JS			|					|																																			|			
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------




FUTURE FUNCTIONALITY: -

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
CATEGORY	|	FUNCTIONALITY		|	DESCRIPTION																											|	NOTES
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
JS       |  CORS              | CROSS ORIGIN RESOURCE SHARING ALLOWS CROSS DOMAIN XMLHTTPREQUESTS                           | SUPPORTED IN MOST BROWSERS BUT NOT OPERA AND IE8 HAS NON-STANDARD IMPLEMENTATION BACKWARDS COMPATIBILITY IS AN ISSUE
JS			|	WEB SOCKETS			|	FULL DUPLEX TCP COMMS WITH SERVER SIDE (MULTI PORT) PROCESSES OVER A SINGLE TCP SOCKET, LOCAL BROADCAST SUPPORT		| 	LOCAL BROADCAST SUPPORT (SEE http://www.w3.org/TR/2008/WD-html5-20080122/#broadcast) WOULD ALLOW MULTICAST DETECTION OF CONTROLLERS ON LAN, VERY STRICT IMPLEMENTATION REQUIREMENTS, ONLY A DRAFT AT PRESENT NO IMPLEMENTATIONS AS OF (SEPT 2011)
JS			|	FILE API			|	SANDBOXED LOCAL FILE STORAGE AND FILE ACCESS																		| 	REPLACE COOKIE USAGE (COOKIES ARE SENT WITH EVERY HTTP REQUEST AND LIMITED TO ABOUT 4K), THIS WOULD ALLOW FOR STORAGE OF USER PREFERENCES AND POSSIBLY STORAGE OF CONTROLLER FILES (XML, RESOURCES, ETC.) WITH POTENTIAL FOR CONTROLLER AND CONSOLE TO BE COMBINED INTO SINGLE WEB APP
JS			|	STREAM API			|	TWO WAY ACCESS TO EXTERNAL DEVICES (WEBCAMS, MICROPHONES, ETC.)														|	WOULD BE REQUIRED FOR SIP (MORE GENERALLY TWO WAY COMMS) WITHIN CONSOLE
JS			|	DEVICE ORIENTATION	| 	DETECT DEVICE ORIENTATION AND MOTION EVENTS																			| 	LIMITED SUPPORT AT PRESENT (HAVE A WORK AROUND WHICH INVOLVES DETECTING WINDOW RESIZE EVENTS, THIS API MAY ALLOW DISABLING OF AUTO WEB PAGE ROTATION)
HTML5		| 	OFFLINE WEB APPS	|	CACHING OF RESOURCES (SCRIPTS, IMAGES, ETC.) THROUGH MANIFEST FILE													|	WOULD BE USEFUL IF WE CAN GET WEB CONSOLE RUNNING 100% CLIENT SIDE (REQUIRES A SOLUTION FOR LAN CONTROLLER DISCOVERY)
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 	

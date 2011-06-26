package org.openremote.web.console.utils;

import com.google.gwt.user.client.Window;

	public class BrowserUtils {
		
		static final String[] MOBILE_SPECIFIC_SUBSTRING = {
	      "iphone","android","midp","opera mobi",
	      "opera mini","blackberry","hp ipaq","iemobile",
	      "msiemobile","windows phone","htc","lg",
	      "mot","nokia","symbian","fennec",
	      "maemo","tear","midori","armv",
	      "windows ce","windowsce","smartphone","240x320",
	      "176x220","320x320","160x160","webos",
	      "palm","sagem","samsung","sgh",
	      "sie","sonyericsson","mmp","ucweb","ipod"};

	
		public static boolean isMobile() {
			String userAgent = Window.Navigator.getUserAgent();
			for (String mobile: MOBILE_SPECIFIC_SUBSTRING) {
				if (userAgent.toLowerCase().contains(mobile)) {
					return true;
	         }
			}
	     return false;
		}
		
		public static boolean isWebkit() {
			String userAgent = Window.Navigator.getUserAgent();
			if (userAgent.toLowerCase().contains("webkit")) {
				return true;
			}
			return false;
		}
}

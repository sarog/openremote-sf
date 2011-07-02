package org.openremote.web.console.utils;

import org.openremote.web.console.client.WebConsole;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.web.bindery.event.shared.HandlerRegistration;

	public class BrowserUtils {
		public static boolean isMobile;
		
		static HandlerRegistration scrollHandler = null;
		
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

		
		static {
			isMobile = isMobile();
		}
		
		private static boolean isMobile() {
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
		
		public static void setBodySize(int width, int height) {
			RootPanel.getBodyElement().setAttribute("style", "width: " + width + "px; height:" + height + "px;");
		}
		
		public static void removeBodySize() {
			RootPanel.getBodyElement().removeAttribute("style");
		}

		public static void initWindow(final WebConsole webConsole) {
			Timer addressBarMonitor = new Timer() {
				public void run() {
					// Attempt scroll again just in case missed first time
					Window.scrollTo(0, 1);
					
					// Get Window information
					webConsole.getWindowInfo();
					
					// Indicate system is initialised
					webConsole.isInitialised = true;
			  }
			};
			
		   // Make body twice window height to ensure there's something to scroll
		   BrowserUtils.setBodySize(webConsole.getWindowWidth(), webConsole.getWindowHeight()*2);
		   
		   // Scroll Window to hide address bar
		   Window.scrollTo(0, 1);

			// Wait 1s for first run as some browsers take a while to do the scroll
			addressBarMonitor.schedule(1000);
		}
}

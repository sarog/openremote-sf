package org.openremote.web.console.utils;

import org.openremote.web.console.client.WebConsole;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.web.bindery.event.shared.HandlerRegistration;

	public class BrowserUtils {
		public static boolean isMobile;
		public static boolean isWebkit;
		public static boolean isApple;
		private static WebConsole handle; 
		static String userAgent = Window.Navigator.getUserAgent();
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
	      "sie","sonyericsson","mmp","ucweb","ipod", "ipad"};

		
		static {
			isMobile = isMobile();
			isWebkit = isWebkit();
			isApple = isApple();
		}
		
		private static boolean isMobile() {
			for (String mobile: MOBILE_SPECIFIC_SUBSTRING) {
				if (userAgent.toLowerCase().contains(mobile)) {
					return true;
	         }
			}
	     return false;
		}
		
		private static boolean isWebkit() {
			if (userAgent.toLowerCase().contains("webkit")) {
				return true;
			}
			return false;
		}
		
		public static boolean isApple() {
			if (userAgent.toLowerCase().contains("ipod") || userAgent.toLowerCase().contains("iphone") || userAgent.toLowerCase().contains("ipad")) {
				return true;
			}
			return false;
		}

		// Create a native orientation change handler as resize handler
		// isn't reliable on ipod
	   public static void nativeOrientationHandler() {
	   	handle.resizeHandler();
	   }
	   
	   public static native void addNativeOrientationHandler() /*-{
	   	function eventHandler(e) {
				@org.openremote.web.console.utils.BrowserUtils::nativeOrientationHandler()();
	   	}
	   	$wnd.addEventListener("orientationchange", eventHandler, false);
		}-*/;
		  
		// Seem to have issue with getting height using GWT on ipod so resort to native JS
		public native static int getNativeWindowDim(String dim) /*-{
			var height = $wnd.innerHeight;
			var width = $wnd.innerWidth;
			return (dim=="width" ? width : height);
		}-*/;
		
		public static int getNativeHeight() {
			return getNativeWindowDim("height");
		}
		
		public static int getNativeWidth() {
			return getNativeWindowDim("width");
		}
		
		public static void initWindow(final WebConsole webConsole) {
			handle = webConsole;
			initWindow();
		}
		
		public static void initWindow() {
			Timer addressBarMonitor = new Timer() {
				public void run() {
					// Attempt scroll again just in case missed first time
					Window.scrollTo(0, 1);
					
					// Get Window information
					handle.getWindowSize();
					
					// Indicate system is initialised if already initialised
					// then this is the second init so go to do resize
					if (!handle.isInitialised) {
						handle.isInitialised = true;
					} else {
						handle.doResize();
					}
			  }
			};
			
		   // Make body twice window height to ensure there's something to scroll
		   //handle.setBodySize(handle.windowHeight, handle.windowHeight);

		   // Scroll Window to hide address bar
		   Window.scrollTo(0, 1);

			// Wait 1s for first run as some browsers take a while to do the scroll
		   addressBarMonitor.schedule(1000);
		}
}

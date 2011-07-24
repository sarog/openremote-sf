package org.openremote.web.console.utils;

import org.openremote.web.console.client.WebConsole;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
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
	   
//		// Create a native shake handler for detecting shake event
//	   public static void nativeShakeHandler() {
//	   	handle.resizeHandler();
//	   }
//	   
//	   public static native void addNativeShakeHandler() /*-{
//	   	if (typeof window.DeviceMotionEvent != 'undefined') {
//   			function motionHandler(e) {
//					x1 = e.accelerationIncludingGravity.x;
//					y1 = e.accelerationIncludingGravity.y;
//					z1 = e.accelerationIncludingGravity.z;
//					
//					// Shake sensitivity (a lower number is more)
//					var sensitivity = 20;
//				
//					// Position variables
//					var x1 = 0, y1 = 0, z1 = 0, x2 = 0, y2 = 0, z2 = 0;
//				
//					// Periodically check the position and fire
//					// if the change is greater than the sensitivity
//					setInterval(function () {
//						var change = Math.abs(x1-x2+y1-y2+z1-z2);
//				
//						if (change > sensitivity) {
//							@org.openremote.web.console.utils.BrowserUtils::nativeShakeHandler()();
//						}
//				
//						// Update new position
//						x2 = x1;
//						y2 = y1;
//						z2 = z1;
//					}, 150);
//   			}
//				$wnd.addEventListener("devicemotion", motionHandler, false);
//			}
//		}-*/;

//	   /**
//	    * Capture menu key press on Android devices
//	    */
//		public static void addMenuKeyEventHandler() {			
//			RootPanel.get().addDomHandler(new KeyPressHandler() {
//				public void onKeyPress(KeyPressEvent e) {
//					Window.alert("KEY PRESS");
//					e.preventDefault();
//					for (char menuKey: MENU_KEYCODES) {
//						if (e.getCharCode() == menuKey) {
//							Window.alert("MENU KEY PRESS");		
//			         }
//					}
//				}
//			}, KeyPressEvent.getType());
//		}
//	   public static native void addNativeKeyHandler() /*-{
//	   	function eventHandler(e) {
//				@org.openremote.web.console.utils.BrowserUtils::nativeKeyHandler()();
//	   	}
//	   	$wnd.addEventListener("backKeyDown", eventHandler, false);
//		}-*/;
//	   public static void nativeKeyHandler() {
//	   	Window.alert("BACK BUTTON");
//	   }
		
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
}

package org.openremote.web.console.util;

import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.event.rotate.RotationEvent;
import org.openremote.web.console.event.ui.WindowResizeEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

	public class BrowserUtils {
		public static boolean isMobile;
		public static boolean isApple;
		public static boolean isCssDodgy;
		public static boolean isIE;
		private static String windowOrientation = "portrait";
		private static int windowHeight;
		private static int windowWidth;
		private static AbsolutePanel consoleContainer;
		private static String userAgent = Window.Navigator.getUserAgent();
		static final String[] MOBILE_SPECIFIC_SUBSTRING = {
	      "iphone","android","midp","opera mobi",
	      "opera mini","blackberry","hp ipaq","iemobile",
	      "msiemobile","windows phone","htc","lg",
	      "mot","nokia","symbian","fennec",
	      "maemo","tear","midori","armv",
	      "windows ce","windowsce","smartphone","240x320",
	      "176x220","320x320","160x160","webos",
	      "palm","sagem","samsung","sgh",
	      "sonyericsson","mmp","ucweb","ipod", "ipad"};

		static {
			isMobile = isMobile();
			isApple = isApple();
			isCssDodgy = isCssDodgy();
			isIE = isIE();
		}
		
		public static int getWindowHeight() {
			if (isMobile) {
				return getNativeWindowDim("height");
			} else {
				return Window.getClientHeight();
			}
		}
		
		public static int getWindowWidth() {
			if (isMobile) {
				return getNativeWindowDim("width");
			} else {
				return Window.getClientWidth();
			}
		}
		
		public static String getWindowOrientation() {
			return windowOrientation;
		}
		
		public static AbsolutePanel getConsoleContainer() {
			return consoleContainer;
		}

		public static void initWindow() {
			consoleContainer = new AbsolutePanel();
			consoleContainer.setWidth("3000px");
			consoleContainer.setHeight("3000px");

			RootPanel.get().add(consoleContainer, 0, 0);
			
			updateWindowInfo();
			
			if (isMobile) {
				initMobile();
			} else {
				// Prevent scrollbars from being shown
				Window.enableScrolling(false);
			}
			
			// Add window resize handler
			Window.addResizeHandler(new ResizeHandler() {
				@Override
				public void onResize(ResizeEvent event) {
					doResizeAndRotate();
				}
			});
		}
		
		private static void doResizeAndRotate() {
			String oldOrientation = windowOrientation;
			int oldWidth = windowWidth;
			int oldHeight = windowHeight;
			
			updateWindowInfo();
			
			if (!windowOrientation.equalsIgnoreCase(oldOrientation)) {
				ConsoleUnitEventManager.getInstance().getEventBus().fireEvent(new RotationEvent(getWindowOrientation(), getWindowWidth(), getWindowHeight()));
			}
			
			if ((oldOrientation.equalsIgnoreCase(windowOrientation) && (oldWidth != windowWidth || oldHeight != windowHeight)) || (!oldOrientation.equalsIgnoreCase(windowOrientation) && (oldWidth != windowHeight || oldHeight != windowWidth))) {
				Window.scrollTo(0, 1);
				int height = windowHeight;
				int width = windowWidth;
				if (isMobile) {
					if (windowOrientation.equalsIgnoreCase("landscape")) {
						int tempHeight = height;
						height = width;
						width = tempHeight;
					}
				}
				ConsoleUnitEventManager.getInstance().getEventBus().fireEvent(new WindowResizeEvent(getWindowWidth(), getWindowHeight()));
			}
		}
		
		private static void initMobile() {
			// Prevent window scrolling
			RootPanel.get().addDomHandler(new TouchMoveHandler() {
				public void onTouchMove(TouchMoveEvent e) {
						e.preventDefault();
				}
			}, TouchMoveEvent.getType());
			
		   // Scroll Window to hide address bar
			Window.scrollTo(0, 1);
			Timer addressBarScroller = new Timer() {
				public void run() {
					Window.scrollTo(0, 1);
				}
			};
	
			addressBarScroller.scheduleRepeating(5000);
		   
			// Determine current window orientation
			if (getWindowHeight() < getWindowWidth()) {
				windowOrientation = "landscape";
			}
		   
			// Create a native orientation change handler as resize handler isn't reliable on iOS 3.x
			addNativeOrientationHandler();
			
		   // If Apple device then check if loaded from bookmark
			if (!isBookmarked()) {
				// TODO: Handle bookmark warning
				Window.alert("NOT BOOKMARKED!");
			}
		}
		
		private static void onRotate() {
			doResizeAndRotate();
		}

		public static void updateWindowInfo() {
			int winHeight = getWindowHeight();
			int winWidth = getWindowWidth();
			String winOrientation = "portrait";
			
			if (winHeight < winWidth) {
				winOrientation = "landscape";
			}
			
			windowWidth = winWidth;
			windowHeight = winHeight;
			windowOrientation = winOrientation;
		}
		
// -------------------------------------------------------------
//		UTILITIES BELOW HERE
// -------------------------------------------------------------
		
		private static boolean isMobile() {
			for (String mobile: MOBILE_SPECIFIC_SUBSTRING) {
				if (userAgent.toLowerCase().contains(mobile)) {
					return true;
	         }
			}
	     return false;
		}
		
		private static boolean isIE() {
			if (userAgent.toLowerCase().contains("msie")) {
				return true;
			}
			return false;
		}
		
		private static boolean isApple() {
			if (userAgent.toLowerCase().contains("ipod") || userAgent.toLowerCase().contains("iphone") || userAgent.toLowerCase().contains("ipad")) {
				return true;
			}
			return false;
		}
		
		private static boolean isCssDodgy() {
			if (userAgent.toLowerCase().contains("firefox") || userAgent.toLowerCase().contains("msie")) {
//				int start = userAgent.indexOf("msie");
//				int end = userAgent.indexOf(";", userAgent.indexOf("msie"));
//				if (start >= 0 && end >= 0) {
//					String version = userAgent.substring(start, end);
//					try {
//						double vNum = Double.parseDouble(version);
//						if (vNum > 9) {
//							return false;
//						}
//					} catch (Exception e) {}
//				}
				return true;
			}
			return false;			
		}
		
		/*
		 * This method creates a label widget somewhere and shortens the string
		 * one character at a time until the label width is less than or equal
		 * to the specified value
		 */
		public static String limitStringLength(String name, int width, int fontSize) {
			Label label = new Label(name);
			label.setHeight("auto");
			label.setWidth("auto");
			DOM.setStyleAttribute(label.getElement(), "position", "absolute");
			DOM.setStyleAttribute(label.getElement(), "visibility", "hidden");
			DOM.setStyleAttribute(label.getElement(), "fontSize", fontSize + "px");
			
			RootPanel.get().add(label);
			String retName = name;
			
			// Check length of name and whether it is completely visible
			boolean textResized = false;
			String newName = name;
			int currentWidth = label.getOffsetWidth();
			int iterations = 0;
			
			while (currentWidth > width && iterations <= 100 && newName.length()>1) {
				newName = newName.substring(0, newName.length()-1);
				label.setText(newName);
				textResized = true;
				currentWidth = label.getOffsetWidth();
				iterations++;
			}
			
			if (textResized && newName.length() > 1) {
				retName = newName.substring(0, newName.length()-1);
				retName += "..";
			}
			RootPanel.get().remove(label);
			return retName;
		}		
		
// -------------------------------------------------------------
//			NATIVE METHODS BELOW HERE
// -------------------------------------------------------------		
		
		// Seem to have issue with getting height using GWT on ipod so resort to native JS
		public native static int getNativeWindowDim(String dim) /*-{
			if (typeof $wnd.innerWidth != 'undefined') {
				var height = $wnd.innerHeight;
				var width = $wnd.innerWidth;
			} else {
				var height = document.documentElement.clientHeight;
				var width = document.documentElement.clientWidth;
			}
			return (dim=="width" ? width : height);
		}-*/;
		
		public native static void addNativeOrientationHandler() /*-{
	   	if (typeof window.onorientationchange != 'undefined') {
		   	function eventHandler(e) {
					@org.openremote.web.console.util.BrowserUtils::onRotate()();
		   	}
		   	$wnd.addEventListener("orientationchange", eventHandler, false);
	   	}
		}-*/;
		
		public native static boolean isBookmarked() /*-{
			bookmarked = true;
    		if (("standalone" in window.navigator) && !window.navigator.standalone) {
				bookmarked = false;
    		}
    		return bookmarked;
		}-*/;
		
		public native static String randomUUID() /*-{
		  var s = [];
		  for (var i = 0; i <36; i++) s.push(Math.floor(Math.random()*10));
		  return s.join('');
		}-*/;
}

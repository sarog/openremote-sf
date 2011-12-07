package org.openremote.web.console.util;

import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.event.rotate.RotationEvent;
import org.openremote.web.console.event.ui.AnimationEndHandler;
import org.openremote.web.console.event.ui.WindowResizeEvent;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
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
		public static boolean isWebkit;
		public static boolean isApple;
		public static boolean isCssDodgy;
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
	      "sie","sonyericsson","mmp","ucweb","ipod", "ipad"};

		static {
			isMobile = isMobile();
			isWebkit = isWebkit();
			isApple = isApple();
			isCssDodgy = isCssDodgy();
		}
		
		public static int getWindowHeight() {
			return getNativeWindowDim("height");
		}
		
		public static int getWindowWidth() {
			return getNativeWindowDim("width");
		}
		
		public static String getWindowOrientation() {
			return windowOrientation;
		}
		
		public static AbsolutePanel getConsoleContainer() {
			return consoleContainer;
		}

		public static void initWindow() {
			consoleContainer = new AbsolutePanel();
			consoleContainer.setWidth("100%");
			consoleContainer.setHeight("100%");

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
		
		private static boolean isWebkit() {
			if (userAgent.toLowerCase().contains("webkit")) {
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
			if (userAgent.toLowerCase().contains("firefox")) {
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
		public static String limitStringLength(String name, int width) {
			Label label = new Label(name);
			label.setHeight("auto");
			label.setWidth("auto");
			DOM.setStyleAttribute(label.getElement(), "position", "absolute");
			DOM.setStyleAttribute(label.getElement(), "visibility", "hidden");
			
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
		
		private native void registerAnimationEndHandler(final Element pElement, final AnimationEndHandler pHandler) /*-{
	   	var callback = function() {
	      	pHandler.@org.openremote.web.console.event.ui.AnimationEndHandler::onAnimationEnd()();
	    	}
	    	if (navigator.userAgent.indexOf('MSIE') < 0) {  // no MSIE support
	      	pElement.addEventListener("webkitAnimationEnd", callback, false); // Webkit
	       	pElement.addEventListener("animationend", callback, false); // Mozilla
	    	}
		}-*/;
		
		// Seem to have issue with getting height using GWT on ipod so resort to native JS
		public native static int getNativeWindowDim(String dim) /*-{
			var height = $wnd.innerHeight;
			var width = $wnd.innerWidth;
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
		
// -------------------------------------------------------------
//		BORROWED UTILITY BELOW HERE
// -------------------------------------------------------------
		
		/* randomUUID.js - Version 1.0
		*
		* Copyright 2008, Robert Kieffer
		*
		* This software is made available under the terms of the Open Software License
		* v3.0 (available here: http://www.opensource.org/licenses/osl-3.0.php )
		*
		* The latest version of this file can be found at:
		* http://www.broofa.com/Tools/randomUUID.js
		*
		* For more information, or to comment on this, please go to:
		* http://www.broofa.com/blog/?p=151
		*/
		 
		/**
		* Create and return a "version 4" RFC-4122 UUID string.
		*/
		public native static String randomUUID() /*-{
		  var s = [], itoh = '0123456789ABCDEF';
		 
		  // Make array of random hex digits. The UUID only has 32 digits in it, but we
		  // allocate an extra items to make room for the '-'s we'll be inserting.
		  for (var i = 0; i <36; i++) s[i] = Math.floor(Math.random()*0x10);
		 
		  // Conform to RFC-4122, section 4.4
		  s[14] = 4;  // Set 4 high bits of time_high field to version
		  s[19] = (s[19] & 0x3) | 0x8;  // Specify 2 high bits of clock sequence
		 
		  // Convert to hex chars
		  for (var i = 0; i <36; i++) s[i] = itoh[s[i]];
		 
		  // Insert '-'s
		  s[8] = s[13] = s[18] = s[23] = '-';
		 
		  return s.join('');
		}-*/;
}

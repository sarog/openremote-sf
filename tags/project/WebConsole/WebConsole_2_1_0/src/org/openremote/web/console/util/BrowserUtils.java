/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package org.openremote.web.console.util;

import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.event.rotate.RotationEvent;
import org.openremote.web.console.event.ui.WindowResizeEvent;
import org.openremote.web.console.service.AsyncControllerCallback;
import org.openremote.web.console.unit.ConsoleUnit;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * 
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
	public class BrowserUtils {
		public static boolean isMobile;
		public static boolean isApple;
		public static boolean isCssDodgy;
		public static boolean isIE;
		private static String windowOrientation = "portrait";
		private static int windowHeight;
		private static int windowWidth;
		private static HTML probeElement;
		private static final String LOADING_IMAGE_ID = "console_loading_image";
		private static final String LOADING_MSG_ID = "console_loading_msg";
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
		
	   // Scroll Window to hide address bar
		private static Timer addressBarScroller = new Timer() {
			public void run() {
				int currentWinHeight = getWindowHeight();
				if (currentWinHeight > windowWidth && currentWinHeight > windowHeight) {
					Window.scrollTo(0, 1);
					this.schedule(3000);
				}
			}
		};
		
		static {
			isMobile = isMobile();
			isApple = isApple();
			isIE = isIE();
			HTML panel = new HTML();
			Element elem = panel.getElement();
			elem.getStyle().setVisibility(Visibility.HIDDEN);
			BrowserUtils.setStyleAttributeAllBrowsers(panel.getElement(), "boxSizing", "border-box");
			probeElement = panel;
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
		
//		public static AbsolutePanel getConsoleContainer() {
//			return consoleContainer;
//		}

		public static void initWindow() {
//			consoleContainer = new AbsolutePanel();
//			consoleContainer.setWidth("3000px");
//			consoleContainer.setHeight("3000px");
//			RootPanel.get().add(consoleContainer, 0, 0);
//			//consoleContainer.getElement().getStyle().setPosition(Position.FIXED);
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
			
			if ((oldOrientation.equalsIgnoreCase(windowOrientation) && (oldWidth != windowWidth || oldHeight != windowHeight)) || (!oldOrientation.equalsIgnoreCase(windowOrientation) && (oldWidth != windowHeight || oldHeight != windowWidth))) {
				ConsoleUnitEventManager.getInstance().getEventBus().fireEvent(new WindowResizeEvent(getWindowWidth(), getWindowHeight()));
			}
			
			if (WebConsole.getConsoleUnit().getIsFullscreen() && !windowOrientation.equalsIgnoreCase(oldOrientation)) {
				ConsoleUnitEventManager.getInstance().getEventBus().fireEvent(new RotationEvent(getWindowOrientation(), getWindowWidth(), getWindowHeight()));
			}
		}
		
		private static void initMobile() {
			// Prevent window scrolling
			RootPanel.get().addDomHandler(new TouchMoveHandler() {
				public void onTouchMove(TouchMoveEvent e) {
						e.preventDefault();
				}
			}, TouchMoveEvent.getType());
			
			Window.scrollTo(0, 1);
			new Timer() {
				public void run() {
					Window.scrollTo(0, 1);
				}
			}.schedule(1000);
			
			// Determine current window orientation
			if (getWindowHeight() < getWindowWidth()) {
				windowOrientation = "landscape";
			}
		   
			// Create a native orientation change handler as resize handler isn't reliable on iOS 3.x
			addNativeOrientationHandler();
			
		   // If Apple device then check if loaded from bookmark
			if (!isBookmarked()) {
				Window.alert("Please add this page to your Home Screen to view in fullscreen!");
			}
		}
		
		private static void onRotate() {
			doResizeAndRotate();
			addressBarScroller.schedule(3000);
		}

		public static void updateWindowInfo() {
			int winHeight = getWindowHeight();
			int winWidth = getWindowWidth();
			
			if (isMobile() && windowWidth == winWidth && windowHeight > winHeight) {
				// Ignore this as it just means the soft keyboard has been shown
				return;
			}
			
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
		
		public static String getSystemImageDir() {
//			String dir = "http://";
//			dir = Window.Location.getHost() + Window.Location.getPath();
//			dir = dir.replaceFirst("/+[a-z|A-Z|0-9]+\\.html*", "");
//			dir = dir.replaceFirst("^http://", "");
//			dir += "resources/images";
			return "resources/images/";
		}
		
		public static void setStyleAttributeAllBrowsers(Element elem, String attr, String value) {
			String[] browsers = {"Webkit", "Moz", "O", "Ms", "Khtml"};
			DOM.setStyleAttribute(elem, attr, value);
			attr = Character.toUpperCase(attr.charAt(0)) + attr.substring(1);
			for (String browser : browsers) {
				DOM.setStyleAttribute(elem, browser + attr, value);
			}
		}
		
		public static void hideLoadingMsg() {
			RootPanel.get(LOADING_IMAGE_ID).setVisible(false);
			RootPanel.get(LOADING_MSG_ID).setVisible(false);
		}
		
		public static void showLoadingMsg() {
			showLoadingMsg("");
		}
		
		public static void showLoadingMsg(String msg) {
			RootPanel.get(LOADING_MSG_ID).getElement().setInnerHTML("<span>" + msg  + "</span>");
			RootPanel.get(LOADING_IMAGE_ID).setVisible(true);
			RootPanel.get(LOADING_MSG_ID).setVisible(true);
		}
		
		public static void hideAlert() {
			//WebConsole.getConsoleUnit().getConsoleDisplay().setVisible(true);
			DOM.getElementById("alert_popup").getStyle().setVisibility(Visibility.HIDDEN);
		}
		
		public static void showAlert(String msg) {
			ConsoleUnit console = WebConsole.getConsoleUnit();
			Element elem = DOM.getElementById("alert_popup");
			DOM.getElementById("alert_popup_msg").setInnerHTML(msg);
			int halfHeight = (int) Math.round((double)elem.getClientHeight()/2);
			int halfWidth = (int) Math.round((double)elem.getClientWidth()/2);
			elem.getStyle().setMarginTop(-halfHeight, Unit.PX);
			elem.getStyle().setMarginLeft(-halfWidth, Unit.PX);
			DOM.getElementById("alert_popup").getStyle().setVisibility(Visibility.VISIBLE);
		}
		
		public static void isURLSameOrigin_old(String url, final AsyncControllerCallback<Boolean> callback) {
			RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
			
	    try {
	      Request request = builder.sendRequest(null, new RequestCallback() {
	        public void onError(Request request, Throwable exception) {
	        	callback.onSuccess(true);
	        }

	        public void onResponseReceived(Request request, Response response) {
	        	if (response.getStatusCode() == 0) {
	        		// We get here for modern browsers that will allow CORS
	        		callback.onSuccess(false);
	        	} else {
	        		callback.onSuccess(true);
	        	}
	        }
	      });
	    } catch (RequestException e) {
    		// Violates SOP
    		callback.onSuccess(false);
	    }
		}
		
		public static void isURLSameOrigin(String url, final AsyncControllerCallback<Boolean> callback) {
			RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url + "rest/panels/");
			builder.setHeader("Accept", "application/json");
			builder.setTimeoutMillis(2000);
			
	    try {
	      Request request = builder.sendRequest(null, new RequestCallback() {
	        public void onError(Request request, Throwable exception) {
	        	callback.onSuccess(true);
	        }

	        public void onResponseReceived(Request request, Response response) {
	        	if (response.getStatusCode() == 0) {
	        		callback.onSuccess(false);
	        	} else {
	        		callback.onSuccess(true);
	        	}
	        }
	      });
	    } catch (RequestException e) {
    		// Violates SOP
    		callback.onSuccess(false);
	    }
		}
		
		public static String getImageProxyURL(String username, String password, String url) {
			String imageUrl = url;
		
			if (username != null && password != null) {
				String authStr = username + ":" + password;
				authStr = BrowserUtils.base64Encode(authStr);
				
				imageUrl =  GWT.getModuleBaseURL() + "imageproxy?userpass=" + authStr + "&url=" + URL.encode(url);
			}	
			
			return imageUrl;
		}
		
		/**
		 * History management - currently this just disables the back button
		 * need full history support for screen history.
		 */
		public static void setupHistory() {
      final String initToken = History.getToken();
      if (initToken.length() == 0) {
          History.newItem("main");
      }

      // Add history listener
      HandlerRegistration historyHandlerRegistration = History.addValueChangeHandler(new ValueChangeHandler<String>() {
          @Override
          public void onValueChange(ValueChangeEvent<String> event) {
              String token = event.getValue();
              if (initToken.equals(token)) {
                  History.newItem(initToken);
              }
          }
      });

      // Now that we've setup our listener, fire the initial history state.
      History.fireCurrentHistoryState();

      Window.addWindowClosingHandler(new ClosingHandler() {
          boolean reloading = false;

          @Override
          public void onWindowClosing(ClosingEvent event) {
              if (!reloading) {
                  String userAgent = Window.Navigator.getUserAgent();
                  if (userAgent.contains("MSIE")) {
                      if (!Window.confirm("Do you really want to exit?")) {
                          reloading = true;
                          Window.Location.reload(); // For IE
                      }
                  }
                  else {
                      event.setMessage("Web Console"); // For other browser
                  }
              }
          }
      });
		}
		
		public static int[] getSizeFromStyle(String style) {
			return getSizeFromStyle(style, false);
		}
		
		public static int[] getSizeFromStyle(String style, boolean useText) {
			if (!probeElement.isAttached()) {
				RootPanel.get().add(probeElement);
			}
			
			if (useText) {
				probeElement.setHTML("M");
			} else {
				probeElement.setHTML("");
			}
			
			int[] values = new int[4];
			probeElement.getElement().addClassName(style);
			values[0] = probeElement.getElement().getOffsetWidth();
			values[1] = probeElement.getElement().getOffsetHeight();
			values[2] = probeElement.getElement().getClientWidth();
			values[3] = probeElement.getElement().getClientHeight();
			probeElement.getElement().removeClassName(style);
			return values;
		}
		
// -------------------------------------------------------------
//			NATIVE METHODS BELOW HERE
// -------------------------------------------------------------		
		
		// Gets display density as an integer (1.0 = 10)
		public native static int getDisplayDensityValue() /*-{
			var displayDensity = 10;
			if (typeof $wnd.devicePixelRatio != 'undefined') {
				displayDensity = $wnd.devicePixelRatio * 10;
			}
			return (displayDensity < 13 ? 10 : displayDensity < 18 ? 15 : 20);
		}-*/;
		
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
		
		public static native void exportStaticMethod() /*-{
			$wnd.hideAlert = $entry(@org.openremote.web.console.util.BrowserUtils::hideAlert());
		}-*/;
		
		public static native String base64Encode(String str) /*-{
			return $wnd.btoa(str);
		}-*/;
		
		public static native String getBuildVersionString() /*-{
		return $wnd.buildVersionStr;
	}-*/;
}

package org.openremote.web.console.util;

import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.service.AsyncControllerCallback;
import org.openremote.web.console.service.ControllerService;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
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
		
		public static boolean isMobile() {
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
		
//		public static void checkImageExists(String imageUrl, ImageExistsCallback callback) {
//			if (callback == null) {
//				return;
//			}
//			if (imageUrl.equals("")) {
//				callback.onResponse(false);
//			}
//			RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(imageUrl));
//			try {
//			  Request request = builder.sendRequest(null, callback);
//			} catch (RequestException e) {
//				callback.onResponse(false);
//			}
//		}
}

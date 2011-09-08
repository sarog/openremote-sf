package org.openremote.web.console.rpc;

import org.openremote.web.console.controller.ControllerConnector;
import org.openremote.web.console.controller.EnumControllerCommand;
import org.openremote.web.console.service.ControllerService;
import org.openremote.web.console.service.ControllerServiceImpl;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.URL;

public class ControllerJsonConnector extends ControllerConnector {
	private int jsonRequestId = 0;
	
	private static enum JsonMethodType {
		GET_PANEL_LIST ("rest/panels/"),
		GET_PANEL_LAYOUT ("rest/panel/"),
		SEND_COMMAND ("rest/control/"),
		GET_SENSOR_STATUS ("rest/status/"),
		DO_SENSOR_POLLING ("rest/polling/"),
		GET_ROUND_ROBIN_LIST ("rest/servers/");
		
		private final String url;
		
		JsonMethodType(String url) {
			this.url = url;
		}
		
		public String getUrl() {
			return url;
		}
		
	   @Override
	   public String toString() {
	      return super.toString().toLowerCase();
	   }
	   
	   private static JsonMethodType enumValueOf(String commandActionTypeValue) {
	   	JsonMethodType result = null;
	      try {
	         result = Enum.valueOf(JsonMethodType.class, commandActionTypeValue.toUpperCase());
	      } catch (Exception e) {}
	      return result;
	   }
	}
	
	public ControllerJsonConnector() {
		
	}
	
	/**
   * Make call to controller using JSONP
   */
	private native static void getJson(int requestId, String url, EnumControllerCommand command, ControllerService handler) /*-{
	   var callback = "callback" + requestId;
	
	   // [1] Create a script element.
	   var script = document.createElement("script");
	   script.setAttribute("src", url+callback);
	   script.setAttribute("type", "text/javascript");
	
	   // [2] Define the callback function on the window object.
	   window[callback] = function(jsonObj) {
	     handler.@org.openremote.web.console.service.ControllerService::processControllerResponse(Lorg/openremote/web/console/controller/EnumControllerCommand;Ljava/lang/Object;)(command, Obj);
	     window[callback + "done"] = true;
	   }
	
	   // [3] JSON download has 1-second timeout.
	   setTimeout(function() {
	     if (!window[callback + "done"]) {
	       handler.@org.openremote.web.console.service.ControllerService::processControllerResponse(Lorg/openremote/web/console/controller/EnumControllerCommand;Ljava/lang/Object;)(command, null);
	     }
	
	     // [4] Cleanup. Remove script and callback elements.
	     document.body.removeChild(script);
	     delete window[callback];
	     delete window[callback + "done"];
	   }, 1000);
	
	   // [5] Attach the script element to the document body.
	   document.body.appendChild(script);
  	}-*/;
	
	private static String buildCompleteJsonUrl(String controllerUrl, JsonMethodType methodType, String[] params) {
		String url = controllerUrl;
		int paramCounter = 0;
		url = url.endsWith("/") ? url : url + "/";
		url += methodType.getUrl();
		
		for (String param : params) {
			url += param;
			paramCounter++;
			if (paramCounter < params.length) {
				url = url.endsWith("/") ? url : url + "/";
			}
		}
		url = URL.encode(url) + "?callback=";
		return url;
	}

	@Override
	public void getData(String controllerUrl, EnumControllerCommand command, String[] params, ControllerServiceImpl handler) {
		JsonMethodType methodType = JsonMethodType.enumValueOf(command.toString());
		getJson(jsonRequestId++, buildCompleteJsonUrl(controllerUrl, methodType, params), command, handler);
	}

	@Override
	public void sendData() {
		// TODO Auto-generated method stub
		
	}
}

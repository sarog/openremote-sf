package org.openremote.web.console.rpc.json;

import org.openremote.web.console.controller.ControllerCallBackHandler;
import org.openremote.web.console.controller.ControllerConnector;
import org.openremote.web.console.controller.EnumControllerCommand;
import org.openremote.web.console.controller.message.ControllerRequestMessage;
import org.openremote.web.console.controller.message.ControllerResponseMessage;
import org.openremote.web.console.controller.message.EnumControllerResponseCode;
import org.openremote.web.console.panel.PanelIdentity;
import org.openremote.web.console.service.ControllerService;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;

public class ControllerJsonConnector implements ControllerConnector {
	
	private static String getJsonMethodUrl(EnumControllerCommand command) {
		String methodUrl = "";
		switch (command) {
			case GET_PANEL_LIST:
				methodUrl = "rest/panels/";
				break;
			case GET_PANEL_LAYOUT:
				methodUrl = "rest/panel/";
				break;
			case SEND_COMMAND:
				methodUrl = "rest/control/";
				break;
			case GET_SENSOR_STATUS:
				methodUrl = "rest/status/";
				break;
			case DO_SENSOR_POLLING:
				methodUrl = "rest/polling/";
				break;
			case GET_ROUND_ROBIN_LIST:
				methodUrl = "rest/servers/";
		}
		return methodUrl;
	}
	
	public ControllerJsonConnector() {
		
	}
	
	/**
   * Make call to controller using JSONP
   */
	private native static void getJson(int requestId, String url, EnumControllerCommand command, ControllerCallBackHandler handler, ControllerJsonConnector connector) /*-{
	   var callback = "callback" + requestId;
	   
	   // [1] Create a script element.
	   var script = document.createElement("script");
	   script.setAttribute("src", url+callback);
	   script.setAttribute("type", "text/javascript");
	
	   // [2] Define the callback function on the window object.
	   window[callback] = function(jsonObj) {
	      connector.@org.openremote.web.console.rpc.json.ControllerJsonConnector::generateResponseMessage(ILcom/google/gwt/core/client/JavaScriptObject;Lorg/openremote/web/console/controller/EnumControllerCommand;Lorg/openremote/web/console/controller/ControllerCallBackHandler;)(requestId, jsonObj, command, handler);
	     	window[callback + "done"] = true;
	   }
	
	   // [3] JSON download has 1-second timeout.
	   setTimeout(function() {
	   	if (!window[callback + "done"]) {
				connector.@org.openremote.web.console.rpc.json.ControllerJsonConnector::generateResponseMessage(ILcom/google/gwt/core/client/JavaScriptObject;Lorg/openremote/web/console/controller/EnumControllerCommand;Lorg/openremote/web/console/controller/ControllerCallBackHandler;)(requestId, null, command, handler);
	     	}
	
		  	// [4] Cleanup. Remove script and callback elements.
		  	document.body.removeChild(script);
		  	delete window[callback];
		  	delete window[callback + "done"];
	   }, 3000);
	
	   // [5] Attach the script element to the document body.
	   document.body.appendChild(script);
  	}-*/;
	
	private static String buildCompleteJsonUrl(String controllerUrl, EnumControllerCommand command, String[] params) {
		String url = controllerUrl;
		int paramCounter = 0;
		url = url.endsWith("/") ? url : url + "/";
		String methodUrl = getJsonMethodUrl(command);
		
		if (methodUrl.equals("")) {
			return "";
		} else {
			url += methodUrl;
		}		
		
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
	public void getData(int requestId, String controllerUrl, ControllerRequestMessage message, ControllerCallBackHandler handler) {
		String jsonUrl = buildCompleteJsonUrl(controllerUrl, message.getCommand(), message.getParams());
		if (!jsonUrl.equals("")) {
			getJson(requestId, jsonUrl, message.getCommand(), handler, this);
		}
	}

	@Override
	public void sendData() {
		// TODO Auto-generated method stub
		
	}
	
	public void generateResponseMessage(int requestId, JavaScriptObject jsObj, EnumControllerCommand command, ControllerCallBackHandler handler) {
		ControllerResponseMessage responseMessage = null;
		EnumControllerResponseCode responseCode = null;
		Object responseObject = null;
		
		if (jsObj == null) {
			responseMessage = new ControllerResponseMessage(requestId, EnumControllerResponseCode.NO_RESPONSE, null);
			handler.processControllerResponse(responseMessage);
			return;
		}
		
		// Check for JSONP Error Response and return that if it is set
		JSONObject jsonObj = new JSONObject(jsObj);
		
		if (jsonObj.containsKey("error")) {
			int errorCode = (int) jsonObj.get("error").isObject().get("code").isNumber().doubleValue();
			responseCode = EnumControllerResponseCode.getResponseCode(errorCode);
			
			responseMessage = new ControllerResponseMessage(requestId, responseCode, null); 
		}
		
		// If we've got this far then we assume JSON response is correctly formatted so we build the response object
		responseCode = EnumControllerResponseCode.OK;
		
		switch(command) {
			case GET_PANEL_LIST:
				JavaScriptObject obj = jsonObj.get("panel").isArray().getJavaScriptObject();
				JsArray<PanelIdentityJso> panels = getPanels(obj);
				responseObject = panels;
				break;
			
		}
		
		responseMessage = new ControllerResponseMessage(requestId, responseCode, responseObject);
		handler.processControllerResponse(responseMessage);
		return;
	}
	
	@Override
	public boolean isAlive(int requestId, String controllerUrl) {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public boolean isSecure(int requestId, String controllerUrl) {
		// TODO Auto-generated method stub
		return false;
	}
	
	// JSON Overlays below here	
	private native final JsArray<PanelIdentityJso> getPanels(JavaScriptObject jsObj) /*-{
		return jsObj;
	}-*/;
}

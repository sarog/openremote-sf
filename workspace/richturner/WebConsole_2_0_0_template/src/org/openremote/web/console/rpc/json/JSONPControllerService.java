package org.openremote.web.console.rpc.json;

import java.util.HashMap;
import java.util.Map;

import org.openremote.web.console.controller.Controller;
import org.openremote.web.console.controller.ControllerCallBackHandler;
import org.openremote.web.console.controller.EnumControllerCommand;
import org.openremote.web.console.controller.EnumControllerResponseCode;
import org.openremote.web.console.controller.message.ControllerRequestMessage;
import org.openremote.web.console.controller.message.ControllerResponseMessage;
import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.event.controller.ControllerMessageEvent;
import org.openremote.web.console.event.value.UiValueChangeEvent;
import org.openremote.web.console.event.value.UiValueChangeHandler;
import org.openremote.web.console.panel.Panel;
import org.openremote.web.console.panel.PanelIdentity;
import org.openremote.web.console.service.AsyncControllerCallback;
import org.openremote.web.console.service.AutoBeanService;
import org.openremote.web.console.service.ControllerService;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;

public class JSONPControllerService extends ControllerService {
	private Controller controller;
	private int requestId = 0;
	private Map<Integer, CommandCallbackPair> requestMap = new HashMap<Integer, CommandCallbackPair>();
	
	private class CommandCallbackPair {
		private EnumControllerCommand command;
		private AsyncControllerCallback<?> callback;
		
		public CommandCallbackPair(EnumControllerCommand command, AsyncControllerCallback<?> callback) {
			this.command = command;
			this.callback = callback;
		}

		public EnumControllerCommand getCommand() {
			return command;
		}

		public AsyncControllerCallback<?> getCallback() {
			return callback;
		}
	}
	
	private static String getJsonMethodUrl(EnumControllerCommand command) {
		String methodUrl = "";
		switch (command) {
			case GET_PANEL_LIST:
				methodUrl = "rest/panels/";
				break;
			case IS_SECURE:
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
				break;
			case IS_ALIVE:
				methodUrl = "rest/panels/";
				break;
		}
		return methodUrl;
	}
	
	public JSONPControllerService() {
		this(null);
	}
	
	public JSONPControllerService(Controller controller) {
		setController(controller);
	}
	
	// ------------------------   Interface Overrides	-------------------------------------------
	
	@Override
	public void getPanelIdentities(String controllerUrl, AsyncControllerCallback<PanelIdentity[]> callback) {
		EnumControllerCommand command = EnumControllerCommand.GET_PANEL_LIST;
		sendCommand(requestId++, buildCompleteJsonUrl(controllerUrl, command), command, callback);
	}
	
	@Override
	public void getPanel(String controllerUrl, String panelName, AsyncControllerCallback<Panel> callback) {
		EnumControllerCommand command = EnumControllerCommand.GET_PANEL_LAYOUT;
		sendCommand(requestId++, buildCompleteJsonUrl(controllerUrl, new String[] {panelName}, command), command, callback);		
	}
	
	@Override
	public void isSecure(String controllerUrl, AsyncControllerCallback<Boolean> callback) {
		EnumControllerCommand command = EnumControllerCommand.IS_SECURE;
		sendCommand(requestId++, buildCompleteJsonUrl(controllerUrl, command), command, callback);
	}

	@Override
	public void isAlive(String controllerUrl, AsyncControllerCallback<Boolean> callback) {
		sendCommand(requestId++, controllerUrl, EnumControllerCommand.IS_ALIVE, callback);		
	}
	
	// ------------------------	Internal JSON Methods ------------------------------------------
	
	private void sendCommand(int requestId, String commandUrl, EnumControllerCommand command, AsyncControllerCallback<?> callback) {
		if (!commandUrl.equals("")) {
			CommandCallbackPair callbackMap = new CommandCallbackPair(command, callback);
			requestMap.put(requestId, callbackMap);
			getJson(requestId, commandUrl, this);
		}
	}
	
	/**
	   * Make call to controller using JSONP
	   */
		private native static void getJson(int requestId, String url, JSONPControllerService handler) /*-{
		   var callback = "callback" + requestId;
		   
		   // [1] Create a script element.
		   var script = document.createElement("script");
		   script.setAttribute("src", url+callback);
		   script.setAttribute("type", "text/javascript");
		   // [2] Define the callback function on the window object.
		   window[callback] = function(jsonObj) {
		      handler.@org.openremote.web.console.rpc.json.JSONPControllerService::handleResponse(ILcom/google/gwt/core/client/JavaScriptObject;)(requestId, jsonObj);
		     	window[callback + "done"] = true;
		   }
		
		   // [3] JSON download has 1-second timeout.
		   setTimeout(function() {
		   	if (!window[callback + "done"]) {
					handler.@org.openremote.web.console.rpc.json.JSONPControllerService::handleResponse(ILcom/google/gwt/core/client/JavaScriptObject;)(requestId, null);
		     	}
		
			  	// [4] Cleanup. Remove script and callback elements.
			  	document.body.removeChild(script);
			  	delete window[callback];
			  	delete window[callback + "done"];
		   }, 1000);
		
		   // [5] Attach the script element to the document body.
		   document.body.appendChild(script);
	  	}-*/;
		
		private static String buildCompleteJsonUrl(String controllerUrl, EnumControllerCommand command) {
			return buildCompleteJsonUrl(controllerUrl, new String[0], command);
		}
		
		private static String buildCompleteJsonUrl(String controllerUrl, String[] params, EnumControllerCommand command) {
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
		
		@SuppressWarnings("unchecked")
		public void handleResponse(int requestId, JavaScriptObject jsObj) {
			CommandCallbackPair callbackMap = requestMap.remove(requestId);
			EnumControllerCommand command;
			
			if (callbackMap == null) {
				return;
			}

			
			command = callbackMap.getCommand();
			
			// Throw a Controller Error on the callback and exit
			if (jsObj == null) {
				callbackMap.getCallback().onFailure(new Exception("Error: " + EnumControllerResponseCode.NOT_FOUND.getDescription()));
				return;
			}
			
			// Check for JSONP Error Response and if set throw appropriate exception
			JSONObject jsonObj = new JSONObject(jsObj);
			boolean isSecure = false;
			if (jsonObj.containsKey("error")) {
				int errorCode = (int) jsonObj.get("error").isObject().get("code").isNumber().doubleValue();
				if (!(command == EnumControllerCommand.IS_SECURE && errorCode == 403)) {
					callbackMap.getCallback().onFailure(new Exception("Error: " + EnumControllerResponseCode.getResponseCode(errorCode).getDescription()));
				} else {
					isSecure = true;
				}
			}
			
			// If we've got this far then we assume JSON response is correctly formatted so we build the response object
			switch(command) {
				case GET_PANEL_LIST:
					AsyncControllerCallback<PanelIdentity[]> panelListCallback = (AsyncControllerCallback<PanelIdentity[]>)callbackMap.getCallback();
					JsArray<PanelIdentityJso> jsoPanels = getPanelIdentities(jsObj);
					PanelIdentity[] panels = new PanelIdentity[jsoPanels.length()];
					for (int i=0; i<jsoPanels.length(); i++) {
						panels[i] = (PanelIdentity)jsoPanels.get(i);
					}
					panelListCallback.onSuccess(panels);
					break;
				case GET_PANEL_LAYOUT:
					AsyncControllerCallback<Panel> panelLayoutCallback = (AsyncControllerCallback<Panel>)callbackMap.getCallback();
					String json = jsObj.toString();
					Panel panel = null; //AutoBeanService.getInstance().fromJsonString(Panel.class, json);
					//PanelJso jsoPanel = getPanel(jsObj);
					panelLayoutCallback.onSuccess(panel);
					break;
				case IS_ALIVE:
					AsyncControllerCallback<Boolean> isAliveCallback = (AsyncControllerCallback<Boolean>)callbackMap.getCallback();
					isAliveCallback.onSuccess(true);
					break;
				case IS_SECURE:
					AsyncControllerCallback<Boolean> isSecureCallback = (AsyncControllerCallback<Boolean>)callbackMap.getCallback();
					isSecureCallback.onSuccess(isSecure);
					break;
			}
		}
		
		
		// --------------------- 	JSON Overlays below here ----------------------------------
		
		private native final JsArray<PanelIdentityJso> getPanelIdentities(JavaScriptObject jsObj) /*-{
			return jsObj.panel;
		}-*/;
		
//		private native final PanelJso getPanel(JavaScriptObject jsObj) /*-{
//			return jsObj;
//		}-*/;
}
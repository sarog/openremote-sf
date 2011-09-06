package org.openremote.web.console.service;

import org.openremote.web.console.controller.ControllerCredentials;
import org.openremote.web.console.controller.ControllerGetSensorValueResponse;
import org.openremote.web.console.controller.ControllerResponse;
import org.openremote.web.console.controller.ControllerSendCommandResponse;
import org.openremote.web.console.event.value.ControllerValueChangeEvent;
import org.openremote.web.console.event.value.UiValueChangeEvent;
import org.openremote.web.console.panel.PanelIdentity;
import org.openremote.web.console.panel.entity.Panel;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.URL;

public class ControllerServiceImpl implements ControllerService {
	private ControllerCredentials credentials;
	private int jsonRequestId = 0;
	
	public ControllerServiceImpl(ControllerCredentials credentials) {
		this.credentials = credentials;
	}

	@Override
	public void onControllerValueChange(ControllerValueChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUiValueChange(UiValueChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PanelIdentity[] getPanelNames() {
		// TODO Auto-generated method stub
		getJson(jsonRequestId++, credentials.getUrl(), this);
		return null;
	}

	@Override
	public ControllerResponse authenticate(ControllerCredentials credentials) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Panel getPanelLayout(String panelName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendCommand(int controlId, String commandParameter,
			AsyncControllerCallback<ControllerSendCommandResponse> callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getSensorValue(int sensorId,
			AsyncControllerCallback<ControllerGetSensorValueResponse> callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void monitorSensorValues(int[] sensorIds,
			AsyncControllerCallback<ControllerGetSensorValueResponse> callback) {
		// TODO Auto-generated method stub		
	}
	
	
	// -----------------------------------------------------------------------------------------------
	// JSON CODE BELOW HERE, COULD BE A SUB CLASS OF CONTROLLER SERVICE MAYBE BUT THIS WILL DO FOR NOW
	// -----------------------------------------------------------------------------------------------

	private static enum JsonMethodType {	
		PANEL_LIST ("rest/panels/"),
		PANEL_LAYOUT ("rest/panel/"),
		SEND_COMMAND ("rest/control/"),
		SENSOR_STATUS ("rest/status/"),
		SENSOR_POLLING ("rest/polling/"),
		ROUND_ROBIN ("rest/servers/");
		
		private final String url;
		
		JsonMethodType(String url) {
			this.url = url;
		}
		
		public String getUrl() {
			return url;
		}
	}
	
	
	/**
   * Make call to controller using JSONP
   */
	private native static void getJson(int requestId, String url, ControllerServiceImpl handler) /*-{
	   var callback = "callback" + requestId;
	
	   // [1] Create a script element.
	   var script = document.createElement("script");
	   script.setAttribute("src", url+callback);
	   script.setAttribute("type", "text/javascript");
	
	   // [2] Define the callback function on the window object.
	   window[callback] = function(jsonObj) {
	     handler.@org.openremote.web.console.service.ControllerServiceImpl::handleJsonResponse(Lcom/google/gwt/core/client/JavaScriptObject;)(jsonObj);
	     window[callback + "done"] = true;
	   }
	
	   // [3] JSON download has 1-second timeout.
	   setTimeout(function() {
	     if (!window[callback + "done"]) {
	       handler.@org.openremote.web.console.service.ControllerServiceImpl::handleJsonResponse(Lcom/google/gwt/core/client/JavaScriptObject;)(null);
	     }
	
	     // [4] Cleanup. Remove script and callback elements.
	     document.body.removeChild(script);
	     delete window[callback];
	     delete window[callback + "done"];
	   }, 1000);
	
	   // [5] Attach the script element to the document body.
	   document.body.appendChild(script);
  	}-*/;
	
	private void handleJsonResponse(JavaScriptObject jsObj) {
		//TODO: Handle Json Response
	}
	
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
}


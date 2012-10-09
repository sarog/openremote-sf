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
package org.openremote.web.console.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openremote.web.console.controller.EnumControllerCommand;
import org.openremote.web.console.controller.EnumControllerResponseCode;
import org.openremote.web.console.panel.Panel;
import org.openremote.web.console.panel.PanelIdentityList;
import org.openremote.web.console.panel.entity.Status;
import org.openremote.web.console.panel.entity.StatusList;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * JSONP Controller Service capable of making Cross Origin Requests; the
 * caveat is that you cannot add headers to the request so it does not
 * support authentication.
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public class JSONPControllerConnector implements ControllerConnector {
	
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
			case LOGOUT:
				methodUrl = "rest/logout/";
		}
		return methodUrl;
	}
	
	public JSONPControllerConnector() {

	}
	
	// ------------------------   Interface Overrides	-------------------------------------------
	
	@Override
	public void getPanelIdentities(String controllerUrl, String username, String password, AsyncControllerCallback<PanelIdentityList> callback) {
		EnumControllerCommand command = EnumControllerCommand.GET_PANEL_LIST;
		doJsonpRequest(buildCompleteJsonUrl(controllerUrl, command), new JSONPControllerCallback(command, callback));
	}
	
	@Override
	public void getPanel(String controllerUrl, String username, String password, String panelName, AsyncControllerCallback<Panel> callback) {
		EnumControllerCommand command = EnumControllerCommand.GET_PANEL_LAYOUT;
		doJsonpRequest(buildCompleteJsonUrl(controllerUrl, new String[] {panelName}, command), new JSONPControllerCallback(command, callback),20000);
	}
	
	@Override
	public void isSecure(String controllerUrl, String username, String password, AsyncControllerCallback<Boolean> callback) {
		EnumControllerCommand command = EnumControllerCommand.IS_SECURE;
		doJsonpRequest(buildCompleteJsonUrl(controllerUrl, command), new JSONPControllerCallback(command, callback));
	}

	@Override
	public void isAlive(String controllerUrl, String username, String password, AsyncControllerCallback<Boolean> callback) {
		EnumControllerCommand command = EnumControllerCommand.IS_ALIVE;
		doJsonpRequest(buildCompleteJsonUrl(controllerUrl, new String[] {}, command), new JSONPControllerCallback(command, callback),5000);	
	}
	
	@Override
	public void sendCommand(String controllerUrl, String username, String password, String sendCommand, AsyncControllerCallback<Boolean> callback) {
		EnumControllerCommand command = EnumControllerCommand.SEND_COMMAND;
		doJsonpRequest(buildCompleteJsonUrl(controllerUrl, new String[] {sendCommand}, command), new JSONPControllerCallback(command, callback));
	}

	@Override
	public void monitorSensors(String controllerUrl, String username, String password, Integer[] sensorIds, String uuid, AsyncControllerCallback<Map<Integer, String>> callback) {
		EnumControllerCommand command = EnumControllerCommand.DO_SENSOR_POLLING;
		doJsonpRequest(buildCompleteJsonUrl(controllerUrl, new String[] {uuid, Arrays.toString(sensorIds).replace(", ", ",").replace("]","").replace("[","")}, command), new JSONPControllerCallback(command, callback),55000);
	}
	
	@Override
	public void getSensorValues(String controllerUrl, String username, String password, Integer[] sensorIds, AsyncControllerCallback<Map<Integer, String>> callback) {
		EnumControllerCommand command = EnumControllerCommand.GET_SENSOR_STATUS;
		doJsonpRequest(buildCompleteJsonUrl(controllerUrl, new String[] {Arrays.toString(sensorIds).replace(", ", ",").replace("]","").replace("[","")}, command), new JSONPControllerCallback(command, callback),20000);
	}
	
	@Override
	public void logout(String controllerUrl, AsyncControllerCallback<Boolean> callback) {
		EnumControllerCommand command = EnumControllerCommand.LOGOUT;
		doJsonpRequest(buildCompleteJsonUrl(controllerUrl, new String[] {}, command), new JSONPControllerCallback(command, callback),20000);
	}
	
	// ------------------------   Interface Overrides End -------------------------------------------
	
	@SuppressWarnings("unchecked")
	private class JSONPControllerCallback implements AsyncCallback<JavaScriptObject> {
		EnumControllerCommand command;
		AsyncControllerCallback<?> callback;
		
		protected JSONPControllerCallback(EnumControllerCommand command, AsyncControllerCallback<?> callback) {
			this.command = command;
			this.callback = callback;
		}
		
		@Override
		public void onFailure(Throwable caught) {
				callback.onFailure(caught);
		}

		@Override
		public void onSuccess(JavaScriptObject jsObj) {

			if (jsObj == null) {
				//callback.onFailure(new Exception(new Exception("Unknown Error JSON Response is Empty")));
				callback.onFailure(EnumControllerResponseCode.UNKNOWN_ERROR);
				return;
			}
			
			// Check for JSONP Error Response and if set throw appropriate exception
			JSONObject jsonObj = new JSONObject(jsObj);
			int errorCode = 0;
			
			if (jsonObj.containsKey("error")) {
				errorCode = (int) jsonObj.get("error").isObject().get("code").isNumber().doubleValue();
				if (!((command == EnumControllerCommand.IS_SECURE) || (command == EnumControllerCommand.DO_SENSOR_POLLING && errorCode == 504) || errorCode == 200)) {
					callback.onFailure(EnumControllerResponseCode.getResponseCode(errorCode));
					return;
				}
			}
			
			// If we've got this far then we assume JSON response is correctly formatted so we build the response object
			try { 
				switch(command) {
					case GET_PANEL_LIST:
						AsyncControllerCallback<PanelIdentityList> panelListCallback = (AsyncControllerCallback<PanelIdentityList>)callback;
						PanelIdentityList panels = AutoBeanService.getInstance().fromJsonString(PanelIdentityList.class, jsonObj.toString()).as();
						panelListCallback.onSuccess(panels);
						break;
					case GET_PANEL_LAYOUT:
						AsyncControllerCallback<Panel> panelLayoutCallback = (AsyncControllerCallback<Panel>)callback;
						Panel panel = AutoBeanService.getInstance().fromJsonString(Panel.class, jsonObj.toString()).as();
						panelLayoutCallback.onSuccess(panel);
						break;
					case IS_ALIVE:
						AsyncControllerCallback<Boolean> isAliveCallback = (AsyncControllerCallback<Boolean>)callback;
						isAliveCallback.onSuccess(true);
						break;
					case IS_SECURE:
						AsyncControllerCallback<Boolean> isSecureCallback = (AsyncControllerCallback<Boolean>)callback;
						if (errorCode == 403 || errorCode == 401) {
							isSecureCallback.onSuccess(true);
						} else {
							isSecureCallback.onSuccess(false);
						}
						break;
					case SEND_COMMAND:
						AsyncControllerCallback<Boolean> successCallback = (AsyncControllerCallback<Boolean>)callback;
						if (errorCode == 200) {
							successCallback.onSuccess(true);
						} else {
							successCallback.onSuccess(false);
						}
						break;
					case DO_SENSOR_POLLING:
						AsyncControllerCallback<Map<Integer,String>> pollingCallback = (AsyncControllerCallback<Map<Integer, String>>)callback;
						Map<Integer,String> pollValues = new HashMap<Integer, String>();
						if (errorCode == 504) {
							pollingCallback.onSuccess(null);	
						} else {
							List<Status> statuses = AutoBeanService.getInstance().fromJsonString(StatusList.class, jsonObj.toString()).as().getStatus();
							if (statuses != null) {
								for (Status status : statuses) {
									if (status != null) {
										pollValues.put(status.getId(), status.getContent());
									}
								}
								pollingCallback.onSuccess(pollValues);
							}
						}
						break;
					case GET_SENSOR_STATUS:
						AsyncControllerCallback<Map<Integer,String>> statusCallback = (AsyncControllerCallback<Map<Integer, String>>)callback;
						Map<Integer,String> statusValues = new HashMap<Integer, String>();
						List<Status> statuses = AutoBeanService.getInstance().fromJsonString(StatusList.class, jsonObj.toString()).as().getStatus();
						if (statuses != null) {
							for (Status status : statuses) {
								if (status != null) {
									statusValues.put(status.getId(), status.getContent());
								}
							}
							statusCallback.onSuccess(statusValues);
						}
						break;
					case LOGOUT:
						AsyncControllerCallback<Boolean> logoutCallback = (AsyncControllerCallback<Boolean>)callback;
						if (errorCode == 401) {
							logoutCallback.onSuccess(true);
						} else {
							logoutCallback.onSuccess(false);
						}
						break;
				}
			} catch (Exception e) {
				callback.onFailure(EnumControllerResponseCode.UNKNOWN_ERROR);
			}
		}
	};
	
	private void doJsonpRequest(String url, JSONPControllerCallback callback) {
		doJsonpRequest(url, callback, null);
	}
	private void doJsonpRequest(String url, JSONPControllerCallback callback, Integer timeout) {
		JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
		if (timeout != null) {
			jsonp.setTimeout(timeout);
		}
		jsonp.requestObject(url, callback);
	}
	
	// ------------------------	Internal JSON Methods ------------------------------------------
		
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
		url = URL.encode(url);
		return url;
	}
}
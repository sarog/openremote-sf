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
import org.openremote.web.console.util.BrowserUtils;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

/**
 * JSON Controller Service uses standard Request Builder allowing headers
 * to be added to the request. Caveat is that it is restricted by the same
 * origin policy (need to look at HTML5 CORS to see how this could solve the
 * problem). 
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public class JSONControllerConnector implements ControllerConnector {

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
	
	public JSONControllerConnector() {

	}
	
	// ------------------------   Interface Overrides	-------------------------------------------
	
	@Override
	public void getPanelIdentities(String controllerUrl, String username, String password, AsyncControllerCallback<PanelIdentityList> callback) {
		EnumControllerCommand command = EnumControllerCommand.GET_PANEL_LIST;
		doJsonRequest(buildCompleteJsonUrl(controllerUrl, command), username, password, new JSONControllerCallback(command, callback));
	}
	
	@Override
	public void getPanel(String controllerUrl, String username, String password, String panelName, AsyncControllerCallback<Panel> callback) {
		EnumControllerCommand command = EnumControllerCommand.GET_PANEL_LAYOUT;
		doJsonRequest(buildCompleteJsonUrl(controllerUrl, new String[] {panelName}, command), username, password, new JSONControllerCallback(command, callback),20000);
	}
	
	@Override
	public void isSecure(String controllerUrl, String username, String password, AsyncControllerCallback<Boolean> callback) {
		EnumControllerCommand command = EnumControllerCommand.IS_SECURE;
		doJsonRequest(buildCompleteJsonUrl(controllerUrl, command), username, password, new JSONControllerCallback(command, callback));
	}

	@Override
	public void isAlive(String controllerUrl, String username, String password, AsyncControllerCallback<Boolean> callback) {
		EnumControllerCommand command = EnumControllerCommand.IS_ALIVE;
		doJsonRequest(buildCompleteJsonUrl(controllerUrl, new String[] {}, command), username, password, new JSONControllerCallback(command, callback),5000);	
	}
	
	@Override
	public void sendCommand(String controllerUrl, String username, String password, String sendCommand, AsyncControllerCallback<Boolean> callback) {
		EnumControllerCommand command = EnumControllerCommand.SEND_COMMAND;
		doJsonRequest(buildCompleteJsonUrl(controllerUrl, new String[] {sendCommand}, command), username, password, new JSONControllerCallback(command, callback));
	}

	@Override
	public void monitorSensors(String controllerUrl, String username, String password, Integer[] sensorIds, String uuid, AsyncControllerCallback<Map<Integer, String>> callback) {
		EnumControllerCommand command = EnumControllerCommand.DO_SENSOR_POLLING;
		doJsonRequest(buildCompleteJsonUrl(controllerUrl, new String[] {uuid, Arrays.toString(sensorIds).replace(", ", ",").replace("]","").replace("[","")}, command), username, password, new JSONControllerCallback(command, callback),55000);
	}
	
	@Override
	public void getSensorValues(String controllerUrl, String username, String password, Integer[] sensorIds, AsyncControllerCallback<Map<Integer, String>> callback) {
		EnumControllerCommand command = EnumControllerCommand.GET_SENSOR_STATUS;
		doJsonRequest(buildCompleteJsonUrl(controllerUrl, new String[] {Arrays.toString(sensorIds).replace(", ", ",").replace("]","").replace("[","")}, command), username, password, new JSONControllerCallback(command, callback),20000);
	}
	
	@Override
	public void logout(String controllerUrl, AsyncControllerCallback<Boolean> callback) {
		EnumControllerCommand command = EnumControllerCommand.LOGOUT;
		doJsonRequest(buildCompleteJsonUrl(controllerUrl, new String[] {}, command), null, null, new JSONControllerCallback(command, callback),20000);
	}
	
	// ------------------------   Interface Overrides End -------------------------------------------
	
	@SuppressWarnings("unchecked")
	private class JSONControllerCallback implements RequestCallback {
		EnumControllerCommand command;
		AsyncControllerCallback<?> callback;
		
		protected JSONControllerCallback(EnumControllerCommand command, AsyncControllerCallback<?> callback) {
			this.command = command;
			this.callback = callback;
		}
		
		@Override
		public void onError(Request request, Throwable caught) {
				callback.onFailure(caught);
		}

		@Override
		public void onResponseReceived(Request request, Response response) {
			// Check secure request
			if (command == EnumControllerCommand.IS_SECURE) {
				AsyncControllerCallback<Boolean> isSecureCallback = (AsyncControllerCallback<Boolean>)callback;
				if (response.getStatusCode() == 401 || response.getStatusCode() == 403) {
					isSecureCallback.onSuccess(true);
				} else {
					isSecureCallback.onSuccess(false);
				}
				return;
			}
			
			JSONObject jsonObj = null;
			
			if (response != null) {
				String str = response.getText();
				JSONValue value = JSONParser.parseStrict(str);
				jsonObj = (JSONObject)value;
			}
			
			if (jsonObj == null) {
				//callback.onFailure(new Exception(new Exception("Unknown Error JSON Response is Empty")));
				callback.onFailure(EnumControllerResponseCode.UNKNOWN_ERROR);
				return;
			}
			
			// Check for JSONP Error Response and if set throw appropriate exception
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
	
	private void doJsonRequest(String url, String username, String password, JSONControllerCallback callback) {
		doJsonRequest(url, username, password, callback, null);
	}
	private void doJsonRequest(String url, String username, String password, JSONControllerCallback callback, Integer timeout) {
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
		Request request = null;
		
		// Add accept header
		builder.setHeader("Accept", "application/json");
		
		if (username != null && username.length() > 0) {
			if (password == null) password = "";
			
			// Add authentication header
			String authStr = username + ":" + password;
			String authEnc = "Basic " + BrowserUtils.base64Encode(authStr);
			builder.setHeader("Authorization", authEnc);
		}
		
		builder.setCallback(callback);
		
		if (timeout != null) {
			builder.setTimeoutMillis(timeout);
		}
		
    try {
  		request = builder.send();
    } catch (RequestException e) {
  		callback.onError(request, e);
    }
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
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

import java.util.Map;

import org.openremote.web.console.controller.Controller;
import org.openremote.web.console.panel.Panel;
import org.openremote.web.console.panel.PanelIdentityList;
/**
 * Controller Service Interface for defining the communication with
 * a controller, based along the lines of GWT RPC but controller service
 * may use alternative communication mechanism (JSON, Socket, etc) but the
 * idea is that it must be an Asynchronous service.
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public abstract class ControllerService {
	Controller controller;
	
	public void setController(Controller controller) {
		this.controller = controller;
	}
	
	public Controller getController() {
		return controller;
	}
	
	/*
	 * Method for retrieving panel identities for requested controller
	 */
	public void getPanelIdentities(AsyncControllerCallback<PanelIdentityList> callback) {
		if (controller != null) {
			getPanelIdentities(controller.getUrl(), callback);
		}
	}
	
	public abstract void getPanelIdentities(String controllerUrl, AsyncControllerCallback<PanelIdentityList> callback);
	
	/*
	 * Method for retrieving panel definition from requested controller
	 */
	public void getPanel(String panelName, AsyncControllerCallback<Panel> callback) {
		if (controller != null) {
			getPanel(controller.getUrl(), panelName, callback);
		}
	}
	public abstract void getPanel(String controllerUrl, String panelName, AsyncControllerCallback<Panel> callback);

	/*
	 * Static Method for retrieving controller security status
	 */
	public void isSecure(AsyncControllerCallback<Boolean> callback) {
		if (controller != null) {
			isSecure(controller.getUrl(), callback);
		}
	}
	public abstract void isSecure(String controllerUrl, AsyncControllerCallback<Boolean> callback);
	
	/*
	 * Static Method for retrieving controller alive status
	 */
	public void isAlive(AsyncControllerCallback<Boolean> callback) {
		if (controller != null) {
			isAlive(controller.getUrl(), callback);
		}
	}
	public abstract void isAlive(String controllerUrl, AsyncControllerCallback<Boolean> callback);
	
	/*
	 * Method for sending a command request to the controller
	 */
	public void sendCommand(String command, AsyncControllerCallback<Boolean> callback) {
		if (controller != null) {
			sendCommand(controller.getUrl(), command, callback);
		}
	}
	public abstract void sendCommand(String controllerUrl, String command, AsyncControllerCallback<Boolean> callback);
	
	/*
	 * Monitor sensors for change
	 */
	public void monitorSensors(Integer[] sensorIds, AsyncControllerCallback<Map<Integer, String>> callback) {
		if (controller != null) {
			monitorSensors(controller.getUrl(), sensorIds, callback);
		}
	}
	public abstract void monitorSensors(String controllerUrl, Integer[] sensorIds, AsyncControllerCallback<Map<Integer, String>> callback);
	
	/*
	 * Get sensor values
	 */
	public void getSensorValues(Integer[] sensorIds, AsyncControllerCallback<Map<Integer, String>> callback) {
		if (controller != null) {
			getSensorValues(controller.getUrl(), sensorIds, callback);
		}
	}
	public abstract void getSensorValues(String controllerUrl, Integer[] sensorIds, AsyncControllerCallback<Map<Integer, String>> callback);
}
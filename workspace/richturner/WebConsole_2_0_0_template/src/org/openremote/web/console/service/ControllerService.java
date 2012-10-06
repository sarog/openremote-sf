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
import org.openremote.web.console.util.BrowserUtils;
/**
 * Controller Service Interface for defining the communication with
 * a controller, based along the lines of GWT RPC but controller service
 * may use alternative communication mechanism (JSON, Socket, etc) but the
 * idea is that it must be an Asynchronous service.
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public class ControllerService {
	private static ControllerService instance = null;
	private String uuid;
	Controller controller;
	ControllerConnector connector = new JSONPControllerConnector();
	
	private ControllerService() {
		uuid = BrowserUtils.randomUUID();
	}	
	
	public static ControllerService getInstance() {
		if (instance == null) instance = new ControllerService();
		
		return instance;
	}
	
	/**
	 * Set the current controller (default that the service will use)
	 * @param controller
	 */
	public void setController(Controller controller) {
		this.controller = controller;
	}
	
	/**
	 * Get the current controller (default that the service uses)
	 * @return {@link org.openremote.web.console.controller.Controller}
	 */
	public Controller getController() {
		return controller;
	}
	
	public void setConnector(ControllerConnector connector) {
		this.connector = connector;
	}
	
	/**
	 * Retrieve panel identities for current controller
	 * @param callback
	 */
	public void getPanelIdentities(AsyncControllerCallback<PanelIdentityList> callback) {
		getPanelIdentities(controller, callback);
	}
	
	/**
	 * Retrieve panel identities for the specified controller
	 * @param controller
	 * @param callback
	 */
	public void getPanelIdentities(Controller controller, AsyncControllerCallback<PanelIdentityList> callback) {	
		if (controller != null) {
			connector.getPanelIdentities(controller.getUrl(), controller.getUsername(), controller.getPassword(), callback);
		}
	}

	/**
	 * Retrieve Panel definition for the current controller
	 * @param panelName
	 * @param callback
	 */
	public void getPanel(String panelName, AsyncControllerCallback<Panel> callback) {
		getPanel(controller, panelName, callback);
	}

	/**
	 * Retrieve Panel definition for the specified controller
	 * @param controller
	 * @param panelName
	 * @param callback
	 */
	public void getPanel(Controller controller, String panelName, AsyncControllerCallback<Panel> callback) {
		if (controller != null) {
			connector.getPanel(controller.getUrl(), controller.getUsername(), controller.getPassword(), panelName, callback);
		}
	}

	/**
	 * Determine if the current controller is secured
	 * @param callback
	 */
	public void isSecure(AsyncControllerCallback<Boolean> callback) {
		isSecure(controller, callback);
	}

	/**
	 * Determine if the specified controller is secured
	 * @param controller
	 * @param callback
	 */
	public void isSecure(Controller controller, AsyncControllerCallback<Boolean> callback) {
		if (controller != null) {
			connector.isSecure(controller.getUrl(), controller.getUsername(), controller.getPassword(), callback);
		}
	}

	/**
	 * Determine if the current controller is alive
	 * @param callback
	 */
	public void isAlive(AsyncControllerCallback<Boolean> callback) {
		isAlive(controller, callback);
	}

	/**
	 * Determine if the specified controller is alive
	 * @param controller
	 * @param callback
	 */
	public void isAlive(Controller controller, AsyncControllerCallback<Boolean> callback) {
		if (controller != null) {
			connector.isAlive(controller.getUrl(), controller.getUsername(), controller.getPassword(), callback);
		}
	}

	/**
	 * Send a command to the current controller
	 * @param command
	 * @param callback
	 */
	public void sendCommand(String command, AsyncControllerCallback<Boolean> callback) {
		sendCommand(controller, command, callback);
	}
	
	/**
	 * Send a command to the specified controller
	 * @param controller
	 * @param command
	 * @param callback
	 */
	public void sendCommand(Controller controller, String command, AsyncControllerCallback<Boolean> callback) {
		if (controller != null) {
			connector.sendCommand(controller.getUrl(), controller.getUsername(), controller.getPassword(), command, callback);
		}
	}

	/**
	 * Monitor the specified sensors for value changes on the current controller
	 * @param sensorIds
	 * @param callback
	 */
	public void monitorSensors(Integer[] sensorIds, AsyncControllerCallback<Map<Integer, String>> callback) {
		monitorSensors(controller, sensorIds, callback);
	}
	
	/**
	 * Monitor the specified sensors for value changes on the specified controller
	 * @param controller
	 * @param sensorIds
	 * @param callback
	 */
	public void monitorSensors(Controller controller, Integer[] sensorIds, AsyncControllerCallback<Map<Integer, String>> callback) {
		if (controller != null) {
			connector.monitorSensors(controller.getUrl(), controller.getUsername(), controller.getPassword(), sensorIds, uuid, callback);
		}
	}

	/**
	 * Get the current values of the specified sensors for the current controller
	 * @param sensorIds
	 * @param callback
	 */
	public void getSensorValues(Integer[] sensorIds, AsyncControllerCallback<Map<Integer, String>> callback) {
		getSensorValues(controller, sensorIds, callback);
	}

	/**
	 * Get the current values of the specified sensors for the specified controller
	 * @param controller
	 * @param sensorIds
	 * @param callback
	 */
	public void getSensorValues(Controller controller, Integer[] sensorIds, AsyncControllerCallback<Map<Integer, String>> callback) {
		if (controller != null) {
			connector.getSensorValues(controller.getUrl(), controller.getUsername(), controller.getPassword(), sensorIds, callback);
		}
	}
}
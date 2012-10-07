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
import org.openremote.web.console.panel.Panel;
import org.openremote.web.console.panel.PanelIdentityList;

/**
 * This interface defines the contract for controller connectors. Implementations
 * of this interface are used to make the connection to the controller.
 * 
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 *
 */
public interface ControllerConnector {
	/**
	 * Returns {@link org.openremote.web.console.panel.PanelIdentityList} of Panels that the specified controller owns.
	 * 
	 * @param controllerUrl The URL of the controller including http|https prefix
	 * @param username For authentication with the controller
	 * @param password For authentication with the controller
	 * @param callback {@link AsyncControllerCallback} callback for handling the response asynchronously
	 */
	void getPanelIdentities(String controllerUrl, String username, String password, AsyncControllerCallback<PanelIdentityList> callback);
	
	
	/**
	 * Returns {@link org.openremote.web.console.panel.Panel} definition of the specified panel.
	 * 
	 * @param controllerUrl The URL of the controller including http|https prefix
	 * @param username For authentication with the controller
	 * @param password For authentication with the controller
	 * @param panelName Name of the panel to retrieve
	 * @param callback {@link AsyncControllerCallback} callback for handling the response asynchronously
	 */
	void getPanel(String controllerUrl, String username, String password, String panelName, AsyncControllerCallback<Panel> callback);
	
	
	/**
	 * Returns {@link Boolean} indicating whether specified controller is secured.
	 * 
	 * @param controllerUrl The URL of the controller including http|https prefix
	 * @param username For authentication with the controller
	 * @param password For authentication with the controller
	 * @param callback {@link AsyncControllerCallback} callback for handling the response asynchronously
	 */
	void isSecure(String controllerUrl, String username, String password, AsyncControllerCallback<Boolean> callback);
	
	/**
	 * Returns {@link Boolean} indicating whether specified controller is alive.
	 * 
	 * @param controllerUrl The URL of the controller including http|https prefix
	 * @param username For authentication with the controller
	 * @param password For authentication with the controller
	 * @param callback {@link AsyncControllerCallback} callback for handling the response asynchronously
	 */
	void isAlive(String controllerUrl, String username, String password, AsyncControllerCallback<Boolean> callback);
	
	/**
	 * Returns {@link Boolean} indicating whether command send request was successful.
	 * 
	 * @param controllerUrl The URL of the controller including http|https prefix
	 * @param username For authentication with the controller
	 * @param password For authentication with the controller
	 * @param command Command to send
	 * @param callback {@link AsyncControllerCallback} callback for handling the response asynchronously
	 */
	void sendCommand(String controllerUrl, String username, String password, String command, AsyncControllerCallback<Boolean> callback);
	
	/**
	 * Returns Map<Integer, String> of sensor IDs and values only for sensors whose values
	 * have changed since the last request.
	 * 
	 * @param controllerUrl The URL of the controller including http|https prefix
	 * @param username For authentication with the controller
	 * @param password For authentication with the controller
	 * @param sensorIds Array of sensor IDs to monitor
	 * @param uuid Unique ID identifying this client to the controller 
	 * @param callback {@link AsyncControllerCallback} callback for handling the response asynchronously
	 */
	void monitorSensors(String controllerUrl, String username, String password, Integer[] sensorIds, String uuid, AsyncControllerCallback<Map<Integer, String>> callback);
	
	
	/**
	 * Returns Map<Integer, String> of sensor IDs and values.
	 * 
	 * @param controllerUrl The URL of the controller including http|https prefix
	 * @param username For authentication with the controller
	 * @param password For authentication with the controller
	 * @param sensorIds Array of sensor IDs to get values for
	 * @param callback {@link AsyncControllerCallback} callback for handling the response asynchronously
	 */
	void getSensorValues(String controllerUrl, String username, String password, Integer[] sensorIds, AsyncControllerCallback<Map<Integer, String>> callback);
	

	/**
	 * Logs out of the specified controller.
	 * 
	 * @param controllerUrl The URL of the controller including http|https prefix
	 * @param callback {@link AsyncControllerCallback} callback for handling the response asynchronously
	 */
	void logout(String controllerUrl, AsyncControllerCallback<Boolean> callback);
}

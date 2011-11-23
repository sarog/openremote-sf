package org.openremote.web.console.service;

import java.util.List;
import java.util.Map;
import org.openremote.web.console.controller.Controller;
import org.openremote.web.console.panel.Panel;
import org.openremote.web.console.panel.PanelIdentity;

/**
 * Controller Service Interface for defining the communication with
 * a controller, based along the lines of GWT RPC but controller service
 * may use alternative communication mechanism (JSON, Socket, etc) but the
 * idea is that it must be an Asynchronous service.
 * @author rich
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
	public void getPanelIdentities(AsyncControllerCallback<List<PanelIdentity>> callback) {
		if (controller != null) {
			getPanelIdentities(controller.getUrl(), callback);
		}
	}
	
	public abstract void getPanelIdentities(String controllerUrl, AsyncControllerCallback<List<PanelIdentity>> callback);
	
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
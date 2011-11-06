package org.openremote.web.console.service;

import java.util.List;

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
	 * Method for retrieving controller security status
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
	public abstract void isAlive(String controllerUrl, AsyncControllerCallback<Boolean> callback);
	
	/*
	 * Check if image exists on controller
	 */
	public void imageExists(String imageUrl, AsyncControllerCallback<Boolean> callback) {
		if (controller != null) {
			imageExists(controller.getUrl(), imageUrl, callback);
		}
	}
	
	public void imageExists(String controllerUrl, String imageUrl,	AsyncControllerCallback<Boolean> callback) {
		// TODO Auto-generated method stub
		
	}
}
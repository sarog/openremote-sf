package org.openremote.web.console.service;

import org.openremote.web.console.controller.ControllerCredentials;

public interface LocalDataService {
	public ControllerCredentials getLastControllerCredentials();
	
	public String getLastPanelName();
	
	public ControllerCredentials getDefaultControllerCredentials();
	
	public void setLastControllerCredentials(ControllerCredentials controllerCredentials);
	
	public void setLastPanelName(String panelName);
	
	public void setDefaultControllerCredentials(ControllerCredentials controllerCredentials);
}

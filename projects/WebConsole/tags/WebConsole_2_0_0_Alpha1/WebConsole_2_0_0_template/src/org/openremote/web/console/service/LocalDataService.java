package org.openremote.web.console.service;

import java.util.List;

import org.openremote.web.console.controller.ControllerCredentials;

public interface LocalDataService {
	ControllerCredentials getLastControllerCredentials();
	String getLastPanelName();
	ControllerCredentials getDefaultControllerCredentials();
	List<ControllerCredentials> getStoredControllerCredentials();
	
	void setLastControllerCredentials(ControllerCredentials controllerCredentials);
	void setLastPanelName(String panelName);
	void setDefaultControllerCredentials(ControllerCredentials controllerCredentials);
	void setStoredControllerCredentials(List<ControllerCredentials> storedCredentials);
}

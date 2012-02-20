package org.openremote.web.console.service;

import org.openremote.web.console.controller.ControllerCredentials;
import org.openremote.web.console.controller.ControllerCredentialsList;

public interface LocalDataService {
	ControllerCredentials getLastControllerCredentials();
	String getLastPanelName();
	ControllerCredentials getDefaultControllerCredentials();
	ControllerCredentialsList getControllerCredentialsList();
	
	void setLastControllerCredentials(ControllerCredentials controllerCredentials);
	void setLastPanelName(String panelName);
	void setDefaultControllerCredentials(ControllerCredentials controllerCredentials);
	void setControllerCredentialsList(ControllerCredentialsList storedCredentials);
	void clearData(String dataName);
	void clearAllData();
}

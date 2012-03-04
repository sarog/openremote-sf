package org.openremote.web.console.service;

import org.openremote.web.console.controller.ControllerCredentials;
import org.openremote.web.console.controller.ControllerCredentialsList;

public interface LocalDataService {
	ControllerCredentials getLastControllerCredentials();
	ControllerCredentialsList getControllerCredentialsList();
	String getObjectString(String name);
	
	void setLastControllerCredentials(ControllerCredentials controllerCredentials);
	void setControllerCredentialsList(ControllerCredentialsList controllerCredentialsList);
	void clearData(String dataName);
	void clearAllData();
	void setObject(String name, String obj);
}

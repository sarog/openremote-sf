package org.openremote.web.console.controller;

import java.util.List;

public interface ControllerCredentialsList {
	public List<ControllerCredentials> getControllerCredentials();
	
	public void setControllerCredentials(List<ControllerCredentials> credentials);
}

package org.openremote.web.console.service;

import org.openremote.web.console.controller.ControllerCredentials;
import org.openremote.web.console.panel.PanelCredentials;

public interface DataService {
	public PanelCredentials getLastPanelCredentials();
	
	public ControllerCredentials getControllerCredentials(String controllerUrl);
}

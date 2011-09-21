package org.openremote.web.console.service;

import org.openremote.web.console.panel.PanelCredentials;

public interface LocalDataService {
	public PanelCredentials getLastPanelCredentials();
	
	public void setLastPanelCredentials(PanelCredentials panelCredentials);
}

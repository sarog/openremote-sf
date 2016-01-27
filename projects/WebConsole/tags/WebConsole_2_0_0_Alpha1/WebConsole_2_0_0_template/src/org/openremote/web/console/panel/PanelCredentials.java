package org.openremote.web.console.panel;

public interface PanelCredentials {
	String getControllerUrl();	
	int getId();
	String getName();
	
	void setControllerUrl(String controllerUrl);
	void setId(int id);
	void setName(String name);
}

package org.openremote.web.console.panel.entity;

public interface FormButton {
	String getType();
	Navigate getNavigate();
	
	void setType(String type);
	void setNavigate(Navigate navigate);
}

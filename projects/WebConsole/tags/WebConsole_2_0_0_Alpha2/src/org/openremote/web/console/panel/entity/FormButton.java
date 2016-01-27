package org.openremote.web.console.panel.entity;

public interface FormButton {
	String getType();
	String getName();
	Navigate getNavigate();
   String getAction();
	
	void setType(String type);
	void setName(String name);
	void setNavigate(Navigate navigate);
   void setAction(String action);
}

package org.openremote.web.console.panel.entity;

public interface Field {
	String getLabel();
	String getInputType();
	String getName();
	String getValidationString();
	Boolean getOptional();
	
	void setLabel(String label);
	void setInputType(String inputType);
	void setName(String name);
	void setValidationString(String validationString);
	void setOptional(Boolean optional);
}

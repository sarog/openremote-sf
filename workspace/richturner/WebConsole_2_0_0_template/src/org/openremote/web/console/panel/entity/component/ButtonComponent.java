package org.openremote.web.console.panel.entity.component;

import org.openremote.web.console.panel.entity.Navigate;

public interface ButtonComponent {
	Integer getId();
	String getName();
	Navigate getNavigate();
	Boolean getHasControlCommand();
	
	void setId(Integer id);
	void setName(String name);
	void setNavigate(Navigate navigate);
	void setHasControlCommand(Boolean hasControlCommand);
}

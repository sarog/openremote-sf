package org.openremote.web.console.panel.entity.component;

import org.openremote.web.console.panel.entity.Navigate;

public interface ButtonComponent {
	int getId();
	String getName();
	Navigate getNavigate();
	boolean getHasControlCommand();
	
	void setId(int id);
	void setName(String name);
	void setNavigate(Navigate navigate);
	void setHasControlCommand(boolean hasControlCommand);
}

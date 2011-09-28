package org.openremote.web.console.panel.entity.component;

import org.openremote.web.console.panel.entity.Link;

public interface SwitchComponent {
	int getId();
	Link getLink();
	
	void setId(int id);
	void setLink(Link link);
}

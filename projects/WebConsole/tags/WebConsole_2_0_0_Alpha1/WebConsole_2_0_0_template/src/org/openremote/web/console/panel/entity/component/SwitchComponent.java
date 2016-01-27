package org.openremote.web.console.panel.entity.component;

import org.openremote.web.console.panel.entity.Link;

public interface SwitchComponent {
	Integer getId();
	Link getLink();
	
	void setId(Integer id);
	void setLink(Link link);
}

package org.openremote.web.console.service;

import org.openremote.web.console.entity.Group;
import org.openremote.web.console.entity.Screen;
import org.openremote.web.console.panel.Panel;

public interface PanelService {
	Screen getScreenById(int screenId);
	
	Screen getScreenByName(String name);
	
	Group getDefaultGroup();
	
	Group getGroupById();
	
	Panel getCurrentPanel();
	
	void setCurrentPanel(Panel currentPanel);
}

package org.openremote.web.console.service;

import org.openremote.web.console.panel.Panel;
import org.openremote.web.console.panel.entity.Group;
import org.openremote.web.console.panel.entity.Screen;

public interface PanelService {
	Screen getScreenById(int screenId);
	
	Screen getScreenByName(String name);
	
	Screen getDefaultScreen();
	
	Group getGroupById();
	
	Panel getCurrentPanel();
	
	void setCurrentPanel(Panel currentPanel);
}

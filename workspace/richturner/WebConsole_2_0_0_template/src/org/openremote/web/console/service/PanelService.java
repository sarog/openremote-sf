package org.openremote.web.console.service;

import org.openremote.web.console.panel.Panel;
import org.openremote.web.console.panel.entity.Group;
import org.openremote.web.console.panel.entity.Screen;
import org.openremote.web.console.panel.entity.TabBar;

public interface PanelService {
	Integer getDefaultGroupId();
	
	Screen getDefaultScreen(Integer groupId);
	
	Panel getCurrentPanel();
	
	void setCurrentPanel(Panel currentPanel);
	
	Screen getScreenById(int screenId);
	
	Screen getScreenByName(String name);
	
	TabBar getTabBar(int groupId);
	
	Screen getInverseScreen(int screenId);
	
	String getScreenOrientation(int screenId);
	
	boolean isInitialized();
}

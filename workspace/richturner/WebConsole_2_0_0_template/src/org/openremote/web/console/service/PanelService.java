package org.openremote.web.console.service;

import org.openremote.web.console.panel.Panel;
import org.openremote.web.console.panel.entity.Screen;
import org.openremote.web.console.panel.entity.TabBar;

public interface PanelService {
	Integer getDefaultGroupId();
	
	Screen getDefaultScreen(Integer groupId);
	
	Panel getCurrentPanel();
	
	void setCurrentPanel(Panel currentPanel);
	
	Screen getScreenById(Integer screenId);
	
	Screen getScreenByName(String name);
	
	Screen getNextScreen(Integer groupId, Integer screenId);
	
	Screen getPreviousScreen(Integer groupId, Integer screenId);
	
	TabBar getTabBar(Integer groupId);
	
	Screen getInverseScreen(Integer screenId);
	
	String getScreenOrientation(Integer screenId);
	
	boolean isInitialized();
}

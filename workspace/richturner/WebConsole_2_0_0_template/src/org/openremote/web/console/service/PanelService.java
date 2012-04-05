package org.openremote.web.console.service;

import java.util.List;

import org.openremote.web.console.panel.Panel;
import org.openremote.web.console.panel.PanelSize;
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
	
	boolean isInitialized();
	
	PanelSize getPanelSize();

	List<Integer> getGroupScreenIds(Integer groupId);
	
	List<Integer> getGroupScreenIdsWithSameOrientation(Integer screenId, Integer groupId);
}

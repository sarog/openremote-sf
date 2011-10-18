package org.openremote.web.console.service;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.openremote.web.console.panel.Panel;
import org.openremote.web.console.panel.entity.Group;
import org.openremote.web.console.panel.entity.Screen;
import org.openremote.web.console.panel.entity.ScreenRef;
import org.openremote.web.console.panel.entity.TabBar;

public class PanelServiceImpl implements PanelService {
	Panel currentPanel;
	Map<Integer, Group> currentGroupMap = null;
	Map<Integer, Screen> currentScreenMap = null;
	TabBar currentPanelTabBar = null;
	boolean isInitialised = false;
	
	@Override
	public Screen getScreenById(int screenId) {
		Screen screen = null;
		
		if (currentScreenMap != null) {
				screen = currentScreenMap.get(screenId);
		}
		return screen;
	}
	
	private Group getGroupById(int id) {
		Group group = null;
		
		if (currentGroupMap != null) {
				group = currentGroupMap.get(id);
		}
		return group;
	}

	@Override
	public Screen getScreenByName(String name) {
		Screen screen = null;
		
		if (currentScreenMap != null) {
			for (Iterator<Integer> it = currentScreenMap.keySet().iterator(); it.hasNext();) {
				Integer id = it.next();
				Screen screenElem = currentScreenMap.get(id);
				if (screenElem.getName().equalsIgnoreCase(name)) {
					screen = screenElem;
					break;
				}				
			}
		}	
		return screen;
	}

	@Override
	public Screen getDefaultScreen(Integer groupId) {
		Screen screen = null;
		
		if (groupId != null && currentGroupMap != null && currentScreenMap != null) {
			Group group = getGroupById(groupId);
			if (group != null) {
				List<ScreenRef> screenRefs = group.getInclude();
				if (screenRefs != null) {
					int screenId = screenRefs.get(0).getRef();
					screen = getScreenById(screenId);
				}
			}	
		}
		return screen;
	}
	
	@Override
	public Integer getDefaultGroupId() {
		Integer groupId = null;
		
		if (currentGroupMap != null) {
			Iterator<Integer> it = currentGroupMap.keySet().iterator();
			groupId = it.next();
		}
		return groupId;
	}
	
	public Panel getCurrentPanel() {
		return currentPanel;
	}

	public void setCurrentPanel(Panel currentPanel) { 
		this.currentPanel = currentPanel;
		this.currentGroupMap = null;
		this.currentScreenMap = null;
		this.currentPanelTabBar = null;
		
		if (currentPanel != null) {
			// Set Group and Screen Map and Panel Tab Bar
			currentGroupMap = new LinkedHashMap<Integer, Group>();
			currentScreenMap = new LinkedHashMap<Integer, Screen>();
			currentPanelTabBar = currentPanel.getTabbar();
			
			List<Group> groups = currentPanel.getGroups().getGroup();
			if (groups != null) {
				for (Group groupElem : groups) {
					currentGroupMap.put(groupElem.getId(), groupElem);
				}
			}

			List<Screen> screens = currentPanel.getScreens().getScreen();
			if (screens != null) {
				for (Screen screenElem : screens) {
					currentScreenMap.put(screenElem.getId(), screenElem);
				}
			}
			
			isInitialised = true;
		}
	}

	@Override
	public TabBar getTabBar(int groupId) {
		TabBar tabBar = null;
		
		if (currentGroupMap != null) {
			Group group = getGroupById(groupId);
			if (group != null) {
				tabBar = group.getTabbar();
			}
		}
		
		if (tabBar == null) {
			tabBar = currentPanelTabBar;
		}
		return tabBar;
	}	
}

package org.openremote.web.console.service;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.openremote.web.console.panel.Panel;
import org.openremote.web.console.panel.PanelSize;
import org.openremote.web.console.panel.entity.AbsoluteLayout;
import org.openremote.web.console.panel.entity.GridLayout;
import org.openremote.web.console.panel.entity.Group;
import org.openremote.web.console.panel.entity.Screen;
import org.openremote.web.console.panel.entity.ScreenRef;
import org.openremote.web.console.panel.entity.TabBar;
import org.openremote.web.console.widget.panel.PanelComponent;
import org.openremote.web.console.widget.panel.PanelComponent.DimensionResult;
import org.openremote.web.console.widget.panel.PanelComponent.DimensionUnit;

public class PanelServiceImpl implements PanelService {
	private static PanelServiceImpl instance;
	private Panel currentPanel;
	private Map<Integer, Group> currentGroupMap = null;
	private Map<Integer, Screen> currentScreenMap = null;
	private TabBar currentPanelTabBar = null;
	private boolean isInitialised = false;
	private PanelSize panelSize = null;
	
	private PanelServiceImpl() {}
	
	public static synchronized PanelServiceImpl getInstance() {
		if (instance == null) {
			instance = new PanelServiceImpl();
		}
		return instance;
	}
	
	@Override
	public Screen getScreenById(Integer screenId) {
		Screen screen = null;
		
		if (currentScreenMap != null && screenId != null) {
				screen = currentScreenMap.get(screenId);
		}
		return screen;
	}
	
	private Group getGroupById(Integer id) {
		Group group = null;
		
		if (currentGroupMap != null && id != null) {
				group = currentGroupMap.get(id);
		}
		return group;
	}

	@Override
	public Screen getScreenByName(String name) {
		Screen screen = null;
		
		if (currentScreenMap != null && name != null) {
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
		return getScreenByGroupIndex(groupId, 0);
	}
	
	@Override
	public Screen getNextScreen(Integer groupId, Integer screenId) {
		Screen screen = null;
		Integer screenIndex = getGroupScreenIndex(groupId, screenId);
		
		if (screenIndex != null && screenIndex >= 0) {
			screen = getScreenByGroupIndex(groupId, screenIndex+1);
		}		
		return screen;
	}
	
	@Override
	public Screen getPreviousScreen(Integer groupId, Integer screenId) {
		Screen screen = null;
		Integer screenIndex = getGroupScreenIndex(groupId, screenId);
		
		if (screenIndex != null && screenIndex > 0) {
			screen = getScreenByGroupIndex(groupId, screenIndex-1);
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
	
	@Override
	public Panel getCurrentPanel() {
		return currentPanel;
	}

	@Override
	public void setCurrentPanel(Panel currentPanel) { 
		this.currentPanel = currentPanel;
		this.currentGroupMap = null;
		this.currentScreenMap = null;
		this.currentPanelTabBar = null;
		this.panelSize = null;
		isInitialised = false;
		
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
	public TabBar getTabBar(Integer groupId) {
		TabBar tabBar = null;
		
		if (currentGroupMap != null && groupId != null) {
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

	@Override
	public Screen getInverseScreen(Integer screenId) {
		Screen screen = null;
		Screen inverseScreen = null;
		
		if (currentScreenMap != null && screenId != null) {
				screen = currentScreenMap.get(screenId);
		}
		
		if (screen != null) {
			Integer inverseId = screen.getInverseScreenId();
			if (inverseId != null) {
				inverseScreen = currentScreenMap.get(inverseId);
			}
		}
		
		return inverseScreen;
	}

	@Override
	public boolean isInitialized() {
		return this.isInitialised;
	}
	
	private Integer getGroupScreenIndex(Integer groupId, Integer screenId) {
		Integer screenIndex = null;
		
		Group group = getGroupById(groupId);
		if (group != null) {
			List<ScreenRef> screenRefs = group.getInclude();
			if (screenRefs != null) {
				for (int i=0; i < screenRefs.size(); i++) {
					if (screenId == screenRefs.get(i).getRef()) {
						screenIndex = i;
						break;
					}
				}
			}
		}
		
		return screenIndex;
	}
	
	private Screen getScreenByGroupIndex(Integer groupId, Integer index) {
		Screen screen = null;
		
		if (index == null || index < 0) {
			return screen;
		}
		
		if (groupId != null && currentGroupMap != null && currentScreenMap != null) {
			Group group = getGroupById(groupId);
			if (group != null) {
				List<ScreenRef> screenRefs = group.getInclude();
				if (screenRefs != null && index <= (screenRefs.size()-1)) {
					ScreenRef ref = screenRefs.get(index); 
					if (ref != null) {
						int screenId = ref.getRef();
						screen = getScreenById(screenId);
					}
				}
			}	
		}
		
		return screen;
	}

	@Override
	public PanelSize getPanelSize() {
		if (panelSize != null) {
			return panelSize;
		}
		PanelSize size = new PanelSize();
		int width = 0;
		int height = 0;
		
		// No direct way to get this info so cycle through elements to determine
		if (currentPanel != null) {
			for (Screen screen : currentPanel.getScreens().getScreen()) {
				if (screen.getAbsolute() != null) {
					for (AbsoluteLayout panel : screen.getAbsolute()) {
						int maxX = 0;
						int maxY = 0;
						DimensionResult l = PanelComponent.getDimFromString(panel.getLeft());
						DimensionResult t = PanelComponent.getDimFromString(panel.getTop());
						DimensionResult w = PanelComponent.getDimFromString(panel.getWidth());
						DimensionResult h = PanelComponent.getDimFromString(panel.getWidth());
						if (screen.getLandscape() != null && screen.getLandscape()) {
							if (l.getUnit() == DimensionUnit.PERCENTAGE || t.getUnit() == DimensionUnit.PERCENTAGE || w.getUnit() == DimensionUnit.PERCENTAGE || h.getUnit() == DimensionUnit.PERCENTAGE) {
								return panelSize;
							}
							maxY = (int)Math.round(l.getValue() + w.getValue());
							maxX = (int)Math.round(t.getValue() + h.getValue());
						} else {
							maxX = (int)Math.round(l.getValue() + w.getValue());
							maxY = (int)Math.round(t.getValue() + h.getValue());
						}
						width = maxX >= width ? maxX : width;
						height = maxY >= height ? maxY : height;
					}
				}
				if (screen.getGrid() != null) {
					for (GridLayout panel : screen.getGrid()) {
						int maxX = 0;
						int maxY = 0;
						DimensionResult l = PanelComponent.getDimFromString(panel.getLeft());
						DimensionResult t = PanelComponent.getDimFromString(panel.getTop());
						DimensionResult w = PanelComponent.getDimFromString(panel.getWidth());
						DimensionResult h = PanelComponent.getDimFromString(panel.getWidth());
						if (screen.getLandscape() != null && screen.getLandscape()) {
							maxY = (int)Math.round(l.getValue() + w.getValue());
							maxX = (int)Math.round(t.getValue() + h.getValue());
						} else {
							maxX = (int)Math.round(l.getValue() + w.getValue());
							maxY = (int)Math.round(t.getValue() + h.getValue());
						}
						width = maxX >= width ? maxX : width;
						height = maxY >= height ? maxY : height;
					}
				}
			}
		}
		size.setWidth(width+10);
		size.setHeight(height+10);
		return size;
	}
}

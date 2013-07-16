/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package org.openremote.web.console.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.panel.Panel;
import org.openremote.web.console.panel.PanelSize;
import org.openremote.web.console.panel.entity.AbsoluteLayout;
import org.openremote.web.console.panel.entity.Cell;
import org.openremote.web.console.panel.entity.ComponentContainer;
import org.openremote.web.console.panel.entity.GridLayout;
import org.openremote.web.console.panel.entity.Group;
import org.openremote.web.console.panel.entity.Image;
import org.openremote.web.console.panel.entity.ListItemLayout;
import org.openremote.web.console.panel.entity.ListLayout;
import org.openremote.web.console.panel.entity.Screen;
import org.openremote.web.console.panel.entity.ScreenRef;
import org.openremote.web.console.panel.entity.StateMap;
import org.openremote.web.console.panel.entity.TabBar;
import org.openremote.web.console.panel.entity.TabBarItem;
import org.openremote.web.console.panel.entity.component.ButtonComponent;
import org.openremote.web.console.panel.entity.component.ImageComponent;
import org.openremote.web.console.panel.entity.component.SliderComponent;
import org.openremote.web.console.panel.entity.component.SliderMinMax;
import org.openremote.web.console.panel.entity.component.SwitchComponent;
import org.openremote.web.console.unit.ConsoleUnit;
import org.openremote.web.console.util.BrowserUtils;
import org.openremote.web.console.widget.panel.PanelComponent;
import org.openremote.web.console.widget.panel.PanelComponent.DimensionResult;
import org.openremote.web.console.widget.panel.PanelComponent.DimensionUnit;
/**
 * 
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public class PanelServiceImpl implements PanelService {
	private static PanelServiceImpl instance;
	private Panel currentPanel;
	private Map<Integer, Group> currentGroupMap = null;
	private Map<Integer, Screen> currentScreenMap = null;
	private TabBar currentPanelTabBar = null;
	private boolean isInitialised = false;
	private PanelSize panelSize = null;
	private Map<Panel, String[]> panelImageUrlMap = new LinkedHashMap<Panel, String[]>();
	
	private PanelServiceImpl() {}
	
	public static synchronized PanelServiceImpl getInstance() {
		if (instance == null) {
			instance = new PanelServiceImpl();
		}
		return instance;
	}
	
	@Override
	public Screen getScreenById(Integer screenId) {
		return getScreenById(screenId, null);
	}
	
	@Override
	public Screen getScreenById(Integer screenId, Integer groupId) {
		Screen screen = null;
		boolean groupContainsScreen = false;
		
		if (groupId == null) {
			groupContainsScreen = true;
		} else {
			for (Integer sId : getGroupScreenIds(groupId)) {
				if (sId.equals(screenId)) {
					groupContainsScreen = true;
					break;
				}
			}
		}
		if (groupContainsScreen) {
			if (currentScreenMap != null && screenId != null) {
					screen = currentScreenMap.get(screenId);
			}
		} else if (groupId != null) {
			screen = getDefaultScreen(groupId);
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
			boolean isScreenLandscape = false;
			
			Screen oldScreen = getScreenById(screenId);
			if (oldScreen.getLandscape() != null && oldScreen.getLandscape()) isScreenLandscape = true;
			List<Integer> screenIds = getGroupScreenIds(groupId);
			
				for(int i=screenIndex+1; i<screenIds.size(); i++) {
					boolean isCompareScreenLandscape = false;
					Screen scrn = getScreenById(screenIds.get(i));
					if (scrn != null) {
						if (scrn.getLandscape() != null && scrn.getLandscape()) isCompareScreenLandscape = true;
						if (isCompareScreenLandscape == isScreenLandscape) {
							screen = scrn;
							break;
						}
					}
				}
		}		
		return screen;
	}
	
	@Override
	public Screen getPreviousScreen(Integer groupId, Integer screenId) {
		Screen screen = null;
		Integer screenIndex = getGroupScreenIndex(groupId, screenId);
		
		if (screenIndex != null && screenIndex >= 0) {
			boolean isScreenLandscape = false;
			
			Screen oldScreen = getScreenById(screenId);
			if (oldScreen.getLandscape() != null && oldScreen.getLandscape()) isScreenLandscape = true;
			List<Integer> screenIds = getGroupScreenIds(groupId);
			
				for(int i=screenIndex-1; i>=0; i--) {
					boolean isCompareScreenLandscape = false;
					Screen scrn = getScreenById(screenIds.get(i));
					if (scrn != null) {
						if (scrn.getLandscape() != null && scrn.getLandscape()) isCompareScreenLandscape = true;
						if (isCompareScreenLandscape == isScreenLandscape) {
							screen = scrn;
							break;
						}
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
			
			if (currentPanel.getTabbar() != null) {
				currentPanelTabBar = currentPanel.getTabbar(); 
			}
			
			// Get image Urls
			if (!panelImageUrlMap.containsKey(currentPanel))
			{
				panelImageUrlMap.put(currentPanel, getPanelImageUrls());
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
		int width = ConsoleUnit.DEFAULT_DISPLAY_WIDTH;
		int height = ConsoleUnit.DEFAULT_DISPLAY_HEIGHT;
		
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
						DimensionResult h = PanelComponent.getDimFromString(panel.getHeight());
						if (screen.getLandscape() != null && screen.getLandscape()) {
							if (l.getUnit() == DimensionUnit.PERCENTAGE || t.getUnit() == DimensionUnit.PERCENTAGE || w.getUnit() == DimensionUnit.PERCENTAGE || h.getUnit() == DimensionUnit.PERCENTAGE) {
								continue;
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

	
	@Override
	public List<Integer> getGroupScreenIds(Integer groupId) {
		List<Integer> screenIds = new ArrayList<Integer>();
		
		if (groupId != null && currentGroupMap != null) {
			Group group = getGroupById(groupId);
			if (group != null) {
				List<ScreenRef> screenRefs = group.getInclude();
				if (screenRefs != null) {
					for(ScreenRef ref : screenRefs) { 
							screenIds.add(ref.getRef());
					}
				}
			}
		}
		return screenIds;
	}
	
	@Override
	public List<Integer> getGroupScreenIdsWithSameOrientation(Integer screenId, Integer groupId) {
		List<Integer> screenIds = getGroupScreenIds(groupId);
		List<Integer> filteredScreenIds = new ArrayList<Integer>();
		boolean isScreenLandscape = false;
		
		if (screenIds.size() > 0) {
			Screen screen = getScreenById(screenId);
			boolean isCompareScreenLandscape = false;
			if (screen.getLandscape() != null && screen.getLandscape()) isScreenLandscape = true; 
			for(int id : screenIds) { 
				Screen scrn = getScreenById(id);
				
				if (scrn != null) {
					isCompareScreenLandscape = (scrn.getLandscape() != null && scrn.getLandscape()) ? true : false;
					if (isCompareScreenLandscape == isScreenLandscape) {
						filteredScreenIds.add(id);
					}
				}
			}
		}
		
		return filteredScreenIds;
	}

	@Override
	public String[] getPanelImageUrls() {
		if (panelImageUrlMap.containsKey(currentPanel)) {
			return panelImageUrlMap.get(currentPanel);
		}
		
		Set<String> imageUrls = new HashSet<String>();
		
		// Cycle through panel looking for images that may be required
		if (currentScreenMap != null) {
			for (int screenId : currentScreenMap.keySet()) {
				Screen screenElem = currentScreenMap.get(screenId);
				
				// Check background
				if (screenElem.getBackground() != null && screenElem.getBackground().getImage() != null) {
					processImageElement(screenElem.getBackground().getImage(), imageUrls);
				}
				
				// Process Absolute and grid layouts
				processAbsoluteAndGridLayouts(screenElem.getAbsolute(), screenElem.getGrid(), imageUrls);
				
				// Cycle through list panel elements
				if (screenElem.getList() != null) {
					for (ListLayout layout : screenElem.getList()) {
						ListItemLayout itemLayout = layout.getItemTemplate();
						if (itemLayout != null) {
							processAbsoluteAndGridLayouts(itemLayout.getAbsolute(), itemLayout.getGrid(), imageUrls);
						}
					}
				}
			}
		}
			
		// Cycle through groups looking for tab bar images
		// Cycle through panel looking for images that may be required
		if (currentGroupMap != null) {
			for (int groupId : currentGroupMap.keySet()) {
				Group group = currentGroupMap.get(groupId);
				if (group != null && group.getTabbar() != null && group.getTabbar().getItem() != null)
				{
					for (TabBarItem item : group.getTabbar().getItem()) {
						if (item != null) {
							if (item.getImage() != null) {
								if (item.getImage().getSystemImage() != null && item.getImage().getSystemImage()) {
									imageUrls.add(BrowserUtils.getSystemImageDir() + item.getImage().getSrc());
								} else {
									imageUrls.add(WebConsole.getConsoleUnit().getControllerService().getController().getUrl() + item.getImage().getSrc());
								}
							}
						}
					}
				}
			}
		}
			
		// Cycle through panel tab bar
		if (currentPanel.getTabbar() != null && currentPanel.getTabbar().getItem() != null)
		{
			for (TabBarItem item : currentPanel.getTabbar().getItem()) {
				if (item != null) {
					if (item.getImage() != null) {
						if (item.getImage().getSystemImage() != null && item.getImage().getSystemImage()) {
							imageUrls.add(BrowserUtils.getSystemImageDir() + item.getImage().getSrc());
						} else {
							imageUrls.add(WebConsole.getConsoleUnit().getControllerService().getController().getUrl() + item.getImage().getSrc());
						}
					}
				}
			}
		}
		
		return imageUrls.toArray(new String[] {});
	}
	
	private void processAbsoluteAndGridLayouts(List<AbsoluteLayout> absLayouts, List<GridLayout> gridLayouts, Set<String> imageUrls) {
		// Cycle through absolute elements
		if (absLayouts != null) {
			for (AbsoluteLayout abs : absLayouts) {
				if (abs != null) {
					processComponentContainer(abs, imageUrls);
				}
			}
		}
		
		// Cycle through grid elements
		if (gridLayouts != null) {
			for (GridLayout grid : gridLayouts) {
				List<Cell> cells = grid.getCell();
			
				if (cells != null) {
					for (Cell cell : cells) {
						if (cell != null) {
							processComponentContainer(cell, imageUrls);
						}
					}
				}
			}
		}
	}
	
	private void processComponentContainer(ComponentContainer compContainer, Set<String> imageUrls) {
		if (compContainer != null) {
			// Check Button
			if (compContainer.getButton() != null) processButtonElement(compContainer.getButton(), imageUrls);
			// Check Image
			if (compContainer.getImage() != null) processImageComponentElement(compContainer.getImage(), imageUrls);
			// Check Slider
			if (compContainer.getSlider() != null) processSliderElement(compContainer.getSlider(), imageUrls);
			// Check Switch
			if (compContainer.getSwitch() != null) processSwitchElement(compContainer.getSwitch(), imageUrls);
		}		
	}
	
	private void processImageComponentElement(ImageComponent img, Set<String> imageUrls) {
		if (img != null) {
			// Check src and check link
			if (img.getSrc() != null) imageUrls.add(WebConsole.getConsoleUnit().getControllerService().getController().getUrl() + img.getSrc());
			if (img.getLink() != null) {
				List<StateMap> stateMap = img.getLink().getState();
				if (stateMap != null) {
					for (StateMap state : stateMap) {
						imageUrls.add(WebConsole.getConsoleUnit().getControllerService().getController().getUrl() + state.getValue());
					}
				}
			}
		}
	}
	
	private void processButtonElement(ButtonComponent btn, Set<String> imageUrls) {
		if (btn != null) {
			if (btn.getDefault() != null && btn.getDefault().getImage() != null) {
				if (btn.getDefault().getImage().getSystemImage() != null && btn.getDefault().getImage().getSystemImage()) {
					imageUrls.add(BrowserUtils.getSystemImageDir() + btn.getDefault().getImage().getSrc());
				} else {
					imageUrls.add(WebConsole.getConsoleUnit().getControllerService().getController().getUrl() + btn.getDefault().getImage().getSrc());
				}
			}
			if (btn.getPressed() != null && btn.getPressed().getImage() != null) {
				if (btn.getPressed().getImage().getSystemImage() != null && btn.getPressed().getImage().getSystemImage()) {
					imageUrls.add(BrowserUtils.getSystemImageDir() + btn.getPressed().getImage().getSrc());
				} else {
					imageUrls.add(WebConsole.getConsoleUnit().getControllerService().getController().getUrl() + btn.getPressed().getImage().getSrc());
				}
			}
		}
	}
	
	private void processSwitchElement(SwitchComponent swtch, Set<String> imageUrls) {
		if (swtch != null) {
			if (swtch.getLink() != null) {
				if (swtch.getLink().getState() != null) {
					for(StateMap map : swtch.getLink().getState()) {
							imageUrls.add(WebConsole.getConsoleUnit().getControllerService().getController().getUrl() + map.getValue());
					}
				}
			}
		}
	}
	
	private void processSliderElement(SliderComponent slider, Set<String> imageUrls) {
		if (slider != null) {
			if (slider.getThumbImage() != null) imageUrls.add(WebConsole.getConsoleUnit().getControllerService().getController().getUrl() + slider.getThumbImage());
			SliderMinMax max = slider.getMax();
			SliderMinMax min = slider.getMin();
			if (max != null) {
				if (max.getImage() != null) imageUrls.add(WebConsole.getConsoleUnit().getControllerService().getController().getUrl() + max.getImage());
				if (max.getTrackImage() != null) imageUrls.add(WebConsole.getConsoleUnit().getControllerService().getController().getUrl() + max.getTrackImage());
			}
			if (min != null) {
				if (min.getImage() != null) imageUrls.add(WebConsole.getConsoleUnit().getControllerService().getController().getUrl() + min.getImage());
				if (min.getTrackImage() != null) imageUrls.add(WebConsole.getConsoleUnit().getControllerService().getController().getUrl() + min.getTrackImage());
			}
		}
	}
	
	private void processImageElement(Image img, Set<String> imageUrls) {
		if (img != null) {
			if (img.getSystemImage() != null && img.getSystemImage()) {
				imageUrls.add(BrowserUtils.getSystemImageDir() + img.getSrc());
			} else {
				imageUrls.add(WebConsole.getConsoleUnit().getControllerService().getController().getUrl() + img.getSrc());
			}
		}
	}
}
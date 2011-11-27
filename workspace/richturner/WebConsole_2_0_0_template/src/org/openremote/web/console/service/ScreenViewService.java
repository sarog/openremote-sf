package org.openremote.web.console.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.panel.entity.AbsoluteLayout;
import org.openremote.web.console.panel.entity.GridLayout;
import org.openremote.web.console.panel.entity.Screen;
import org.openremote.web.console.view.AddEditControllerScreenView;
import org.openremote.web.console.view.LoadingScreenView;
import org.openremote.web.console.view.ScreenViewImpl;
import org.openremote.web.console.widget.panel.AbsolutePanelComponent;
import org.openremote.web.console.widget.panel.GridPanelComponent;

import com.google.gwt.user.client.Window;

public class ScreenViewService {
	Map<Integer, ScreenViewImpl> screenViewMap = new HashMap<Integer, ScreenViewImpl>();	
	
	public enum EnumSystemScreen {
		LOADING_SCREEN(-1, "loadingscreen"),
		CONTROLLER_LIST(-2, "controllerlist"),
		ADD_EDIT_CONTROLLER(-2, "editcontroller"),
		CONSOLE_SETTINGS(-2, "consolesettings"),
		LOGIN(-2, "login"),
		LOGOUT(-2, "logout"),
		PANEL_SELECTION(-2, "panelselection");
		
		private final int id;
		private final String name;
		
		EnumSystemScreen(int id, String name) {
			this.id = id;
			this.name = name;
		}
		
		public int getId() {
			return id;
		}
		
		public String getName() {
			return name;
		}
		
		public static EnumSystemScreen getSystemScreen(int id) {
			EnumSystemScreen result = null;
			for (EnumSystemScreen screen : EnumSystemScreen.values()) {
				if (screen.getId() == id) {
					result = screen;
					break;
				}
			}
			return result;
		}
		
		public static EnumSystemScreen getSystemScreen(String name) {
			EnumSystemScreen result = null;
			for (EnumSystemScreen screen : EnumSystemScreen.values()) {
				if (screen.getName() == name) {
					result = screen;
					break;
				}
			}
			return result;
		}
	}
	
	
	public ScreenViewService() {
		
	}
	
	public ScreenViewImpl getScreenView(EnumSystemScreen screen) {
		int screenId = screen.getId();
		ScreenViewImpl screenView = screenViewMap.get(screenId);
		if (screenView == null) {
			buildSystemScreenViews();
			screenView = screenViewMap.get(screenId);
		}
		return screenView;
	}
	
	public ScreenViewImpl getScreenView(int loadingScreenId) {
		ScreenViewImpl screenView;
		screenView = screenViewMap.get(loadingScreenId);
		return screenView;
	}
	
	public ScreenViewImpl getScreenView(Screen screen) {
		ScreenViewImpl screenView = null;
		if (screen != null) {
			int screenId = screen.getId();
			screenView = screenViewMap.get(screenId);
			if (screenView == null) {
				screenView = buildScreenView(screen);
				if (screenView != null) {
					screenViewMap.put(screenId, screenView);
				}
			}
		}
		return screenView;
	}
	
	private ScreenViewImpl buildScreenView(Screen screen) {
		ScreenViewImpl screenView = new ScreenViewImpl();
		
		// Set background if defined
		screenView.setBackground(screen.getBackground());
		
		// Cycle through absolute and grid lists and create components
		try {
			List<AbsoluteLayout> absoluteElems = screen.getAbsolute();
			
			if (absoluteElems != null) {
				for (AbsoluteLayout layout : absoluteElems) {
					// Create Absolute Panel Component
					AbsolutePanelComponent absPanel = AbsolutePanelComponent.build(layout);
					screenView.addPanelComponent(absPanel);
				}
			}
			
			List<GridLayout> gridElems = screen.getGrid();
			
			if (gridElems != null) {
				for (GridLayout layout : gridElems) {
					// Create Grid Panel Component
					GridPanelComponent gridPanel = GridPanelComponent.build(layout);
					screenView.addPanelComponent(gridPanel);
				}
			}
		} catch (Exception e) {
			Window.alert("Problem with JSON Parsing");
			WebConsole.getConsoleUnit().loadScreenView(getScreenView(ScreenViewService.EnumSystemScreen.CONTROLLER_LIST), null);
		}
		return screenView;
	}
	
	private void buildSystemScreenViews() {
		for(EnumSystemScreen screen : EnumSystemScreen.values()) {
			ScreenViewImpl screenView = null;
			switch (screen) {
			case LOADING_SCREEN:
				screenView = new LoadingScreenView();
				break;
			case ADD_EDIT_CONTROLLER:
				screenView = new AddEditControllerScreenView();	
				break;
			}
			if (screenView != null) {
				screenViewMap.put(screen.getId(), screenView);
			}
		}
	}
}

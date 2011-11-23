package org.openremote.web.console.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openremote.web.console.panel.entity.AbsoluteLayout;
import org.openremote.web.console.panel.entity.GridLayout;
import org.openremote.web.console.panel.entity.Screen;
import org.openremote.web.console.view.LoadingScreenView;
import org.openremote.web.console.view.ScreenViewImpl;
import org.openremote.web.console.widget.panel.AbsolutePanelComponent;
import org.openremote.web.console.widget.panel.GridPanelComponent;

import com.google.gwt.user.client.Window;

public class ScreenViewService {
	public static final int LOADING_SCREEN_ID = -1;
	Map<Integer, ScreenViewImpl> screenViewMap = new HashMap<Integer, ScreenViewImpl>();	
	
	public ScreenViewService() {
		
	}
	
	public ScreenViewImpl getScreenView(int screenId) {
		ScreenViewImpl screenView;
		screenView = screenViewMap.get(screenId);
		if (screenView == null) {
			if (screenId < 0) {
				buildSystemScreenViews();
				screenView = screenViewMap.get(screenId);
			}
		}
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
			// TODO: Handle error
		}
		return screenView;
	}
	
	private void buildSystemScreenViews() {
		ScreenViewImpl loadingScreen = new LoadingScreenView();
		screenViewMap.put(LOADING_SCREEN_ID, loadingScreen);
	}
}

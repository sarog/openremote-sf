package org.openremote.web.console.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openremote.web.console.panel.entity.AbsoluteLayout;
import org.openremote.web.console.panel.entity.FormLayout;
import org.openremote.web.console.panel.entity.GridLayout;
import org.openremote.web.console.panel.entity.Screen;
import org.openremote.web.console.view.ScreenViewImpl;
import org.openremote.web.console.widget.panel.AbsolutePanelComponent;
import org.openremote.web.console.widget.panel.GridPanelComponent;
import org.openremote.web.console.widget.panel.form.FormPanelComponent;

public class ScreenViewService {
	private static ScreenViewService instance = null;
	Map<Integer, ScreenViewImpl> screenViewMap = new HashMap<Integer, ScreenViewImpl>();	
	
	private ScreenViewService() {}
	
	public static synchronized ScreenViewService getInstance() {
		if (instance == null) {
			instance = new ScreenViewService();
		}
		return instance;
	}
	
	public ScreenViewImpl getScreenView(int screenId) {
		ScreenViewImpl screenView;
		screenView = screenViewMap.get(screenId);
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
	
	
	public void reset() {
		screenViewMap.clear();
	}
	
	private ScreenViewImpl buildScreenView(Screen screen) {
		ScreenViewImpl screenView = new ScreenViewImpl();
		
		// Set background if defined
		screenView.setBackground(screen.getBackground());
		
		// Check orientation
		Boolean isLandscape = screen.getLandscape();
		if (isLandscape != null && isLandscape) {
			screenView.setIsLandscape(true);
		}
		
		// Cycle through absolute, grid and form panels and create components
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
			
			List<FormLayout> formElems = screen.getForm();
			
			if (formElems != null) {
				for (FormLayout layout : formElems) {
					FormPanelComponent formPanel = FormPanelComponent.build(layout);
					screenView.addPanelComponent(formPanel);
				}
			}
		} catch (Exception e) {
			return null;
		}
		return screenView;
	}
}

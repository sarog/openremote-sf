package org.openremote.web.console.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openremote.web.console.panel.entity.AbsoluteLayout;
import org.openremote.web.console.panel.entity.Screen;
import org.openremote.web.console.panel.entity.component.ButtonComponent;
import org.openremote.web.console.panel.entity.component.ImageComponent;
import org.openremote.web.console.panel.entity.component.LabelComponent;
import org.openremote.web.console.panel.entity.component.SliderComponent;
import org.openremote.web.console.panel.entity.component.SwitchComponent;
import org.openremote.web.console.view.LoadingScreenView;
import org.openremote.web.console.view.ScreenViewImpl;
import org.openremote.web.console.widget.AbsolutePanelComponent;
import org.openremote.web.console.widget.ConsoleComponent;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;

public class ScreenViewService {
	public static final int LOADING_SCREEN_ID = -1;
	Map<Integer, ScreenViewImpl> screenViewMap = new HashMap<Integer, ScreenViewImpl>();	
	
	public ScreenViewService() {
		
	}
	
	public ScreenViewImpl getScreenView(int screenId) {
		ScreenViewImpl screenView;
		screenView = screenViewMap.get(screenId);
		if (screenView == null && screenId < 0) {
			buildSystemScreenViews();
			screenView = screenViewMap.get(screenId);
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
		
		// Cycle through absolute and grid lists and create components
		try {
			List<AbsoluteLayout> absoluteElems = screen.getAbsolute();
			
			for (AbsoluteLayout layout : absoluteElems) {
				LabelComponent labelComponent = layout.getLabel();
				ImageComponent imageComponent = layout.getImage();
				SliderComponent sliderComponent = layout.getSlider();
				SwitchComponent switchComponent = layout.getSwitch();
				ButtonComponent buttonComponent = layout.getButton();
				
				// Create Absolute Panel Component
				AbsolutePanelComponent absPanel = new AbsolutePanelComponent();
				absPanel.setHeight(layout.getHeight());
				absPanel.setWidth(layout.getWidth());
				absPanel.setPosition(layout.getLeft(),layout.getTop());
				
				// Create Console Component
				ConsoleComponent component = null;
				
				if (labelComponent != null) {
					component = org.openremote.web.console.widget.LabelComponent.build(labelComponent);
				} else if (imageComponent != null) {
					component = org.openremote.web.console.widget.ImageComponent.build(imageComponent);
				} else if (sliderComponent != null) {
					component = org.openremote.web.console.widget.SliderComponent.build(sliderComponent);
				} else if (switchComponent != null) {
					component = org.openremote.web.console.widget.SwitchComponent.build(switchComponent);
				} else if (buttonComponent != null) {
					component = org.openremote.web.console.widget.ButtonComponent.build(buttonComponent);
				} else {
					return null;
				}
				
				if (component != null) {
					absPanel.setComponent(component);
					screenView.addConsoleWidget(absPanel);
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

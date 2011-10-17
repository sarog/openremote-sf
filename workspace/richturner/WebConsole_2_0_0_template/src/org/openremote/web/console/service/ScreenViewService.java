package org.openremote.web.console.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openremote.web.console.panel.entity.AbsoluteLayout;
import org.openremote.web.console.panel.entity.TabImage;
import org.openremote.web.console.panel.entity.Screen;
import org.openremote.web.console.panel.entity.component.ButtonComponent;
import org.openremote.web.console.panel.entity.component.ImageComponent;
import org.openremote.web.console.panel.entity.component.LabelComponent;
import org.openremote.web.console.panel.entity.component.SliderComponent;
import org.openremote.web.console.panel.entity.component.SwitchComponent;
import org.openremote.web.console.view.LoadingScreenView;
import org.openremote.web.console.view.ScreenView;
import org.openremote.web.console.view.ScreenViewImpl;
import org.openremote.web.console.widget.AbsolutePanelComponent;
import org.openremote.web.console.widget.ConsoleComponent;

import com.google.gwt.user.client.Window;

public class ScreenViewService {
	public static final int LOADING_SCREEN_ID = -1;
	Map<Integer, ScreenView> screenViewMap = new HashMap<Integer, ScreenView>();	
	
	public ScreenViewService() {
		
	}
	
	public ScreenView getScreenView(int screenId) {
		ScreenView screenView;
		screenView = screenViewMap.get(screenId);
		if (screenView == null && screenId < 0) {
			buildSystemScreenViews();
			screenView = screenViewMap.get(screenId);
		}
		return screenView;
	}
	
	public ScreenView getScreenView(Screen screen) {
		ScreenView screenView = null;
		if (screen != null) {
			int screenId = screen.getId();
			screenView = screenViewMap.get(screenId);
			if (screenView == null) {
				screenView = buildScreenView(screen);
			}
			if (screenView != null) {
				screenViewMap.put(screenId, screenView);
			}
		}
		return screenView;
	}
	
	private ScreenView buildScreenView(Screen screen) {
		ScreenViewImpl screenView = new ScreenViewImpl();
		
		// Cycle through absolute and grid lists and create components
		List<AbsoluteLayout> absoluteElems = screen.getAbsolute();
		for (AbsoluteLayout layout : absoluteElems) {
			LabelComponent label = layout.getLabel();
			ImageComponent image = layout.getImage();
			SliderComponent slider = layout.getSlider();
			SwitchComponent switchComp = layout.getSwitch();
			ButtonComponent button = layout.getButton();
			
			// Create Absolute Panel Component
			AbsolutePanelComponent absPanel = new AbsolutePanelComponent();
			absPanel.setHeight(layout.getHeight() + "px");
			absPanel.setWidth(layout.getWidth() + "px");
			absPanel.setPosition(layout.getLeft(),layout.getTop());
			
			// Create Console Component
			ConsoleComponent component = null;
			
			if (label != null) {
				component = org.openremote.web.console.widget.LabelComponent.build(label);
			} else if (image != null) {
				
			} else if (slider != null) {
				
			} else if (switchComp != null) {
				
			} else if (button != null) {
				
			} else {
				return null;
			}
			
			if (component != null) {
				absPanel.setComponent(component);
				screenView.addConsoleWidget(absPanel);
			}
		}
		
		return screenView;
	}
	
	private void buildSystemScreenViews() {
		ScreenView loadingScreen = new LoadingScreenView();
		screenViewMap.put(LOADING_SCREEN_ID, loadingScreen);
	}
}

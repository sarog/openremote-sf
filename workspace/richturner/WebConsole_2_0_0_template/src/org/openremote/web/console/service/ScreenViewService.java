package org.openremote.web.console.service;

import java.util.HashMap;
import java.util.Map;

import org.openremote.web.console.panel.entity.Screen;
import org.openremote.web.console.view.LoadingScreenView;
import org.openremote.web.console.view.ScreenView;

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
				buildScreenView(screen);
			}
			if (screenView != null) {
				screenViewMap.put(screenId, screenView);
			}
		}
		return screenView;
	}
	
	private ScreenView buildScreenView(Screen screen) {
		// TODO Build Screen View from Screen
		ScreenView screenView = null;
		
		return screenView;
	}
	
	private void buildSystemScreenViews() {
		ScreenView loadingScreen = new LoadingScreenView();
		screenViewMap.put(LOADING_SCREEN_ID, loadingScreen);
	}
}

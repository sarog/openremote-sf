package org.openremote.web.console.service;

import org.openremote.web.console.panel.Panel;
import org.openremote.web.console.panel.entity.Group;
import org.openremote.web.console.panel.entity.Screen;
import org.openremote.web.console.panel.entity.Screens;
import org.openremote.web.console.view.ScreenView;

public class PanelServiceImpl implements PanelService {
	Panel currentPanel;

	@Override
	public Screen getScreenById(int screenId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Screen getScreenByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Screen getDefaultScreen() {
		// TODO Auto-generated method stub
		Screen screen = null;
		if (currentPanel != null) {
			Screen[] screens = currentPanel.getScreens().getScreen();
			int count = screens.length;
			//screen = currentPanel.getScreens().getScreen()[0];
		}
		return screen;
	}

	@Override
	public Group getGroupById() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Panel getCurrentPanel() {
		return currentPanel;
	}

	public void setCurrentPanel(Panel currentPanel) {
		this.currentPanel = currentPanel;
	}
}

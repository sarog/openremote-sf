package org.openremote.web.console.service;

import org.openremote.web.console.entity.Group;
import org.openremote.web.console.entity.Screen;
import org.openremote.web.console.panel.Panel;

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
	public Group getDefaultGroup() {
		// TODO Auto-generated method stub
		return null;
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

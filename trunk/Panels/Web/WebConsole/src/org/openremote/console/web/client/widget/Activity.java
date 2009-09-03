package org.openremote.console.web.client.widget;

import java.util.List;

/**
 * Represents an Activity containing a list of screens.
 * 
 * @author David Reines
 */
public class Activity {

	private final String name;
	private final List<Screen> screens;

	private Screen currentScreen;

	public Activity(String name, List<Screen> screens) {
		this.name = name;
		this.screens = screens;
		if (!this.screens.isEmpty()) {
			this.currentScreen = screens.get(0);
		}
	}

	public void show() {
		if (this.currentScreen != null) {
			this.currentScreen.setVisible(true);
		}
	}

	public String getName() {
		return name;
	}

	public List<Screen> getScreens() {
		return screens;
	}

}

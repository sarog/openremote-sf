package org.openremote.web.console.components;

import org.openremote.web.console.views.ConsoleScreenView;
import org.openremote.web.console.views.LoadingScreenView;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class ConsoleDisplay extends SimplePanel {

	private final ConsoleScreenView loadingScreen;
	private ConsoleScreenView currentScreen;
	
	public ConsoleDisplay(int width, int height) {
		// Create the empty layout panel which will be
		// the screen where content will be displayed
		super();
		this.setWidth(width + "px");
		this.setHeight(height + "px");
		this.setStylePrimaryName("consoleDisplay");
		
		// Initialise loading screen
		loadingScreen = new LoadingScreenView();
	}
	
	public void showLoadingScreen() {
		this.setWidget(loadingScreen);
//		HTML label = new HTML();
//		label.setStylePrimaryName("labeled");
//		label.setText("LOADING");
//		this.setWidget(label);
	}
	
	public void clearScreen() {
		this.setWidget(null);
	}
}

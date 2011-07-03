package org.openremote.web.console.components;

import org.openremote.web.console.views.ConsoleScreenView;
import org.openremote.web.console.views.LoadingScreenView;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ConsoleDisplay extends AbsolutePanel {
	
	private SimplePanel portraitDisplay;
	private SimplePanel landscapeDisplay;
	private String currentOrientation;
	private final ConsoleScreenView loadingScreen;
	private ConsoleScreenView currentScreen;
	
	public ConsoleDisplay(int width, int height) {
		// Create the empty layout panel which will be
		// the screen where content will be displayed
		super();
		this.setWidth(width + "px");
		this.setHeight(height + "px");
		this.setStylePrimaryName("consoleDisplay");
		currentOrientation = "portrait";
		
		// Create display widgets for both orientations
		portraitDisplay = new SimplePanel();
		portraitDisplay.setWidth(width + "px");
		portraitDisplay.setHeight(height + "px");
		portraitDisplay.setStylePrimaryName("portraitDisplay");
		
		landscapeDisplay = new SimplePanel();
		landscapeDisplay.setWidth(height + "px");
		landscapeDisplay.setHeight(width + "px");
		landscapeDisplay.setStylePrimaryName("LandscapeDisplay");
		
		this.add(portraitDisplay, 0, 0);
		this.add(landscapeDisplay, (width/2)-(height/2), (height/2)-(width/2));
		// Set default display orientation to portrait
		setOrientation("portrait");
		
		// Initialise loading screen
		loadingScreen = new LoadingScreenView();
		
		// Display loading screen
		showScreen(loadingScreen);
	}
	
	public void setOrientation(String orientation) {
		if ("portrait".equals(orientation)) {
//			getElement().removeClassName("landscape");
//			getElement().addClassName("portrait");
			landscapeDisplay.setVisible(false);
			portraitDisplay.setVisible(true);
			currentOrientation = orientation;
		}
		if ("landscape".equals(orientation)) {
//			getElement().removeClassName("portrait");
//			getElement().addClassName("landscape");
			portraitDisplay.setVisible(false);
			landscapeDisplay.setVisible(true);
			currentOrientation = orientation;
		}
	}
	
	public void showScreen(ConsoleScreenView screen) {
		currentScreen = screen;
		portraitDisplay.setWidget(screen);
		landscapeDisplay.setWidget(new LoadingScreenView());
	}
	
	public void clearScreen() {
		portraitDisplay.setWidget(null);
		landscapeDisplay.setWidget(null);
	}
}

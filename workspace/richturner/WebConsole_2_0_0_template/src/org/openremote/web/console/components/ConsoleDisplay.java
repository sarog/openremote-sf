package org.openremote.web.console.components;

import org.openremote.web.console.views.ConsoleScreenView;
import org.openremote.web.console.views.LoadingScreenView;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ConsoleDisplay extends AbsolutePanel {
	
	private SimplePanel display;
	private String currentOrientation;
	private final ConsoleScreenView loadingScreen;
	private ConsoleScreenView currentScreen;
	private int width;
	private int height;
	
	public ConsoleDisplay(int width, int height) {
		// Create the empty layout panel which will be
		// the screen where content will be displayed
		super();
		this.width = width;
		this.height = height;
		this.setWidth(width + "px");
		this.setHeight(height + "px");
		this.setStylePrimaryName("consoleDisplay");
		currentOrientation = "portrait";
		
		// Create display widgets for both orientations
		display = new SimplePanel();
		display.setWidth(width + "px");
		display.setHeight(height + "px");
		display.setStylePrimaryName("portraitDisplay");
		
		this.add(display, 0, 0);
		//this.add(landscapeDisplay, (width/2)-(height/2), (height/2)-(width/2));
		// Set default display orientation to portrait
		setOrientation("portrait");
		
		// Initialise loading screen
		loadingScreen = new LoadingScreenView();
		
		// Display loading screen
		showScreen(loadingScreen);
	}
	
	public void setOrientation(String orientation) {
		if ("portrait".equals(orientation)) {
		   this.setWidgetPosition(display,0,0);
		   display.setStylePrimaryName("portraitDisplay");
		   display.setWidth(width + "px");
		   display.setHeight(height + "px");		   
			currentOrientation = orientation;
		}
		if ("landscape".equals(orientation)) {
			this.setWidgetPosition(display, (width/2)-(height/2), (height/2)-(width/2));
			display.setStylePrimaryName("landscapeDisplay");
		   display.setWidth(height + "px");
		   display.setHeight(width + "px");
			currentOrientation = orientation;
		}
	}
	
	public void showScreen(ConsoleScreenView screen) {
		currentScreen = screen;
		display.setWidget(screen);
	}
	
	public void clearScreen() {
		display.setWidget(null);
	}
}

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
	
	private SimplePanel portraitDisplay;
	private SimplePanel landscapeDisplay;
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
		portraitDisplay = new SimplePanel();
		portraitDisplay.setWidth(width + "px");
		portraitDisplay.setHeight(height + "px");
		portraitDisplay.setStylePrimaryName("portraitDisplay");
		
//		landscapeDisplay = new SimplePanel();
//		landscapeDisplay.setWidth(height + "px");
//		landscapeDisplay.setHeight(width + "px");
//		landscapeDisplay.setStylePrimaryName("landscapeDisplay");
		
		this.add(portraitDisplay, 0, 0);
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
		   this.setWidgetPosition(portraitDisplay,0,0);
		   portraitDisplay.setStylePrimaryName("portraitDisplay");
		   portraitDisplay.setWidth(width + "px");
		   portraitDisplay.setHeight(height + "px");		   
//			landscapeDisplay.setVisible(false);
//			portraitDisplay.setVisible(true);
			currentOrientation = orientation;
		}
		if ("landscape".equals(orientation)) {
			this.setWidgetPosition(portraitDisplay, (width/2)-(height/2), (height/2)-(width/2));
			portraitDisplay.setStylePrimaryName("landscapeDisplay");
		   portraitDisplay.setWidth(height + "px");
		   portraitDisplay.setHeight(width + "px");
//			portraitDisplay.setVisible(false);
//			landscapeDisplay.setVisible(true);
			currentOrientation = orientation;
		}
	}
	
	public void showScreen(ConsoleScreenView screen) {
		currentScreen = screen;
		portraitDisplay.setWidget(screen);
	}
	
	public void clearScreen() {
		portraitDisplay.setWidget(null);
		//landscapeDisplay.setWidget(null);
	}
	
	/**
	 * Adds a border to the display to give boss illusion
	 */
	public void displayBoss() {
		portraitDisplay.getElement().getStyle().setBorderWidth(2,Unit.PX);
		portraitDisplay.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		portraitDisplay.setWidth((width - 4) + "px");
		portraitDisplay.setHeight((height - 4) + "px");
		portraitDisplay.getElement().getStyle().setBorderColor("#333");
//		landscapeDisplay.getElement().getStyle().setBorderWidth(2,Unit.PX);
//		landscapeDisplay.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
//		landscapeDisplay.setWidth((height - 4) + "px");
//		landscapeDisplay.setHeight((width - 4) + "px");
//		landscapeDisplay.getElement().getStyle().setBorderColor("#333");
	}
}

package org.openremote.web.console.components;

import org.openremote.web.console.views.ConsoleScreenView;
import org.openremote.web.console.views.LoadingScreenView;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * This is the container where content will actually be displayed
 * An absolute panel is used as a wrapper to ease with repositioning
 * the display within the console unit during orientation change
 * @author rich
 *
 */
public class ConsoleDisplay extends AbsolutePanel {
	
	public static final int DEFAULT_DISPLAY_WIDTH = 320;
	public static final int DEFAULT_DISPLAY_HEIGHT = 480;
	private static final String DEFAULT_DISPLAY_COLOUR = "black";
	private SimplePanel display;
	private String currentOrientation;
	private final ConsoleScreenView loadingScreen;
	private ConsoleScreenView currentScreen;
	private int width;
	private int height;
	public String colour;
	
	public ConsoleDisplay(int width, int height) {
		super();
		this.width = width;
		this.height = height;
		setWidth(this.width + "px");
		setHeight(this.height + "px");
		getElement().setId("consoleDisplayWrapper");
		setStylePrimaryName("consoleDisplay");
		
		// Create display panel where screen is actually loaded
		display = new SimplePanel();
		display.setWidth(width + "px");
		display.setHeight(height + "px");
		display.getElement().setId("consoleDisplay");
		
		// Add display to the wrapper
		add(display, 0, 0);

		// Set default display orientation to portrait
		setOrientation("portrait");
		
		// Set default colour
		setColour(DEFAULT_DISPLAY_COLOUR);
		
		// Initialise loading screen
		loadingScreen = new LoadingScreenView();
		
		// Display loading screen
		showScreen(loadingScreen);
	}
	
	/**
	 * Set the display orientation which changes the CSS class causing a
	 * rotate transform to be applied, have to also adjust the display
	 * size and position within the wrapper
	 * @param orientation
	 */
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
	
	public void setColour(String colour) {
		getElement().getStyle().setBackgroundColor(colour);
		this.colour = colour;
	}
	
	public String getColour() {
		return colour;
	}
	
	/**
	 * Shows the specified screen on the display
	 * @param screen
	 */
	public void showScreen(ConsoleScreenView screen) {
		currentScreen = screen;
		display.setWidget(screen);
	}
	
	/**
	 * Completely clear the display
	 */
	public void clearScreen() {
		display.setWidget(null);
	}
}

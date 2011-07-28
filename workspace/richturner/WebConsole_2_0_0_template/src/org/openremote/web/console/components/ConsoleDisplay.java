package org.openremote.web.console.components;

import org.openremote.web.console.events.ConsoleUnitEventManager;
import org.openremote.web.console.screens.ConsoleScreen;
import org.openremote.web.console.screens.LoadingScreen;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.shared.HandlerManager;
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
	private ConsoleScreen currentScreen;
	private int width;
	private int height;
	public String colour;
	private ConsoleUnitEventManager eventManager;
	
	public ConsoleDisplay(ConsoleUnitEventManager eventManager, int width, int height) {
		super();
		this.eventManager = eventManager;
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
		
		// Register Event Handlers
		registerHandlers();
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
	public void setScreen(ConsoleScreen screen) {
		currentScreen = screen;
		display.setWidget(screen);
	}
	
	/**
	 * Completely clear the display
	 */
	public void clearScreen() {
		setScreen(null);
	}
	
	public void registerHandlers() {
		this.addDomHandler(eventManager, MouseDownEvent.getType());
		this.addDomHandler(eventManager, TouchStartEvent.getType());
		this.addDomHandler(eventManager, MouseMoveEvent.getType());
		this.addDomHandler(eventManager, TouchMoveEvent.getType());
		this.addDomHandler(eventManager, MouseUpEvent.getType());
		this.addDomHandler(eventManager, TouchEndEvent.getType());
		this.addDomHandler(eventManager, MouseOutEvent.getType());
	}
}

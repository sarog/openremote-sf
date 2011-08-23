package org.openremote.web.console.client.unit;

import java.awt.List;
import java.util.ArrayList;

import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.event.press.PressCancelEvent;
import org.openremote.web.console.event.press.PressMoveEvent;
import org.openremote.web.console.screen.ConsoleScreen;
import org.openremote.web.console.widget.ConsoleComponent;

import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * This is the container where content will actually be displayed
 * An absolute panel is used as a wrapper to ease with repositioning
 * the display within the console unit during orientation change
 * @author rich
 *
 */
public class ConsoleDisplay extends ConsoleComponent implements TouchMoveHandler, MouseMoveHandler, MouseOutHandler {
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
	private AbsolutePanel container = new AbsolutePanel();
	private ArrayList<HandlerRegistration> moveHandlers = new ArrayList<HandlerRegistration>();
	
	public ConsoleDisplay(ConsoleUnitEventManager eventManager, int width, int height) {
		this.eventManager = eventManager;
		this.width = width;
		this.height = height;
		container.setWidth(this.width + "px");
		container.setHeight(this.height + "px");
		container.getElement().setId("consoleDisplayWrapper");
		container.setStylePrimaryName("consoleDisplay");
		
		// Create display panel where screen is actually loaded
		display = new SimplePanel();
		display.setWidth(width + "px");
		display.setHeight(height + "px");
		display.getElement().setId("consoleDisplay");
		
		// Add display to the wrapper
		container.add(display, 0, 0);

		// Set default display orientation to portrait
		setOrientation("portrait");
		
		// Set default colour
		setColour(DEFAULT_DISPLAY_COLOUR);
		
		// Add press handlers on entire widget
		registerPressHandlers();
		
		// Add move handlers
		this.addDomHandler(this, MouseMoveEvent.getType());
		this.addDomHandler(this, TouchMoveEvent.getType());
		this.addDomHandler(this, MouseOutEvent.getType());
		
		// Init widget
		this.initWidget(container);
	}
	
	/**
	 * Set the display orientation which changes the CSS class causing a
	 * rotate transform to be applied, have to also adjust the display
	 * size and position within the wrapper
	 * @param orientation
	 */
	public void setOrientation(String orientation) {
		if ("portrait".equals(orientation)) {
			container.setWidgetPosition(display,0,0);
		   display.setStylePrimaryName("portraitDisplay");
		   display.setWidth(width + "px");
		   display.setHeight(height + "px");		   
			currentOrientation = orientation;
		}
		if ("landscape".equals(orientation)) {
			container.setWidgetPosition(display, (width/2)-(height/2), (height/2)-(width/2));
			display.setStylePrimaryName("landscapeDisplay");
		   display.setWidth(height + "px");
		   display.setHeight(width + "px");
			currentOrientation = orientation;
		}
	}
	
	public void setColour(String colour) {
		container.getElement().getStyle().setBackgroundColor(colour);
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
	
	@Override
	public void onTouchMove(TouchMoveEvent event) {
		event.preventDefault();
		event.stopPropagation();
		lastMoveEvent = new PressMoveEvent(event);
		this.fireEvent(lastMoveEvent);
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		event.stopPropagation();
		lastMoveEvent = new PressMoveEvent(event);
		this.fireEvent(lastMoveEvent);
	}
	
	@Override
	public void onMouseOut(MouseOutEvent event) {
		event.stopPropagation();
		this.fireEvent(new PressCancelEvent(event));
		reset();
	}
	
//	public void addTempHandlers() {
//		moveHandlers.add(this.addDomHandler(this, MouseMoveEvent.getType()));
//		moveHandlers.add(this.addDomHandler(this, TouchMoveEvent.getType()));
//		moveHandlers.add(this.addDomHandler(this, MouseOutEvent.getType()));
//	}
//	
//	public void removeTempHandlers() {
//		for (HandlerRegistration handler : moveHandlers) {
//			handler.removeHandler();
//		}
//	}
}

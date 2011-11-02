package org.openremote.web.console.unit;

import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.event.press.PressCancelEvent;
import org.openremote.web.console.event.press.PressMoveEvent;
import org.openremote.web.console.util.BrowserUtils;
import org.openremote.web.console.widget.ConsoleComponentImpl;
import org.openremote.web.console.widget.InteractiveConsoleComponent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;

/**
 * This is the container where content will actually be displayed
 * An absolute panel is used as a wrapper to ease with repositioning
 * the display within the console unit during orientation change
 * @author rich
 *
 */
public class ConsoleDisplay extends InteractiveConsoleComponent implements TouchMoveHandler, MouseMoveHandler, MouseOutHandler {
	public static final int DEFAULT_DISPLAY_WIDTH = 320;
	public static final int DEFAULT_DISPLAY_HEIGHT = 460;
	private static final String DEFAULT_DISPLAY_COLOUR = "black";
	private AbsolutePanel display;
	private int width;
	private int height;
	private String colour;
	private AbsolutePanel container;
	private String currentOrientation = "portrait";
	
	public ConsoleDisplay(int width, int height) {
		super(new AbsolutePanel());
		container = (AbsolutePanel)this.getWidget();
		
		this.width = width;
		this.height = height;
		container.setWidth(this.width + "px");
		container.setHeight(this.height + "px");
		container.getElement().setId("consoleDisplayWrapper");
		container.setStylePrimaryName("consoleDisplay");
		
		// Create display panel where screen is actually loaded
		display = new AbsolutePanel();
		display.setWidth(width + "px");
		display.setHeight(height + "px");
		display.setStylePrimaryName("portraitDisplay");
		display.getElement().setId("consoleDisplay");
		
		// Add display to the wrapper
		container.add(display, 0, 0);
		
		// Set default colour
		setColour(DEFAULT_DISPLAY_COLOUR);
		
		// Add mouse and touch handlers on entire widget
		registerMouseAndTouchHandlers();
		
		// Add move handlers which are only used on this display component
		if(BrowserUtils.isMobile()) {
			this.addDomHandler(this, TouchMoveEvent.getType());
		} else {
			this.addDomHandler(this, MouseMoveEvent.getType());
			this.addDomHandler(this, MouseOutEvent.getType());
		}
		
		setVisible(true);
	}
	
	/**
	 * Set the display orientation which changes the CSS class causing a
	 * rotate transform to be applied, have to also adjust the display
	 * size and position within the wrapper
	 * @param orientation
	 */
	public void setOrientation(String orientation) {
		if (currentOrientation.equalsIgnoreCase(orientation)) {
			return;
		}
		
		this.currentOrientation = orientation;
		
		int width = getWidth();
		int height = getHeight();
		
		if ("portrait".equals(orientation)) {
			container.setWidgetPosition(display,0,0);
		   display.setStylePrimaryName("portraitDisplay");
		} else {
			container.setWidgetPosition(display, (height/2)-(width/2), (width/2)-(height/2));
			display.setStylePrimaryName("landscapeDisplay");
		}
		
	   display.setWidth(width + "px");
	   display.setHeight(height + "px");
	}
	
	public String getOrientation() {
		return this.currentOrientation;
	}
	
	public void setColour(String colour) {
		container.getElement().getStyle().setBackgroundColor(colour);
		this.colour = colour;
	}
	
	public String getColour() {
		return colour;
	}
	
	public boolean getIsVertical() {
		boolean response = false;
		if (currentOrientation.equalsIgnoreCase("portrait")) {
			response = true;
		}
		return response;
	}
	
	public int getWidth() {
		int value = 0; 
		if ("portrait".equals(currentOrientation)) {
			value = this.width;
		} else {
			value = this.height;
		}
		return value;
	}
	
	public int getHeight() {
		int value = 0; 
		if ("portrait".equals(currentOrientation)) {
			value = this.height;
		} else {
			value = this.width;
		}
		return value;
	}
	
	/**
	 * Completely clear the display
	 */
	public void clearDisplay() {
		display.clear();
	}
	
	protected void addComponent(ConsoleComponentImpl component) {
		addComponent(component, 0, 0);
	}
	
	protected void addComponent(ConsoleComponentImpl component, int left, int top) {
		display.add(component, left, top);
		component.onAdd(component.getOffsetWidth(), component.getOffsetHeight());
	}
	
	protected void removeComponent(ConsoleComponentImpl component) {
		component.onRemove();
		display.remove(component);
	}
	
	protected void setComponentPosition(ConsoleComponentImpl component, int left, int top) {
		display.setWidgetPosition(component, left, top);
	}
	
	protected void doResize(int width, int height) {
		this.width = width;
		this.height = height;
		container.setWidth(this.width + "px");
		container.setHeight(this.height + "px");
		display.setWidth(width + "px");
		display.setHeight(height + "px");
	}
	
	@Override
	public void onTouchMove(TouchMoveEvent event) {
		event.preventDefault();
		event.stopPropagation();
		lastMoveEvent = new PressMoveEvent(event);
		ConsoleUnitEventManager.getInstance().getEventBus().fireEvent(lastMoveEvent);
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		event.preventDefault();
		event.stopPropagation();
		lastMoveEvent = new PressMoveEvent(event);
		ConsoleUnitEventManager.getInstance().getEventBus().fireEvent(lastMoveEvent);
	}
	
	@Override
	public void onMouseOut(MouseOutEvent event) {
		event.stopPropagation();
		ConsoleUnitEventManager.getInstance().getEventBus().fireEvent(new PressCancelEvent(event));
		reset();
	}

	@Override
	public void onRender(int width, int height) {}
}

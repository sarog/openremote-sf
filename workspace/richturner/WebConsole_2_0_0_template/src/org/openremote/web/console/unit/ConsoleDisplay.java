package org.openremote.web.console.unit;

import java.util.ArrayList;
import java.util.List;

import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.event.press.PressCancelEvent;
import org.openremote.web.console.event.press.PressMoveEvent;
import org.openremote.web.console.event.rotate.RotationEvent;
import org.openremote.web.console.util.BrowserUtils;
import org.openremote.web.console.view.ScreenView;
import org.openremote.web.console.widget.ConsoleComponent;
import org.openremote.web.console.widget.InteractiveConsoleComponent;
import org.openremote.web.console.widget.Positional;
import org.openremote.web.console.widget.TabBarComponent;

import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

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
	public boolean isVertical = true;
	private Widget currentTabBar;
	private Widget currentScreen;
	
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
	public void setOrientation(RotationEvent event, boolean rotateDisplay) {
		String orientation = event.getOrientation();
		
		if ("portrait".equals(orientation)) {
			isVertical = true;
		} else {
			isVertical = false;
		}
		
		// Use same screen for portrait and landscape so don't rotate
		if (!rotateDisplay) {
			return;
		}
		
		if ("portrait".equals(orientation)) {
			container.setWidgetPosition(display,0,0);
		   display.setStylePrimaryName("portraitDisplay");
		   display.setWidth(width + "px");
		   display.setHeight(height + "px");
		} else {
			container.setWidgetPosition(display, (width/2)-(height/2), (height/2)-(width/2));
			display.setStylePrimaryName("landscapeDisplay");
		   display.setWidth(height + "px");
		   display.setHeight(width + "px");
		}
	}
	
	public void setColour(String colour) {
		container.getElement().getStyle().setBackgroundColor(colour);
		this.colour = colour;
	}
	
	public String getColour() {
		return colour;
	}
	
	protected void setScreenView(ScreenView screenView) {
		if (screenView == null) {
			return;
		}
		
		if (currentScreen != null && screenView != currentScreen) {
			display.remove(currentScreen);
		}
		
		display.add((Widget)screenView, 0, 0);
		currentScreen = (Widget)screenView;
		screenView.onAdd();
	}
	
	/**
	 * Completely clear the display
	 */
	private void clearDisplay() {
		display.clear();
	}
	
	protected void setTabBar(TabBarComponent tabBar) {
		if (tabBar == null) {
			return;
		}
		
		if (currentTabBar != null && tabBar != currentTabBar) {
			display.remove(currentTabBar);
		}
		display.add(tabBar, 0, height- tabBar.getHeight());
		DOM.setIntStyleAttribute( tabBar.getElement(), "zIndex", 30000 );
		currentTabBar = tabBar;
		tabBar.onAdd();
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
	public void onRender() {}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
}

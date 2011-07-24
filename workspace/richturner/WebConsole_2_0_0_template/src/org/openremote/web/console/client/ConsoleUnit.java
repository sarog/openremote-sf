package org.openremote.web.console.client;

import org.openremote.web.console.components.ConsoleDisplay;
import org.openremote.web.console.events.HoldEvent;
import org.openremote.web.console.events.HoldHandler;
import org.openremote.web.console.events.RotationEvent;
import org.openremote.web.console.events.RotationHandler;
import org.openremote.web.console.events.SwipeEvent;
import org.openremote.web.console.events.SwipeHandler;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ConsoleUnit extends SimplePanel implements RotationHandler, SwipeHandler, HoldHandler {
	private WebConsole consoleModule;
	public static final String CONSOLE_HTML_ELEMENT_ID = "consoleUnit";
	public static final String LOGO_TEXT_LEFT = "Open";
	public static final String LOGO_TEXT_RIGHT = "Remote";
	private VerticalPanel componentContainer;
	protected ConsoleDisplay consoleDisplay;
	protected int width;
	protected int height;
	
	
	public ConsoleUnit(WebConsole consoleModule) {
		this(consoleModule, ConsoleDisplay.DEFAULT_DISPLAY_WIDTH, ConsoleDisplay.DEFAULT_DISPLAY_HEIGHT);
	}
	
	public ConsoleUnit(WebConsole consoleModule, int width, int height) {
		super();
		this.consoleModule = consoleModule;
		
		// Create console container to store display and possibly logo for resizable units
		componentContainer = new VerticalPanel();
		componentContainer.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		super.add(componentContainer);
		
		// Create a console wrapper to allow easy positioning of the console unit
		AbsolutePanel consoleWrapper = new AbsolutePanel();
		consoleWrapper.setWidth("100%");
		consoleWrapper.setHeight("100%");
		consoleWrapper.add(this);
		RootPanel.get(WebConsole.CONSOLE_UNIT_CONTAINER_ID).add(consoleWrapper);
		
		// Set console unit properties
		setSize(width, height);
		this.getElement().setId(CONSOLE_HTML_ELEMENT_ID);
		this.addStyleName("consoleUnit");
		
		// Create a display and add to console container
		consoleDisplay = new ConsoleDisplay(width, height);
		add(consoleDisplay);
		
		// Set Console Orientation
		setOrientation();
		setPosition();
		
		// Register handlers
		this.addHandler(this, RotationEvent.getType());
		this.addHandler(this, SwipeEvent.getType());
		this.addHandler(this, HoldEvent.getType());
	}
	
	@Override
	public void onRotate(RotationEvent event) {
		setOrientation();
		setPosition();
	}
	
	@Override
	public void onHold(HoldEvent event) {
		Window.alert("HOLD: " + event.getXPos() + " : " + event.getYPos());		
	}

	@Override
	public void onSwipe(SwipeEvent event) {
		Window.alert("SWIPE: " + event.getDirection());
	}
	
	@Override
	public void add(Widget widget) {
		componentContainer.add(widget);
	}
	
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		//this.setWidth(width + "px");
		//this.setHeight(height + "px");
	}
	
	public ConsoleDisplay getConsoleDisplay() {
		return this.consoleDisplay;
	}
	
//	public void setConsoleDisplay(ConsoleDisplay consoleDisplay) {
//		this.remove(this.consoleDisplay);
//		this.consoleDisplay = null;
//		this.consoleDisplay = consoleDisplay;
//		componentContainer.insert(this.consoleDisplay, 0);		
//	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	/**
	 * Position the console unit in the centre of the window
	 */
	public void setPosition() {
		int winWidth = consoleModule.getWindowWidth();
		int winHeight = consoleModule.getWindowHeight();
		AbsolutePanel consoleContainer = (AbsolutePanel)this.getParent();
		consoleContainer.setWidgetPosition(this, (winWidth/2)-(width/2), (winHeight/2)-(height/2));
	}

	/**
	 * Adjusts the CSS class to either landscape or portrait
	 * @param orientation
	 */
	public void setOrientation() {
		String orientation = consoleModule.getWindowOrientation();
		if ("portrait".equals(orientation)) {
			getElement().removeClassName("landscapeConsole");
			getElement().addClassName("portraitConsole");
		} else {
			getElement().removeClassName("portraitConsole");
			getElement().addClassName("landscapeConsole");
		}
		// Set CSS to rotate the console display
		consoleDisplay.setOrientation(orientation);
	}
	
	public void hide() {
		 setVisible(false);
	}
	
	public void show() {
		 setVisible(true);
	}
	
//	/*
//	 * Redraws the console unit when a change occurs to
//	 * the window that allows/requires a different console
//	 * type to be used
//	 */
//	public ConsoleUnit redraw(int windowWidth, int windowHeight) {
//		ConsoleUnit newConsole;
//		// Reset the body background colour
//		RootPanel.get().removeStyleName("consoleDisplay");
//		
//		// Remove boss from console display
//		consoleDisplay.removeStyleName("consoleDisplayBoss");
//		
//		// Create the new console and add display from old console
//		newConsole = ConsoleUnit.create(windowWidth, windowHeight, this.displayWidth, this.displayHeight, this.consoleDisplay);
//		newConsole.setConsoleDisplay(consoleDisplay);
//		return newConsole;
//	}
}

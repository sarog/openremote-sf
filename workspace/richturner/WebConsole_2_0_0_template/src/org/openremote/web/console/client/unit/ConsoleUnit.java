package org.openremote.web.console.client.unit;

import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.event.hold.HoldEvent;
import org.openremote.web.console.event.hold.HoldHandler;
import org.openremote.web.console.event.press.PressCancelEvent;
import org.openremote.web.console.event.press.PressEndEvent;
import org.openremote.web.console.event.press.PressMoveEvent;
import org.openremote.web.console.event.press.PressStartEvent;
import org.openremote.web.console.event.rotate.RotationEvent;
import org.openremote.web.console.event.rotate.RotationHandler;
import org.openremote.web.console.event.swipe.SwipeEvent;
import org.openremote.web.console.event.swipe.SwipeHandler;
import org.openremote.web.console.screen.ConsoleScreen;
import org.openremote.web.console.screen.TestScreen;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ConsoleUnit extends SimplePanel implements RotationHandler, SwipeHandler, HoldHandler {
	private ConsoleUnitEventManager eventManager;
	public static final String CONSOLE_HTML_ELEMENT_ID = "consoleUnit";
	public static final String LOGO_TEXT_LEFT = "Open";
	public static final String LOGO_TEXT_RIGHT = "Remote";
	private VerticalPanel componentContainer;
	protected ConsoleDisplay consoleDisplay;
	protected int width;
	protected int height;
	private ConsoleScreen loadingScreen;
	
	public ConsoleUnit() {
		this(ConsoleDisplay.DEFAULT_DISPLAY_WIDTH, ConsoleDisplay.DEFAULT_DISPLAY_HEIGHT);
	}
	
	public ConsoleUnit(int width, int height) {
		eventManager = new ConsoleUnitEventManager(this);
		
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
		consoleDisplay = new ConsoleDisplay(eventManager, width, height);
		add(consoleDisplay);
		
		consoleDisplay.addHandler(eventManager.getPressMoveReleaseHandler(), PressStartEvent.getType());
		consoleDisplay.addHandler(eventManager.getPressMoveReleaseHandler(), PressMoveEvent.getType());
		consoleDisplay.addHandler(eventManager.getPressMoveReleaseHandler(), PressEndEvent.getType());
		consoleDisplay.addHandler(eventManager.getPressMoveReleaseHandler(), PressCancelEvent.getType());
		
		// Register handlers
		registerGestureHandlers();
	
		// Create loading screen
		//loadingScreen = new LoadingScreen(eventManager);
		loadingScreen = new TestScreen(eventManager);
		
		// Show loading screen
		setScreen(loadingScreen);
	}
	
	@Override
	public void onRotate(RotationEvent event) {
		setOrientation(event.getOrientation());
		setPosition(event.getWindowWidth(), event.getWindowHeight());
	}
	
	@Override
	public void onHold(HoldEvent event) {
		Window.alert("HOLD");		
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
	}
	
	public ConsoleDisplay getConsoleDisplay() {
		return this.consoleDisplay;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	/**
	 * Position the console unit in the centre of the window
	 */
	public void setPosition(int winWidth, int winHeight) {
		AbsolutePanel consoleContainer = (AbsolutePanel)this.getParent();
		consoleContainer.setWidgetPosition(this, (winWidth/2)-(width/2), (winHeight/2)-(height/2));
	}

	/**
	 * Adjusts the CSS class to either landscape or portrait
	 * @param orientation
	 */
	public void setOrientation(String orientation) {
		//String orientation = consoleModule.getWindowOrientation();
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
	
	// Display specified screen
	public void setScreen(ConsoleScreen screen) {
		consoleDisplay.setScreen(loadingScreen);	
	}
	
	public void registerGestureHandlers() {
		this.addHandler(this, RotationEvent.getType());
		this.addHandler(this, SwipeEvent.getType());
		this.addHandler(this, HoldEvent.getType());
	}
	
	public HandlerManager getEventBus() {
		return eventManager.getEventBus();
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

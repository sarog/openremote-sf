package org.openremote.web.console.client.unit;

import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.event.hold.HoldEvent;
import org.openremote.web.console.event.hold.HoldHandler;
import org.openremote.web.console.event.rotate.RotationEvent;
import org.openremote.web.console.event.rotate.RotationHandler;
import org.openremote.web.console.event.swipe.SwipeEvent;
import org.openremote.web.console.event.swipe.SwipeHandler;
import org.openremote.web.console.event.tap.DoubleTapEvent;
import org.openremote.web.console.event.tap.DoubleTapHandler;
import org.openremote.web.console.event.tap.TapEvent;
import org.openremote.web.console.event.tap.TapHandler;
import org.openremote.web.console.screen.view.ScreenView;
import org.openremote.web.console.screen.view.LoadingScreenView;
import org.openremote.web.console.screen.view.TestScreenView;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ConsoleUnit extends SimplePanel implements RotationHandler, SwipeHandler, HoldHandler, TapHandler, DoubleTapHandler {
	public static final String CONSOLE_HTML_ELEMENT_ID = "consoleUnit";
	public static final String LOGO_TEXT_LEFT = "Open";
	public static final String LOGO_TEXT_RIGHT = "Remote";
	private VerticalPanel componentContainer;
	protected ConsoleDisplay consoleDisplay;
	protected int width;
	protected int height;
	private ScreenView loadingScreen;
	private String orientation = "portrait";
	
	public ConsoleUnit() {
		this(ConsoleDisplay.DEFAULT_DISPLAY_WIDTH, ConsoleDisplay.DEFAULT_DISPLAY_HEIGHT);
	}
	
	public ConsoleUnit(int width, int height) {
		// Create console container to store display and possibly logo for resizable units
		componentContainer = new VerticalPanel();
		componentContainer.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		super.add(componentContainer);
		
		// Set console unit properties
		setSize(width, height);
		this.getElement().setId(CONSOLE_HTML_ELEMENT_ID);
		this.addStyleName("consoleUnit");
		
		// Create a display and add to console container
		consoleDisplay = new ConsoleDisplay(width, height);
		add(consoleDisplay);
		
		// Register handlers
		registerGestureHandlers();
	
		// Create and show loading screen
		loadingScreen = new LoadingScreenView();
		loadingScreen = new TestScreenView();
		setScreen(loadingScreen);
	}
	
	@Override
	public void onRotate(RotationEvent event) {
		boolean rotateDisplay = false;
		
		setOrientation(event);
		setPosition(event.getWindowWidth(), event.getWindowHeight());
		
		// Load in the inverse screen to what is currently loaded
		
		
		// Adjust console display
		consoleDisplay.setOrientation(event, rotateDisplay);
	}
	
	@Override
	public void onHold(HoldEvent event) {
		//TODO Handle Hold Event	
	}

	@Override
	public void onSwipe(SwipeEvent event) {
		//TODO Handle swipe Event
	}

	@Override
	public void onTap(TapEvent event) {
		//TODO Handle Tap Event
	}
	
	@Override
	public void onDoubleTap(DoubleTapEvent event) {
		//TODO Handle Double Tap
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
	public void setOrientation(RotationEvent event) {
		String orientation = event.getOrientation();

		if ("portrait".equals(orientation)) {
			getElement().removeClassName("landscapeConsole");
			getElement().addClassName("portraitConsole");
		} else {
			getElement().removeClassName("portraitConsole");
			getElement().addClassName("landscapeConsole");
		}

		this.orientation = orientation;
	}
	
	public String getOrientation() {
		return orientation;
	}
	
	public void hide() {
		 setVisible(false);
	}
	
	public void show() {
		 setVisible(true);
	}
	
	// Display specified screen
	public void setScreen(ScreenView screen) {
		consoleDisplay.setScreen(loadingScreen);	
	}
	
	public void registerGestureHandlers() {
		HandlerManager eventBus = ConsoleUnitEventManager.getInstance().getEventBus();
		eventBus.addHandler(RotationEvent.getType(), this);
		eventBus.addHandler(SwipeEvent.getType(), this);
		eventBus.addHandler(HoldEvent.getType(), this);
		eventBus.addHandler(TapEvent.getType(), this);
		eventBus.addHandler(DoubleTapEvent.getType(), this);
	}
}

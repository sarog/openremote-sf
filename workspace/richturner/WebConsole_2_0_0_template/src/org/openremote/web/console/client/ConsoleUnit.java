package org.openremote.web.console.client;

import org.openremote.web.console.components.ConsoleDisplay;
import org.openremote.web.console.types.*;
import org.openremote.web.console.utils.BrowserUtils;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ConsoleUnit extends SimplePanel {
	public static final int DEFAULT_DISPLAY_WIDTH = 320;
	public static final int DEFAULT_DISPLAY_HEIGHT = 460;
	public static final String CONSOLE_HTML_ELEMENT_ID = "consoleUnit";
	public static final String LOGO_TEXT_LEFT = "Open";
	public static final String LOGO_TEXT_RIGHT = "Remote";
	private VerticalPanel consoleContainer;
	public ConsoleDisplay consoleDisplay;
	protected int displayWidth;
	protected int displayHeight;
	protected int consoleWidth;
	protected int consoleHeight;
	protected String DEFAULT_DISPLAY_COLOUR = "black";
	protected String displayColour = DEFAULT_DISPLAY_COLOUR;
	
	public ConsoleUnit(int width, int height) {
		this(width, height, null);
	}
	
	public ConsoleUnit(int width, int height, ConsoleDisplay consoleDisplay) {
		super();
		consoleContainer = new VerticalPanel();
		consoleContainer.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		super.add(consoleContainer);
		displayWidth = width;
		displayHeight = height;
		setDimensions(width, height);
		this.getElement().setId(CONSOLE_HTML_ELEMENT_ID);
		this.getElement().addClassName("consoleUnit");
		if (consoleDisplay == null) {
			consoleDisplay = new ConsoleDisplay(displayWidth, displayHeight);
		}
		
		// Set console display color
		consoleDisplay.getElement().getStyle().setBackgroundColor(displayColour);
		this.consoleDisplay = consoleDisplay;
		this.add(this.consoleDisplay);
	}
	
	@Override
	public void add(Widget widget) {
		consoleContainer.add(widget);
	}
	
	public void setDimensions(int width, int height) {
		consoleWidth = width;
		consoleHeight = height;
		this.setWidth(width + "px");
		this.setHeight(height + "px");
	}
	
	/**
	 * Create new console unit with type based on window size and requested display size
	 * @return
	 */
	public static ConsoleUnit create(int windowWidth, int windowHeight) {
		return create(windowWidth, windowHeight, DEFAULT_DISPLAY_WIDTH, DEFAULT_DISPLAY_HEIGHT, null);
	}
	
	public static ConsoleUnit create(int windowWidth, int windowHeight, int requiredDisplayWidth, int requiredDisplayHeight, ConsoleDisplay consoleDisplay) {
		ConsoleUnit console;

		// Look at window size to determine console unit type for mobiles always use fullscreen
		if(BrowserUtils.isMobile || windowWidth < ResizableUnit.requiredConsoleWidth(requiredDisplayWidth) || windowHeight < ResizableUnit.requiredConsoleHeight(requiredDisplayHeight)) {
			console = new FullScreenUnit(requiredDisplayWidth, requiredDisplayHeight, consoleDisplay);
		} else {
			console = new ResizableUnit(requiredDisplayWidth, requiredDisplayHeight, consoleDisplay);
		}
		return console;
	}
	
	public ConsoleDisplay getConsoleDisplay() {
		return this.consoleDisplay;
	}
	
	public void setConsoleDisplay(ConsoleDisplay consoleDisplay) {
		this.remove(this.consoleDisplay);
		this.consoleDisplay = null;
		this.consoleDisplay = consoleDisplay;
		consoleContainer.insert(this.consoleDisplay, 0);		
	}
	
	public int getWidth() {
		return consoleWidth;
	}
	
	public int getHeight() {
		return consoleHeight;
	}

	/*
	 * Redraws the console unit when a change occurs to
	 * the window that allows/requires a different console
	 * type to be used
	 */
	public ConsoleUnit redraw(int windowWidth, int windowHeight) {
		ConsoleUnit newConsole;
		// Reset the body background colour
		RootPanel.get().removeStyleName("consoleDisplay");
		
		// Remove boss from console display
		consoleDisplay.removeStyleName("consoleDisplayBoss");
		
		// Create the new console and add display from old console
		newConsole = ConsoleUnit.create(windowWidth, windowHeight, this.displayWidth, this.displayHeight, this.consoleDisplay);
		newConsole.setConsoleDisplay(consoleDisplay);
		return newConsole;
	}
	
	public void setOrientationAndPosition(String orientation) {
		// Set CSS to rotate the console unit and reposition it
		AbsolutePanel consoleContainer = (AbsolutePanel)this.getParent();
		int winWidth = Window.getClientWidth();
		int winHeight = Window.getClientHeight();
		
		if ("portrait".equals(orientation)) {
			getElement().removeClassName("landscapeConsole");
			getElement().addClassName("portraitConsole");
			consoleContainer.setWidgetPosition(this, (winWidth/2)-(consoleWidth/2), (winHeight/2)-(consoleHeight/2));
		}
		if ("landscape".equals(orientation)) {
			getElement().removeClassName("portraitConsole");
			getElement().addClassName("landscapeConsole");
			consoleContainer.setWidgetPosition(this, (winWidth/2)-(consoleHeight/2), (winHeight/2)-(consoleWidth/2));
		}
		
		// Set CSS to rotate the console display
		consoleDisplay.setOrientation(orientation);
	}
}

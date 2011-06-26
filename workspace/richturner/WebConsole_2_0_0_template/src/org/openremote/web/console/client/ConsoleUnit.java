package org.openremote.web.console.client;

import org.openremote.web.console.components.ConsoleDisplay;
import org.openremote.web.console.types.*;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ConsoleUnit extends VerticalPanel {
	public static final int DEFAULT_DISPLAY_WIDTH = 320;
	public static final int DEFAULT_DISPLAY_HEIGHT = 460;
	public static final String CONSOLE_HTML_ELEMENT_ID = "consoleUnit";
	public static final String LOGO_TEXT_LEFT = "Open";
	public static final String LOGO_TEXT_RIGHT = "Remote";
	
	public ConsoleDisplay consoleDisplay;
	public int width;
	public int height;
	
	public ConsoleUnit(int width, int height) {
		super();
		setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.width = width;
		this.height = height;
		DOM.setElementAttribute(this.getElement(), "id", CONSOLE_HTML_ELEMENT_ID);
		this.getElement().addClassName("consoleUnit");
		createDisplay();
		this.add(consoleDisplay);
	}
	
	/**
	 * Create new console unit with type based on window size and display size requested
	 * @return
	 */
	public static ConsoleUnit create(int windowWidth, int windowHeight) {
		return create(windowWidth, windowHeight, DEFAULT_DISPLAY_WIDTH, DEFAULT_DISPLAY_HEIGHT);
	}
	public static ConsoleUnit create(int width, int height, int requiredDisplayWidth, int requiredDisplayHeight) {
		ConsoleUnit console;

		// Look at window size to determine console unit type
		if (width < requiredDisplayWidth || height < requiredDisplayHeight) {
			console = new MiniatureUnit(requiredDisplayWidth, requiredDisplayHeight);
		} else if(width < ResizableUnit.requiredConsoleWidth(requiredDisplayWidth) || height < ResizableUnit.requiredConsoleHeight(requiredDisplayHeight)) {
			console = new FullScreenUnit(requiredDisplayWidth, requiredDisplayHeight);
		} else {
			console = new ResizableUnit(requiredDisplayWidth, requiredDisplayHeight);
		}
		return console;
	}
	
	public ConsoleDisplay getConsoleDisplay() {
		return this.consoleDisplay;
	}
	
	// Create the console unit's display this is where
	// the screen content will be displayed and represents
	// a physical LCD display
	public void createDisplay() {
		consoleDisplay = new ConsoleDisplay(width, height);
	}
	
	public int getWidth() {
		Element consoleElem = DOM.getElementById(CONSOLE_HTML_ELEMENT_ID);
		return consoleElem.getClientWidth();
	}
	
	public int getHeight() {
		Element consoleElem = DOM.getElementById(CONSOLE_HTML_ELEMENT_ID);
		return consoleElem.getClientHeight();
	}
}

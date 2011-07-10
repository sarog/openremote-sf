package org.openremote.web.console.types;

import org.openremote.web.console.client.ConsoleUnit;
import org.openremote.web.console.components.ConsoleDisplay;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * 
 * @author Rich Turner
 *	Static Console Unit which cannot be resized and is the same
 * size as the physical window it is being displayed on
 */
public class ResizableUnit extends ConsoleUnit {
	public static final int FRAME_WIDTH_TOP = 20;
	public static final int FRAME_WIDTH_BOTTOM = 50;
	public static final int FRAME_WIDTH_LEFT = 20;
	public static final int FRAME_WIDTH_RIGHT = 20;
	
	public ResizableUnit() {
		this(ConsoleUnit.DEFAULT_DISPLAY_WIDTH, ConsoleUnit.DEFAULT_DISPLAY_HEIGHT, null);
	}
	
	public ResizableUnit(int displayWidth, int displayHeight, ConsoleDisplay consoleDisplay) {
		// Create basic Console Unit
		super(displayWidth, displayHeight, consoleDisplay);
		
		// Clear document body colour setting
		RootPanel.getBodyElement().getStyle().clearBackgroundColor();
		
		// Create console frame
		createFrame();
		
		// Update Console dimensions	
		setDimensions(consoleWidth + FRAME_WIDTH_LEFT + FRAME_WIDTH_RIGHT, consoleHeight + FRAME_WIDTH_TOP + FRAME_WIDTH_BOTTOM);	
	}
	
	public static int requiredConsoleWidth() {
		return requiredConsoleWidth(ConsoleUnit.DEFAULT_DISPLAY_WIDTH);
	}
	
	public static int requiredConsoleWidth(int displayWidth) {
		return displayWidth + FRAME_WIDTH_LEFT + FRAME_WIDTH_RIGHT;
	}
	
	public static int requiredConsoleHeight() {
		return requiredConsoleHeight(ConsoleUnit.DEFAULT_DISPLAY_HEIGHT);
	}
	
	public static int requiredConsoleHeight(int displayHeight) {
		return displayHeight + FRAME_WIDTH_TOP + FRAME_WIDTH_BOTTOM;
	}
	
	public void createFrame() {
		// Add the frame for this re-sizable console unit by setting style attributes
		// Only need to add padding to top as Vertical Panel deals with left right padding
		//this.getElement().setAttribute("style", "padding: " + FRAME_WIDTH_TOP + "px " + FRAME_WIDTH_RIGHT + "px 0px " + FRAME_WIDTH_LEFT + "px;");
		this.getElement().setAttribute("style", "padding: " + FRAME_WIDTH_TOP + "px 0px 0px 0px;");
		addStyleName("consoleFrame");
		addDisplayBoss();
		
		// Add the logo along the bottom of the frame
		HorizontalPanel logoPanel = new HorizontalPanel();
		logoPanel.setStylePrimaryName("consoleFrameLogo");
		logoPanel.setHeight(FRAME_WIDTH_BOTTOM + "px");
		logoPanel.getElement().setAttribute("style", "line-height: " + FRAME_WIDTH_BOTTOM + "px;");		
		Label logoLeft = new Label();
		logoLeft.setText(LOGO_TEXT_LEFT);
		logoLeft.getElement().setId("consoleFrameLogoLeft");
		logoPanel.add(logoLeft);
		Label logoRight = new Label();
		logoRight.setText(LOGO_TEXT_RIGHT);
		logoRight.getElement().setId("consoleFrameLogoRight");
		logoPanel.add(logoRight);
		add(logoPanel);
	}
	
	/**
	 * Adds a border to the display to give boss illusion
	 */
	public void addDisplayBoss() {
		this.consoleDisplay.getElement().getStyle().setBorderWidth(2,Unit.PX);
		this.consoleDisplay.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		this.consoleDisplay.getElement().getStyle().setBorderColor("#333");
	}
}

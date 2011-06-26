package org.openremote.web.console.types;

import org.openremote.web.console.client.ConsoleUnit;
import org.openremote.web.console.components.ConsoleDisplay;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;

/**
 * 
 * @author Rich Turner
 *	Static Console Unit which cannot be resized and is the same
 * size as the physical window it is being displayed on
 */
public class ResizableUnit extends ConsoleUnit {
	public static final int FRAME_WIDTH_TOP = 20;
	public static final int FRAME_WIDTH_BOTTOM = 60;
	public static final int FRAME_WIDTH_LEFT = 20;
	public static final int FRAME_WIDTH_RIGHT = 20;
	
	public ResizableUnit(int displayWidth, int displayHeight) {
		// Create basic Console Unit
		super(displayWidth, displayHeight);
		
		// Add the frame for this re-sizable console unit by setting style attributes
		DOM.setElementAttribute(this.getElement(), "style", "padding: " + FRAME_WIDTH_TOP + "px " + FRAME_WIDTH_RIGHT + "px 0px " + FRAME_WIDTH_LEFT + "px;");
		this.addStyleName("consoleFrame");
		ConsoleDisplay consoleDisplay = super.getConsoleDisplay();
		consoleDisplay.addStyleName("consoleDisplayBoss");
		
		// Add the logo along the bottom of the frame
		HorizontalPanel logoPanel = new HorizontalPanel();
		logoPanel.setStylePrimaryName("consoleFrameLogo");
		logoPanel.setHeight(FRAME_WIDTH_BOTTOM + "px");
		DOM.setElementAttribute(logoPanel.getElement(), "style", "line-height: " + FRAME_WIDTH_BOTTOM + "px;");		
		Label logoLeft = new Label();
		logoLeft.setText(LOGO_TEXT_LEFT);
		DOM.setElementAttribute(logoLeft.getElement(), "id", "consoleFrameLogoLeft");
		logoPanel.add(logoLeft);
		Label logoRight = new Label();
		logoRight.setText(LOGO_TEXT_RIGHT);
		DOM.setElementAttribute(logoRight.getElement(), "id", "consoleFrameLogoRight");
		logoPanel.add(logoRight);
		add(logoPanel);
	}
	
	public static int requiredConsoleWidth(int requiredDisplayWidth) {
		return requiredDisplayWidth + FRAME_WIDTH_LEFT + FRAME_WIDTH_RIGHT;
	}
	
	public static int requiredConsoleHeight(int requiredDisplayHeight) {
		return requiredDisplayHeight + FRAME_WIDTH_TOP + FRAME_WIDTH_BOTTOM;
	}
}

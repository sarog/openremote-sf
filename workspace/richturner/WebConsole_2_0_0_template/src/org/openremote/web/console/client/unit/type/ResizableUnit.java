package org.openremote.web.console.client.unit.type;

import org.openremote.web.console.client.unit.ConsoleDisplay;
import org.openremote.web.console.client.unit.ConsoleUnit;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
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
	public static final int BOSS_WIDTH = 2;
	
	public ResizableUnit() {
		super();
		initialiseUnit();
	}
	
	public ResizableUnit(int displayWidth, int displayHeight) {
		super(displayWidth, displayHeight);
		initialiseUnit();
	}
	
	private void initialiseUnit() {
		super.addStyleName("resizableConsole");
		
		// Clear document body colour setting
		RootPanel.getBodyElement().getStyle().clearBackgroundColor();
		
		// Create console frame
		createFrame();
		
		// Update console unit size
		setSize(width + FRAME_WIDTH_LEFT + FRAME_WIDTH_RIGHT, height + FRAME_WIDTH_TOP + FRAME_WIDTH_BOTTOM);
	}
	
	
	public static int requiredConsoleWidth() {
		return requiredConsoleWidth(ConsoleDisplay.DEFAULT_DISPLAY_WIDTH);
	}
	
	public static int requiredConsoleWidth(int displayWidth) {
		return displayWidth + FRAME_WIDTH_LEFT + FRAME_WIDTH_RIGHT;
	}
	
	public static int requiredConsoleHeight() {
		return requiredConsoleHeight(ConsoleDisplay.DEFAULT_DISPLAY_HEIGHT);
	}
	
	public static int requiredConsoleHeight(int displayHeight) {
		return displayHeight + FRAME_WIDTH_TOP + FRAME_WIDTH_BOTTOM;
	}
	
	/**
	 * Frame is added to console unit by applying a margin to the console display
	 * and setting the CSS class of the console unit
	 */
	public void createFrame() {
		this.consoleDisplay.getElement().getStyle().setMarginTop(FRAME_WIDTH_TOP-BOSS_WIDTH, Unit.PX);
		this.consoleDisplay.getElement().getStyle().setMarginRight(FRAME_WIDTH_RIGHT-BOSS_WIDTH, Unit.PX);
		this.consoleDisplay.getElement().getStyle().setMarginLeft(FRAME_WIDTH_LEFT-BOSS_WIDTH, Unit.PX);
		this.consoleDisplay.getElement().getStyle().setMarginBottom(-BOSS_WIDTH, Unit.PX);
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
		this.consoleDisplay.getElement().getStyle().setBorderWidth(BOSS_WIDTH,Unit.PX);
		this.consoleDisplay.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		this.consoleDisplay.getElement().getStyle().setBorderColor("#333");
	}
}

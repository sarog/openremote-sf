package org.openremote.web.console.types;

import org.openremote.web.console.client.ConsoleUnit;
import org.openremote.web.console.components.ConsoleDisplay;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * 
 * @author Rich Turner
 *	Static Console Unit which cannot be resized and is the same
 * size as the physical window it is being displayed on
 */
public class FullScreenUnit extends ConsoleUnit {
	public FullScreenUnit(int displayWidth, int displayHeight, ConsoleDisplay consoleDisplay) {
		super(displayWidth, displayHeight, consoleDisplay);
		
		// Set document body colour the same as the console display
		RootPanel.getBodyElement().getStyle().setBackgroundColor(displayColour);
	}
}

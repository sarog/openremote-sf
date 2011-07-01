package org.openremote.web.console.types;

import org.openremote.web.console.client.ConsoleUnit;
import org.openremote.web.console.components.ConsoleDisplay;

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
		
		// Make body background same colour as console display by setting style to consoleDisplay
		RootPanel.get().addStyleName("consoleDisplay");
	}
}

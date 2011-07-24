package org.openremote.web.console.types;

import org.openremote.web.console.client.ConsoleUnit;
import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.components.ConsoleDisplay;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * 
 * @author Rich Turner
 *	Static Console Unit which cannot be resized and is the same
 * size as the physical window it is being displayed on
 */
public class FullScreenUnit extends ConsoleUnit {
	
//	public FullScreenUnit(WebConsole consoleModule) {
//		super(consoleModule);
//		initialiseUnit();
//	}
	public FullScreenUnit(WebConsole consoleModule) {
		super(consoleModule, consoleModule.getWindowWidth(), consoleModule.getWindowHeight());
		initialiseUnit();
	}
	
	private void initialiseUnit() {
		super.addStyleName("fullscreenConsole");
		
		// Set document body colour the same as the console display
		RootPanel.getBodyElement().getStyle().setBackgroundColor(consoleDisplay.colour);
	}
}

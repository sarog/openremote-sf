package org.openremote.web.console.type;

import org.openremote.web.console.client.unit.ConsoleUnit;
import org.openremote.web.console.event.ConsoleUnitEventManager;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * 
 * @author Rich Turner
 *	Static Console Unit which cannot be resized and is the same
 * size as the physical window it is being displayed on need to
 * initialise the console using portrait window dimensions
 */
public class FullScreenUnit extends ConsoleUnit {

	public FullScreenUnit(ConsoleUnitEventManager eventManager, int winWidth, int winHeight) {
		super(eventManager, winWidth, winHeight);
		initialiseUnit();
	}

	private void initialiseUnit() {
		super.addStyleName("fullscreenConsole");
		
		// Set document body colour the same as the console display
		RootPanel.getBodyElement().getStyle().setBackgroundColor(consoleDisplay.colour);
	}
}

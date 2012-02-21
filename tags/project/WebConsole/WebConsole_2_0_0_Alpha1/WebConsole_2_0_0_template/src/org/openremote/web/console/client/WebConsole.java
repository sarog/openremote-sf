package org.openremote.web.console.client;

import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.event.rotate.RotationEvent;
import org.openremote.web.console.unit.ConsoleUnit;
import org.openremote.web.console.util.BrowserUtils;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * This class configures the Window for mobile and desktop environments
 * and determines the size of the window we have to work with:
 * windowHeight = Longest window dimension (i.e. portrait)
 * windowWidth = Shortest window dimension
 */
public class WebConsole implements EntryPoint {
	private static ConsoleUnit consoleUnit;	
	
	public void onModuleLoad() {
		BrowserUtils.initWindow();
		ConsoleUnit console;
		
		// Initialise the Console Unit - if mobile or Setting defined to load in fullscreen mode
		if (BrowserUtils.isMobile) {
			console = new ConsoleUnit(BrowserUtils.getWindowWidth(), BrowserUtils.getWindowHeight());
		} else {
			console = new ConsoleUnit();
		}
		consoleUnit = console;
		
		// Hide the loading message
		RootPanel.get("welcome-content").setVisible(false);
		
		BrowserUtils.getConsoleContainer().add(consoleUnit);
		getConsoleUnit().onAdd();
		
		if (!BrowserUtils.isMobile) {
			SlidingToolbar.initialise();
		}
	}
	
	public static ConsoleUnit getConsoleUnit() {
		return consoleUnit;
	}
}

package org.openremote.web.console.widget;

import org.openremote.web.console.event.ConsoleUnitEventManager;

public interface ConsoleWidget {
	// Calls configure method and makes widget visible
	public void initialise(ConsoleUnitEventManager eventManager);
	
	// Configures the widget based on the values supplied
	public void configure();
	
	// Sets the visibility of the widget
	public void setVisible(boolean visible);
}

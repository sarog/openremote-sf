package org.openremote.web.console.screens;

import org.openremote.web.console.events.ConsoleUnitEventManager;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.LayoutPanel;

/**
 * Defines a set of widgets that form a specific
 * screen for display on the console display
 * @author rich
 *
 */
public class ConsoleScreen extends AbsolutePanel {
	private ConsoleUnitEventManager eventManager;
	
	public ConsoleScreen(ConsoleUnitEventManager eventManager) {
		super();
		this.eventManager = eventManager;
		this.setWidth("100%");
		this.setHeight("100%");
	}
}

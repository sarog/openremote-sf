package org.openremote.web.console.view;

import java.util.ArrayList;
import java.util.List;
import org.openremote.web.console.widget.ConsoleComponent;

/**
 * Defines a set of widgets that form a specific
 * screen for display on the console display
 * @author rich
 *
 */
public class ScreenViewImpl implements ScreenView {
	List<ConsoleComponent> consoleWidgets = new ArrayList<ConsoleComponent>();
	
	public ScreenViewImpl() {
	}
	
	public void addConsoleWidget(ConsoleComponent widget) {
		consoleWidgets.add(widget);
	}

	@Override
	public List<ConsoleComponent> getConsoleWidgets() {
		return consoleWidgets;
	}
}

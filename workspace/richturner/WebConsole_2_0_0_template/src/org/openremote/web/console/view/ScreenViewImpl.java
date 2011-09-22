package org.openremote.web.console.view;

import java.util.ArrayList;
import java.util.List;
import org.openremote.web.console.widget.ConsoleWidget;

/**
 * Defines a set of widgets that form a specific
 * screen for display on the console display
 * @author rich
 *
 */
public class ScreenViewImpl implements ScreenView {
	List<ConsoleWidget> consoleWidgets = new ArrayList<ConsoleWidget>();
	
	public ScreenViewImpl() {
	}
	
	public void addConsoleWidget(ConsoleWidget widget) {
		consoleWidgets.add(widget);
	}

	@Override
	public List<ConsoleWidget> getConsoleWidgets() {
		return consoleWidgets;
	}
}

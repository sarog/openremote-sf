package org.openremote.web.console.view;

import java.util.ArrayList;
import java.util.List;
import org.openremote.web.console.widget.ConsoleComponent;
import org.openremote.web.console.widget.ConsoleComponentImpl;
import org.openremote.web.console.widget.Positional;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Defines a set of widgets that form a specific
 * screen for display on the console display
 * @author rich
 *
 */
public class ScreenViewImpl extends ConsoleComponentImpl implements ScreenView {
	List<ConsoleComponent> consoleWidgets = new ArrayList<ConsoleComponent>();
	AbsolutePanel container = new AbsolutePanel();
	
	public ScreenViewImpl() {
		super(new AbsolutePanel());
		container = (AbsolutePanel)this.getWidget();
		setWidth("100%");
		setHeight("100%");
	}
	
	public void addConsoleWidget(ConsoleComponent widget) {
		consoleWidgets.add(widget);
		addToPanel(widget);
	}

	@Override
	public List<ConsoleComponent> getConsoleWidgets() {
		return consoleWidgets;
	}
	
	/**
	 * Add specified component to the Screen View Panel
	 */
	private void addToPanel(ConsoleComponent widget) {
		int left = 0;
		int top = 0;
	
		if (widget instanceof Positional) {
			left = ((Positional) widget).getLeft();
			top = ((Positional) widget).getTop();
		}
		container.add((Widget) widget, left, top);
	}
	
	/**
	 * Adjust specified widgets position
	 * @param widget
	 * @param left
	 * @param top
	 */
	public void setConsoleWidgetPosition(ConsoleComponent widget, int left, int top) {
		if (container.getWidgetIndex((Widget)widget) >= 0) {
			container.setWidgetPosition((Widget)widget, left, top);
		}		
	}

	@Override
	public void onRender() {
		for (ConsoleComponent component : consoleWidgets) {
			component.onAdd();
		}
	}
}

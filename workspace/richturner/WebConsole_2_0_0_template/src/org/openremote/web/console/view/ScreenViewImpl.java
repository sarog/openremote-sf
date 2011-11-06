package org.openremote.web.console.view;

import java.util.ArrayList;
import java.util.List;
import org.openremote.web.console.widget.ConsoleComponent;
import org.openremote.web.console.widget.ConsoleComponentImpl;
import org.openremote.web.console.widget.Positional;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Defines a set of widgets that form a specific
 * screen for display on the console display
 * @author rich
 *
 */
public class ScreenViewImpl extends ConsoleComponentImpl implements ScreenView {
	List<ConsoleComponent> consoleWidgets = new ArrayList<ConsoleComponent>();
	public static final String CLASS_NAME = "screenView";
	public ScreenViewImpl() {
		super(new AbsolutePanel(), CLASS_NAME);
		setWidth("100%");
		setHeight("100%");
	}
	
	public void addConsoleWidget(ConsoleComponent widget) {
		addToScreen(widget);
	}

	@Override
	public List<ConsoleComponent> getConsoleWidgets() {
		return consoleWidgets;
	}
	
	/**
	 * Add specified component to the Screen View Panel
	 */
	private void addToScreen(ConsoleComponent widget) {
		int left = 0;
		int top = 0;
		consoleWidgets.add(widget);
		
		if (widget instanceof Positional) {
			left = ((Positional) widget).getLeft();
			top = ((Positional) widget).getTop();
		}
		((AbsolutePanel)getWidget()).add((Widget) widget, left, top);
	}
	
	/**
	 * Adjust specified widgets position
	 * @param widget
	 * @param left
	 * @param top
	 */
	public void setConsoleWidgetPosition(ConsoleComponent widget, int left, int top) {
		if (((AbsolutePanel)getWidget()).getWidgetIndex((Widget)widget) >= 0) {
			((AbsolutePanel)getWidget()).setWidgetPosition((Widget)widget, left, top);
		}		
	}

	@Override
	public void onAdd(int width, int height) {
		setVisible(true);
		onRender(width, height);
	}
	
	@Override
	public void onRender(int width, int height) {
		for (ConsoleComponent component : consoleWidgets) {
			component.onAdd(width, height);
		}
	}
	
	@Override
	public void onRemove() {
		for (ConsoleComponent component : consoleWidgets) {
			component.onRemove();
		}
	}
}

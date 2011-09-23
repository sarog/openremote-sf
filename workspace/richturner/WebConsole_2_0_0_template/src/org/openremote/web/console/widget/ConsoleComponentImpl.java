package org.openremote.web.console.widget;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public abstract class ConsoleComponentImpl extends Composite implements ConsoleComponent {
	protected boolean isInitialised = false;
	
	protected ConsoleComponentImpl(Widget container) {
		initWidget(container);
		setVisible(false);
		container.addStyleName("consoleWidget");
	}
	
	public void onAdd() {
		// Check that handlers have been registered if interactive if not register them on the parent
		if (this instanceof InteractiveConsoleComponent) {
			InteractiveConsoleComponent thisWidget = (InteractiveConsoleComponent) this;
			if (!thisWidget.handlersRegistered) {
				thisWidget.registerMouseAndTouchHandlers();
			}
		}
		
		// Call Widgets onRender Method and then display it
		onRender();
		setVisible(true);
		isInitialised = true;
	}
}

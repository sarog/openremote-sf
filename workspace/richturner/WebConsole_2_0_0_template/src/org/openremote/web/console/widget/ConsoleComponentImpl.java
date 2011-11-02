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
		DOM.setStyleAttribute(container.getElement(), "WebkitUserSelect", "none");
		DOM.setStyleAttribute(container.getElement(), "cursor", "pointer");
	}
	
	public void onAdd(int width, int height) {
		// Call Widgets onRender Method and then display it
		setVisible(true);
		onRender(width, height);
		
		// Check that handlers have been registered if interactive if not register them on the top level widget
		if (this instanceof InteractiveConsoleComponent) {
			InteractiveConsoleComponent thisWidget = (InteractiveConsoleComponent) this;
			if (!thisWidget.handlersRegistered) {
				thisWidget.registerMouseAndTouchHandlers();
			}
		}
		isInitialised = true;
	}
	
	public void onRemove() {
		if (this instanceof InteractiveConsoleComponent) {
			InteractiveConsoleComponent thisWidget = (InteractiveConsoleComponent) this;
			thisWidget.unRegisterMouseAndTouchHandlers();		
		}
	}
}

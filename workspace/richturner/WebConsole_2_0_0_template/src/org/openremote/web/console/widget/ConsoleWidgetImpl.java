package org.openremote.web.console.widget;

import com.google.gwt.user.client.ui.Composite;

public abstract class ConsoleWidgetImpl extends Composite implements ConsoleWidget {
	protected boolean isInitialised = false;
	
	public ConsoleWidgetImpl() {
		//this.setVisible(false);
	}
	
	public void onAdd() {
		// Check that handlers have been registered if interactive if not register them on the parent
		if (this instanceof InteractiveConsoleWidget) {
			InteractiveConsoleWidget thisWidget = (InteractiveConsoleWidget) this;
			if (!thisWidget.handlersRegistered) {
				thisWidget.registerMouseAndTouchHandlers();
			}
		}
		
		// Call Widgets onRender Method and then display it
		onRender();
		setVisible(true);
		isInitialised = true;
	}
	
	public void setVisible(boolean visible) {
		this.getWidget().setVisible(visible);
	}
}

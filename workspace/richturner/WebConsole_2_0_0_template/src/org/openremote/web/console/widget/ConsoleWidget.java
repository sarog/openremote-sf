package org.openremote.web.console.widget;

public interface ConsoleWidget {
	// Calls configure method and makes widget visible
	public void initialise();
	
	// Configures the widget based on the values supplied
	public void onRender();
	
	// Sets the visibility of the widget
	public void setVisible(boolean visible);
}

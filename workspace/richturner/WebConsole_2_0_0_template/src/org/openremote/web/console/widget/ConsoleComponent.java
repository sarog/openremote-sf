package org.openremote.web.console.widget;

public interface ConsoleComponent {
	// Called when widget added to console display
	public void onAdd();
	
	// Configures the widget based on the values supplied
	public void onRender();
	
	// Sets the visibility of the widget
	public void setVisible(boolean visible);
}

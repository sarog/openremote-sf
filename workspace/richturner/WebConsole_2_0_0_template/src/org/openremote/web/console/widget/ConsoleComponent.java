package org.openremote.web.console.widget;

public interface ConsoleComponent {
	// Called when widget added to console display
	public void onAdd(int width, int height);
	
	// Called when widget removed from console display
	public void onRemove();
	
	// Configures the widget based on the values supplied
	public void onRender(int width, int height);
	
	// Sets the visibility of the widget
	public void setVisible(boolean visible);
}

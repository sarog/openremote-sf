package org.openremote.web.console.widget;

public interface ConsoleComponent {
	// Called when widget added to console display
	public void onAdd(int width, int height);
	
	// Called when screen size changes
	public void onRefresh(int width, int height);
	
	// Configures the widget based on the values supplied
	public void onRender(int width, int height);
	
	// Called when screen size changes
	public void onUpdate(int width, int height);
	
	// Called when widget removed from console display
	public void onRemove();
	
	// Sets the visibility of the widget
	public void setVisible(boolean visible);
	
	// Sets the sensor info associated with this component
	public void setSensor(Sensor sensor);
	
	// Gets the sensor info associated with this component
	public Sensor getSensor();
}

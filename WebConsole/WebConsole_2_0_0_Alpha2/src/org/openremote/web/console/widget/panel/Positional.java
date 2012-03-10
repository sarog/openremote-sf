package org.openremote.web.console.widget.panel;

public interface Positional {
	public void setPosition(int left, int top);
	
	public void setPosition(String left, String top);
	
	public int getLeft();
	
	public int getTop();
}

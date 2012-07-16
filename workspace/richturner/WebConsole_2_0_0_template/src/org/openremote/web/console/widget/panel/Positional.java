package org.openremote.web.console.widget.panel;

public interface Positional {
	public void setPosition(Integer left, Integer top, Integer right, Integer bottom);
	
	public void setPosition(String left, String top, String right, String bottom);
	
	public Integer getLeft();
	
	public Integer getTop();
	
	public Integer getRight();
	
	public Integer getBottom();
}

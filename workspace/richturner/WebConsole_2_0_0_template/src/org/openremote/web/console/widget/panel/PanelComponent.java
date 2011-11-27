package org.openremote.web.console.widget.panel;

import java.util.List;
import java.util.Set;
import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.panel.entity.DataValuePair;
import org.openremote.web.console.widget.ConsoleComponent;
import org.openremote.web.console.widget.PassiveConsoleComponent;
import org.openremote.web.console.widget.Sensor;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class PanelComponent extends PassiveConsoleComponent implements Positional {
	private static final String CLASS_NAME = "panelComponent";
	private int left;
	private int top;
	private int height;
	private int width;
	
	private enum ScreenDirection {
		WIDTH,
		HEIGHT;
	}
	
	public PanelComponent() {
		super(new SimplePanel(), CLASS_NAME);
		super.getWidget().addStyleName(CLASS_NAME);
		super.getWidget().setStylePrimaryName(getClassName());
	}
	
	public void setHeight(int height) {
		this.height = height;
		super.setHeight(height + "px");
	}
	
	public void setWidth(int width) {
		this.width = width;
		super.setWidth(width + "px");
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getWidth() {
		return width;
	}
	
	public void setPanelWidget(Widget panelWidget) {
		((SimplePanel)super.getWidget()).add(panelWidget);
	}
	
	@Override
	public Widget getWidget() {
		return ((SimplePanel)super.getWidget()).getWidget();
	}
	
	@Override
	public void setStylePrimaryName(String style) {
		super.getWidget().setStylePrimaryName(style);
	}
	
	@Override
	public void onAdd(int width, int height) {
		onAdd(null);
	}
	
	public void onAdd(int width, int height, List<DataValuePair> data) {
		onAdd(data);
	}
	
	private void onAdd(List<DataValuePair> data) {
		setVisible(true);
		getWidget().setWidth(width + "px");
		getWidget().setHeight(height + "px");
		onRender(width, height);
		isInitialised = true;
	}
	
	@Override
	public void onRender(int width, int height) {
		onRender(width, height, null);
	}
	
	@Override
	public void setHeight(String height) {
		setHeight(calculateDimensionFromString(height, ScreenDirection.HEIGHT));
	}
	
	@Override
	public void setWidth(String width) {
		setWidth(calculateDimensionFromString(width, ScreenDirection.WIDTH));
	}
	
	@Override
	public void setPosition(int left, int top) {
		this.left = left;
		this.top = top;
	}
	
	@Override
	public void setPosition(String leftStr, String topStr) {
		int left = 0;
		int top = 0;
		
		left = calculateDimensionFromString(leftStr, ScreenDirection.WIDTH);
		top = calculateDimensionFromString(topStr, ScreenDirection.HEIGHT);
		
		setPosition(left, top);
	}

	@Override
	public int getLeft() {
		return this.left;
	}

	@Override
	public int getTop() {
		return this.top;
	}
	
	private int calculateDimensionFromString(String dimStr, ScreenDirection direction) {
		int dim = 0;
		int refLength = 0;
		
		switch(direction) {
			case WIDTH:
				refLength = WebConsole.getConsoleUnit().getConsoleDisplay().getWidth();
				break;
			case HEIGHT:
				refLength = WebConsole.getConsoleUnit().getConsoleDisplay().getHeight();
		}
		if (dimStr.endsWith("%")) {
			dimStr = dimStr.replaceAll("%", "");
			try {
				double calc = Double.parseDouble(dimStr);
				dim = (int)Math.round((calc / 100) * refLength); 
			} catch (Exception e) {}
		} else if (dimStr.endsWith("px")) {
			dimStr = dimStr.replaceAll("px", "");
			try {
				dim = Integer.parseInt(dimStr);
			} catch (Exception e) {}
		}
		return dim;
	}
	
	public abstract Set<Sensor> getSensors();
	
	public abstract Set<ConsoleComponent> getComponents();
	
	public abstract String getClassName();
	
	public abstract void onRender(int width, int height, List<DataValuePair> data);
}

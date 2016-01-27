package org.openremote.web.console.widget.panel;

import java.util.List;
import java.util.Set;

import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.event.sensor.SensorChangeEvent;
import org.openremote.web.console.event.sensor.SensorChangeHandler;
import org.openremote.web.console.event.ui.BindingDataChangeHandler;
import org.openremote.web.console.panel.entity.DataValuePairContainer;
import org.openremote.web.console.view.ScreenViewImpl;
import org.openremote.web.console.widget.ConsoleComponent;
import org.openremote.web.console.widget.PassiveConsoleComponent;
import org.openremote.web.console.widget.Sensor;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class PanelComponent extends PassiveConsoleComponent implements Positional {
	private static final String CLASS_NAME = "panelComponent";
	private int left;
	private int top;
	protected int height;
	protected int width;
	private Double widthPercentage = null;
	private Double heightPercentage = null;
	private Double leftPercentage = null;
	private Double topPercentage = null;
	protected ScreenViewImpl parent = null;
	
	public static enum DimensionUnit {
		PX,
		PERCENTAGE;
	}
	
	public static class DimensionResult {
		private double value;
		private DimensionUnit unit;
		
		public DimensionResult(double value, DimensionUnit unit) {
			this.value = value;
			this.unit = unit;
		}
		
		public double getValue() {
			return value;
		}
		
		public DimensionUnit getUnit() {
			return unit;
		}
	}
	
	public PanelComponent() {
		super(new SimplePanel(), CLASS_NAME);
		super.getWidget().addStyleName(CLASS_NAME);
		super.getWidget().setStylePrimaryName(getClassName());
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
	public void onAdd(int screenWidth, int screenHeight) {
		onAdd(null, screenWidth, screenHeight, null);
	}
	
	public void onAdd(ScreenViewImpl parent, int screenWidth, int screenHeight) {
		onAdd(parent, screenWidth, screenHeight, null);
	}
	
	public void onAdd(ScreenViewImpl parent, int screenWidth, int screenHeight, List<DataValuePairContainer> data) {
		this.parent = parent;
		setPositionAndSize(screenWidth, screenHeight);
		onRender(width, height, data);
		setVisible(true);
		
		initHandlers();
		
		isInitialised = true;
	}
	
	@Override
	public void onRender(int width, int height) {
		onRender(width, height, null);
	}
	
	public void onRefresh(int screenWidth, int screenHeight) {
		setPositionAndSize(screenWidth, screenHeight);
		onUpdate(width, height);
	}
	
	private void setPositionAndSize(int screenWidth, int screenHeight) {
		if (widthPercentage != null) {
			width = (int)Math.round(widthPercentage * screenWidth); 
		}
		if (heightPercentage != null) {
			height = (int)Math.round(heightPercentage * screenHeight); 
		}
		if (leftPercentage != null) {
			left = (int)Math.round(leftPercentage * screenWidth); 
		}
		if (topPercentage != null) {
			top = (int)Math.round(topPercentage * screenHeight); 
		}
		if (parent != null && (leftPercentage != null || topPercentage != null)) {
			parent.getWidget().setWidgetPosition(this, left, top);
		}
		getWidget().setWidth(width + "px");
		getWidget().setHeight(height + "px");
	}
	
	@Override
	public void setHeight(String height) {
		DimensionResult result = getDimFromString(height);
		if (result.getUnit() == DimensionUnit.PERCENTAGE) {
			heightPercentage = result.getValue();
		} else {
			this.height = (int)Math.round(result.getValue());
		}
	}
	
	@Override
	public void setWidth(String width) {
		DimensionResult result = getDimFromString(width);
		if (result.getUnit() == DimensionUnit.PERCENTAGE) {
			widthPercentage = result.getValue();
		} else {
			this.width = (int)Math.round(result.getValue());
		}
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getWidth() {
		return width;
	}
	
	@Override
	public void setPosition(int left, int top) {
		this.left = left;
		this.top = top;
	}
	
	@Override
	public void setPosition(String leftStr, String topStr) {
		DimensionResult left = getDimFromString(leftStr);
		DimensionResult top = getDimFromString(topStr);
		
		if (left.getUnit() == DimensionUnit.PERCENTAGE) {
			leftPercentage = left.getValue();
		} else {
			this.left = (int)Math.round(left.getValue()); 
		}
		if (top.getUnit() == DimensionUnit.PERCENTAGE) {
			topPercentage = top.getValue();
		} else {
			this.top = (int)Math.round(top.getValue()); 
		}
	}

	@Override
	public int getLeft() {
		return this.left;
	}

	@Override
	public int getTop() {
		return this.top;
	}
	
	public static DimensionResult getDimFromString(String dimStr) {
		double dim = 0;
		DimensionUnit unit = DimensionUnit.PX;
		
		if (dimStr.endsWith("%")) {
			dimStr = dimStr.replaceAll("%", "");
			try {
				dim = (Double.parseDouble(dimStr)/100); 
			} catch (Exception e) {
				dim = 1;
			}
			unit = DimensionUnit.PERCENTAGE;
		} else {
			if (dimStr.endsWith("px")) {
				dimStr = dimStr.replaceAll("px", "");
			}
			try {
				dim = Integer.parseInt(dimStr);
			} catch (Exception e) {}
		}
		
		return new DimensionResult(dim, unit);
	}
	
	public abstract Set<Sensor> getSensors();
	
	public abstract Set<ConsoleComponent> getComponents();
	
	public abstract String getClassName();
	
	public abstract void onRender(int width, int height, List<DataValuePairContainer> data);
	
	public abstract void onUpdate(int width, int height);
}

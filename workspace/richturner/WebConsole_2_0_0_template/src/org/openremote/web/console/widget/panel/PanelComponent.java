/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package org.openremote.web.console.widget.panel;

import java.util.List;
import java.util.Set;

import org.openremote.web.console.panel.entity.DataValuePairContainer;
import org.openremote.web.console.widget.ConsoleComponent;
import org.openremote.web.console.widget.PassiveConsoleComponent;
import org.openremote.web.console.widget.Sensor;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class PanelComponent extends PassiveConsoleComponent implements Positional {
	private static final String CLASS_NAME = "panelComponent";
	private Integer left;
	private Integer top;
	private Integer right;
	private Integer bottom;
	protected Integer height;
	protected Integer width;
	private Double widthPercentage = null;
	private Double heightPercentage = null;
	private Double leftPercentage = null;
	private Double topPercentage = null;
	private Double rightPercentage = null;
	private Double bottomPercentage = null;
	private AbsolutePanel parent = null;
	
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
	
	public void onAdd(AbsolutePanel parent, int screenWidth, int screenHeight) {
		onAdd(parent, screenWidth, screenHeight, null);
	}
	
	public void onAdd(AbsolutePanel parent, int screenWidth, int screenHeight, List<DataValuePairContainer> data) {
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
		int currentWidth = width;
		int currentHeight = height;
		setPositionAndSize(screenWidth, screenHeight);
		if (currentWidth != this.width || currentHeight != this.height) {
			onUpdate(width, height);
		}
	}
	
	private void setPositionAndSize(int screenWidth, int screenHeight) {
		Integer left = 0;
		Integer top = 0;
		
		if (widthPercentage != null) {
			width = (int)Math.round(widthPercentage * screenWidth); 
		}
		if (heightPercentage != null) {
			height = (int)Math.round(heightPercentage * screenHeight); 
		}
		if (leftPercentage != null) {
			this.left = (int)Math.round(leftPercentage * screenWidth); 
		}
		if (topPercentage != null) {
			this.top = (int)Math.round(topPercentage * screenHeight); 
		}
		if (rightPercentage != null) {
			right = (int)Math.round(rightPercentage * screenWidth); 
		}
		if (bottomPercentage != null) {
			bottom = (int)Math.round(bottomPercentage * screenHeight); 
		}
		// Determine how to size and position the widget
		if (width == null || width == 0) {
			if (this.left != null && this.right != null) {
				width = (screenWidth - this.left - right);
			}
		}
		if (height == null || height == 0) {
			if (this.top != null && bottom != null) {
				height = (screenHeight - this.top - bottom);
			}
		}
		if (this.left == null && right != null) {
			left = (screenWidth - right - width);
		} else {
			left = this.left;
		}
		if (this.top == null && bottom != null) {
			top = (screenHeight - bottom - height);
		} else {
			top = this.top;
		}
		
		if (parent != null && left != null && top != null) {
			parent.setWidgetPosition(this, left, top);
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
			setHeight((int)Math.round(result.getValue()));
		}
	}
	
	@Override
	public void setWidth(String width) {
		DimensionResult result = getDimFromString(width);
		if (result.getUnit() == DimensionUnit.PERCENTAGE) {
			widthPercentage = result.getValue();
		} else {
			setWidth((int)Math.round(result.getValue()));
		}
	}
	
	public void setHeight(Integer height) {
		this.height = height;
	}
	
	public void setWidth(Integer width) {
		this.width = width;
	}
	
	public Integer getHeight() {
		return height;
	}
	
	public Integer getWidth() {
		return width;
	}
	
	@Override
	public void setPosition(Integer left, Integer top, Integer right, Integer bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}
	
	@Override
	public void setPosition(String leftStr, String topStr, String rightStr, String bottomStr) {
		DimensionResult left = getDimFromString(leftStr);
		DimensionResult top = getDimFromString(topStr);
		DimensionResult right = getDimFromString(rightStr);
		DimensionResult bottom = getDimFromString(bottomStr);
		if (left != null) {
			if (left.getUnit() == DimensionUnit.PERCENTAGE) {
				leftPercentage = left.getValue();
			} else {
				this.left = (int)Math.round(left.getValue()); 
			}
		}
		if (top != null) {
			if (top.getUnit() == DimensionUnit.PERCENTAGE) {
				topPercentage = top.getValue();
			} else {
				this.top = (int)Math.round(top.getValue()); 
			}
		}
		if (right != null) {
			if (right.getUnit() == DimensionUnit.PERCENTAGE) {
				rightPercentage = right.getValue();
			} else {
				this.right = (int)Math.round(right.getValue()); 
			}
		}
		if (bottom != null) {
			if (bottom.getUnit() == DimensionUnit.PERCENTAGE) {
				bottomPercentage = bottom.getValue();
			} else {
				this.bottom = (int)Math.round(bottom.getValue()); 
			}
		}
	}

	@Override
	public Integer getLeft() {
		return this.left;
	}

	@Override
	public Integer getTop() {
		return this.top;
	}

	@Override
	public Integer getRight() {
		return this.right;
	}

	@Override
	public Integer getBottom() {
		return this.bottom;
	}
	
	public static DimensionResult getDimFromString(String dimStr) {
		double dim = 0;
		DimensionUnit unit = DimensionUnit.PX;
		
		if (dimStr == null) return null;
		
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

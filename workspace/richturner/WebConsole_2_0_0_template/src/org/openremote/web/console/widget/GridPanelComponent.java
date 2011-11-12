package org.openremote.web.console.widget;

import java.util.Iterator;
import java.util.List;
import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.panel.entity.Cell;
import org.openremote.web.console.panel.entity.GridLayout;
import org.openremote.web.console.panel.entity.component.ButtonComponent;
import org.openremote.web.console.panel.entity.component.ImageComponent;
import org.openremote.web.console.panel.entity.component.LabelComponent;
import org.openremote.web.console.panel.entity.component.SliderComponent;
import org.openremote.web.console.panel.entity.component.SwitchComponent;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;

public class GridPanelComponent extends PassiveConsoleComponent implements Positional {
	private static final String CLASS_NAME = "gridPanelComponent";
	//private ConsoleComponent component;
	private int left;
	private int top;
	private int height;
	private int width;
	private int rows;
	private int cols;
	private CellData[][] cellDataArr;
	
	private class CellData {
		private int rowSpan;
		private int colSpan;
	}
	
	public GridPanelComponent() {
		super(new FlexTable(), CLASS_NAME);
		FlexTable grid = (FlexTable)getWidget();
		grid.setCellPadding(0);
		grid.setCellSpacing(0);
	}

	@Override
	public void onAdd(int width, int height) {
		setVisible(true);
		onRender(width, height);
	}
	
	@Override
	// Pass size info to widget so explicit size can be set to avoid any cross browser rendering issues
	public void onRender(int width, int height) {
		FlexTable grid = (FlexTable)getWidget();
		int colWidth = (int)Math.round((double)this.width / cols);
		int rowHeight = (int)Math.round((double)this.height / rows);
		
		for (int i=0; i<rows; i++) {
			for (int j=0; j<cols; j++) {
				CellData cellData = cellDataArr[i][j];
				if (cellData != null) {
					HTMLTable.CellFormatter formatter = grid.getCellFormatter();
					formatter.setHeight(i, j, rowHeight + "px");
					formatter.setWidth(i, j, colWidth + "px");
					formatter.setHorizontalAlignment(i, j, HasHorizontalAlignment.ALIGN_CENTER);
					formatter.setVerticalAlignment(i, j, HasVerticalAlignment.ALIGN_MIDDLE);
					
					int cellWidth = cellData.colSpan * colWidth;
					int cellHeight = cellData.rowSpan * rowHeight;
					Widget widget = grid.getWidget(i, j);
					if (widget != null) {
						ConsoleComponent component = (ConsoleComponent)widget;
						if (component != null) {
							component.onAdd(cellWidth, cellHeight);
						}
					}
				}
			}
		}
	}
	
	@Override
	public void onRemove() {
		Iterator<Widget> iterator = ((FlexTable)getWidget()).iterator();
		while(iterator.hasNext()) {
			Widget widget = iterator.next();			
			ConsoleComponent component = (ConsoleComponent)widget;
			if (component != null) {
				component.onRemove();
			}
		}
	}
	
	public void setComponent(int row, int col, ConsoleComponent component) {
		((FlexTable)getWidget()).setWidget(row, col, (Widget)component);
	}
	
	public ConsoleComponent getComponent(int row, int col) {
		return (ConsoleComponent)(((FlexTable)getWidget()).getWidget(row, col));
	}

	@Override
	public void setPosition(int left, int top) {
		this.left = left;
		this.top = top;
	}

	@Override
	public int getLeft() {
		return this.left;
	}

	@Override
	public int getTop() {
		return this.top;
	}
	
	public void setHeight(int height) {
		this.height = height;
		super.setHeight(height + "px");
	}
	
	public void setWidth(int width) {
		this.width = width;
		super.setWidth(width + "px");
	}
	
	private void init() {
		FlexTable grid = (FlexTable)getWidget();
		for (int i=0; i<rows; i++) {
			for (int j=0; j<cols; j++) {
				grid.setText(i, j, "&nbsp");
			}
		}
	}
	
	@Override
	public void setHeight(String height) {
		int heightInt = 0;
		if (height.endsWith("%")) {
			height = height.replaceAll("%", "");
			try {
				double calc = Integer.parseInt(height);
				int displayHeight = WebConsole.getConsoleUnit().getConsoleDisplay().getHeight();
				heightInt = (int)Math.round((calc / 100) * displayHeight); 
			} catch (Exception e) {}
		} else if (height.endsWith("px")) {
			height = height.replaceAll("px", "");
			try {
				heightInt = Integer.parseInt(height);
			} catch (Exception e) {}
		}
		setHeight(heightInt);
	}
	
	@Override
	public void setWidth(String width) {
		int widthInt = 0;
		if (width.endsWith("%")) {
			width = width.replaceAll("%", "");
			try {
				double calc = Integer.parseInt(width);
				int displayWidth = WebConsole.getConsoleUnit().getConsoleDisplay().getWidth();
				widthInt = (int)Math.round((calc / 100) * displayWidth); 
			} catch (Exception e) {}
		} else if (width.endsWith("px")) {
			width = width.replaceAll("px", "");
			try {
				widthInt = Integer.parseInt(width);
			} catch (Exception e) {}
		}
		setWidth(widthInt);
	}
	
	public void setRowCount(int count) {
		this.rows = count;
	}
	
	public void setColCount(int count) {
		this.cols = count;
	}
	
	public static GridPanelComponent build(GridLayout layout) throws Exception {
		GridPanelComponent panel = new GridPanelComponent();
		if (layout == null) {
			return panel;
		}
		panel.setHeight(layout.getHeight());
		panel.setWidth(layout.getWidth());
		panel.setPosition(layout.getLeft(),layout.getTop());
		panel.setColCount(layout.getCols());
		panel.setRowCount(layout.getRows());
		
		// Initialise the table
		panel.init();
		
		// Create cells
		List<Cell> cells = layout.getCell();
		panel.cellDataArr = new CellData[panel.rows][panel.cols];
		
		if (cells != null) {
			for (Cell cell : cells) {
				int row = cell.getY();
				int col = cell.getX();
				int rowSpan = cell.getRowspan();
				int colSpan = cell.getColspan();
				CellData cellData = panel.new CellData();
				cellData.rowSpan = rowSpan;
				cellData.colSpan = colSpan;
				panel.cellDataArr[row][col] = cellData;
				
				// Create component
				LabelComponent labelComponent = cell.getLabel();
				ImageComponent imageComponent = cell.getImage();
				SliderComponent sliderComponent = cell.getSlider();
				SwitchComponent switchComponent = cell.getSwitch();
				ButtonComponent buttonComponent = cell.getButton();
				
				// Create Console Component
				ConsoleComponent component = null;
				
				if (labelComponent != null) {
					component = org.openremote.web.console.widget.LabelComponent.build(labelComponent);
				} else if (imageComponent != null) {
					component = org.openremote.web.console.widget.ImageComponent.build(imageComponent);
				} else if (sliderComponent != null) {
					component = org.openremote.web.console.widget.SliderComponent.build(sliderComponent);
				} else if (switchComponent != null) {
					component = org.openremote.web.console.widget.SwitchComponent.build(switchComponent);
				} else if (buttonComponent != null) {
					component = org.openremote.web.console.widget.ButtonComponent.build(buttonComponent);
				} else {
					return null;
				}
				
				if (component != null) {
					panel.setComponent(row, col, component);
				}
				if (rowSpan != 1) {
					((FlexTable)panel.getWidget()).getFlexCellFormatter().setRowSpan(row, col, rowSpan);
				}
				if (colSpan != 1) {
					((FlexTable)panel.getWidget()).getFlexCellFormatter().setColSpan(row, col, colSpan);
				}
			}
		}
		
		return panel;
	}
}

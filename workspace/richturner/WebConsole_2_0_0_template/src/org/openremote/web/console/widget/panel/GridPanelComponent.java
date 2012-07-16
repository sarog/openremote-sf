package org.openremote.web.console.widget.panel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.openremote.web.console.panel.entity.Cell;
import org.openremote.web.console.panel.entity.Column;
import org.openremote.web.console.panel.entity.DataValuePairContainer;
import org.openremote.web.console.panel.entity.GridLayout;
import org.openremote.web.console.panel.entity.component.ButtonComponent;
import org.openremote.web.console.panel.entity.component.ImageComponent;
import org.openremote.web.console.panel.entity.component.LabelComponent;
import org.openremote.web.console.panel.entity.component.SliderComponent;
import org.openremote.web.console.panel.entity.component.SwitchComponent;
import org.openremote.web.console.widget.ConsoleComponent;
import org.openremote.web.console.widget.Sensor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;

public class GridPanelComponent extends PanelComponent {
	private static final String CLASS_NAME = "gridPanelComponent";
	private int rows;
	private int cols;
	private List<String> colWidths = new ArrayList<String>();
	private CellData[][] cellDataArr;
	private Set<ConsoleComponent> components = new HashSet<ConsoleComponent>();
	
	private class CellData {
		private int rowSpan;
		private int colSpan;
	}
	
	public GridPanelComponent() {
		FlexTable grid = new FlexTable();
		grid.setCellPadding(0);
		grid.setCellSpacing(0);
		setPanelWidget(grid);
	}
	
	public void setComponent(int row, int col, ConsoleComponent component) {
		((FlexTable)getWidget()).setWidget(row, col, (Widget)component);
		components.add(component);
	}
	
	public ConsoleComponent getComponent(int row, int col) {
		return (ConsoleComponent)(((FlexTable)getWidget()).getWidget(row, col));
	}
	
	private void init() {
		FlexTable grid = (FlexTable)getWidget();
		for (int i=0; i<rows; i++) {
			for (int j=0; j<cols; j++) {
				grid.setText(i, j, "&nbsp");
			}
		}
	}
	
	public void setRowCount(int count) {
		this.rows = count;
	}
	
	public void setColCount(int count) {
		this.cols = count;
	}
	
	// ---------------------------------------------------------------------------------
	//			SUPER CLASS OVERRIDES BELOW
	// ---------------------------------------------------------------------------------
	
	@Override
	public void onRender(int width, int height, List<DataValuePairContainer> data) {
		FlexTable grid = (FlexTable)getWidget();
		int colWidth = (int)Math.round((double)width / cols);
		int rowHeight = (int)Math.round((double)height / rows);
		
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
	public void onUpdate(int width, int height) {
		int colWidth = (int)Math.round((double)width / cols);
		int rowHeight = (int)Math.round((double)height / rows);
		for (ConsoleComponent component : components) {
			component.onRefresh(colWidth, rowHeight);
		}
	}
	
	@Override
	public void onRemove() {
		for (ConsoleComponent component : components) {
			if (component != null) {
				component.onRemove();
			}
		}
	}
	
	@Override
	public Set<Sensor> getSensors() {
		Set<Sensor> sensors = new HashSet<Sensor>();
		for (ConsoleComponent component : components) {
			sensors.add(component.getSensor());
		}
		return sensors;
	}
	
	@Override
	public Set<ConsoleComponent> getComponents() {
		return components;
	}
	
	@Override
	public String getClassName() {
		return CLASS_NAME;
	}

	// ---------------------------------------------------------------------------------
	//			BUILD METHOD BELOW HERE
	// ---------------------------------------------------------------------------------
	
	public static GridPanelComponent build(GridLayout layout) throws Exception {
		GridPanelComponent panel = new GridPanelComponent();
		if (layout == null) {
			return panel;
		}
		panel.setHeight(layout.getHeight());
		panel.setWidth(layout.getWidth());
		panel.setPosition(layout.getLeft(),layout.getTop(), layout.getRight(), layout.getBottom());
		panel.setColCount(layout.getCols());
		panel.setRowCount(layout.getRows());
		
		// Initialise the table
		panel.init();
		
		// Check for column definitions
		List<Column> cols = layout.getCol();
		if (cols != null) {
			for (int i=0; i<cols.size(); i++) {
				((FlexTable)panel.getWidget()).getColumnFormatter().setWidth(i, cols.get(i).getWidth());
			}
		}
		
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
					org.openremote.web.console.widget.LabelComponent lblComponent = new org.openremote.web.console.widget.LabelComponent();
					lblComponent.setText("COMPONENT TYPE NOT SUPPORTED.");
					component = lblComponent;
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

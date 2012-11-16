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
import org.openremote.web.console.panel.entity.component.WebElementComponent;
import org.openremote.web.console.util.BrowserUtils;
import org.openremote.web.console.widget.ConsoleComponent;
import org.openremote.web.console.widget.PassiveConsoleComponent;
import org.openremote.web.console.widget.Sensor;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;
/**
 * 
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public class GridPanelComponent extends PanelComponent {
	private static final String CLASS_NAME = "gridPanelComponent";
	private int rows;
	private int cols;
	private List<String> colWidths = new ArrayList<String>();
	private CellData[][] cellDataArr;
	private Set<ConsoleComponent> components = new HashSet<ConsoleComponent>();
	
	private class CellData {
		private int rowSpan = 1;
		private int colSpan = 1;
		private boolean ignore = false;
	}
	
	public GridPanelComponent() {
		FlexTable grid = new FlexTable();
		grid.setCellPadding(0);
		grid.setCellSpacing(0);
		setPanelWidget(grid);
	}
	
	public void setComponent(int row, int col, ConsoleComponent component) {
		if (component != null) {
			((FlexTable)getWidget()).setWidget(row, col, (Widget)component);
			components.add(component);
		} else {
			((FlexTable)getWidget()).setText(row, col, "");
		}
	}
	
	public ConsoleComponent getComponent(int row, int col) {
		return (ConsoleComponent)(((FlexTable)getWidget()).getWidget(row, col));
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
					if (!cellData.ignore) {
						FlexCellFormatter formatter = grid.getFlexCellFormatter();
						int cellWidth = cellData.colSpan * colWidth;
						int cellHeight = cellData.rowSpan * rowHeight;
						formatter.setRowSpan(i, j, cellData.rowSpan);
						formatter.setColSpan(i, j, cellData.colSpan);
						formatter.setHeight(i, j, cellHeight + "px");
						formatter.setWidth(i, j, cellWidth + "px");
						formatter.setHorizontalAlignment(i, j, HasHorizontalAlignment.ALIGN_CENTER);
						formatter.setVerticalAlignment(i, j, HasVerticalAlignment.ALIGN_MIDDLE);
						
						Widget widget = grid.getWidget(i, j);
						if (widget != null) {
							ConsoleComponent component = (ConsoleComponent)widget;
							if (component != null) {
								component.onAdd(cellWidth, cellHeight);
							}
						} else {
							// Just add an empty cell
							this.setComponent(i, j, null);
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
		FlexTable grid = (FlexTable)panel.getWidget();
		
		if (layout == null) {
			return panel;
		}
		panel.setHeight(layout.getHeight());
		panel.setWidth(layout.getWidth());
		panel.setPosition(layout.getLeft(),layout.getTop(), layout.getRight(), layout.getBottom());
		int cols = layout.getCols();
		int rows = layout.getRows();
		panel.setColCount(cols);
		panel.setRowCount(rows);

		// Initialise the table
		panel.cellDataArr = new CellData[panel.rows][panel.cols];
		for (int i=0; i<rows; i++) {
			for (int j=0; j<cols; j++) {
				panel.cellDataArr[i][j] = panel.new CellData();
			}
		}
		
		// Check for column definitions
		List<Column> colList = layout.getCol();
		if (colList != null) {
			for (int i=0; i<colList.size(); i++) {
				grid.getColumnFormatter().setWidth(i, colList.get(i).getWidth());
			}
		}
		
		// Update explicit cell definitions
		List<Cell> cells = layout.getCell();
		
		if (cells != null) {
			for (Cell cell : cells) {
				int row = cell.getY();
				int col = cell.getX();
				int rowSpan = cell.getRowspan();
				int colSpan = cell.getColspan();
				CellData cellData = panel.cellDataArr[row][col];
				cellData.rowSpan = rowSpan;
				cellData.colSpan = colSpan;
				
				// Set ignore flag of spanned cells
				for (int j=1; j<colSpan; j++) {
						CellData cData = panel.cellDataArr[row][col+j];
						cData.ignore = true;
				}
				for (int i=1; i<rowSpan; i++) {
					CellData cData = panel.cellDataArr[row+i][col];
					cData.ignore = true;
				}
				
				// Create component
				LabelComponent labelComponent = cell.getLabel();
				ImageComponent imageComponent = cell.getImage();
				SliderComponent sliderComponent = cell.getSlider();
				SwitchComponent switchComponent = cell.getSwitch();
				ButtonComponent buttonComponent = cell.getButton();
				WebElementComponent webComponent = cell.getWeb();
				
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
				} else if (webComponent != null) {
					component = org.openremote.web.console.widget.WebElementComponent.build(webComponent);
				} else {
					org.openremote.web.console.widget.LabelComponent lblComponent = new org.openremote.web.console.widget.LabelComponent();
					lblComponent.setText("COMPONENT TYPE NOT SUPPORTED.");
					component = lblComponent;
				}
				
				if (component != null) {
					// REMOVED THIS BECAUSE GRID PANEL ONLY SUPPORTS ONE WIDGET PER CELL SO NOT NEEDED
					// Add CSS4 pointer-events attribute to allow pointer events to pass through
					// passive components; this isn't the best place to set this but not many other
					// options given code structure
//					if (component instanceof PassiveConsoleComponent) {
//						BrowserUtils.setStyleAttributeAllBrowsers(grid.getCellFormatter().getElement(row, col), "pointerEvents", "none");
//					}					
					panel.setComponent(row, col, component);
				}
			}
		}

		return panel;
	}
}

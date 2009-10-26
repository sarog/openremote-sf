/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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
package org.openremote.modeler.client.widget.uidesigner;

import java.util.List;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.gxtextends.ScreenDropTarget;
import org.openremote.modeler.client.model.Position;
import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.client.widget.control.ScreenButton;
import org.openremote.modeler.domain.Cell;
import org.openremote.modeler.domain.Grid;
import org.openremote.modeler.domain.UIScreen;
import org.openremote.modeler.domain.control.UIButton;
import org.openremote.modeler.touchpanel.TouchPanelDefinition;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.DragSource;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.event.ResizeEvent;
import com.extjs.gxt.ui.client.event.ResizeListener;
import com.extjs.gxt.ui.client.fx.Resizable;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlexTable;

/**
 * A layout container to display as grid, the inner is relative position.
 */
public class GridLayoutContainer extends LayoutContainer {

   /** The screen. */
   private UIScreen screen;

   private GridCellContainer selectedComponent;
   
   /** The Constant POSITION. */
   private static final String POSITION = "position";

   /** Make resizable in east and south direction. */
   private static final String RESIZABLE_HANDLES = "e s";
   
   /** The btn in area. */
   private boolean[][] btnInArea;
   
   /** The panel definition. */
   private TouchPanelDefinition panelDefinition;

   /**
    * Instantiates a new grid layout container.
    * 
    * @param screen the screen
    */
   public GridLayoutContainer(UIScreen screen) {
      this.screen = screen;
      this.panelDefinition = screen.getTouchPanelDefinition();
      Grid grid = screen.getGrid();
      btnInArea = new boolean[grid.getColumnCount()][grid.getRowCount()];
      addStyleName("screen-background");
      initCellInArea(grid);
      createGrid(grid);

   }

   /**
    * Inits the cell in area.
    */
   private void initCellInArea(Grid grid) {
      for (int x = 0; x < grid.getColumnCount(); x++) {
         for (int y = 0; y < grid.getRowCount(); y++) {
            btnInArea[x][y] = false;
         }
      }
   }

   /**
    * Creates the grid, and init it's cells.
    * 
    * @param grid the grid
    */
   private void createGrid(final Grid grid) {
//      int gridWidth = grid.getWidth();
//      int gridHeight = grid.getHeight();
      int gridWidth = panelDefinition.getGrid().getWidth(); // temp use.
      int gridHeight = panelDefinition.getGrid().getHeight();
      setSize(gridWidth, gridHeight);
      setPosition(0, 0);
      setBorders(false);
      FlexTable screenTable = new FlexTable();
      screenTable.setCellPadding(0);
      screenTable.setCellSpacing(0);
      screenTable.addStyleName("panel-table");
      screenTable.setPixelSize(gridWidth, gridHeight);
      add(screenTable);
      final int cellWidth =  (gridWidth - (grid.getColumnCount() + 1)) / grid.getColumnCount();
      final int cellHeight = (gridHeight - (grid.getRowCount() + 1)) / grid.getRowCount();
      DNDListener dndListener = new DNDListener() {
         @SuppressWarnings("unchecked")
         public void dragDrop(DNDEvent e) {
            LayoutContainer targetCell = (LayoutContainer) e.getDropTarget().getComponent();
            Position targetPosition = (Position) targetCell.getData(POSITION);
            GridCellContainer cellContainer = new GridCellContainer();
            Object data = e.getData();
            if (data instanceof GridCellContainer) {
               cellContainer = (GridCellContainer) data;
               if (canDrop(targetPosition.getPosX(), targetPosition.getPosY(), cellContainer.getCell(), grid)) {
                  cellContainer.setCellPosition(targetPosition.getPosX(), targetPosition.getPosY());
                  cellContainer.setPagePosition(targetCell.getAbsoluteLeft(), targetCell.getAbsoluteTop());
               } else {
                  cellContainer.setPagePosition(cellContainer.getAbsoluteLeft(), cellContainer.getAbsoluteTop());
               }
            } else if (data instanceof List) {
               List<ModelData> models = (List<ModelData>) e.getData();
               if (models.size() > 0) {
                  BeanModel dataModel = models.get(0).get("model");
                  Cell cell = new Cell(IDUtil.nextID());
                  grid.addCell(cell);
                  if(dataModel.getBean() instanceof UIButton) {
                     cell.setUiControl(new UIButton("Button"));
                     cellContainer = createCellContainer(cell, cellWidth, cellHeight);
//                     cellContainer.setSize(cellWidth, cellHeight);
                     cellContainer.setCellSpan(1, 1);
                     cellContainer.setCellPosition(targetPosition.getPosX(), targetPosition.getPosY());
                     cellContainer.setPagePosition(targetCell.getAbsoluteLeft(), targetCell.getAbsoluteTop());
                     
                     add(cellContainer);
                     createDragSource(cellContainer);
                  }
               }
            }
            cellContainer.fillArea(btnInArea);
            if (selectedComponent != null) {
               selectedComponent.removeStyleName("button-border");
            }
            selectedComponent = cellContainer;
            selectedComponent.addStyleName("button-border");
            PropertyPanel.getInstance().update(selectedComponent);
            makeCellContainerResizable(cellWidth, cellHeight, cellContainer);
            layout();
            super.dragDrop(e);
         }

      };
      for (int i = 0; i < grid.getRowCount(); i++) {  // Initial the screen table, make it can be drop buttons.  
         for (int j = 0; j < grid.getColumnCount(); j++) {
            LayoutContainer cell = new LayoutContainer();
            cell.setSize(cellWidth, cellHeight);
            screenTable.setWidget(i, j, cell);
            cell.setData(POSITION, new Position(j, i));
            ScreenDropTarget dropTarget = new ScreenDropTarget(cell);
            dropTarget.setGroup(Constants.CONTROL_DND_GROUP);
            dropTarget.setOverStyle("background-color");
            dropTarget.addDNDListener(dndListener);
         }
      }
      if (grid.getCells().size() > 0) {  // If the Panel has closed, there may have some buttons on screen to be rendered. 
         List<Cell> cells= grid.getCells();
         for (Cell cell : cells) {
            GridCellContainer cellContainer = createCellContainer(cell, (cellWidth + 1) * cell.getColspan() - 1,
                  (cellHeight + 1) * cell.getRowspan() - 1);
            makeCellContainerResizable(cellWidth, cellHeight, cellContainer);
            cellContainer.setPosition(cellWidth * cell.getPosX() + cell.getPosX() + 1, cellHeight * cell.getPosY()
                  + cell.getPosY() + 1);
            cellContainer.setName(cell.getUiControl().getName());
//            if (button.getIcon() != null && !"".equals(button.getIcon())) {
//               screenBtn.setIcon(button.getIcon());
//            }
            add(cellContainer);
            cellContainer.fillArea(btnInArea);
            createDragSource(cellContainer);
            layout();

         }
      }

   }
   
   /**
    * init a cell with it's width and height.
    * 
    */
   private GridCellContainer createCellContainer(Cell cell, int cellWidth, int cellHeight) {
      GridCellContainer cellContainer =  new GridCellContainer(cell, new ScreenButton()) {
         @Override
         public void onBrowserEvent(Event event) {
            if (event.getTypeInt() == Event.ONMOUSEDOWN) {
               this.addStyleName("button-border");
               if (selectedComponent != null && (LayoutContainer) this != selectedComponent) {
                  selectedComponent.removeStyleName("button-border");
               }
               selectedComponent = (GridCellContainer) this;
               PropertyPanel.getInstance().update(selectedComponent);
            }
            super.onBrowserEvent(event);
         }
      };
      cellContainer.setSize(cellWidth, cellHeight);
      return cellContainer;
   }
   
   /**
    * Find max x direction in grid when resize the cell container.
    * 
    * @param cellContainer the cell container
    * @param grid the grid
    * 
    */
   private int findMaxXWhenResize(GridCellContainer cellContainer, Grid grid) {
      Cell cell = cellContainer.getCell();
      int maxX = cell.getPosX();
      for (; maxX < grid.getColumnCount() - 1; maxX++) {
         for (int tmpY = cell.getPosY(); ((tmpY < grid.getRowCount()) && (tmpY < cell.getPosY()
               + cell.getRowspan())); tmpY++) {
            if ((btnInArea[maxX + 1][tmpY])
                  && ((maxX + 1) > (cell.getPosX() + cell.getColspan() - 1))) {
               return maxX;
            }
         }
      }
      return maxX;
   }

   /**
    * Find max y direction in grid when resize the cell container.
    * 
    * @param cellContainer the cell container
    * @param grid the grid
    * 
    */
   private int findMaxYWhenResize(GridCellContainer cellContainer, Grid grid) {
      Cell cell = cellContainer.getCell();
      int maxY = cell.getPosY();
      for (; maxY < grid.getRowCount() - 1; maxY++) {
         for (int tmpX = cell.getPosX(); ((tmpX < grid.getColumnCount()) && (tmpX < cell.getPosX()
               + cell.getColspan())); tmpX++) {
            if ((btnInArea[tmpX][maxY + 1])
                  && ((maxY + 1) > (cell.getPosY() + cell.getRowspan() - 1))) {
               return maxY;
            }
         }
      }
      return maxY;
   }

   /**
    * Compute the cell container if can be drop in the position(x,y).
    */
   private boolean canDrop(int x, int y, Cell cell, Grid grid) {
      for (int tmpX = x; tmpX < x + cell.getColspan(); tmpX++) {
         for (int tmpY = y; tmpY < y + cell.getRowspan(); tmpY++) {
            if (tmpX > grid.getColumnCount() - 1 || tmpY > grid.getRowCount() - 1) {
               return false;
            }
            if (btnInArea[tmpX][tmpY]) {
               return false;
            }
         }
      }

      return true;
   }

   /**
    * Creates the drag source.
    * 
    * @param cellContainer the cell container
    */
   private void createDragSource(final GridCellContainer cellContainer) {
      DragSource source = new DragSource(cellContainer) {
         @Override
         protected void onDragStart(DNDEvent event) {
            cellContainer.clearArea(btnInArea);
            event.setData(cellContainer);
            event.getStatus().setStatus(true);
            event.getStatus().update("1 item selected");
         }
      };
      source.setGroup(Constants.CONTROL_DND_GROUP);
   }

   /**
    * Make cell container resizable.
    * 
    * @param cellWidth the cell width
    * @param cellHeight the cell height
    * @param cellContainer the cell container
    */
   private void makeCellContainerResizable(final int cellWidth, final int cellHeight, GridCellContainer cellContainer) {
      final Resizable resizable = new Resizable(cellContainer, RESIZABLE_HANDLES);
      resizable.addResizeListener(new ResizeListener() {

         @Override
         public void resizeEnd(ResizeEvent re) {

            GridCellContainer resizedCellContainer = (GridCellContainer) re.getComponent();
            int vSize = (int) Math.round((float) resizedCellContainer.getHeight() / cellHeight);
            int hSize = (int) Math.round((float) resizedCellContainer.getWidth() / cellWidth);
            resizedCellContainer.setHeight(vSize * cellHeight + vSize - 1);
            resizedCellContainer.setWidth(hSize * cellWidth + hSize - 1);
            resizedCellContainer.setCellSpan(hSize, vSize);
//            resizedCellContainer.adjust(); // Adjust the button's display.
            resizedCellContainer.fillArea(btnInArea);
         }

         @Override
         public void resizeStart(ResizeEvent re) {
            GridCellContainer resizeCellContainer = (GridCellContainer) re.getComponent();
//            if (resizeCellContainer.getWidth() / cellWidth == 2) { // In IE if the button width is 2 cell width, it can't be resize to 1 cell width.
//               resizeCellContainer.adjustCenter(cellWidth);
//            }
            int maxX = findMaxXWhenResize(resizeCellContainer, screen.getGrid());
            int maxY = findMaxYWhenResize(resizeCellContainer, screen.getGrid());
            resizable.setMaxWidth((maxX - resizeCellContainer.getCell().getPosX() + 1) * cellWidth);
            resizable.setMaxHeight((maxY - resizeCellContainer.getCell().getPosY() + 1) * cellHeight);
            resizeCellContainer.clearArea(btnInArea);
         }

      });
   }


}

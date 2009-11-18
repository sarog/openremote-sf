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
import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.client.utils.SelectedWidgetContainer;
import org.openremote.modeler.client.widget.control.ScreenControl;
import org.openremote.modeler.domain.Cell;
import org.openremote.modeler.domain.Grid;
import org.openremote.modeler.domain.UIScreen;
import org.openremote.modeler.domain.control.UIButton;
import org.openremote.modeler.domain.control.UIControl;
import org.openremote.modeler.domain.control.UISwitch;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.DragSource;
import com.extjs.gxt.ui.client.dnd.DropTarget;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.ResizeEvent;
import com.extjs.gxt.ui.client.event.ResizeListener;
import com.extjs.gxt.ui.client.fx.Resizable;
import com.extjs.gxt.ui.client.util.KeyNav;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.util.Rectangle;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlexTable;

/**
 * A layout container to display as grid, the inner is relative position.
 */
public class GridLayoutContainer extends LayoutContainer {

   /** The screen. */
   private UIScreen screen;

   /** The Constant POSITION. */
   private static final String POSITION = "position";

   /** The btn in area. */
   private boolean[][] btnInArea;
   
   
   private BoundsRecorder boundsRecorder = null;
   
   /**
    * Instantiates a new grid layout container.
    * 
    * @param screen the screen
    */
   public GridLayoutContainer(UIScreen screen) {
      this.screen = screen;
      Grid grid = screen.getGrid();
      btnInArea = new boolean[grid.getColumnCount()][grid.getRowCount()];
      addStyleName("screen-background");
      initCellInArea(grid);
      createGrid(grid);
      
      addTempDropTarget(this);
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
      int gridWidth = grid.getWidth();
      int gridHeight = grid.getHeight();
      setSize(gridWidth, gridHeight);
      setPosition(grid.getLeft(), grid.getTop());
      setBorders(false);
      FlexTable screenTable = new FlexTable();
      screenTable.setCellPadding(0);
      screenTable.setCellSpacing(0);
      screenTable.addStyleName("panel-table");
      screenTable.setPixelSize(gridWidth, gridHeight);
      add(screenTable);
      final int cellWidth = (gridWidth - (grid.getColumnCount() + 1)) / grid.getColumnCount();
      final int cellHeight = (gridHeight - (grid.getRowCount() + 1)) / grid.getRowCount();
      DNDListener dndListener = new DNDListener() {

         @SuppressWarnings("unchecked")
         public void dragDrop(DNDEvent e) {
            LayoutContainer targetCell = (LayoutContainer) e.getDropTarget().getComponent();
            Point targetPosition = (Point) targetCell.getData(POSITION);
            GridCellContainer cellContainer = new GridCellContainer();
            Object data = e.getData();
            if (data instanceof GridCellContainer) {
               GridCellContainer container = (GridCellContainer) data;
               cellContainer = cloneCellContainer(container);
               cellContainer.setBounds(boundsRecorder.getBounds());
               //cellContainer.add
               if (canDrop(targetPosition.x, targetPosition.y, cellContainer.getCell(), grid)) {
                  cellContainer.setCellPosition(targetPosition.x, targetPosition.y);
                  cellContainer.setPagePosition(targetCell.getAbsoluteLeft(), targetCell.getAbsoluteTop());
               }
               add(cellContainer);
               createDragSource(cellContainer);
            } else if (data instanceof List) {
               List<ModelData> models = (List<ModelData>) e.getData();
               if (models.size() > 0) {
                  BeanModel dataModel = models.get(0).get("model");
                  cellContainer = createNewCellContainer((UIControl) dataModel.getBean(), grid, cellWidth, cellHeight);
                  cellContainer.setCellSpan(1, 1);
                  cellContainer.setCellPosition(targetPosition.x, targetPosition.y);
                  cellContainer.setPagePosition(targetCell.getAbsoluteLeft(), targetCell.getAbsoluteTop());

                  add(cellContainer);
                  createDragSource(cellContainer);
               }
            }
            cellContainer.fillArea(btnInArea);
            SelectedWidgetContainer.setSelectWidget(cellContainer);
            makeCellContainerResizable(cellWidth, cellHeight, cellContainer);
            layout();

            super.dragDrop(e);
         }

      };
      for (int i = 0; i < grid.getRowCount(); i++) { // Initial the screen table, make it can be drop buttons.
         for (int j = 0; j < grid.getColumnCount(); j++) {
            LayoutContainer cell = new LayoutContainer();
            cell.setSize(cellWidth, cellHeight);
            screenTable.setWidget(i, j, cell);
            cell.setData(POSITION, new Point(j, i));
            ScreenDropTarget dropTarget = new ScreenDropTarget(cell);
            dropTarget.setGroup(Constants.CONTROL_DND_GROUP);
            dropTarget.setOverStyle("background-color");
            dropTarget.addDNDListener(dndListener);
         }
      }
      if (grid.getCells().size() > 0) { // If the Panel has closed, there may have some buttons on screen to be
                                        // rendered.
         List<Cell> cells = grid.getCells();
         for (Cell cell : cells) {
            GridCellContainer cellContainer = createCellContainer(grid, cell, (cellWidth + 1) * cell.getColspan() - 1,
                  (cellHeight + 1) * cell.getRowspan() - 1);
            makeCellContainerResizable(cellWidth, cellHeight, cellContainer);
            cellContainer.setPosition(cellWidth * cell.getPosX() + cell.getPosX() + 1, cellHeight * cell.getPosY()
                  + cell.getPosY() + 1);
            cellContainer.setName(cell.getUiControl().getName());
            add(cellContainer);
            cellContainer.setBorders(true);
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
   private GridCellContainer createCellContainer(final Grid grid, Cell cell, int cellWidth, int cellHeight) {
      final GridCellContainer cellContainer =  new GridCellContainer(cell, ScreenControl.build(cell.getUiControl())) {
         @Override
         public void onBrowserEvent(Event event) {
            if (event.getTypeInt() == Event.ONMOUSEDOWN) {
               SelectedWidgetContainer.setSelectWidget((GridCellContainer) this);
            }
            super.onBrowserEvent(event);
         }
      };
      new KeyNav<ComponentEvent>(){
         @Override
         public void onDelete(ComponentEvent ce) {
            super.onDelete(ce);
            MessageBox box = new MessageBox();
            box.setButtons(MessageBox.YESNO);
            box.setIcon(MessageBox.QUESTION);
            box.setTitle("Delete");
            box.setMessage("Are you sure you want to delete?");
            box.addCallback(new Listener<MessageBoxEvent>() {
                public void handleEvent(MessageBoxEvent be) {
                    if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                       grid.removeCell(cellContainer.getCell());
                       cellContainer.removeFromParent();
                       SelectedWidgetContainer.setSelectWidget(null);
                    }
                }
            });
            box.show();
         }
         
      }.bind(cellContainer);
      cellContainer.setSize(cellWidth, cellHeight);
      return cellContainer;
   }
   
   private GridCellContainer cloneCellContainer(GridCellContainer container) {
      Cell cell = container.getCell();
      final GridCellContainer cellContainer =  new GridCellContainer(cell, container.getScreenControl()) {
         @Override
         public void onBrowserEvent(Event event) {
            if (event.getTypeInt() == Event.ONMOUSEDOWN) {
               SelectedWidgetContainer.setSelectWidget((GridCellContainer) this);
            }
            super.onBrowserEvent(event);
         }
      };
      new KeyNav<ComponentEvent>(){
         @Override
         public void onDelete(ComponentEvent ce) {
            super.onDelete(ce);
            MessageBox box = new MessageBox();
            box.setButtons(MessageBox.YESNO);
            box.setIcon(MessageBox.QUESTION);
            box.setTitle("Delete");
            box.setMessage("Are you sure you want to delete?");
            box.addCallback(new Listener<MessageBoxEvent>() {
                public void handleEvent(MessageBoxEvent be) {
                    if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                       screen.getGrid().removeCell(cellContainer.getCell());
                       cellContainer.removeFromParent();
                       SelectedWidgetContainer.setSelectWidget(null);
                    }
                }
            });
            box.show();
         }
         
      }.bind(cellContainer);
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
         protected void onDragDrop(DNDEvent event) {
            super.onDragDrop(event);
         }

         @Override
         protected void onDragStart(DNDEvent event) {
            boundsRecorder = new BoundsRecorder(cellContainer);
            cellContainer.clearArea(btnInArea);
            event.setData(cellContainer);
            cellContainer.removeFromParent();
            event.getStatus().setStatus(true);
            event.getStatus().update("1 item selected");
         }
      };
      source.setGroup(Constants.CONTROL_DND_GROUP);
      source.enable();

      addTempDropTarget(cellContainer);
   }

   private void addTempDropTarget(final LayoutContainer container) {
      DropTarget target = new DropTarget(container);
      target.setGroup(Constants.CONTROL_DND_GROUP);
      target.addDNDListener(new DNDListener() {

         @Override
         public void dragDrop(DNDEvent e) {
            GridCellContainer cellContainer = new GridCellContainer();
            Object data = e.getData();
            if (data instanceof GridCellContainer) {
               GridCellContainer container = (GridCellContainer) data;
               cellContainer = cloneCellContainer(container);
               cellContainer.setBounds(boundsRecorder.getBounds());
               add(cellContainer);
               createDragSource(cellContainer);
               layout();
            }
         }

      });
   }

   /**
    * Make cell container resizable.
    * 
    * @param cellWidth the cell width
    * @param cellHeight the cell height
    * @param cellContainer the cell container
    */
   private void makeCellContainerResizable(final int cellWidth, final int cellHeight, GridCellContainer cellContainer) {
      final Resizable resizable = new Resizable(cellContainer, Constants.RESIZABLE_HANDLES);
      resizable.addResizeListener(new ResizeListener() {

         @Override
         public void resizeEnd(ResizeEvent re) {

            GridCellContainer resizedCellContainer = (GridCellContainer) re.getComponent();
            int vSize = (int) Math.round((float) resizedCellContainer.getHeight() / cellHeight);
            int hSize = (int) Math.round((float) resizedCellContainer.getWidth() / cellWidth);
            resizedCellContainer.setHeight(vSize * cellHeight + vSize - 1);
            resizedCellContainer.setWidth(hSize * cellWidth + hSize - 1);
            resizedCellContainer.setCellSpan(hSize, vSize);
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

   /**
    * Creates the new cell container according to uiControl type.
    * 
    */
   private GridCellContainer createNewCellContainer(UIControl uiControl, Grid grid, int cellWidth, int cellHeight) {
      Cell cell = new Cell(IDUtil.nextID());
      if(uiControl instanceof UIButton) {
         cell.setUiControl(new UIButton(IDUtil.nextID()));
      } else if(uiControl instanceof UISwitch) {
         cell.setUiControl(new UISwitch(IDUtil.nextID()));
      }
      grid.addCell(cell);
      return createCellContainer(grid, cell, cellWidth, cellHeight);
   }
   
   private class BoundsRecorder{
      int left = 0;
      int top = 0;
      int width = 0;
      int height = 0;
      
      BoundsRecorder(GridCellContainer container){
         Grid screenGrid = screen.getGrid();
         int cellWidth = screenGrid.getWidth()/screenGrid.getColumnCount();
         int cellHeight = screenGrid.getHeight()/screenGrid.getRowCount();
         this.left = container.getAbsoluteLeft();
         this.top = container.getAbsoluteTop();
         Cell cell = container.getCell();
         this.width = cell.getColspan()*cellWidth+1;
         this.height= cell.getRowspan()*cellHeight+1;
      }
      
      public Rectangle getBounds(){
         return new Rectangle(left,top,width,height);
      }
   }
}

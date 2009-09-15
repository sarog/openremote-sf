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
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroRef;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.UIButton;
import org.openremote.modeler.domain.UICommand;
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
 * The Class ScreenPanel.
 */
public class ScreenPanel extends LayoutContainer {

   /** The screen. */
   private Screen screen;

   /** The Constant POSITION. */
   private static final String POSITION = "position";

   /** The Constant RESIZABLE_HANDLES. */
   private static final String RESIZABLE_HANDLES = "e s"; // Make resizable in east and south direction.
   
   /** The button in area. Record the buttons position and hold area in screen table. */
   private boolean[][] btnInArea;
   
   /** The selected button. */
   private ScreenButton selectedButton;
   
   private TouchPanelDefinition panelDefinition;

   /**
    * Instantiates a new screen panel.
    * 
    * @param s the s
    */
   public ScreenPanel(Screen screen, TouchPanelDefinition panelDefinition) {
      this.screen = screen;
      this.panelDefinition = panelDefinition;
      btnInArea = new boolean[screen.getColumnCount()][screen.getRowCount()];
      addStyleName("screen-background");
      setSize(panelDefinition.getWidth(), panelDefinition.getHeight());
      setStyleAttribute("backgroundImage", "url(" + panelDefinition.getBgImage() + ")");
      setStyleAttribute("paddingLeft", String.valueOf(panelDefinition.getPaddingLeft()));
      setStyleAttribute("paddingTop", String.valueOf(panelDefinition.getPaddingTop()));
      initBtnInArea();
      createTable();

   }

   /**
    * Make sure btnInArea to be initial.
    */
   private void initBtnInArea() {
      for (int x = 0; x < screen.getColumnCount(); x++) {
         for (int y = 0; y < screen.getRowCount(); y++) {
            btnInArea[x][y] = false;
         }
      }
   }

   /**
    * Creates the screen table.
    */
   private void createTable() {
      int gridWidth = panelDefinition.getGrid().getWidth();
      int gridHeight = panelDefinition.getGrid().getHeight();
      FlexTable screenTable = new FlexTable();
      screenTable.setCellPadding(0);
      screenTable.setCellSpacing(0);
      screenTable.addStyleName("panel-table");
      screenTable.setPixelSize(gridWidth, gridHeight);
      add(screenTable);
      final int cellWidth = (int) Math.round((float) gridWidth / screen.getColumnCount());
      final int cellHeight = (int) Math.round((float) gridHeight / screen.getRowCount());
      DNDListener dndListener = new DNDListener() {
         @SuppressWarnings("unchecked")
         public void dragDrop(DNDEvent e) {
            LayoutContainer targetCell = (LayoutContainer) e.getDropTarget().getComponent();
            Position targetPosition = (Position) targetCell.getData(POSITION);
            ScreenButton screenBtn = new ScreenButton();

            Object data = e.getData();
            if (data instanceof ScreenButton) {
               screenBtn = (ScreenButton) data;
               if (canDrop(targetPosition.getPosX(), targetPosition.getPosY(), screenBtn, screen)) {
                  screenBtn.setButtonPosition(targetPosition);
                  screenBtn.setPagePosition(targetCell.getAbsoluteLeft(), targetCell.getAbsoluteTop());
               } else {
                  screenBtn.setPagePosition(screenBtn.getAbsoluteLeft(), screenBtn.getAbsoluteTop());
               }
               screenBtn.addStyleName("button-border");
            } else if (data instanceof List) {
               List<ModelData> models = (List<ModelData>) e.getData();
               if (models.size() > 0) {
                  BeanModel dataModel = models.get(0).get("model");
                  UIButton button = new UIButton(IDUtil.nextID());
                  button.setLabel(dataModel.get("name").toString());
                  button.setWidth(1);
                  button.setHeight(1);
                  UICommand uiCommand = null;
                  if (dataModel.getBean() instanceof DeviceCommand) {
                     uiCommand = new DeviceCommandRef((DeviceCommand) dataModel.getBean());
                  } else if (dataModel.getBean() instanceof DeviceMacro) {
                     uiCommand = new DeviceMacroRef((DeviceMacro) dataModel.getBean());
                  }
                  button.setUiCommand(uiCommand);
                  button.setPosition(targetPosition);
                  screen.addButton(button);
                  screenBtn = createScreenButton(cellWidth, cellHeight, button);
                  createDragSource(screenBtn);
                  add(screenBtn);
                  screenBtn.setPagePosition(targetCell.getAbsoluteLeft(), targetCell.getAbsoluteTop());
               }
            }
            screenBtn.fillArea(btnInArea);
            if (selectedButton != null) {
               selectedButton.removeStyleName("button-border");
            }
            screenBtn.addStyleName("button-border");
            selectedButton = screenBtn;
            makeButtonResizable(cellWidth, cellHeight, screenBtn);
            layout();
            super.dragDrop(e);
         }
      };
      for (int i = 0; i < screen.getRowCount(); i++) {  // Initial the screen table, make it can be drop buttons.  
         for (int j = 0; j < screen.getColumnCount(); j++) {
            LayoutContainer cell = new LayoutContainer();
            cell.setSize(cellWidth, cellHeight);
            screenTable.setWidget(i, j, cell);
            cell.setData(POSITION, new Position(j, i));
            ScreenDropTarget dropTarget = new ScreenDropTarget(cell);
            dropTarget.setGroup(Constants.BUTTON_DND_GROUP);
            dropTarget.setOverStyle("background-color");
            dropTarget.addDNDListener(dndListener);
         }
      }
      if (screen.getButtons().size() > 0) {  // If the Panel has closed, there may have some buttons on screen to be rendered. 
         List<UIButton> buttons = screen.getButtons();
         for (UIButton button : buttons) {
            Position pos = button.getPosition();
            ScreenButton screenBtn = createScreenButton((cellWidth + 1) * button.getWidth() - 1, (cellHeight + 1) * button.getHeight() -1, button);
            makeButtonResizable(cellWidth, cellHeight, screenBtn);
            screenBtn.setPosition(panelDefinition.getPaddingLeft() + cellWidth * pos.getPosX() + pos.getPosX() + 1, panelDefinition.getPaddingTop() + cellHeight
                  * pos.getPosY() + pos.getPosY() + 1);
            if(button.getIcon() != null && !"".equals(button.getIcon())){
               screenBtn.setIcon(button.getIcon());
            }
            add(screenBtn);
            screenBtn.fillArea(btnInArea);
            createDragSource(screenBtn);
            layout();

         }
      }

   }

   /**
    * Find max x position when resize.
    * 
    * @param screenButton the screen button
    * @param screen the screen
    * 
    * @return the max x position
    */
   private int findMaxXWhenResize(ScreenButton screenButton, Screen screen) {
      Position btnPosition = screenButton.getButtonPosition();
      int maxX = btnPosition.getPosX();
      for (; maxX < screen.getColumnCount() - 1; maxX++) {
         for (int tmpY = btnPosition.getPosY(); ((tmpY < screen.getRowCount()) && (tmpY < btnPosition.getPosY()
               + screenButton.getButtonHeight())); tmpY++) {
            if ((btnInArea[maxX + 1][tmpY])
                  && ((maxX + 1) > (btnPosition.getPosX() + screenButton.getButtonWidth() - 1))) {
               return maxX;
            }
         }
      }
      return maxX;
   }

   /**
    * Find max y position when resize.
    * 
    * @param screenButton the screen button
    * @param screen the screen
    * 
    * @return the max y position
    */
   private int findMaxYWhenResize(ScreenButton screenButton, Screen screen) {
      Position btnPosition = screenButton.getButtonPosition();
      int maxY = btnPosition.getPosY();
      for (; maxY < screen.getRowCount() - 1; maxY++) {
         for (int tmpX = btnPosition.getPosX(); ((tmpX < screen.getColumnCount()) && (tmpX < btnPosition.getPosX()
               + screenButton.getButtonWidth())); tmpX++) {
            if ((btnInArea[tmpX][maxY + 1])
                  && ((maxY + 1) > (btnPosition.getPosY() + screenButton.getButtonHeight() - 1))) {
               return maxY;
            }
         }
      }
      return maxY;
   }

   /**
    * Calculate the position can drop the screenButton.
    * 
    * @param x the x
    * @param y the y
    * @param screenButton the screen button
    * @param screen the screen
    * 
    * @return true, if successful
    */
   private boolean canDrop(int x, int y, ScreenButton screenButton, Screen screen) {
      for (int tmpX = x; tmpX < x + screenButton.getButtonWidth(); tmpX++) {
         for (int tmpY = y; tmpY < y + screenButton.getButtonHeight(); tmpY++) {
            if (tmpX > screen.getColumnCount() - 1 || tmpY > screen.getRowCount() - 1) {
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
    * @param screenBtn the screen button
    */
   private void createDragSource(final ScreenButton screenBtn) {
      DragSource source = new DragSource(screenBtn) {
         @Override
         protected void onDragStart(DNDEvent event) {
            screenBtn.clearArea(btnInArea);
            event.setData(screenBtn);
            event.getStatus().setStatus(true);
            event.getStatus().update("1 item selected");
         }
      };
      source.setGroup(Constants.BUTTON_DND_GROUP);
   }

   /**
    * Creates the screen button.
    * 
    * @param width the button width
    * @param height the button height
    * @param button the button
    * 
    * @return the screen button
    */
   private ScreenButton createScreenButton(final int width, final int height, UIButton button) {
      ScreenButton screenBtn = new ScreenButton(button, width, height) {
         @Override
         public void onBrowserEvent(Event event) {
            if (event.getTypeInt() == Event.ONMOUSEDOWN) {
               this.addStyleName("button-border");
               if (selectedButton != null && (ScreenButton) this != selectedButton) {
                  selectedButton.removeStyleName("button-border");
               }
               selectedButton = (ScreenButton) this;
            }
            super.onBrowserEvent(event);
         }
      };
      return screenBtn;
   }

   /**
    * Gets the selected button.
    * 
    * @return the selected button
    */
   public ScreenButton getSelectedButton() {
      return selectedButton;
   }

   /**
    * Delete.
    * 
    * @param screenButton the screen button
    */
   public void delete(ScreenButton screenButton) {
      screenButton.clearArea(btnInArea);
      remove(screenButton);
   }

   /**
    * Make the button can be resizable.
    * @param cellWidth
    * @param cellHeight
    * @param screenBtn
    */
   private void makeButtonResizable(final int cellWidth, final int cellHeight, ScreenButton screenBtn) {
      final Resizable resizable = new Resizable(screenBtn, RESIZABLE_HANDLES);
      resizable.addResizeListener(new ResizeListener() {

         @Override
         public void resizeEnd(ResizeEvent re) {

            ScreenButton resizedBtn = (ScreenButton) re.getComponent();
            int vSize = (int) Math.round((float) resizedBtn.getHeight() / cellHeight);
            int hSize = (int) Math.round((float) resizedBtn.getWidth() / cellWidth);
            resizedBtn.setHeight(vSize * cellHeight + vSize - 1);
            resizedBtn.setWidth(hSize * cellWidth + hSize - 1);
            resizedBtn.setButtonWidth(hSize);
            resizedBtn.setButtonHeight(vSize);
            resizedBtn.adjust(); // Adjust the button's display.
            resizedBtn.fillArea(btnInArea);
         }

         @Override
         public void resizeStart(ResizeEvent re) {
            ScreenButton resizeBtn = (ScreenButton) re.getComponent();
            if(resizeBtn.getWidth()/cellWidth == 2){  // In IE if the button width is 2 cell width, it can't be resize to 1 cell width.
               resizeBtn.adjustCenter(cellWidth);
            }
            int maxX = findMaxXWhenResize(resizeBtn, screen);
            int maxY = findMaxYWhenResize(resizeBtn, screen);
            resizable.setMaxWidth((maxX - resizeBtn.getButtonPosition().getPosX() + 1) * cellWidth);
            resizable.setMaxHeight((maxY - resizeBtn.getButtonPosition().getPosY() + 1) * cellHeight);
            resizeBtn.clearArea(btnInArea);
         }

      });
   }

}

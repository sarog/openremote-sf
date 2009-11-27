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

import org.openremote.modeler.client.widget.component.ScreenComponent;
import org.openremote.modeler.domain.Cell;

import com.extjs.gxt.ui.client.widget.form.FormPanel;

/**
 * The Class GridCellContainer.
 */
public class GridCellContainer extends ComponentContainer implements PropertyPanelBuilder{

   private Cell cell;
   private ScreenComponent screenControl;
   
   public GridCellContainer(ScreenCanvas canvas) {
      super(canvas);
   }
   public GridCellContainer(ScreenCanvas canvas,Cell cell, ScreenComponent screenControl) {
      super(canvas);
      this.cell = cell;
      this.screenControl = screenControl;
      addStyleName("cursor-move");
      setStyleAttribute("position", "absolute");
      add(screenControl);
   }

   public Cell getCell() {
      return cell;
   }

   public ScreenComponent getScreenControl() {
      return screenControl;
   }
   
   public void setCellPosition(int posX, int posY) {
      cell.setPosX(posX);
      cell.setPosY(posY);
   }

   public void setCellSpan(int colspan, int rowspan) {
      cell.setColspan(colspan);
      cell.setRowspan(rowspan);
   }
   @Override
   public void setSize(int width, int height) {
      super.setSize(width, height);
      screenControl.setSize(width - 2, height - 2);
   }
   
   /**
    * Fill the cell holds area.
    */
   public void fillArea(boolean[][] btnArea) {
      for (int i = 0; i < cell.getColspan(); i++) {
         int x = cell.getPosX() + i;
         for (int j = 0; j < cell.getRowspan(); j++) {
            int y = cell.getPosY() + j;
            btnArea[x][y] = true;
         }
      }
   }

   /**
    * Clear the cell holds area.
    */
   public void clearArea(boolean[][] btnArea) {
      for (int i = 0; i < cell.getColspan(); i++) {
         int x = cell.getPosX() + i;
         for (int j = 0; j < cell.getRowspan(); j++) {
            int y = cell.getPosY() + j;
            btnArea[x][y] = false;
         }
      }
   }
   
   public void setName(String name) {
      screenControl.setName(name);
   }
   @Override
   public FormPanel buildPropertiesForm() {
     return this.screenControl.buildPropertiesForm();
   }
}

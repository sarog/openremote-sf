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
package org.openremote.modeler.domain.component;

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.domain.Cell;
/**
 * 
 * @author Javen
 *
 */
@SuppressWarnings("serial")
public class UIGrid extends UIComponent {

   private int left;
   private int top;
   private int width;
   private int Height;
   private int rowCount;
   private int columnCount;
   private List<Cell> cells = new ArrayList<Cell>();
   
   
   public UIGrid() {
      super();
   }

   public UIGrid(int left, int top, int width, int height, int rowCount, int columnCount) {
      super();
      Height = height;
      this.columnCount = columnCount;
      this.left = left;
      this.rowCount = rowCount;
      this.top = top;
      this.width = width;
   }

   public int getLeft() {
      return left;
   }

   public void setLeft(int left) {
      this.left = left;
   }

   public int getTop() {
      return top;
   }

   public void setTop(int top) {
      this.top = top;
   }

   public int getWidth() {
      return width;
   }

   public void setWidth(int width) {
      this.width = width;
   }

   public int getHeight() {
      return Height;
   }

   public void setHeight(int height) {
      Height = height;
   }

   public int getRowCount() {
      return rowCount;
   }

   public void setRowCount(int rowCount) {
      this.rowCount = rowCount;
   }

   public int getColumnCount() {
      return columnCount;
   }

   public void setColumnCount(int columnCount) {
      this.columnCount = columnCount;
   }

   public List<Cell> getCells() {
      return cells;
   }

   public void setCells(List<Cell> cells) {
      this.cells = cells;
   }

   public void addCell(Cell cell) {
      cells.add(cell);
   }
   
   public void removeCell(Cell cell) {
      cells.remove(cell);
   }
   
   @Override
   public String getPanelXml() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void transImagePathToRelative(String relativeSessionFolderPath) {
      // TODO Auto-generated method stub

   }

   @Override
   public String getName() {
     return "grid";
   }
   
}

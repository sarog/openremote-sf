/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.web.console.entity;

import java.util.ArrayList;
import java.util.HashSet;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The subclass of LayoutContainer which include grid cells.
 * It contains column size and row size.
 * 
 */
@SuppressWarnings("serial")
public class GridLayoutContainer extends LayoutContainer {

   private int rows;
   private int cols;
   private ArrayList<GridCell> cells;
   
   public GridLayoutContainer() {
   }
   
   public int getRows() {
      return rows;
   }
   
   public int getCols() {
      return cols;
   }
   
   public ArrayList<GridCell> getCells() {
      return cells;
   }
}

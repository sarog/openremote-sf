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

import org.openremote.web.console.entity.component.Component;

/**
 * The grid cell include a component, have position and span in grid.
 */
@SuppressWarnings("serial")
public class GridCell extends Entity {

   private int x;
   private int y;
   private int rowspan = 1;
   private int colspan = 1;
   private Component component;
   
   public GridCell() {
   }
   
   public int getX() {
      return x;
   }
   public int getY() {
      return y;
   }
   public int getRowspan() {
      return rowspan;
   }
   public int getColspan() {
      return colspan;
   }
   public Component getComponent() {
      return component;
   }   
}

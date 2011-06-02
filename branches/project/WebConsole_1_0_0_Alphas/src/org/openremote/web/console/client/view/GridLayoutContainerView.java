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
package org.openremote.web.console.client.view;

import org.openremote.web.console.domain.GridCell;
import org.openremote.web.console.domain.GridLayoutContainer;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * Wraps grid cells, and display in a absolute position.
 */
public class GridLayoutContainerView extends LayoutContainer {

   public GridLayoutContainerView(GridLayoutContainer gridLayoutContainer) {
      setStyleAttribute("position", "absolute");
      setSize(gridLayoutContainer.getWidth(), gridLayoutContainer.getHeight());
      setPosition(gridLayoutContainer.getLeft(), gridLayoutContainer.getTop());
      init(gridLayoutContainer);
   }
   
   private void init(GridLayoutContainer gridLayoutContainer) {
      int cellWidth = gridLayoutContainer.getWidth() / gridLayoutContainer.getCols();
      int cellHeight = gridLayoutContainer.getHeight() / gridLayoutContainer.getRows();
      for (GridCell gridCell: gridLayoutContainer.getCells()) {
         add(new GridCellContainerView(cellWidth, cellHeight, gridCell));
      }
   }
}

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

import org.openremote.modeler.domain.component.UIGrid;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

public class GridContainer extends LayoutContainer {

   private GridLayoutContainer gridlayoutContainer = null;

   public GridContainer(GridLayoutContainer gridlayoutContainer) {
      this.gridlayoutContainer = gridlayoutContainer;
      setSize(16, 16);
      setStyleAttribute("position", "absolute");
      LayoutContainer handle = new LayoutContainer();
      handle.setSize(16, 16);
//      handle.setStyleAttribute("background-color", "red");
      handle.addStyleName("move-cursor");
      add(handle);
      gridlayoutContainer.setPosition(16, 16);
      add(gridlayoutContainer);
   }

   public GridLayoutContainer getGridlayoutContainer() {
      return gridlayoutContainer;
   }

   @Override
   public void setPosition(int left, int top) {
      if(gridlayoutContainer != null) {
         UIGrid grid = gridlayoutContainer.getGrid();
         grid.setLeft(left + 16);
         grid.setTop(top + 16);
      }
      super.setPosition(left, top);
   }
   
   

}

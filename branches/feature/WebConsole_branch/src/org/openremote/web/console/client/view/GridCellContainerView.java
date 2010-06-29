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

import org.openremote.web.console.domain.Button;
import org.openremote.web.console.domain.Component;
import org.openremote.web.console.domain.GridCell;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

/**
 * The Class GridCellContainerView.
 */
public class GridCellContainerView extends LayoutContainer {

   public GridCellContainerView(int cellWidth, int cellHeight, GridCell gridCell) {
      setStyleAttribute("position", "absolute");
      setLayout(new FitLayout());
      setPosition(cellWidth * gridCell.getX(), cellHeight * gridCell.getY());
      setSize(cellWidth * gridCell.getColspan(), cellHeight * gridCell.getRowspan());
      Component component = gridCell.getComponent();
      if (component instanceof Button) {
         // temp display button.
         Button uiButton = (Button)component;
         com.extjs.gxt.ui.client.widget.button.Button btn = new com.extjs.gxt.ui.client.widget.button.Button();
         btn.setText(uiButton.getName());
         add(btn);
      }
   }
}

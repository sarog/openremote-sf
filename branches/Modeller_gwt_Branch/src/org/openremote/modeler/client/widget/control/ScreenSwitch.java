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
package org.openremote.modeler.client.widget.control;

import com.extjs.gxt.ui.client.widget.Text;
import com.google.gwt.user.client.ui.FlexTable;

/**
 * ScreenSwitch is the switch widget in screen.
 */
public class ScreenSwitch extends ScreenControl {
   private FlexTable btnTable = new FlexTable();
   private Text center = new Text("Switch");
   /**
    * Instantiates a new screen button.
    */
   public ScreenSwitch() {
      initial();
   }


   /**
    * Initial the switch as a style box.
    */
   private void initial() {
      addStyleName("screen-btn");
      
      btnTable.addStyleName("screen-btn-cont");
      btnTable.setCellPadding(0);
      btnTable.setCellSpacing(0);
      
      btnTable.setWidget(0, 0, null);
      btnTable.setWidget(0, 1, null);
      btnTable.setWidget(0, 2, null);

      btnTable.setWidget(1, 0, null);
      btnTable.setWidget(1, 1, center);
      btnTable.setWidget(1, 2, null);

      btnTable.setWidget(2, 0, null);
      btnTable.setWidget(2, 1, null);
      btnTable.setWidget(2, 2, null);

      btnTable.getCellFormatter().addStyleName(0, 0, "tl-c");
      btnTable.getCellFormatter().addStyleName(0, 1, "top");
      btnTable.getCellFormatter().addStyleName(0, 2, "tr-c");

      btnTable.getCellFormatter().addStyleName(1, 0, "ml");
      btnTable.getCellFormatter().addStyleName(1, 1, "middle");
      btnTable.getCellFormatter().addStyleName(1, 2, "mr");

      btnTable.getCellFormatter().addStyleName(2, 0, "bl-c");
      btnTable.getCellFormatter().addStyleName(2, 1, "bottom");
      btnTable.getCellFormatter().addStyleName(2, 2, "br-c");

      btnTable.getRowFormatter().addStyleName(0, "screen-top");
      btnTable.getRowFormatter().addStyleName(2, "screen-bottom");

      add(btnTable);
   }
   
   @Override
   public String getName() {
      return center.getText();
   }
}

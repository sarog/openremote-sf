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
package org.openremote.modeler.client.view;

import org.openremote.modeler.client.widget.DevicePanel;
import org.openremote.modeler.client.widget.MacroPanel;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;


/**
 * The Class BuildingModelerView.
 */
public class BuildingModelerView extends TabItem implements View {

   /* (non-Javadoc)
    * @see org.openremote.modeler.client.view.View#initialize()
    */
   public void initialize() {
      setText("Building Modeler");
      setLayout(new BorderLayout());
//      createNorth();
      createWest();
      createCenter();
   }
   
   /**
    * Creates the north.
    */
   private void createNorth(){
      ToolBar north = new ToolBar();
      BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 200);
      northData.setCollapsible(false);
      northData.setMargins(new Margins(0, 2, 0, 2));
      add(north, northData);
   }
   
   /**
    * Creates the west.
    */
   private void createWest(){
      ContentPanel west = new ContentPanel();
      BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST,200);
      westData.setSplit(true);
      westData.setCollapsible(true);
      west.setLayout(new AccordionLayout());
      west.setBodyBorder(false);
      west.setHeading("Explorer");
      west.add(new DevicePanel());
      west.add(new MacroPanel());
  
      westData.setMargins(new Margins(2));
      add(west,westData);
   }
   
   /**
    * Creates the center.
    */
   private void createCenter(){
      ContentPanel center = new ContentPanel();
      BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
      centerData.setMargins(new Margins(2));

      add(center,centerData);
   }
}

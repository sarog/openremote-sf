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

import org.openremote.modeler.domain.UIScreen;
import org.openremote.modeler.touchpanel.TouchPanelDefinition;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;

/**
 * The Class ScreenTabItem contain a screenPanel.
 */
public class ScreenTabItem extends TabItem {


   /** The screen. */
   private UIScreen screen;

   private ScreenCanvas screenCanvas;
   
   /**
    * Instantiates a new screen panel.
    * 
    * @param screen
    *           the s
    */
   public ScreenTabItem(UIScreen screen) {
      this.screen = screen;
      setText(screen.getName());
      setClosable(true);
      setLayout(new FlowLayout());
      setStyleAttribute("overflowY", "auto");
      addScreenContainer();
   }

   /**
    * Adds the screen container.
    */
   private void addScreenContainer() {
      LayoutContainer screenContainer = new LayoutContainer();
      TouchPanelDefinition touchPanelDefinition = screen.getTouchPanelDefinition();
      screenContainer.addStyleName("screen-background");
      screenContainer.setSize(touchPanelDefinition.getWidth(), touchPanelDefinition.getHeight());
      screenContainer.setStyleAttribute("backgroundImage", "url(" + touchPanelDefinition.getBgImage() + ")");
      screenContainer.setStyleAttribute("paddingLeft", String.valueOf(touchPanelDefinition.getPaddingLeft()));
      screenContainer.setStyleAttribute("paddingTop", String.valueOf(touchPanelDefinition.getPaddingTop()));
      screenCanvas = new ScreenCanvas(screen);
      screenContainer.add(screenCanvas);
      screenContainer.setBorders(false);
      add(screenContainer);
   }
   
   /**
    * Gets the screen.
    * 
    * @return the screen
    */
   public UIScreen getScreen() {
      return screen;
   }
   
   public ScreenCanvas getScreenCanvas() {
      return screenCanvas;
   }

}

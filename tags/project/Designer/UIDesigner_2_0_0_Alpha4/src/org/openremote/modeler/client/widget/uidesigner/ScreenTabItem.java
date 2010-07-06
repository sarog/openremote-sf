/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2009, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.openremote.modeler.client.widget.uidesigner;

import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.touchpanel.TouchPanelDefinition;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;

/**
 * The Class ScreenTabItem contain a screenPanel.
 */
public class ScreenTabItem extends TabItem {

   /** The screen. */
   private Screen screen;

   private LayoutContainer screenContainer;
   
   private ScreenCanvas screenCanvas;

   /**
    * Instantiates a new screen panel.
    * 
    * @param screen
    *           the s
    */
   public ScreenTabItem(Screen screen) {
      this.screen = screen;
      setText(screen.getName());
      setClosable(true);
      setLayout(new FlowLayout());
      setStyleAttribute("overflow", "auto");
      addScreenContainer();
   }

   /**
    * Adds the screen container.
    */
   private void addScreenContainer() {
      screenContainer = new LayoutContainer();
      screenContainer.addStyleName("screen-background");
      updateTouchPanel();
      screenCanvas = new ScreenCanvas(screen);
      screenContainer.add(screenCanvas);
      screenContainer.setBorders(false);
      add(screenContainer);
   }

   /**
    * @param screenContainer
    * @param touchPanelDefinition
    */
   public void updateTouchPanel() {
      TouchPanelDefinition touchPanelDefinition = screen.getTouchPanelDefinition();
      if (touchPanelDefinition.getWidth() > 0 && touchPanelDefinition.getHeight() > 0) {
         screenContainer.setSize(touchPanelDefinition.getWidth(), touchPanelDefinition.getHeight());
      }
      if (touchPanelDefinition.getBgImage() != null) {
         screenContainer.setStyleAttribute("backgroundImage", "url(" + touchPanelDefinition.getBgImage() + ")");
      }
      screenContainer.setStyleAttribute("paddingLeft", String.valueOf(touchPanelDefinition.getPaddingLeft()));
      screenContainer.setStyleAttribute("paddingTop", String.valueOf(touchPanelDefinition.getPaddingTop()));
   }

   /**
    * Gets the screen.
    * 
    * @return the screen
    */
   public Screen getScreen() {
      return screen;
   }

   public ScreenCanvas getScreenCanvas() {
      return screenCanvas;
   }

}

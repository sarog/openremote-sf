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

import java.util.ArrayList;

import org.openremote.web.console.client.utils.ClientDataBase;
import org.openremote.web.console.domain.AbsoluteLayoutContainer;
import org.openremote.web.console.domain.Background;
import org.openremote.web.console.domain.GridLayoutContainer;
import org.openremote.web.console.domain.LayoutContainer;
import org.openremote.web.console.domain.Screen;

import com.google.gwt.http.client.URL;

/**
 * The Class ScreenView for init screen components.
 */
public class ScreenView extends com.extjs.gxt.ui.client.widget.LayoutContainer {

   public ScreenView(Screen screen) {
      setStyleAttribute("backgroundColor", "white");
      setStyleAttribute("position", "relative");
      setBorders(true);
      init(screen);
   }
   
   private void init(Screen screen) {
      ArrayList<LayoutContainer> layouts = screen.getLayouts();
      if (layouts.size() > 0) {
         for (LayoutContainer layoutContainer : layouts) {
            if (layoutContainer instanceof AbsoluteLayoutContainer) {
               add(new AbsolutLayoutContainerView((AbsoluteLayoutContainer)layoutContainer));
            } else if (layoutContainer instanceof GridLayoutContainer) {
               add(new GridLayoutContainerView((GridLayoutContainer)layoutContainer));
            }
         }
      }
      
      if (screen.getBackground() != null) {
         addBackground(screen.getBackground());
      }
   }
   
   private void addBackground(Background background) {
      String url = ClientDataBase.appSetting.getResourceRootPath()
            + URL.encode(background.getBackgroundImage().getSrc());
      setStyleAttribute("backgroundImage", "url(" + url + ")");
      setStyleAttribute("backgroundRepeat", "no-repeat");
      setStyleAttribute("overflow", "hidden");
      if (background.isFillScreen()) {
         setStyleAttribute("backgroundPosition", "top left");
      } else if (background.isBackgroundImageAbsolutePosition()) {
         setStyleAttribute("backgroundPosition", background.getBackgroundImageAbsolutePositionLeft() + " "
               + background.getBackgroundImageAbsolutePositionTop());
      } else {
         setStyleAttribute("backgroundPosition", Background.getRelativeMap().get(
               background.getBackgroundImageRelativePosition()));
      }
   }

}

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
package org.openremote.web.console.client.widget;

import org.openremote.web.console.domain.Component;
import org.openremote.web.console.domain.Image;
import org.openremote.web.console.domain.Label;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * The class ScreenComponent is the superclass of screen components.
 */
public class ScreenComponent extends LayoutContainer {

   private Component component;
   
   /**
    * Builds the with component.
    * 
    * @param component the component
    * 
    * @return the screen component
    */
   public static ScreenComponent buildWithComponent(Component component) {
      ScreenComponent screenComponent = null;
      if (component instanceof Label) {
         screenComponent = new ScreenLabel((Label) component);
      } else if (component instanceof Image) {
         screenComponent = new ScreenImage((Image) component);
      } else {
         screenComponent = ScreenControl.buildWithControl(component);
      }
      return screenComponent;
   }
   
   public Component getComponent() {
      return component;
   }

   public void setComponent(Component component) {
      this.component = component;
   }
   
}

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

import org.openremote.modeler.client.widget.control.ScreenControl;
import org.openremote.modeler.domain.Absolute;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * A layout container act as absolute position in screen canvas.
 */
public class AbsoluteLayoutContainer extends LayoutContainer {

   private Absolute absolute;
   private ScreenControl screenControl;
   /**
    * Instantiates a new absolute layout container.
    */
   public AbsoluteLayoutContainer(Absolute absolute, ScreenControl screenControl) {
      addStyleName("cursor-move");
      setStyleAttribute("position", "absolute");
      this.absolute = absolute;
      this.screenControl = screenControl;
      add(screenControl);
   }

   public Absolute getAbsolute() {
      return absolute;
   }

   public ScreenControl getScreenControl() {
      return screenControl;
   }

   @Override
   public void setSize(int width, int height) {
      super.setSize(width, height);
      absolute.setSize(width, height);
      screenControl.setSize(width-2, height-2);
   }

   @Override
   public void setPosition(int left, int top) {
      super.setPosition(left, top);
      absolute.setPosition(left, top);
   }
}

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

import org.openremote.modeler.domain.control.UIControl;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * The Class AbsoluteLayoutContainer.
 */
public class AbsoluteLayoutContainer extends LayoutContainer {

   /** The ui control. */
   private UIControl uiControl;

   /**
    * Instantiates a new absolute layout container.
    */
   public AbsoluteLayoutContainer() {
      addStyleName("cursor-move");
   }
   
   /**
    * Gets the ui control.
    * 
    * @return the ui control
    */
   public UIControl getUiControl() {
      return uiControl;
   }

   /**
    * Sets the ui control.
    * 
    * @param uiControl the new ui control
    */
   public void setUiControl(UIControl uiControl) {
      this.uiControl = uiControl;
   }
   
}

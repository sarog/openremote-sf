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

import org.openremote.modeler.domain.control.UIButton;
import org.openremote.modeler.domain.control.UIControl;
import org.openremote.modeler.domain.control.UISwitch;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.FormPanel;

/**
 * ScreenControl as the component's super class.
 */
public abstract class ScreenControl extends LayoutContainer {
   /**
    * Sets the display name.
    */
   public abstract void setName(String name);
   
   public abstract String getName();
   
   /**
    * Builds the ScreenControl according to uiControl type.
    */
   public static ScreenControl build(UIControl uiControl) {
      if (uiControl instanceof UIButton) {
         return new ScreenButton((UIButton)uiControl);
      } else if(uiControl instanceof UISwitch) {
         return new ScreenSwitch((UISwitch)uiControl);
      }
      return null;
   }
   
   public abstract FormPanel buildPropertiesForm();
}

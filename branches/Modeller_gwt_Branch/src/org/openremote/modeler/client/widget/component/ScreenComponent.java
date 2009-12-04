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
package org.openremote.modeler.client.widget.component;

import org.openremote.modeler.client.widget.uidesigner.ComponentContainer;
import org.openremote.modeler.client.widget.uidesigner.PropertyPanelBuilder;
import org.openremote.modeler.client.widget.uidesigner.ScreenCanvas;
import org.openremote.modeler.domain.component.UIButton;
import org.openremote.modeler.domain.component.UIComponent;
import org.openremote.modeler.domain.component.UISwitch;

import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.google.gwt.user.client.Element;

/**
 * ScreenControl as the component's super class.
 */
public abstract class ScreenComponent extends ComponentContainer implements PropertyPanelBuilder{
   public ScreenComponent(ScreenCanvas screenCanvas){
	   super(screenCanvas);
   }
	/**
    * Sets the display name.
    */
   public abstract void setName(String name);
   
   public abstract String getName();
   
   /**
    * Builds the ScreenControl according to uiControl type.
    */
   public static ScreenComponent build(ScreenCanvas canvas,UIComponent uiComponent) {
      if (uiComponent instanceof UIButton) {
         return new ScreenButton(canvas,(UIButton)uiComponent);
      } else if(uiComponent instanceof UISwitch) {
         return new ScreenSwitch(canvas,(UISwitch)uiComponent);
      } 
      return null;
   }
   @Override
   public FormPanel buildPropertiesForm() {
      return null;
   }
   
}

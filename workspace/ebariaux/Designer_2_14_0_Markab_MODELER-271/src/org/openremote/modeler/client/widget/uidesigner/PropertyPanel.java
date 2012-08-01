/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
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

import java.util.List;

import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.client.widget.propertyform.PropertyForm;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

/**
 * A panel for display and edit different component's properties.
 */
public class PropertyPanel extends ContentPanel {

   private PropertyForm currentPropertyForm;
   
   public PropertyPanel(WidgetSelectionUtil widgetSelectionUtil) {
      setBorders(false);
      setFooter(false);
      setBodyStyleName("zero-padding");
      setBodyBorder(false);
      setHeading("Properties");
      setLayout(new FitLayout());
      setFrame(true);      
   }
   
   public void setPropertyForm(PropertyForm propertyForm) {
      removePropertiesForm();
      if (propertyForm != null) {
        currentPropertyForm = propertyForm;
        add(currentPropertyForm);
      }
      layout();
   }

   private void removePropertiesForm() {
      if (currentPropertyForm != null) {
         currentPropertyForm.removeFromParent();
         currentPropertyForm = null;
      }
   }
   
}

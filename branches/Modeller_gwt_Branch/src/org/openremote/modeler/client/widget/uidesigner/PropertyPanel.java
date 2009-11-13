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

import org.openremote.modeler.client.event.WidgetSelectChangeEvent;
import org.openremote.modeler.client.listener.WidgetSelectChangeListener;
import org.openremote.modeler.client.utils.SelectedWidgetContainer;
import org.openremote.modeler.client.widget.control.ScreenControl;
import org.openremote.modeler.domain.control.UIControl;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

/**
 * A panel for display and edit different component's properties.
 */
public class PropertyPanel extends ContentPanel {

   private LayoutContainer currentLayoutContainer;
   private FormPanel currentPropertyForm;
//   private static PropertyPanel propertyPanel;
   public PropertyPanel() {
      setBodyBorder(false);
      setHeading("Properties");
      setLayout(new FitLayout());
      setFrame(true);
      SelectedWidgetContainer.setChangeListener(new WidgetSelectChangeListener(){
         @Override
         public void changeSelect(WidgetSelectChangeEvent be) {
            update(be.getSelectWidget());
         }
         
      });
   }
   
//   public static PropertyPanel getInstance() {
//      if (propertyPanel == null) {
//         propertyPanel = new PropertyPanel();
//      }
//      return propertyPanel;
//   }
   
   /**
    * Update the panel's content follow with different component.
    */
   public void update(LayoutContainer component) {
      if (component == null) {
         removePropertiesForm();
         layout();
         return;
      }
      if (!component.equals(currentLayoutContainer)) {
         if(component instanceof AbsoluteLayoutContainer) {
            AbsoluteLayoutContainer alc = (AbsoluteLayoutContainer) component;
            ScreenControl screenControl = alc.getScreenControl();
            UIControl uiControl =  alc.getAbsolute().getUiControl();
            addPropertiesForm(screenControl, uiControl);
         } else if (component instanceof GridCellContainer) {
            GridCellContainer gcc = (GridCellContainer) component;
            ScreenControl screenControl = gcc.getScreenControl();
            UIControl uiControl = gcc.getCell().getUiControl();
            addPropertiesForm(screenControl, uiControl);
         }
         layout();
         currentLayoutContainer = component;
      }
      
   }

   /**
    * @param screenControl
    * @param uiControl
    */
   private void addPropertiesForm(ScreenControl screenControl, UIControl uiControl) {
      if(currentPropertyForm != null) {
         currentPropertyForm.removeFromParent();
      }
      currentPropertyForm = screenControl.buildPropertiesForm();
      add(currentPropertyForm);
   }
   
   public void removePropertiesForm() {
      if(currentPropertyForm != null) {
         currentPropertyForm.removeFromParent();
         currentLayoutContainer = null;
         currentPropertyForm = null;
      }
   }
}

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
package org.openremote.modeler.client.utils;

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.client.event.WidgetSelectChangeEvent;
import org.openremote.modeler.client.listener.WidgetSelectChangeListener;
import org.openremote.modeler.client.widget.uidesigner.ComponentContainer;

/**
 * The SelectedWidgetContainer for store selected widget and fire widgetSelectChange event.
 */
public class WidgetSelectionUtil {

   private List<ComponentContainer> selectedWidgets = new ArrayList<ComponentContainer>();
   
   private WidgetSelectChangeListener widgetSelectChangeListener;
   
   public void setChangeListener(WidgetSelectChangeListener listener) {
      widgetSelectChangeListener = listener;
   }
   
   public void resetSelection() {
     setSelectWidget(null);
   }
   
   public void setSelectWidget(ComponentContainer selectedWidget) {
     for (ComponentContainer widget : selectedWidgets) {
       widget.removeStyleName("button-border");
     }
     
      if (selectedWidget != null) {
         selectWidget(selectedWidget);
      }
      selectedWidgets.clear();
      if (selectedWidget != null) {
        selectedWidgets.add(selectedWidget);
      }

      // TODO - EBR : this should go through the event bus, not via a direct dependency
      widgetSelectChangeListener.handleEvent(new WidgetSelectChangeEvent(selectedWidgets));
   }

   public void toggleSelectWidget(ComponentContainer selectedWidget) {
     if (selectedWidget != null) {
       if (selectedWidgets.contains(selectedWidget)) {
         selectedWidget.removeStyleName("button-border");
         selectedWidgets.remove(selectedWidget);
       } else {
         selectWidget(selectedWidget);
         selectedWidgets.add(selectedWidget);
       }
       
       // TODO - EBR : this should go through the event bus, not via a direct dependency
       widgetSelectChangeListener.handleEvent(new WidgetSelectChangeEvent(selectedWidgets));
     }
   }
   
   public List<ComponentContainer> getSelectedWidgets() {
    return selectedWidgets;
  }

  private void selectWidget(ComponentContainer selectedWidget) {
     selectedWidget.addStyleName("button-border");
      
      // add tab index and focus it, for catch keyboard "delete" event in Firefox.
      if (selectedWidget.isRendered()) {
         selectedWidget.el().dom.setPropertyInt("tabIndex", 0);
      }
      selectedWidget.focus();
   }
    
}

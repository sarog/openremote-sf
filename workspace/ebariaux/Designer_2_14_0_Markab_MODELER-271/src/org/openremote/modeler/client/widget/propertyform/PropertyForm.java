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
package org.openremote.modeler.client.widget.propertyform;

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.client.utils.WidgetSelectionUtil;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.form.FormPanel;

/**
 * The PropertyForm initialize the property form display style.
 */
public class PropertyForm extends FormPanel {

   protected WidgetSelectionUtil widgetSelectionUtil;
   
   private List<PropertyFormExtension> formExtensions = new ArrayList<PropertyFormExtension>();

   public PropertyForm() {
     setFrame(true);
     setHeaderVisible(false);
     setBorders(false);
     setBodyBorder(false);
     setPadding(2);
     setLabelWidth(60);
     setFieldWidth(100);
     setScrollMode(Scroll.AUTO);
  }

   public PropertyForm(WidgetSelectionUtil widgetSelectionUtil) {
     this();
      this.widgetSelectionUtil = widgetSelectionUtil;
      setLabelWidth(90);
      setFieldWidth(150);
   }
   
   public String getPropertyFormTitle() {
     return "- EMPTY FORM -";
   }
   
  @Override
  protected void onLoad() {
    super.onLoad();
    for (PropertyFormExtension extension : formExtensions) {
      extension.install(this);
    }
  }

  @Override
  protected void onUnload() {
    for (PropertyFormExtension extension : formExtensions) {
      extension.cleanup(this);
    }
    super.onUnload();
  }

  public void addFormExtension(PropertyFormExtension extension) {
     this.formExtensions.add(extension);
  }
   
}
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
package org.openremote.modeler.client.widget.propertyform;

import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.widget.ImageUploadField;
import org.openremote.modeler.client.widget.NavigateFieldSet;
import org.openremote.modeler.client.widget.component.ScreenTabbarItem;
import org.openremote.modeler.client.widget.uidesigner.PropertyPanel;
import org.openremote.modeler.domain.component.ImageSource;
import org.openremote.modeler.domain.component.Navigate;
import org.openremote.modeler.domain.component.Navigate.ToLogicalType;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldSetEvent;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.form.TextField;

/**
 * A panel for display screen button properties.
 */
public class TabbarItemPropertyForm extends PropertyForm {
   private NavigateFieldSet navigateSet = null;
   private ScreenTabbarItem screenTabbarItem = null;
   private ImageUploadField imageUploader = null;
   
   public TabbarItemPropertyForm(ScreenTabbarItem screenTabbarItem) {
      super(screenTabbarItem);
      this.screenTabbarItem = screenTabbarItem;
      addFields();
      addSubmitListenersToForm();
   }
   private void addFields() {
      // initial name field.
      final TextField<String> name = new TextField<String>();
      name.setFieldLabel("Name");
      name.setValue(screenTabbarItem.getName());
      name.addListener(Events.Blur, new Listener<BaseEvent>() {
         @Override
         public void handleEvent(BaseEvent be) {
            screenTabbarItem.setName(name.getValue());
         }
      });
      
      imageUploader = new ImageUploadField(null) {
         @Override
         protected void onChange(ComponentEvent ce) {
            super.onChange(ce);
            if (!isValid()) {
               return;
            }
            submit();
         }
      };
      imageUploader.setValue(screenTabbarItem.getImageSource().getSrc());
      imageUploader.setFieldLabel("imageSource");
      imageUploader.setActionToForm(this);
      // initial navigate properties
      final Navigate navigate = screenTabbarItem.getNavigate();
      navigateSet = new NavigateFieldSet(navigate, BeanModelDataBase.groupTable.loadAll());
      navigateSet.setCheckboxToggle(true);
      navigateSet.addListener(Events.BeforeExpand, new Listener<FieldSetEvent>() {
         @Override
         public void handleEvent(FieldSetEvent be) {
            if (!navigate.isSet()) {
               navigate.setToLogical(ToLogicalType.login);
            }
            navigateSet.update(navigate);
         }
         
      });
      navigateSet.addListener(Events.BeforeCollapse, new Listener<FieldSetEvent>() {
         @Override
         public void handleEvent(FieldSetEvent be) {
            navigate.clear();
         }
      });
      if (navigate.isSet()) {
         navigateSet.fireEvent(Events.BeforeExpand);
      } else {
         navigateSet.collapse();
      }
      
      add(name);
      add(imageUploader);
      add(navigateSet);
      
   }
   
   private void addSubmitListenersToForm() {
      addListener(Events.Submit, new Listener<FormEvent>() {
         @Override
         public void handleEvent(FormEvent be) {
            String backgroundImgURL = be.getResultHtml();
            boolean success = !"".equals(backgroundImgURL);
            if (success) {
               screenTabbarItem.setImageSource(new ImageSource(backgroundImgURL));
            }
         }
      });
   }
   
   @Override
   protected void afterRender() {
      super.afterRender();
      ((PropertyPanel)this.getParent()).setHeading("Tabbar item properties");
   }
}

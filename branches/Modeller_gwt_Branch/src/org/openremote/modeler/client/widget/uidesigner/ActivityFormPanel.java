/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2009, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.openremote.modeler.client.widget.uidesigner;

import org.openremote.modeler.client.listener.FormSubmitListener;
import org.openremote.modeler.domain.Activity;

import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;

/**
 * ActivityFormPanel to create and edit {@link Activity}.
 */
public class ActivityFormPanel extends FormPanel {

   /** The activity. */
   private BeanModel activityBeanModel;

   /**
    * Instantiates a new activity form panel.
    */
   public ActivityFormPanel() {
      this((new Activity()).getBeanModel());
   }

   /**
    * Instantiates a new activity form panel.
    * 
    * @param a
    *           the a
    */
   public ActivityFormPanel(BeanModel a) {
      activityBeanModel = a;
      createField();
      
   }

   /**
    * Creates the field.
    */
   private void createField() {
      final FormPanel form = this;
      TextField<String> nameField = new TextField<String>();
      nameField.setName("name");
      nameField.setFieldLabel("Name");
      nameField.setAllowBlank(false);
      
      add(nameField);
      Button submitBtn = new Button("submit");
      
      submitBtn.addSelectionListener(new FormSubmitListener(form));
      
      addButton(submitBtn);

      FormBinding binding = new FormBinding(this);
      binding.addFieldBinding(new FieldBinding(nameField, "name"));
      binding.autoBind();
      binding.bind(activityBeanModel);
      
      addListener(Events.BeforeSubmit, new Listener<FormEvent>(){
         public void handleEvent(FormEvent be) {
         }
      });
      
   }
}

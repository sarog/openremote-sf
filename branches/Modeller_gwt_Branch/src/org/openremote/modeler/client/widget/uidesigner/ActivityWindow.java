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

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.client.widget.FormWindow;
import org.openremote.modeler.domain.Activity;
import org.openremote.modeler.selenium.DebugId;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.TextField;

/**
 * The Class ActivityWindow.
 * @author handy.wang
 */
public class ActivityWindow extends FormWindow {

   /** The Constant ACTIVITY_NAME. */
   private static final String ACTIVITY_NAME = "activityName";

   /** The activity model. */
   private BeanModel activityModel = null;

   /**
    * Instantiates a new activity window.
    */
   public ActivityWindow() {
      super();
      initial("New Activity");
      this.ensureDebugId(DebugId.NEW_ACTIVITY_WINDOW);
      show();
   }

   /**
    * Instantiates a new activity window.
    * 
    * @param activityModel
    *           the activity model
    */
   public ActivityWindow(BeanModel activityModel) {
      super();
      this.activityModel = activityModel;
      initial("Edit Activity");
      this.ensureDebugId(DebugId.EDIT_ACTIVITY_WINDOW);
      show();
   }

   /**
    * Initialize the activity window.
    * 
    * @param heading
    *           the heading
    */
   private void initial(String heading) {
      setSize(360, 140);
      setHeading(heading);
      createFields();
      createButtons();
      addListenersToForm();
   }

   /**
    * Creates the fields.
    */
   private void createFields() {
      TextField<String> activityNameField = new TextField<String>();
      activityNameField.setName(ACTIVITY_NAME);
      activityNameField.ensureDebugId(DebugId.ACTIVITY_NAME_FIELD);
      activityNameField.setFieldLabel("Name");
      activityNameField.setAllowBlank(false);activityNameField.setValue("test");
      if (activityModel != null) {
         Activity activity = activityModel.getBean();
         activityNameField.setValue(activity.getName());
      }
      form.add(activityNameField);
   }

   /**
    * Creates the buttons.
    */
   private void createButtons() {
      Button submitBtn = new Button("submit");
      submitBtn.ensureDebugId(DebugId.NEW_ACTIVITY_WINDOW_SUBMIT_BTN);
      Button resetBtn = new Button("reset");
      resetBtn.ensureDebugId(DebugId.NEW_ACTIVITY_WINDOW_RESET_BTN);

      submitBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            if (form.isValid()) {
               form.submit();
            }
         }
      });
      resetBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            form.reset();
         }
      });
      form.addButton(submitBtn);
      form.addButton(resetBtn);
   }

   /**
    * Adds the listeners to form.
    */
   private void addListenersToForm() {
      form.addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
         public void handleEvent(FormEvent be) {
            Activity activity = new Activity();
            if (activityModel == null) {
               activity.setOid(IDUtil.nextID());
            } else {
               activity = activityModel.getBean();
            }
            updateActivityAttrs(activity);
            activityModel = activity.getBeanModel();
            BeanModelDataBase.activityTable.insert(activityModel);
            fireEvent(SubmitEvent.Submit, new SubmitEvent(activityModel));
         }
      });
      add(form);
   }
   
   /**
    * Update activity attrs.
    * 
    * @param activity the activity
    */
   private void updateActivityAttrs(Activity activity) {
      for (Field<?> field : form.getFields()) {
         if ("activityName".equals(field.getName())) {
            activity.setName(field.getValue().toString());
         }
      }
   }
}

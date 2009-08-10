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
package org.openremote.modeler.client.widget;

import java.util.List;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.domain.CommandDelay;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;

/**
 * The Class DelayWindow.
 */
public class DelayWindow extends Window {

   /** The Constant DELAY. */
   public static final String DELAY = "delay";
   
   /** The form. */
   private FormPanel form = new FormPanel();
   
   /** The command delay model. */
   private BeanModel commandDelayModel = null;

   /**
    * Instantiates a new delay window.
    */
   public DelayWindow() {
      initial("Add Delay");
      show();
   }
   
   /**
    * Instantiates a new delay window.
    * 
    * @param commandDelayModel the command delay model
    */
   public DelayWindow(BeanModel commandDelayModel) {
      this.commandDelayModel = commandDelayModel;
      initial("Edit Delay");
      show();
   }
   
   /**
    * Initial.
    * 
    * @param Heading the heading
    */
   private void initial(String Heading) {
      setHeading("Add Delay");
      setLayout(new FillLayout());
      setModal(true);
      setBodyBorder(false);
      setSize(280, 120);
      form.setFrame(true);
      form.setHeaderVisible(false);
      form.setBorders(false);
      
      form.setButtonAlign(HorizontalAlignment.CENTER);

      Button addBtn = new Button("OK");
      
      addBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            if (form.isValid()) {
               form.submit();
            }
         }

      });
      
      form.addButton(addBtn);
      form.addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
         @Override
         public void handleEvent(FormEvent be) {
            List<Field<?>> list = form.getFields();
            String delay = list.get(0).getValue().toString();
            if (commandDelayModel == null) {
               CommandDelay commandDelay = new CommandDelay(delay);
               commandDelayModel = commandDelay.getBeanModel();
            } else {
               commandDelayModel.set("delaySecond", delay);
            }
            fireEvent(SubmitEvent.Submit, new SubmitEvent(commandDelayModel));
         }
         
      });
      createField();
      add(form);
   }
   
   /**
    * Creates the field.
    */
   private void createField() {
      NumberField delayField = new NumberField();
      delayField.setName(DELAY);
      delayField.setFieldLabel("Delay(s)");
      delayField.setAllowBlank(false);
      delayField.setAutoWidth(true);
      if (commandDelayModel != null) {
         delayField.setValue(Float.valueOf(commandDelayModel.get("delaySecond").toString()));
      }
      form.add(delayField);
   }
}

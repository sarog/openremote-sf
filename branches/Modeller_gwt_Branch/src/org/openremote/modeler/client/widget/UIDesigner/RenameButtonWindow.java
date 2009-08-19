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
package org.openremote.modeler.client.widget.UIDesigner;

import java.util.List;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.widget.FormWindow;
import org.openremote.modeler.domain.UIButton;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.TextField;

// TODO: Auto-generated Javadoc
/**
 * The Class RenameButtonWindow.
 */
public class RenameButtonWindow extends FormWindow {
   
   /** The button. */
   private UIButton button;
   
   /**
    * Instantiates a new rename button window.
    * 
    * @param button the button
    */
   public RenameButtonWindow(UIButton button) {
      super();
      this.button = button;
      initial();
      show();
   }
   
   /**
    * Initial.
    */
   private void initial(){
      setSize(360, 140);
      setHeading("Rename Button");
      createField();
      createButtons();
      addForm();
   }

   /**
    * Creates the field.
    */
   private void createField() {
      TextField<String> buttonName = new TextField<String>();
      buttonName.setAllowBlank(false);
      buttonName.setName("name");
      buttonName.setFieldLabel("Name");
      buttonName.setValue(button.getName());
      form.add(buttonName);
   }
   
   /**
    * Creates the buttons.
    */
   private void createButtons() {
      Button submitBtn = new Button("submit");
      Button resetBtn = new Button("reset");

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
    * Adds the form.
    */
   private void addForm() {
      form.addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
         public void handleEvent(FormEvent be) {
            List<Field<?>> list = form.getFields();
            String buttonName = list.get(0).getValue().toString();
            button.setLabel(buttonName);
            fireEvent(SubmitEvent.Submit, new SubmitEvent(button));
         }
      });
      add(form);
   }
}

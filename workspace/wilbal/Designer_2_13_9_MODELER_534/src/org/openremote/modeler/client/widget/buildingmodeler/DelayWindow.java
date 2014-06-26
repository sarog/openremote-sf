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
package org.openremote.modeler.client.widget.buildingmodeler;

import java.util.List;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.listener.FormSubmitListener;
import org.openremote.modeler.client.widget.FormWindow;
import org.openremote.modeler.shared.dto.DTOHelper;
import org.openremote.modeler.shared.dto.DTOReference;
import org.openremote.modeler.shared.dto.MacroItemDetailsDTO;
import org.openremote.modeler.shared.dto.MacroItemType;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Creates a delay command for the macro.
 */
public class DelayWindow extends DialogBox {

   interface DelayWindowUiBinder extends UiBinder<Widget, DelayWindow> {
   }
   
   @UiField
   TextBox delayTextBox;
   
   private static final DelayWindowUiBinder binder = GWT.create(DelayWindowUiBinder.class);
   
   /** The Constant DELAY. */
   public static final String DELAY = "delay";
   
   MacroItemDetailsDTO macroItem;

   private boolean clickedSave;

   public boolean isClickedSave() {
      return clickedSave;
   }

   public void setClickedSave(boolean clickedSave) {
      this.clickedSave = clickedSave;
   }

   /**
    * Instantiates a new delay window.
    */
   public DelayWindow() {
      setWidget(binder.createAndBindUi(this));

      initial("Add Delay");
      macroItem = new MacroItemDetailsDTO();
      macroItem.setType(MacroItemType.Delay);
   }
   
   /**
    * Instantiates a new delay window.
    * 
    * @param commandDelayModel the command delay model
    */
   public DelayWindow(MacroItemDetailsDTO macroItem) {
      setWidget(binder.createAndBindUi(this));
      this.macroItem = macroItem;
      initial("Edit Delay");
   }
   
   /**
    * Initial.
    * 
    * @param heading the heading
    */
   private void initial(String heading) {
      setSize("200px", "100px");
      center();
      //setHeading(heading);
     /* setSize(280, 140);

      Button addBtn = new Button("OK");      
      addBtn.addSelectionListener(new FormSubmitListener(form, addBtn));
      form.addButton(addBtn);
      form.addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
         public void handleEvent(FormEvent be) {
            List<Field<?>> list = form.getFields();
            String delay = list.get(0).getValue().toString();
            macroItem.setDelay(Integer.parseInt(delay));
            fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(macroItem));
         }         
      });
      createField();
      add(form);*/
   }

   @UiHandler("okButton")
   void handleOkButtonclicked(ClickEvent e) {
      macroItem.setDelay(Integer.parseInt(delayTextBox.getText()));
      macroItem.setDto(new DTOReference());
      this.setClickedSave(true);
      this.hide();
   }
   
   @UiHandler("cancelButton")
   void handleCancelButtonclicked(ClickEvent e) {
      this.setClickedSave(false);
      this.hide();
   }
  
}

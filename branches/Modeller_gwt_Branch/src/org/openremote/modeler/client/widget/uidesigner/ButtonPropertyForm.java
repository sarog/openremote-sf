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

import java.util.List;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.model.ComboBoxDataModel;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.widget.control.ScreenButton;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroRef;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.UICommand;
import org.openremote.modeler.domain.control.Navigate;
import org.openremote.modeler.domain.control.UIButton;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;

/**
 * A panel for display screen button properties.
 */
public class ButtonPropertyForm extends FormPanel {

   public ButtonPropertyForm(ScreenButton screenButton, UIButton uiButton) {
      init();
      addFields(screenButton, uiButton);
   }
   
   private void init() {
      setFrame(true);
      setHeaderVisible(false);
      setBorders(false);
      setBodyBorder(false);
      setLabelWidth(60);
      setFieldWidth(80);
      setScrollMode(Scroll.AUTOY);
   }
   private void addFields(final ScreenButton screenButton, final UIButton uiButton) {
      // initial name field.
      final TextField<String> name = new TextField<String>();
      name.setFieldLabel("Name");
      name.setValue(screenButton.getText());
      name.addListener(Events.Blur, new Listener<BaseEvent>(){
         @Override
         public void handleEvent(BaseEvent be) {
            screenButton.setText(name.getValue());
         }
      });
      
      // initial command field.
      final Button command = new Button("Select");
      command.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            SelectCommandWindow selectCommandWindow = new SelectCommandWindow();
            selectCommandWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  BeanModel dataModel = be.<BeanModel> getData();
                  UICommand uiCommand = null;
                  if (dataModel.getBean() instanceof DeviceCommand) {
                     uiCommand = new DeviceCommandRef((DeviceCommand) dataModel.getBean());
                  } else if (dataModel.getBean() instanceof DeviceMacro) {
                     uiCommand = new DeviceMacroRef((DeviceMacro) dataModel.getBean());
                  }
                  uiButton.setUiCommand(uiCommand);
                  command.setText(uiCommand.getDisplayName());
               }
            });
         }
      });
      if(uiButton.getUiCommand() != null) {
         command.setText(uiButton.getUiCommand().getDisplayName());
      }
      AdapterField adapterCommand = new AdapterField(command);
      adapterCommand.setFieldLabel("Command");
      
   // initial toGroup field.
      final ComboBox<ModelData> toGroup = new ComboBox<ModelData>();
      ListStore<ModelData> store = new ListStore<ModelData>();
      toGroup.setStore(store);
      toGroup.setFieldLabel("To Group");
      toGroup.setName("toGroup");
      toGroup.setAllowBlank(false);
      Group blankGroup = new Group();
      blankGroup.setName("--no--");
      ComboBoxDataModel<Group> toGroupValue = new ComboBoxDataModel<Group>(blankGroup.getName(), blankGroup);
      store.add(toGroupValue);
      List<BeanModel> groupModels = BeanModelDataBase.groupTable.loadAll();
      long groupId = -1L;  // select none is -1, it's temp used.
      if (uiButton.getNavigate() != null) {
         groupId = uiButton.getNavigate().getToGroup();
      }
      for (BeanModel groupModel : groupModels) {
         ComboBoxDataModel<Group> data = new ComboBoxDataModel<Group>(groupModel.get("name").toString(), (Group) groupModel.getBean());
         if(groupId == ((Group) groupModel.getBean()).getOid()) {
            toGroupValue = data;
         }
         store.add(data);
      }
      toGroup.setValue(toGroupValue);
      toGroup.setDisplayField(ComboBoxDataModel.getDisplayProperty());
      toGroup.setEmptyText("Please Select group...");
      toGroup.setValueField(ComboBoxDataModel.getDataProperty());
      toGroup.addListener(Events.Blur, new Listener<BaseEvent>() {
         @SuppressWarnings("unchecked")
         @Override
         public void handleEvent(BaseEvent be) {
            ComboBoxDataModel<Group> groupData = (ComboBoxDataModel<Group>) toGroup.getValue();
            if (groupData != null) {
               long groupId = -1L;
               if (!"--no--".equals(groupData.getData().getName())) {
                  groupId = groupData.getData().getOid();
               }
               if (uiButton.getNavigate() != null) {
                  uiButton.getNavigate().setToGroup(groupId);
               } else {
                  Navigate navigate = new Navigate();
                  navigate.setToGroup(groupId);
                  uiButton.setNavigate(navigate);
               }
            }
         }

      });
      
      add(name);
      add(adapterCommand);
      add(toGroup);
   }
}

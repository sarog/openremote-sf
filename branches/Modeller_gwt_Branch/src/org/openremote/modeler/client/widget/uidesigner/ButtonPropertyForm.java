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
import org.openremote.modeler.client.widget.component.ScreenButton;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroRef;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.ScreenRef;
import org.openremote.modeler.domain.UICommand;
import org.openremote.modeler.domain.component.Navigate;
import org.openremote.modeler.domain.component.UIButton;
import org.openremote.modeler.domain.component.UImage;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.FieldSetEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

/**
 * A panel for display screen button properties.
 */
public class ButtonPropertyForm extends PropertyForm {

   public ButtonPropertyForm(ScreenButton screenButton, UIButton uiButton) {
      super();
      addFields(screenButton, uiButton);
   }
   private void addFields(final ScreenButton screenButton, final UIButton uiButton) {
      // initial name field.
      final TextField<String> name = new TextField<String>();
      name.setFieldLabel("Name");
      name.setValue(uiButton.getName());
      name.addListener(Events.Blur, new Listener<BaseEvent>() {
         @Override
         public void handleEvent(BaseEvent be) {
            screenButton.setName(name.getValue());
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
      if (uiButton.getUiCommand() != null) {
         command.setText(uiButton.getUiCommand().getDisplayName());
      }
      AdapterField adapterCommand = new AdapterField(command);
      adapterCommand.setFieldLabel("Command");
      
      // initial navigate properties
      final Navigate navigate = uiButton.getNavigate();
      FieldSet navigateSet = new FieldSet();
      navigateSet.setLayout(new ColumnLayout());
      navigateSet.setHeading("Navigate");
      navigateSet.setCheckboxToggle(true);
      if (!navigate.isSet()) {
         navigateSet.collapse();
      }
      
      final LayoutContainer rightComboBoxes = new LayoutContainer();
      FormLayout layout = new FormLayout();
      layout.setHideLabels(true);
      layout.setDefaultWidth(100);
      rightComboBoxes.setLayout(layout);
      rightComboBoxes.setLayoutOnChange(true);
      rightComboBoxes.disable();
      
      final ComboBox<ModelData> screenList = new ComboBox<ModelData>();
      screenList.setEmptyText("--screen--");
      screenList.setDisplayField(ComboBoxDataModel.getDisplayProperty());
      screenList.setValueField(ComboBoxDataModel.getDataProperty());
      ListStore<ModelData> screenStore = new ListStore<ModelData>();
      screenList.setStore(screenStore);
      screenList.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
         @SuppressWarnings("unchecked")
         @Override
         public void selectionChanged(SelectionChangedEvent<ModelData> se) {
            Screen selectedScreen = ((ComboBoxDataModel<Screen>) se.getSelectedItem()).getData();
            navigate.setToScreen(selectedScreen.getOid());
         }
         
      });
      
      final ComboBox<ModelData> groupList = new ComboBox<ModelData>();
      groupList.setEmptyText("--group--");
      groupList.setDisplayField(ComboBoxDataModel.getDisplayProperty());
      groupList.setValueField(ComboBoxDataModel.getDataProperty());
      ListStore<ModelData> groupStore = new ListStore<ModelData>();
      groupList.setStore(groupStore);
      List<BeanModel> groupModels = BeanModelDataBase.groupTable.loadAll();
      groupList.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
         @SuppressWarnings("unchecked")
         @Override
         public void selectionChanged(SelectionChangedEvent<ModelData> se) {
            Group selectedGroup = ((ComboBoxDataModel<Group>) se.getSelectedItem()).getData();
            navigate.setToGroup(selectedGroup.getOid());
            screenList.clearSelections();
            screenList.getStore().removeAll();
            for (ScreenRef screenRef : selectedGroup.getScreenRefs()) {
               ComboBoxDataModel<Screen> data = new ComboBoxDataModel<Screen>(screenRef.getDisplayName(), screenRef.getScreen());
               screenList.getStore().add(data);
               if (navigate.getToScreen() == screenRef.getScreenId()) {
                  screenList.setValue(data);
               }
            }
            if (screenList.getValue() == null) {
               navigate.setToScreen(-1);
            }
         }
      });
      for (BeanModel groupModel : groupModels) {
         ComboBoxDataModel<Group> data = new ComboBoxDataModel<Group>(groupModel.get("name").toString(), (Group) groupModel.getBean());
         groupStore.add(data);
         if (navigate.getToGroup() == ((Group) groupModel.getBean()).getOid()) {
            groupList.setValue(data);
       }
      }
         
      rightComboBoxes.add(groupList);
      rightComboBoxes.add(screenList);
      
      final RadioGroup navigateGroup = new RadioGroup();
      navigateGroup.setOrientation(Orientation.VERTICAL);
      Radio toGroup = new Radio();
      toGroup.setBoxLabel("ToGroup");
      toGroup.addListener(Events.Change, new Listener<FieldEvent>() {
         @Override
         public void handleEvent(FieldEvent be) {
            Boolean value = (Boolean) be.getValue();
            if (value) {
               rightComboBoxes.enable();
            } else {
               navigate.setToGroup(-1);
               navigate.setToScreen(-1);
               screenList.clearSelections();
               groupList.clearSelections();
               rightComboBoxes.disable();
            }
         }
         
      });
      if (navigate.getToGroup() != -1) {
         toGroup.setValue(true);
      }
      
      Radio toScreen = new Radio();
      toScreen.setBoxLabel("ToScreen");
      toScreen.disable();
      
      Radio toSetting = new Radio();
      toSetting.setBoxLabel("ToSetting");
      toSetting.setValue(navigate.isToSetting());
      toSetting.addListener(Events.Change, new Listener<FieldEvent>() {
         @Override
         public void handleEvent(FieldEvent be) {
            navigate.setToSetting((Boolean) be.getValue());
         }
      });
      
      final Radio back = new Radio();
      back.setBoxLabel("Back");
      back.setValue(navigate.isBack());
      back.addListener(Events.Change, new Listener<FieldEvent>() {
         @Override
         public void handleEvent(FieldEvent be) {
            navigate.setBack((Boolean) be.getValue());
         }
      });
      
      Radio login = new Radio();
      login.setBoxLabel("Login");
      login.setValue(navigate.isLogin());
      login.addListener(Events.Change, new Listener<FieldEvent>() {
         @Override
         public void handleEvent(FieldEvent be) {
            navigate.setLogin((Boolean) be.getValue());
         }
      });
      
      Radio logout = new Radio();
      logout.setBoxLabel("Logout");
      logout.setValue(navigate.isLogout());
      logout.addListener(Events.Change, new Listener<FieldEvent>() {
         @Override
         public void handleEvent(FieldEvent be) {
            navigate.setLogout((Boolean) be.getValue());
         }
      });
      
      Radio previous = new Radio();
      previous.setBoxLabel("Previous");
      previous.setValue(navigate.isPrevious());
      previous.addListener(Events.Change, new Listener<FieldEvent>() {
         @Override
         public void handleEvent(FieldEvent be) {
            navigate.setPrevious((Boolean) be.getValue());
         }
      });
      
      Radio next = new Radio();
      next.setBoxLabel("Next");
      next.setValue(navigate.isNext());
      next.addListener(Events.Change, new Listener<FieldEvent>() {
         @Override
         public void handleEvent(FieldEvent be) {
            navigate.setNext((Boolean) be.getValue());
         }
      });
      
      navigateGroup.add(toGroup);
      navigateGroup.add(toScreen);
      navigateGroup.add(toSetting);
      navigateGroup.add(back);
      navigateGroup.add(login);
      navigateGroup.add(logout);
      navigateGroup.add(previous);
      navigateGroup.add(next);
      
      navigateSet.add(navigateGroup);
      navigateSet.add(rightComboBoxes);
      navigateSet.addListener(Events.BeforeExpand, new Listener<FieldSetEvent>() {
         @Override
         public void handleEvent(FieldSetEvent be) {
            back.setValue(!navigate.isSet());
         }
         
      });
      navigateSet.addListener(Events.BeforeCollapse, new Listener<FieldSetEvent>() {
         @Override
         public void handleEvent(FieldSetEvent be) {
            if (navigateGroup.getValue() != null) {
               navigateGroup.getValue().setValue(false);
            }
         }
         
      });
      
      Button imageBtn = new Button("Select");
      imageBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            final UImage image = uiButton.getImage();
            ChangeIconWindow selectImageONWindow = new ChangeIconWindow(screenButton, image);
            selectImageONWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  String imageUrl = be.getData();
                  screenButton.setIcon(imageUrl);
                  if (image != null) {
                     image.setSrc(imageUrl);
                  } else {
                     uiButton.setImage(new UImage(imageUrl));
                  }
               }
            });
         }
      });
      AdapterField adapterImageBtn = new AdapterField(imageBtn);
      adapterImageBtn.setFieldLabel("Image");
      
      Button onPressImageBtn = new Button("Select");
      onPressImageBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            final UImage onPressImage = uiButton.getPressImage();
            ChangeIconWindow selectImageONWindow = new ChangeIconWindow(screenButton, onPressImage);
            selectImageONWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  String onPressImageUrl = be.getData();
                  if (onPressImage != null) {
                     onPressImage.setSrc(onPressImageUrl);
                  } else {
                     uiButton.setPressImage(new UImage(onPressImageUrl));
                  }
               }
            });
         }
      });
      AdapterField adapterOnPressImageBtn = new AdapterField(onPressImageBtn);
      adapterOnPressImageBtn.setFieldLabel("PressImage");
      
      CheckBoxGroup repeat = new CheckBoxGroup();
      repeat.setFieldLabel("Repeat");
      final CheckBox check = new CheckBox();
      check.setValue(uiButton.isRepeate());
      check.addListener(Events.Blur, new Listener<FieldEvent>() {
         @Override
         public void handleEvent(FieldEvent be) {
            uiButton.setRepeate(check.getValue());
         }
      });
      repeat.add(check); 
      add(name);
      add(adapterCommand);
      add(adapterImageBtn);
      add(adapterOnPressImageBtn);
      add(repeat);
      add(navigateSet);
      
   }
}

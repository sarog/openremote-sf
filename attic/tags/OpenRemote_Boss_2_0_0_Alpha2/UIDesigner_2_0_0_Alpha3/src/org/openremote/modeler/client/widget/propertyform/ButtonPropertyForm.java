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

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.widget.NavigateFieldSet;
import org.openremote.modeler.client.widget.component.ScreenButton;
import org.openremote.modeler.client.widget.uidesigner.ChangeIconWindow;
import org.openremote.modeler.client.widget.uidesigner.SelectCommandWindow;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroRef;
import org.openremote.modeler.domain.UICommand;
import org.openremote.modeler.domain.component.ImageSource;
import org.openremote.modeler.domain.component.Navigate;
import org.openremote.modeler.domain.component.UIButton;
import org.openremote.modeler.domain.component.Navigate.ToLogicalType;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.FieldSetEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;

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
      final NavigateFieldSet navigateSet = new NavigateFieldSet(navigate, BeanModelDataBase.groupTable.loadAll());
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
      
      Button imageBtn = new Button("Select");
      imageBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            final ImageSource image = uiButton.getImage();
            ChangeIconWindow selectImageONWindow = new ChangeIconWindow(screenButton, image);
            selectImageONWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  String imageUrl = be.getData();
                  screenButton.setIcon(imageUrl);
                  if (image != null) {
                     image.setSrc(imageUrl);
                  } else {
                     uiButton.setImage(new ImageSource(imageUrl));
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
            final ImageSource onPressImage = uiButton.getPressImage();
            ChangeIconWindow selectImageONWindow = new ChangeIconWindow(screenButton, onPressImage);
            selectImageONWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  String onPressImageUrl = be.getData();
                  if (onPressImage != null) {
                     onPressImage.setSrc(onPressImageUrl);
                  } else {
                     uiButton.setPressImage(new ImageSource(onPressImageUrl));
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

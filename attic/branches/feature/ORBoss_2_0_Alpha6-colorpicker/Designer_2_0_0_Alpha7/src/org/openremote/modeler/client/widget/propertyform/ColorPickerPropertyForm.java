/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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
import org.openremote.modeler.client.utils.ImageSourceValidator;
import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.client.widget.component.ImageUploadAdapterField;
import org.openremote.modeler.client.widget.component.ScreenColorPicker;
import org.openremote.modeler.client.widget.uidesigner.SelectCommandWindow;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.UICommand;
import org.openremote.modeler.domain.component.ColorPicker;
import org.openremote.modeler.domain.component.ImageSource;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;

/**
 * The Form to configure colorpicker properties, include an image field and a command field.
 */
public class ColorPickerPropertyForm extends PropertyForm {

   private ScreenColorPicker screenColorPicker = null;
   
   public ColorPickerPropertyForm(ScreenColorPicker screenColorPicker) {
      super(screenColorPicker);
      this.screenColorPicker = screenColorPicker;
      if (screenColorPicker != null) {
         addFields();
         addImageUploadListenerToForm();
      }
      super.addDeleteButton();
   }

   private void addFields() {
      this.setLabelWidth(70);
      createImageUploadField();
      createCommandSelectField();
   }
   
   private void createImageUploadField() {
      final ImageUploadAdapterField imageUploadField = new ImageUploadAdapterField(null);
      imageUploadField.addUploadListener(Events.OnChange, new Listener<FieldEvent>() {
         public void handleEvent(FieldEvent be) {
            if (!isValid()) {
               return;
            }
            imageUploadField.setActionToForm(ColorPickerPropertyForm.this);
            submit();
            screenColorPicker.getScreenCanvas().mask("Uploading image...");
         }
      });
      
      imageUploadField.addDeleteListener(new SelectionListener<ButtonEvent>() {
         public void componentSelected(ButtonEvent ce) {
            if (!ColorPicker.DEFAULT_COLORPICKER_URL.equals(screenColorPicker.getColorPicker().getImage().getSrc())){
               screenColorPicker.setImageSource(new ImageSource(ColorPicker.DEFAULT_COLORPICKER_URL));
               WidgetSelectionUtil.setSelectWidget(null);
               WidgetSelectionUtil.setSelectWidget(screenColorPicker);
            }
         }
      });
      imageUploadField.setImage(screenColorPicker.getColorPicker().getImage().getImageFileName());
      imageUploadField.setFieldLabel("Image");
      add(imageUploadField);
   }
   
   private void createCommandSelectField() {
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
                  }
                  screenColorPicker.setUICommand(uiCommand);
                  command.setText(uiCommand.getDisplayName());
               }
            });
         }
      });
      if (screenColorPicker.getUICommand() != null) {
         command.setText(screenColorPicker.getUICommand().getDisplayName());
      }
      AdapterField adapterCommand = new AdapterField(command);
      adapterCommand.setFieldLabel("Command");
      add(adapterCommand);
   }
   
   private void addImageUploadListenerToForm() {
      addListener(Events.Submit, new Listener<FormEvent>() {
         @Override
         public void handleEvent(FormEvent be) {
            String imageURL = ImageSourceValidator.validate(be.getResultHtml());
            if (!"".equals(imageURL)) {
               screenColorPicker.setImageSource(new ImageSource(imageURL));
            }
            screenColorPicker.getScreenCanvas().unmask();
         }
      });
   }
}

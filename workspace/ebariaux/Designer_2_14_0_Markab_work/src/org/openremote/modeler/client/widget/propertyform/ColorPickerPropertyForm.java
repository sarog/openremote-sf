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
import org.openremote.modeler.client.widget.component.ImageSelectAdapterField;
import org.openremote.modeler.client.widget.component.ScreenColorPicker;
import org.openremote.modeler.client.widget.uidesigner.ImageAssetPicker;
import org.openremote.modeler.client.widget.uidesigner.PropertyPanel;
import org.openremote.modeler.client.widget.uidesigner.ImageAssetPicker.ImageAssetPickerListener;
import org.openremote.modeler.client.widget.uidesigner.SelectCommandWindow;
import org.openremote.modeler.domain.component.ColorPicker;
import org.openremote.modeler.domain.component.ImageSource;
import org.openremote.modeler.shared.dto.UICommandDTO;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
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
   
   public ColorPickerPropertyForm(ScreenColorPicker screenColorPicker, ColorPicker colorPicker, WidgetSelectionUtil widgetSelectionUtil) {
     super(screenColorPicker, widgetSelectionUtil);
      this.screenColorPicker = screenColorPicker;
      if (screenColorPicker != null) {
         addFields(colorPicker);
         addImageUploadListenerToForm();
      }
      super.addDeleteButton();
   }

   private void addFields(ColorPicker colorPicker) {
      this.setLabelWidth(70);
      createImageUploadField(colorPicker);
      createCommandSelectField(colorPicker);
   }
   
   private void createImageUploadField(final ColorPicker colorPicker) {
     final ImageSelectAdapterField defaultImageField = new ImageSelectAdapterField("Image");
     defaultImageField.setDeleteButtonEnabled(false);
     if (colorPicker.getImage() != null) {
       if (!ColorPicker.DEFAULT_COLORPICKER_URL.equals(colorPicker.getImage().getSrc())) {
         defaultImageField.setText(colorPicker.getImage().getImageFileName());
         defaultImageField.setDeleteButtonEnabled(true);
       }
     }
     defaultImageField.addSelectionListener(new SelectionListener<ButtonEvent>() {
        @Override
        public void componentSelected(ButtonEvent ce) {
          final ImageSource image = colorPicker.getImage();
          
          ImageAssetPicker imageAssetPicker = new ImageAssetPicker((image != null)?image.getSrc():null);
          imageAssetPicker.show();
          imageAssetPicker.center();
          imageAssetPicker.setListener(new ImageAssetPickerListener() {
           @Override
           public void imagePicked(String imageURL) {
             screenColorPicker.setImageSource(new ImageSource(imageURL));
             if (image != null) {
                image.setSrc(imageURL);
             } else {
               colorPicker.setImage(new ImageSource(imageURL));
             }
             defaultImageField.setText(colorPicker.getImage().getImageFileName());
             defaultImageField.setDeleteButtonEnabled(true);
           }             
          });
        }
     });
     defaultImageField.addDeleteListener(new SelectionListener<ButtonEvent>() {
        @Override
        public void componentSelected(ButtonEvent ce) {
           if (colorPicker.getImage() != null) {
              defaultImageField.removeImageText();
              screenColorPicker.setImageSource(new ImageSource(ColorPicker.DEFAULT_COLORPICKER_URL));
              defaultImageField.setDeleteButtonEnabled(false);
           }
        }
     });

     add(defaultImageField);
   }
   
   private void createCommandSelectField(final ColorPicker colorPicker) {
     final Button command = new Button("Select");
     command.addSelectionListener(new SelectionListener<ButtonEvent>() {
        @Override
        public void componentSelected(ButtonEvent ce) {
           SelectCommandWindow selectCommandWindow = new SelectCommandWindow(false);
           selectCommandWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
              @Override
              public void afterSubmit(SubmitEvent be) {
                 BeanModel dataModel = be.<BeanModel> getData();
                 UICommandDTO dto = dataModel.getBean();
                 colorPicker.setUiCommandDTO(dto);
                 command.setText(dto.getDisplayName());
              }
           });
        }
     });
     if (colorPicker.getUiCommandDTO() != null) {
        command.setText(colorPicker.getUiCommandDTO().getDisplayName());
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

   @Override
   protected void afterRender() {
      super.afterRender();
      ((PropertyPanel)this.getParent()).setHeading("Color Picker properties");
   }
}

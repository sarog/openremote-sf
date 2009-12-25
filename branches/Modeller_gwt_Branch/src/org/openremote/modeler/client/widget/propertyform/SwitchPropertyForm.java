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
import org.openremote.modeler.client.widget.component.ScreenSwitch;
import org.openremote.modeler.client.widget.uidesigner.ChangeIconWindow;
import org.openremote.modeler.client.widget.uidesigner.SelectSwitchWindow;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.domain.component.UISwitch;
import org.openremote.modeler.domain.component.UImage;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;

/**
 * A panel for display screen switch properties. 
 */
public class SwitchPropertyForm extends PropertyForm {

   public SwitchPropertyForm(ScreenSwitch screenSwitch, UISwitch uiSwitch) {
      super();
      addFields(screenSwitch, uiSwitch);
   }
   
   private void addFields(final ScreenSwitch screenSwitch, final UISwitch uiSwitch) {
      Button imageON = new Button("Select");
      imageON.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            final UImage onImage = uiSwitch.getOnImage();
            ChangeIconWindow selectImageONWindow = new ChangeIconWindow(screenSwitch, onImage);
            selectImageONWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  String imageOnUrl = be.getData();
                  if (onImage != null) {
                     onImage.setSrc(imageOnUrl);
                  } else {
                     uiSwitch.setOnImage(new UImage(imageOnUrl));
                  }
                  screenSwitch.setIcon(imageOnUrl);
               }
            });
         }
      });
      AdapterField adapterImageON = new AdapterField(imageON);
      adapterImageON.setFieldLabel("Image(ON)");

      Button imageOFF = new Button("Select");
      imageOFF.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            final UImage offImage = uiSwitch.getOffImage();
            ChangeIconWindow selectImageOFFWindow = new ChangeIconWindow(screenSwitch, offImage);
            selectImageOFFWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  String imageOffUrl = be.getData();
                  if (offImage != null) {
                     offImage.setSrc(imageOffUrl);
                  } else {
                     uiSwitch.setOffImage(new UImage(imageOffUrl));
                  }
               }
            });

         }
      });
      AdapterField adapterImageOFF = new AdapterField(imageOFF);
      adapterImageOFF.setFieldLabel("Image(OFF)");

      Button switchCommand = new Button("Select");
      if (uiSwitch.getSwitchCommand() != null) {
         switchCommand.setText(uiSwitch.getSwitchCommand().getDisplayName());
      }
      switchCommand.addSelectionListener(createSelectionListener(uiSwitch, switchCommand));
      AdapterField adapterSwitchCommand = new AdapterField(switchCommand);
      adapterSwitchCommand.setFieldLabel("Command");
      adapterSwitchCommand.setAutoHeight(true);

      add(adapterImageON);
      add(adapterImageOFF);
      add(adapterSwitchCommand);
   }

   /**
    * @param uiSwitch
    * @param command
    * @return
    */
   private SelectionListener<ButtonEvent> createSelectionListener(final UISwitch uiSwitch, final Button command) {
      return new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            SelectSwitchWindow selectSwitchWindow = new SelectSwitchWindow();
            selectSwitchWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  BeanModel dataModel = be.<BeanModel> getData();
                  if (dataModel.getBean() instanceof Switch) {
                     Switch switchCommand = dataModel.getBean();
                     uiSwitch.setSwitchCommand(switchCommand);
                     command.setText(switchCommand.getDisplayName());
                  }
               }
            });
         
         }
      };
   }
}

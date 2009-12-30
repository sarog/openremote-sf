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
import org.openremote.modeler.client.widget.component.ScreenLabel;
import org.openremote.modeler.client.widget.uidesigner.SelectSensorWindow;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.component.UILabel;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.TextField;

/**
 * A panel for display screen label properties.
 */
public class LabelPropertyForm extends PropertyForm {

   public LabelPropertyForm(ScreenLabel screebLabel) {
      super();
      addFields(screebLabel);
   }
   private void addFields(final ScreenLabel screenLabel) {
      final TextField<String> textField = new TextField<String>();
      textField.setFieldLabel("Text");
      final UILabel uiLabel = screenLabel.getUiLabel();
      textField.setValue(uiLabel.getText());
      textField.addListener(Events.Blur, new Listener<BaseEvent>() {
         @Override
         public void handleEvent(BaseEvent be) {
            screenLabel.setText(textField.getValue());
         }
      });
      
     /* final TextField<String> font = new TextField<String>();
      font.setFieldLabel("Font");
      font.setValue(uiLabel.getFont());
      font.addListener(Events.Blur, new Listener<BaseEvent>() {
         @Override
         public void handleEvent(BaseEvent be) {
            uiLabel.setFont(font.getValue());
            //TODO set font 
         }
      });*/
      final TextField<String> colorField = new TextField<String>();
      colorField.setFieldLabel("Color");
      colorField.setValue(uiLabel.getColor());
      colorField.addListener(Events.Blur, new Listener<BaseEvent>() {
         @Override
         public void handleEvent(BaseEvent be) {
           screenLabel.setColor(colorField.getValue());
         }
      });
      
      final TextField<String> fontSizeField = new TextField<String>();
      fontSizeField.setFieldLabel("Font-Size");
      fontSizeField.setValue(uiLabel.getFontSize()+"");
      fontSizeField.addListener(Events.Blur, new Listener<BaseEvent>() {
         @Override
         public void handleEvent(BaseEvent be) {
           screenLabel.setFontSize(Integer.parseInt(fontSizeField.getValue()));
         }
      });
      final Button sensorSelectBtn = new Button("Select");
      sensorSelectBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            SelectSensorWindow selectSensorWindow = new SelectSensorWindow();
            selectSensorWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  BeanModel dataModel = be.<BeanModel> getData();
                  Sensor sensor = dataModel.getBean();
                  uiLabel.setSensor(sensor);
                  sensorSelectBtn.setText(sensor.getDisplayName());
               }
            });
         }
      });
      
      add(textField);
      add(colorField);
      add(fontSizeField);
      AdapterField adapter = new AdapterField(sensorSelectBtn);
      adapter.setFieldLabel("select sensor");
      add(adapter);
   }
}

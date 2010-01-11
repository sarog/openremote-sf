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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.utils.SensorLinker;
import org.openremote.modeler.client.widget.component.ScreenLabel;
import org.openremote.modeler.client.widget.uidesigner.SelectColorWindow;
import org.openremote.modeler.client.widget.uidesigner.SelectSensorWindow;
import org.openremote.modeler.domain.CustomSensor;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.domain.State;
import org.openremote.modeler.domain.component.UILabel;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

/**
 * A panel for display screen label properties.
 * @author Javen
 */
public class LabelPropertyForm extends PropertyForm {
   
   private ScreenLabel screenLabel;
   private FieldSet optionPanel; 
   
   
   public LabelPropertyForm(ScreenLabel screenLabel) {
      super();
      this.screenLabel = screenLabel;
      addFields();
      createSensorOption();
   }
   private void addFields() {
      final TextField<String> textField = new TextField<String>();
      textField.setFieldLabel("Text");
      textField.setAllowBlank(false);
      final UILabel uiLabel = screenLabel.getUiLabel();
      textField.setValue(uiLabel.getText());
      textField.addListener(Events.Blur, new Listener<BaseEvent>() {
         @Override
         public void handleEvent(BaseEvent be) {
            if(textField.getValue()!=null&&textField.getValue().trim().length()!=0)
            screenLabel.setText(textField.getValue());
         }
      });
      
      final TextField<String> fontSizeField = new TextField<String>();
      fontSizeField.setFieldLabel("Font-Size");
      fontSizeField.setValue(uiLabel.getFontSize()+"");
      fontSizeField.setValidator(new Validator(){

		@Override
		public String validate(Field<?> field, String value) {
			if(value.matches("[a-z,A-Z]+")){
				return "Only number can allowed here";
			}
			return null;
		}
    	  
      });
      fontSizeField.addListener(Events.Blur, new Listener<BaseEvent>() {
         @Override
         public void handleEvent(BaseEvent be) {
            String value = fontSizeField.getValue();
            if (value != null && !value.matches("[a-z,A-Z]+")) {
               screenLabel.setFontSize(Integer.parseInt(fontSizeField.getValue()));
            }
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
                  createSensorOption();
               }
            });
         }
      });
      if(screenLabel.getUiLabel().getSensor()!=null){
         sensorSelectBtn.setText(screenLabel.getUiLabel().getSensor().getDisplayName());
      }
      final Button colorSelectBtn = new Button("Select");
      colorSelectBtn.setStyleAttribute("border", "2px solid #"+screenLabel.getUiLabel().getColor());
      colorSelectBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            SelectColorWindow selectColorWindow = new SelectColorWindow();
            selectColorWindow.setDefaultColor(screenLabel.getUiLabel().getColor());
            selectColorWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  String color = be.getData();
                  screenLabel.setColor(color);
                  colorSelectBtn.setStyleAttribute("border", "2px solid #"+color);
               }
            });
         }
      });
      
      add(textField);
      add(fontSizeField);
      AdapterField adapter = new AdapterField(sensorSelectBtn);
      adapter.setFieldLabel("Sensor");
      
      AdapterField colorBtnAdapter = new AdapterField(colorSelectBtn);
      colorBtnAdapter.setFieldLabel("Color");
      add(colorBtnAdapter);
      add(adapter);
      
      optionPanel = new FieldSet();
      FormLayout layout = new FormLayout();
      layout.setLabelWidth(80);
      layout.setDefaultWidth(80);
      optionPanel.setLayout(layout);
      optionPanel.setHeading("Sensor option");
      add(optionPanel);
      
   }
   
   private void createSensorOption(){
      optionPanel.removeAll();
      SensorLinker sensorLinker = screenLabel.getUiLabel().getSensorLinker();
      final Map<String,String> sensorAttrs = new HashMap<String,String>();
      if(screenLabel.getUiLabel().getSensor()!=null && screenLabel.getUiLabel().getSensor().getType()==SensorType.SWITCH){
        final TextField<String> onField = new TextField<String>();
        final TextField<String> offField = new TextField<String>();
        
        onField.setFieldLabel("On Text");
        offField.setFieldLabel("Off Text");
        
        onField.setAllowBlank(false);
        offField.setAllowBlank(false);
        if(sensorLinker!=null){
           onField.setValue(sensorLinker.getStateValueByStateName("on"));
           offField.setValue(sensorLinker.getStateValueByStateName("off"));
        }
        onField.addListener(Events.Blur, new Listener<BaseEvent>() {
           @Override
           public void handleEvent(BaseEvent be) {
               String onText = onField.getValue();
               if (onText != null && onText.trim().length() != 0) {
                  sensorAttrs.put("name", "on");
                  sensorAttrs.put("value", onText);
                  screenLabel.getUiLabel().getSensorLinker().addOrUpdateChildForSensorLinker("state", sensorAttrs);
               }
            }
        });
        
        offField.addListener(Events.Blur, new Listener<BaseEvent>() {
           @Override
            public void handleEvent(BaseEvent be) {
               String offText = offField.getValue();
               if (offText != null && offText.trim().length() != 0) {
                  sensorAttrs.put("name", "off");
                  sensorAttrs.put("value", offText);
                  screenLabel.getUiLabel().getSensorLinker().addOrUpdateChildForSensorLinker("state", sensorAttrs);
               }
            }
        });
       
        optionPanel.add(onField);
        optionPanel.add(offField);
      } else if(screenLabel.getUiLabel().getSensor()!=null && screenLabel.getUiLabel().getSensor().getType() == SensorType.CUSTOM){
         CustomSensor customSensor = (CustomSensor) screenLabel.getUiLabel().getSensor();
         List<State> states = customSensor.getStates();
         for(final State state: states){
           final TextField<String> stateTextField = new TextField<String>();
           stateTextField.setFieldLabel(state.getDisplayName());
           stateTextField.setAllowBlank(false);
           if(sensorLinker!=null){
              stateTextField.setValue(sensorLinker.getStateValueByStateName(state.getName()));
           }
           stateTextField.addListener(Events.Blur, new Listener<BaseEvent>(){

            @Override
            public void handleEvent(BaseEvent be) {
               String stateText = stateTextField.getValue();
               if(stateText!=null&&!stateText.trim().isEmpty()){
                  sensorAttrs.put("name", state.getName());
                  sensorAttrs.put("value", stateText);
                  screenLabel.getUiLabel().getSensorLinker().addOrUpdateChildForSensorLinker("state", sensorAttrs);
               }
            }
              
           });
           optionPanel.add(stateTextField);
         }
      }
      optionPanel.layout();
   }
   
}

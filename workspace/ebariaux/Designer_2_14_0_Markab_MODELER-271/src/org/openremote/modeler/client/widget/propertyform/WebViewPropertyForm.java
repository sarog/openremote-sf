/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2011, OpenRemote Inc.
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
import org.openremote.modeler.client.utils.SensorLink;
import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.client.widget.component.ScreenWebView;
import org.openremote.modeler.client.widget.uidesigner.PropertyPanel;
import org.openremote.modeler.client.widget.uidesigner.SelectSensorWindow;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.domain.component.UIWebView;
import org.openremote.modeler.shared.dto.SensorDTO;
import org.openremote.modeler.shared.dto.SensorWithInfoDTO;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

/**
 * WebView property editor panel.
 * 
 * @author Javen
 * @author <a href = "mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class WebViewPropertyForm extends PropertyForm {
   
   private UIWebView uiWebView;
   private FieldSet statesPanel;
   
   public WebViewPropertyForm(ScreenWebView screenWebView, UIWebView uiWebView, WidgetSelectionUtil widgetSelectionUtil) {
      super(screenWebView, widgetSelectionUtil);
      this.uiWebView = uiWebView;
      addFields();
      createSensorStates();
   }
   
   private void addFields() {
      this.setLabelWidth(70);
      this.setFieldWidth(150);
      final TextField<String> urlField = new TextField<String>();
      urlField.setFieldLabel("URL");
      urlField.setAllowBlank(false);
      urlField.setValue(uiWebView.getURL());
      urlField.addListener(Events.Blur, new Listener<BaseEvent>() {
         @Override
         public void handleEvent(BaseEvent be) {
            if(urlField.getValue()!=null&&urlField.getValue().trim().length()!=0)
            uiWebView.setURL(urlField.getValue());
         }
      });
      add(urlField);
      
      final TextField<String> usernameField = new TextField<String>();
      usernameField.setFieldLabel("UserName");
      usernameField.setAllowBlank(true);
      usernameField.setValue(uiWebView.getUserName());
      usernameField.addListener(Events.Blur, new Listener<BaseEvent>() {
          @Override
          public void handleEvent(BaseEvent be) {
             if (usernameField.getValue() !=null && usernameField.getValue().trim().length() != 0) {
               uiWebView.setUserName(usernameField.getValue());
             }
          }
       });      
      add(usernameField); 
      
      final TextField<String> passwordField = new TextField<String>();
      passwordField.setFieldLabel("Password");
      passwordField.setAllowBlank(true);
      passwordField.setValue(uiWebView.getPassword());
      passwordField.addListener(Events.Blur, new Listener<BaseEvent>() {
          @Override
          public void handleEvent(BaseEvent be) {
             if (passwordField.getValue() !=null && passwordField.getValue().trim().length() != 0) {
               uiWebView.setPassword(passwordField.getValue());
             }
          }
       });         
      add(passwordField);       
      
      final Button sensorSelectBtn = new Button("Select");
      sensorSelectBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            SelectSensorWindow selectSensorWindow = new SelectSensorWindow();
            selectSensorWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  BeanModel dataModel = be.<BeanModel> getData();
                  SensorWithInfoDTO sensorDTO = dataModel.getBean();
                  uiWebView.setSensorDTOAndInitSensorLink(sensorDTO);
                  sensorSelectBtn.setText(sensorDTO.getDisplayName());
                  if (sensorDTO.getType() == SensorType.SWITCH || sensorDTO.getType()==SensorType.CUSTOM) {
                     statesPanel.show();
                     createSensorStates();
                  } else {
                     statesPanel.hide();
                  }
               }
            });
         }
      });
      if (uiWebView.getSensorDTO() != null) {
         sensorSelectBtn.setText(uiWebView.getSensorDTO().getDisplayName());
      }
     
      AdapterField adapter = new AdapterField(sensorSelectBtn);
      adapter.setFieldLabel("Sensor");
      add(adapter);

      statesPanel = new FieldSet();
      FormLayout layout = new FormLayout();
      layout.setLabelWidth(65);
      layout.setDefaultWidth(145);
      statesPanel.setLayout(layout);
      statesPanel.setHeading("Sensor State");
      add(statesPanel);
      
      SensorWithInfoDTO sensor = uiWebView.getSensorDTO();
      if (sensor == null) {
         statesPanel.hide();
      } else if (sensor.getType() != SensorType.SWITCH && sensor.getType() != SensorType.CUSTOM) {
         statesPanel.hide();
      }
      
   }
   
   private TextField<String> createTextFieldForState(SensorLink sensorLink, final String stateName) {
     final TextField<String> textField = new TextField<String>();
     final Map<String,String> sensorAttrs = new HashMap<String, String>();

     textField.setAllowBlank(false);
     if (sensorLink != null) {
        textField.setValue(sensorLink.getStateValueByStateName(stateName));
     }
     textField.addListener(Events.Blur, new Listener<BaseEvent>() {
        @Override
        public void handleEvent(BaseEvent be) {
            String onText = textField.getValue();
            if (onText != null && onText.trim().length() != 0) {
               sensorAttrs.put("name", stateName);
               sensorAttrs.put("value", onText);
               uiWebView.getSensorLink().addOrUpdateChildForSensorLinker("state", sensorAttrs);
            }
         }
     });
     return textField;
   }
   
   private void createSensorStates() {
      statesPanel.removeAll();
      SensorLink sensorLink = uiWebView.getSensorLink();
      if (uiWebView.getSensorDTO() != null && uiWebView.getSensorDTO().getType() == SensorType.SWITCH) {
        final TextField<String> onField = createTextFieldForState(sensorLink, "on");
        onField.setFieldLabel("On Text");
        statesPanel.add(onField);

        final TextField<String> offField = createTextFieldForState(sensorLink, "off");
        offField.setFieldLabel("Off Text");
        statesPanel.add(offField);
      } else if (uiWebView.getSensorDTO() != null && uiWebView.getSensorDTO().getType() == SensorType.CUSTOM) {
        SensorWithInfoDTO customSensor = uiWebView.getSensorDTO();
        List<String> stateNames = customSensor.getStateNames();
        for (final String stateName: stateNames) {
          final TextField<String> stateTextField = createTextFieldForState(sensorLink, stateName);
           stateTextField.setFieldLabel(stateName);
           statesPanel.add(stateTextField);
         }
      }
      statesPanel.layout();
   }
   
   @Override
   public String getPropertyFormTitle() {
     return "Web View properties";
   }
}

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
import org.openremote.modeler.domain.CustomSensor;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.domain.State;
import org.openremote.modeler.domain.component.UIWebView;

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
   
   private ScreenWebView screenWebView;
   private UIWebView uiWebView;
   private FieldSet statesPanel;
   
   public WebViewPropertyForm(ScreenWebView screenWebView, UIWebView uiWebView, WidgetSelectionUtil widgetSelectionUtil) {
      super(screenWebView, widgetSelectionUtil);
      this.screenWebView = screenWebView;
      this.uiWebView = uiWebView;
      addFields();
      createSensorStates();
      super.addDeleteButton();
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
            screenWebView.setText(urlField.getValue());
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
                  Sensor sensor = dataModel.getBean();
                  uiWebView.setSensorAndInitSensorLink(sensor);
                  sensorSelectBtn.setText(sensor.getDisplayName());
                  if (sensor.getType() == SensorType.SWITCH || sensor.getType() == SensorType.CUSTOM) {
                     statesPanel.show();
                     createSensorStates();
                  } else {
                     statesPanel.hide();
                  }
                  screenWebView.clearSensorStates();
               }
            });
         }
      });
      if (uiWebView.getSensor() != null) {
         sensorSelectBtn.setText(uiWebView.getSensor().getDisplayName());
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
      
      Sensor sensor = uiWebView.getSensor();
      if (sensor == null) {
         statesPanel.hide();
      } else if (sensor.getType() != SensorType.SWITCH && sensor.getType() != SensorType.CUSTOM) {
         statesPanel.hide();
      }
      
   }
   
   private void createSensorStates() {
      statesPanel.removeAll();
      SensorLink sensorLink = uiWebView.getSensorLink();
      final Map<String,String> sensorAttrs = new HashMap<String, String>();
      if (uiWebView.getSensor() != null && uiWebView.getSensor().getType() == SensorType.SWITCH) {
        final TextField<String> onField = new TextField<String>();
        final TextField<String> offField = new TextField<String>();
        
        onField.setFieldLabel("On Text");
        offField.setFieldLabel("Off Text");
        
        onField.setAllowBlank(false);
        offField.setAllowBlank(false);
        if (sensorLink != null) {
           onField.setValue(sensorLink.getStateValueByStateName("on"));
           offField.setValue(sensorLink.getStateValueByStateName("off"));
        }
        onField.addListener(Events.Blur, new Listener<BaseEvent>() {
           @Override
           public void handleEvent(BaseEvent be) {
               String onText = onField.getValue();
               if (onText != null && onText.trim().length() != 0) {
                  sensorAttrs.put("name", "on");
                  sensorAttrs.put("value", onText);
                  uiWebView.getSensorLink().addOrUpdateChildForSensorLinker("state", sensorAttrs);
               }
               screenWebView.clearSensorStates();
            }
        });
        
        offField.addListener(Events.Blur, new Listener<BaseEvent>() {
           @Override
            public void handleEvent(BaseEvent be) {
               String offText = offField.getValue();
               if (offText != null && offText.trim().length() != 0) {
                  sensorAttrs.put("name", "off");
                  sensorAttrs.put("value", offText);
                  uiWebView.getSensorLink().addOrUpdateChildForSensorLinker("state", sensorAttrs);
               }
               screenWebView.clearSensorStates();
            }
        });
       
        statesPanel.add(onField);
        statesPanel.add(offField);
      } else if (uiWebView.getSensor() != null && uiWebView.getSensor().getType() == SensorType.CUSTOM) {
         CustomSensor customSensor = (CustomSensor) uiWebView.getSensor();
         List<State> states = customSensor.getStates();
         for(final State state: states){
           final TextField<String> stateTextField = new TextField<String>();
           stateTextField.setFieldLabel(state.getDisplayName());
           stateTextField.setAllowBlank(false);
           if(sensorLink!=null){
              stateTextField.setValue(sensorLink.getStateValueByStateName(state.getName()));
           }
           stateTextField.addListener(Events.Blur, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
               String stateText = stateTextField.getValue();
               if (stateText != null && !stateText.trim().isEmpty()) {
                  sensorAttrs.put("name", state.getName());
                  sensorAttrs.put("value", stateText);
                  uiWebView.getSensorLink().addOrUpdateChildForSensorLinker("state", sensorAttrs);
               }
               screenWebView.clearSensorStates();
            }              
           });
           statesPanel.add(stateTextField);
         }
      }
      statesPanel.layout();
   }
   
   @Override
   protected void afterRender() {
      super.afterRender();
      ((PropertyPanel)this.getParent()).setHeading("Label properties");
   }
}

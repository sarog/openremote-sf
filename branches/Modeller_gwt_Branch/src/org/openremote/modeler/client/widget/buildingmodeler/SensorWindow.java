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
package org.openremote.modeler.client.widget.buildingmodeler;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.listener.FormResetListener;
import org.openremote.modeler.client.listener.FormSubmitListener;
import org.openremote.modeler.client.model.ComboBoxDataModel;
import org.openremote.modeler.client.proxy.SensorBeanModelProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.FormWindow;
import org.openremote.modeler.client.widget.SimpleComboBox;
import org.openremote.modeler.client.widget.TreePanelBuilder;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.SensorType;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;

public class SensorWindow extends FormWindow {

   private BeanModel sensorModel = null;
   private TextField<String> nameField = new TextField<String>();
   private TreePanel<BeanModel> commandSelectTree = null;
   private SimpleComboBox typeList = new SimpleComboBox();
   public SensorWindow() {
      setHeading("New sensor");
      init();
      show();
   }
   public SensorWindow(BeanModel sensorModel) {
      this.sensorModel = sensorModel;
      setHeading("Edit sensor");
   }
   
   private void init() {
      setWidth(360);
      setAutoHeight(true);
      setLayout(new FlowLayout());
//      FormLayout formLayout = new FormLayout();
//      formLayout.setLabelAlign(LabelAlign.TOP);
//      formLayout.setLabelWidth(200);
//      formLayout.setDefaultWidth(350);
//      setLayout(formLayout);
      form.setFrame(true);
      form.setHeaderVisible(false);
      form.setBorders(false);
      form.setLabelWidth(60);
      createFields();
      createButtons();
      add(form);
      addListenerToForm();
   }
   
   private void createFields() {
      nameField.setFieldLabel("Name");
      nameField.setAllowBlank(false);
      if (sensorModel != null) {
         nameField.setValue(((Sensor)sensorModel.getBean()).getName());
      }
      
      AdapterField commandField = new AdapterField(createCommandTreeView());
      commandField.setFieldLabel("Command");
      commandField.setBorders(true);
      
      SensorType[] sensorTypes = SensorType.values();
      typeList.setFieldLabel("Type");
      typeList.setEmptyText("--type--");
      for (int i = 0; i < sensorTypes.length; i++) {
         ComboBoxDataModel<SensorType> typeItem = new ComboBoxDataModel<SensorType>(sensorTypes[i].toString(),
               sensorTypes[i]);
         typeList.getStore().add(typeItem);
      }
      
      
      form.add(nameField);
      form.add(commandField);
      form.add(typeList);
      
      final FieldSet rangeSet = createRangeSet();
      form.add(rangeSet);
      rangeSet.hide();
      
      typeList.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
         @SuppressWarnings("unchecked")
         public void selectionChanged(SelectionChangedEvent<ModelData> se) {
            SensorType type = ((ComboBoxDataModel<SensorType>)se.getSelectedItem()).getData();
            if (type == SensorType.RANGE) {
               rangeSet.show();
            } else if(type == SensorType.CUSTOM) {
               rangeSet.hide();
            } else {
               rangeSet.hide();
            }
         }
         
      });
   }
   
   private ContentPanel createCommandTreeView() {
      ContentPanel deviceCommandTreeContainer = new ContentPanel();
      deviceCommandTreeContainer.setHeaderVisible(false);
      deviceCommandTreeContainer.setSize(210, 120);
      deviceCommandTreeContainer.setLayout(new FitLayout());
      deviceCommandTreeContainer.setScrollMode(Scroll.AUTO);
      deviceCommandTreeContainer.setStyleAttribute("backgroundColor", "white");
      commandSelectTree = TreePanelBuilder.buildDeviceCommandTree();
      commandSelectTree.getSelectionModel().deselectAll();
      deviceCommandTreeContainer.add(commandSelectTree);
      
      if (null != sensorModel) {
         if (sensorModel.getBean() instanceof Sensor) {
            Sensor sensor = sensorModel.getBean();
            if (sensor.getDeviceCommandRef() != null) {
               BeanModel deviceCommandModel = sensor.getDeviceCommandRef().getDeviceCommand().getBeanModel();
               commandSelectTree.setExpanded(deviceCommandModel, true);
               commandSelectTree.getSelectionModel().select(deviceCommandModel, false);
            }
         }
      }
      return deviceCommandTreeContainer;
   }
   
   private FieldSet createRangeSet() {
      FieldSet rangeSet = new FieldSet();
      FormLayout layout = new FormLayout();
      layout.setLabelWidth(80);
      rangeSet.setLayout(layout);
      rangeSet.setHeading("Range properties");
      
      TextField<Integer> minField = new TextField<Integer>();
      minField.setFieldLabel("Min");
      minField.setAllowBlank(false);
      
      TextField<Integer> maxField = new TextField<Integer>();
      maxField.setFieldLabel("max");
      maxField.setAllowBlank(false);
      
      rangeSet.add(minField);
      rangeSet.add(maxField);
      
      return rangeSet;
   }
   private void createButtons() {
      Button submitBtn = new Button("Submit");
      Button resetBtn = new Button("Reset");

      submitBtn.addSelectionListener(new FormSubmitListener(form));
      resetBtn.addSelectionListener(new FormResetListener(form));

      form.addButton(submitBtn);
      form.addButton(resetBtn);
   }
   
   private void addListenerToForm() {
      form.addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
         @SuppressWarnings("unchecked")
         public void handleEvent(FormEvent be) {
            if (null == sensorModel) {
               Sensor sensor = new Sensor();
               sensor.setName(nameField.getValue());
               if (typeList.getValue() != null) {
                  sensor.setType(((ComboBoxDataModel<SensorType>)typeList.getValue()).getData());
               }
               BeanModel selectedCommand = commandSelectTree.getSelectionModel().getSelectedItem();
               if (selectedCommand != null && selectedCommand.getBean() instanceof DeviceCommand) {
                  sensor.setDeviceCommandRef(new DeviceCommandRef((DeviceCommand)selectedCommand.getBean()));
               }
               SensorBeanModelProxy.saveSensor(sensor, new AsyncSuccessCallback<Sensor>() {
                  public void onSuccess(Sensor result) {
                     fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(result));
                  }
                  
               });
            }
         }

      });
   }
}

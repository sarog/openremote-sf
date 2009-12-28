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

import java.util.List;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.listener.FormResetListener;
import org.openremote.modeler.client.listener.FormSubmitListener;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.model.ComboBoxDataModel;
import org.openremote.modeler.client.proxy.SensorBeanModelProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.FormWindow;
import org.openremote.modeler.client.widget.SimpleComboBox;
import org.openremote.modeler.client.widget.TreePanelBuilder;
import org.openremote.modeler.domain.CustomSensor;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.RangeSensor;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.SensorCommandRef;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.domain.State;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;

public class SensorWindow extends FormWindow {

   private BeanModel sensorModel = null;
   private TextField<String> nameField = new TextField<String>();
   private TreePanel<BeanModel> commandSelectTree = null;
   private SimpleComboBox typeList = new SimpleComboBox();
   private FieldSet customFieldSet = null;
   private TextField<Integer> minField = new TextField<Integer>();
   private TextField<Integer> maxField = new TextField<Integer>();
   private ListView<BeanModel> stateItemListView = new ListView<BeanModel>();
   
   public SensorWindow() {
      setHeading("New sensor");
      init();
      show();
   }
   public SensorWindow(BeanModel sensorModel) {
      this.sensorModel = sensorModel;
      setHeading("Edit sensor");
      init();
      show();
   }
   
   private void init() {
      setWidth(360);
      setAutoHeight(true);
      setLayout(new FlowLayout());
      form.setFrame(true);
      form.setHeaderVisible(false);
      form.setBorders(false);
      form.setLabelWidth(60);
      form.setFieldWidth(230);
      createButtons();
      createFields();
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
               if (customFieldSet != null) {
                  customFieldSet.hide();
               }
               rangeSet.show();
            } else if(type == SensorType.CUSTOM) {
               rangeSet.hide();
               if (customFieldSet == null) {
                  customFieldSet = createCustomSet();
                  form.add(customFieldSet);
               }
               customFieldSet.show();
            } else {
               if (customFieldSet != null) {
                  customFieldSet.hide();
               }
               rangeSet.hide();
            }
            form.layout();
         }
         
      });
      
      if (sensorModel != null) {
         Sensor sensor = sensorModel.getBean();
//         if (sensor.getDeviceCommandRef() != null) {
//            BeanModel selectedCommandModel = sensor.getDeviceCommandRef().getDeviceCommand().getBeanModel();
//            commandSelectTree.setExpanded(selectedCommandModel, true);
//            commandSelectTree.getSelectionModel().select(selectedCommandModel, false);
//         }
         if (sensor.getType() != null) {
            typeList.setValue(new ComboBoxDataModel<SensorType>(sensor.getType().toString(), sensor.getType()));
            if (sensor.getType() == SensorType.RANGE) {
               minField.setValue(((RangeSensor)sensor).getMin());
               maxField.setValue(((RangeSensor)sensor).getMax());
               minField.disable();
               maxField.disable();
            } else if (sensor.getType() == SensorType.CUSTOM) {
               List<State> states = ((CustomSensor)sensor).getStates();
               for (State state: states) {
                  stateItemListView.getStore().add(state.getBeanModel());
               }
               customFieldSet.disable();
            }
         }
         commandSelectTree.disable();
         typeList.disable();
         
      }
   }
   
   private ContentPanel createCommandTreeView() {
      ContentPanel deviceCommandTreeContainer = new ContentPanel();
      deviceCommandTreeContainer.setHeaderVisible(false);
      deviceCommandTreeContainer.setSize(230, 120);
      deviceCommandTreeContainer.setLayout(new FitLayout());
      deviceCommandTreeContainer.setScrollMode(Scroll.AUTO);
      deviceCommandTreeContainer.setStyleAttribute("backgroundColor", "white");
      commandSelectTree = TreePanelBuilder.buildDeviceCommandTree();
      commandSelectTree.getSelectionModel().deselectAll();
      deviceCommandTreeContainer.add(commandSelectTree);
      
      return deviceCommandTreeContainer;
   }
   
   private FieldSet createRangeSet() {
      FieldSet rangeSet = new FieldSet();
      rangeSet.setWidth(300);
      FormLayout layout = new FormLayout();
      layout.setLabelWidth(50);
      rangeSet.setLayout(layout);
      rangeSet.setHeading("Range properties");
      
      minField.setFieldLabel("Min");
      minField.setAllowBlank(false);
      minField.setValue(0);
      
      maxField.setFieldLabel("max");
      maxField.setAllowBlank(false);
      maxField.setValue(1);
      
      rangeSet.add(minField);
      rangeSet.add(maxField);
      
      return rangeSet;
   }
   
   private FieldSet createCustomSet() {
      FieldSet customSet = new FieldSet();
      customSet.setHeading("Custom state items");
      customSet.setWidth(300);
      
      LayoutContainer customStatesContainer = new LayoutContainer();
      customStatesContainer.setBorders(false);
      customStatesContainer.setSize(280, 110);
      HBoxLayout tabbarContainerLayout = new HBoxLayout();
      tabbarContainerLayout.setHBoxLayoutAlign(HBoxLayoutAlign.TOP);
      customStatesContainer.setLayout(tabbarContainerLayout);
      
      ContentPanel stateItemsContainer = new ContentPanel();
      stateItemsContainer.setBorders(false);
      stateItemsContainer.setBodyBorder(false);
      stateItemsContainer.setHeaderVisible(false);
      stateItemsContainer.setWidth(200);
      stateItemsContainer.setHeight(100);
      stateItemsContainer.setLayout(new FitLayout());
      stateItemsContainer.setScrollMode(Scroll.AUTO);
      
      ListStore<BeanModel> store = new ListStore<BeanModel>();
      stateItemListView.setStore(store);
      stateItemListView.setDisplayProperty("displayName");
      stateItemsContainer.add(stateItemListView);
      
      LayoutContainer buttonsContainer = new LayoutContainer();
      buttonsContainer.setSize(80, 100);
      buttonsContainer.setBorders(false);
      buttonsContainer.setLayout(new RowLayout(Orientation.VERTICAL));
      
      Button addItemBtn = new Button("Add");
      addItemBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            StateWindow stateWindow = new StateWindow();
            stateWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               public void afterSubmit(SubmitEvent be) {
                  stateItemListView.getStore().add(be.<State>getData().getBeanModel());
               }
            });
         }
      });
      
      Button deleteItemBtn = new Button("Delete");
      deleteItemBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            BeanModel selectedItem = stateItemListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
               stateItemListView.getStore().remove(selectedItem);
            }
         }
      });
      
      
      buttonsContainer.add(addItemBtn, new RowData(80, -1, new Margins(10)));
      buttonsContainer.add(deleteItemBtn, new RowData(80, -1, new Margins(10)));
      
      customStatesContainer.add(stateItemsContainer);
      customStatesContainer.add(buttonsContainer);
      
      AdapterField statesField = new AdapterField(customStatesContainer);
      statesField.setHideLabel(true);
      customSet.add(statesField);
      return customSet;
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
               if (typeList.getValue() != null) {
                  SensorType type = ((ComboBoxDataModel<SensorType>)typeList.getValue()).getData();
                  if (type == SensorType.RANGE) {
                     sensor = new RangeSensor();
                     ((RangeSensor)sensor).setMin(Integer.valueOf(minField.getRawValue()));
                     ((RangeSensor)sensor).setMax(Integer.valueOf(maxField.getRawValue()));
                  } else if (type == SensorType.CUSTOM) {
                     sensor = new CustomSensor();
                     List<BeanModel> states = stateItemListView.getStore().getModels();
                     for (BeanModel stateModel : states) {
                        State state = stateModel.getBean();
                        state.setSensor((CustomSensor)sensor);
                        ((CustomSensor)sensor).addState(state);
                     }
                  }
                  sensor.setType(type);
               }
               sensor.setName(nameField.getValue());
               BeanModel selectedCommand = commandSelectTree.getSelectionModel().getSelectedItem();
               if (selectedCommand != null && selectedCommand.getBean() instanceof DeviceCommand) {
                  SensorCommandRef sensorCommandRef = new SensorCommandRef();
                  sensorCommandRef.setDeviceCommand((DeviceCommand)selectedCommand.getBean());
                  sensorCommandRef.setSensor(sensor);
                  sensor.setSensorCommandRef(sensorCommandRef);
               }
               SensorBeanModelProxy.saveSensor(sensor, new AsyncSuccessCallback<Sensor>() {
                  public void onSuccess(Sensor result) {
                     fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(result));
                  }
                  
               });
            } else if (null != sensorModel) {
               Sensor sensor = sensorModel.getBean();
               sensor.setName(nameField.getValue());
               SensorBeanModelProxy.updateSensor(sensor, new AsyncSuccessCallback<Sensor>() {
                  public void onSuccess(Sensor result) {
                     fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(result));
                  }
               });
            }
         }

      });
   }
   
   private class StateWindow extends Dialog {
      public StateWindow() {
         setHeading("Add state");
         setMinHeight(120);
         setMinWidth(240);
         setModal(true);
         setBorders(true);
         FormLayout layout = new FormLayout();
         layout.setLabelWidth(60);
         layout.setDefaultWidth(160);
         layout.setLabelPad(10);
         setLayout(layout);
         setButtons(Dialog.OK);
         setButtonAlign(HorizontalAlignment.CENTER);
         setHideOnButtonClick(true);
         final TextField<String> stateField = new TextField<String>();
         stateField.setFieldLabel("State");
         stateField.setValue("state1");
         add(stateField);
         addListener(Events.BeforeHide, new Listener<WindowEvent>() {
            public void handleEvent(WindowEvent be) {
               if (be.getButtonClicked() == getButtonById("ok")) {
                  State state = new State();
                  state.setName(stateField.getValue());
                  fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(state));
               }
            }
         });
         this.show();
      }
   }
}

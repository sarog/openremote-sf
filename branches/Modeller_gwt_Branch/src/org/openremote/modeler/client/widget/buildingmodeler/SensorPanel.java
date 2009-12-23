package org.openremote.modeler.client.widget.buildingmodeler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.gxtextends.SelectionServiceExt;
import org.openremote.modeler.client.gxtextends.SourceSelectionChangeListenerExt;
import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.listener.ConfirmDeleteListener;
import org.openremote.modeler.client.listener.EditDelBtnSelectionListener;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.proxy.DeviceMacroBeanModelProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.TreePanelBuilder;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroRef;
import org.openremote.modeler.domain.Sensor;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.data.ChangeEventSupport;
import com.extjs.gxt.ui.client.data.ChangeListener;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.TreeStoreEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;

public class SensorPanel extends ContentPanel {

   private Icons icons = GWT.create(Icons.class);
   private TreePanel<BeanModel> sensorTree = null;
   private LayoutContainer sensorTreeContainer = null;
   private Map<BeanModel, ChangeListener> changeListenerMap = null;
   private SelectionServiceExt<BeanModel> selectionService;
   
   public SensorPanel() {
      setHeading("Sensor");
      setLayout(new FitLayout());
      selectionService = new SelectionServiceExt<BeanModel>();
      createMenu();
      createSensorTree();
      setIcon(icons.macroIcon());
//      getHeader().ensureDebugId(DebugId.DEVICE_MACRO_PANEL_HEADER);
   }

   private void createMenu() {
      ToolBar sensorToolBar = new ToolBar();
      Button newSensorBtn = new Button("New");
      newSensorBtn.setToolTip("Create Sensor");
      newSensorBtn.setIcon(icons.macroAddIcon());
//      newMacroBtn.ensureDebugId(DebugId.NEW_MACRO_BTN);
      newSensorBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
//            final MacroWindow macroWindow = new MacroWindow();
//
//            macroWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
//               @Override
//               public void afterSubmit(SubmitEvent be) {
//                  afterCreateDeviceMacro(be.<DeviceMacro> getData());
//                  macroWindow.hide();
//               }
//            });
            final SensorWindow sensorWindow = new SensorWindow();
            sensorWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               public void afterSubmit(SubmitEvent be) {
                  Sensor sensor = be.<Sensor> getData();
                  sensorTree.getStore().add(sensor.getBeanModel(), false);
                  sensorTree.setExpanded(sensor.getBeanModel(), true);
                  sensorWindow.hide();
                  Info.display("Info", "Create sensor " + sensor.getName() + " success.");
               }
               
            });
         }
      });
      sensorToolBar.add(newSensorBtn);
      
      List<Button> editDelBtns = new ArrayList<Button>();
      Button editSensorBtn = new Button("Edit");
      editSensorBtn.setEnabled(false);
      editSensorBtn.setToolTip("Edit Sensor");
      editSensorBtn.setIcon(icons.macroEditIcon());
      editSensorBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            onEditDeviceSensorBtnClicked();

         }
      });
      sensorToolBar.add(editSensorBtn);
      Button deleteSensorBtn = new Button("Delete");
      deleteSensorBtn.setEnabled(false);
      deleteSensorBtn.setToolTip("Delete Sensor");
      deleteSensorBtn.setIcon(icons.macroDeleteIcon());
      
      deleteSensorBtn.addSelectionListener(new ConfirmDeleteListener<ButtonEvent>() {
         @Override
         public void onDelete(ButtonEvent ce) {
            onDeleteSensorBtnClicked();
         }
      });
      sensorToolBar.add(deleteSensorBtn);
      editDelBtns.add(editSensorBtn);
      editDelBtns.add(deleteSensorBtn);
      selectionService.addListener(new EditDelBtnSelectionListener(editDelBtns));
      setTopComponent(sensorToolBar);
   }

   /**
    * Creates the macro tree.
    */
   private void createSensorTree() {
      sensorTreeContainer = new LayoutContainer() {
         @Override
         protected void onRender(Element parent, int index) {
            super.onRender(parent, index);
            if (sensorTree == null) {
               sensorTree = TreePanelBuilder.buildSensorTree();
               selectionService.addListener(new SourceSelectionChangeListenerExt(sensorTree.getSelectionModel()));
               selectionService.register(sensorTree.getSelectionModel());
//               addTreeStoreEventListener();
               sensorTreeContainer.add(sensorTree);
            }
            add(sensorTree);
         }
      };
      sensorTreeContainer.setScrollMode(Scroll.AUTO);
      sensorTreeContainer.setStyleAttribute("backgroundColor", "white");
      sensorTreeContainer.setBorders(false);
      sensorTreeContainer.setLayoutOnChange(true);
      sensorTreeContainer.setHeight("100%");
      add(sensorTreeContainer);
   }

   /**
    * Adds the tree store event listener.
    */
   private void addTreeStoreEventListener() {
      sensorTree.getStore().addListener(Store.Add, new Listener<TreeStoreEvent<BeanModel>>() {
         public void handleEvent(TreeStoreEvent<BeanModel> be) {
            addChangeListenerToDragSource(be.getChildren());
         }
      });
      sensorTree.getStore().addListener(Store.DataChanged, new Listener<TreeStoreEvent<BeanModel>>() {
         public void handleEvent(TreeStoreEvent<BeanModel> be) {
            addChangeListenerToDragSource(be.getChildren());
         }
      });
      sensorTree.getStore().addListener(Store.Clear, new Listener<TreeStoreEvent<BeanModel>>() {
         public void handleEvent(TreeStoreEvent<BeanModel> be) {
            removeChangeListenerToDragSource(be.getChildren());
         }
      });
      sensorTree.getStore().addListener(Store.Remove, new Listener<TreeStoreEvent<BeanModel>>() {
         public void handleEvent(TreeStoreEvent<BeanModel> be) {
            removeChangeListenerToDragSource(be.getChildren());
         }
      });
   }

   /**
    * Adds the change listener to drag source.
    * 
    * @param models
    *           the models
    */
   private void addChangeListenerToDragSource(List<BeanModel> models) {
      if (models == null) {
         return;
      }
      for (BeanModel beanModel : models) {
         if (beanModel.getBean() instanceof DeviceMacroRef) {
            BeanModelDataBase.deviceMacroTable.addChangeListener(BeanModelDataBase
                  .getOriginalDeviceMacroItemBeanModelId(beanModel), getDragSourceBeanModelChangeListener(beanModel));
         } else if (beanModel.getBean() instanceof DeviceCommandRef) {
            BeanModelDataBase.deviceCommandTable.addChangeListener(BeanModelDataBase
                  .getOriginalDeviceMacroItemBeanModelId(beanModel), getDragSourceBeanModelChangeListener(beanModel));
            BeanModelDataBase.deviceTable.addChangeListener(BeanModelDataBase.getSourceBeanModelId(beanModel),
                  getDragSourceBeanModelChangeListener(beanModel));
         }
      }
   }

   /**
    * Removes the change listener to drag source.
    * 
    * @param models
    *           the models
    */
   private void removeChangeListenerToDragSource(List<BeanModel> models) {
      if (models == null) {
         return;
      }
      for (BeanModel beanModel : models) {
         if (beanModel.getBean() instanceof DeviceMacroRef) {
            BeanModelDataBase.deviceMacroTable.removeChangeListener(BeanModelDataBase
                  .getOriginalDeviceMacroItemBeanModelId(beanModel), getDragSourceBeanModelChangeListener(beanModel));
         }
         if (beanModel.getBean() instanceof DeviceCommandRef) {
            BeanModelDataBase.deviceCommandTable.removeChangeListener(BeanModelDataBase
                  .getOriginalDeviceMacroItemBeanModelId(beanModel), getDragSourceBeanModelChangeListener(beanModel));
         }
         changeListenerMap.remove(beanModel);
      }
   }

   /**
    * After create device macro.
    * 
    * @param deviceMacro
    *           the device macro
    */
   private void afterCreateDeviceMacro(DeviceMacro deviceMacro) {
      BeanModel deviceBeanModel = deviceMacro.getBeanModel();
      sensorTree.getStore().add(deviceBeanModel, false);
      sensorTree.setExpanded(deviceBeanModel, true);
   }

   /**
    * On edit device macro btn clicked.
    */
   private void onEditDeviceSensorBtnClicked() {
      if (sensorTree.getSelectionModel().getSelectedItem() != null) {
         final BeanModel oldModel = sensorTree.getSelectionModel().getSelectedItem();
         final MacroWindow macroWindow = new MacroWindow(sensorTree.getSelectionModel().getSelectedItem());
         macroWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
            @Override
            public void afterSubmit(SubmitEvent be) {
               afterUpdateDeviceMacroSubmit(oldModel, be.<DeviceMacro> getData());
               macroWindow.hide();
            }
         });
      }
   }

   /**
    * On delete device macro btn clicked.
    */
   private void onDeleteSensorBtnClicked() {
      if (sensorTree.getSelectionModel().getSelectedItems().size() > 0) {
         for (final BeanModel data : sensorTree.getSelectionModel().getSelectedItems()) {
            if (data.getBean() instanceof DeviceMacro) {
               DeviceMacroBeanModelProxy.deleteDeviceMacro(data, new AsyncSuccessCallback<Void>() {
                  @Override
                  public void onSuccess(Void result) {
                     sensorTree.getStore().remove(data);
                     Info.display("Info", "Delete success.");
                  }
               });
            }

         }
      }
   }

   /**
    * After update device macro submit.
    * 
    * @param dataModel
    *           the data model
    * @param deviceMacro
    *           the device macro
    */
   private void afterUpdateDeviceMacroSubmit(final BeanModel dataModel, DeviceMacro deviceMacro) {
      DeviceMacro old = dataModel.getBean();
      old.setName(deviceMacro.getName());
      old.setDeviceMacroItems(deviceMacro.getDeviceMacroItems());
      List<BeanModel> macroItemBeanModels = BeanModelDataBase.getBeanModelsByBeans(deviceMacro.getDeviceMacroItems(),
            BeanModelDataBase.deviceMacroItemTable);
      sensorTree.getStore().removeAll(dataModel);
      for (BeanModel beanModel : macroItemBeanModels) {
         sensorTree.getStore().add(dataModel, beanModel, false);
      }
      sensorTree.getStore().update(dataModel);
      sensorTree.setExpanded(dataModel, true);
   }

   /**
    * Gets the drag source bean model change listener.
    * 
    * @param target
    *           the target
    * 
    * @return the drag source bean model change listener
    */
   private ChangeListener getDragSourceBeanModelChangeListener(final BeanModel target) {
      if (changeListenerMap == null) {
         changeListenerMap = new HashMap<BeanModel, ChangeListener>();
      }
      ChangeListener changeListener = changeListenerMap.get(target);
      if (changeListener == null) {
         changeListener = new ChangeListener() {
            public void modelChanged(ChangeEvent changeEvent) {
               if (changeEvent.getType() == ChangeEventSupport.Remove) {
                  sensorTree.getStore().remove(target);
               }
               if (changeEvent.getType() == ChangeEventSupport.Update) {
                  BeanModel source = (BeanModel) changeEvent.getItem();
                  if (source.getBean() instanceof DeviceMacro) {
                     DeviceMacro deviceMacro = (DeviceMacro) source.getBean();
                     DeviceMacroRef deviceMacroRef = (DeviceMacroRef) target.getBean();
                     deviceMacroRef.setTargetDeviceMacro(deviceMacro);
                  } else if (source.getBean() instanceof DeviceCommand) {
                     DeviceCommand deviceCommand = (DeviceCommand) source.getBean();
                     DeviceCommandRef deviceCommandRef = (DeviceCommandRef) target.getBean();
                     deviceCommandRef.setDeviceCommand(deviceCommand);
                  } else if (source.getBean() instanceof Device) {
                     Device device = (Device) source.getBean();
                     DeviceCommandRef targetDeviceCommandRef = (DeviceCommandRef) target.getBean();
                     targetDeviceCommandRef.setDeviceName(device.getName());
                  }
                  sensorTree.getStore().update(target);
               }
            }
         };
         changeListenerMap.put(target, changeListener);
      }
      return changeListener;
   }


}

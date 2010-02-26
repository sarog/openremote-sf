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
import org.openremote.modeler.client.proxy.DeviceBeanModelProxy;
import org.openremote.modeler.client.proxy.DeviceCommandBeanModelProxy;
import org.openremote.modeler.client.proxy.SensorBeanModelProxy;
import org.openremote.modeler.client.proxy.SliderBeanModelProxy;
import org.openremote.modeler.client.proxy.SwitchBeanModelProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.TreePanelBuilder;
import org.openremote.modeler.domain.CommandRefItem;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.selenium.DebugId;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.data.ChangeEventSupport;
import com.extjs.gxt.ui.client.data.ChangeListener;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.TreeStoreEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The Class DevicePanel.
 */
public class DevicePanel extends ContentPanel {

   /** The tree. */
   private TreePanel<BeanModel> tree;
   
   /** The selection service. */
   private SelectionServiceExt<BeanModel> selectionService;
   
   /** The icon. */
   private Icons icon = GWT.create(Icons.class);
   
   private Map<BeanModel, ChangeListener> changeListenerMap = null;

   /**
    * Instantiates a new device panel.
    */
   public DevicePanel() {
      setHeading("Device");
      setIcon(icon.device());
      setLayout(new FitLayout());
      selectionService = new SelectionServiceExt<BeanModel>();
      createMenu();
      createTreeContainer();
      show();
   }

   /**
    * Creates the tree container.
    */
   private void createTreeContainer() {
      tree = TreePanelBuilder.buildDeviceTree();
      selectionService.addListener(new SourceSelectionChangeListenerExt(tree.getSelectionModel()));
      selectionService.register(tree.getSelectionModel());
      LayoutContainer treeContainer = new LayoutContainer() {
         @Override
         protected void onRender(Element parent, int index) {
            super.onRender(parent, index);
            add(tree);
         }
         
      };
      addTreeStoreEventListener();
      treeContainer.ensureDebugId(DebugId.DEVICE_TREE_CONTAINER);
      treeContainer.setScrollMode(Scroll.AUTO);
      treeContainer.setStyleAttribute("backgroundColor", "white");
      treeContainer.setBorders(false);
      add(treeContainer);
      
   }
   
   /**
    * Creates the menu.
    */
   private void createMenu() {      
      Button newButton = new Button("New");
      newButton.setToolTip("Create Device or DeviceCommand");
      newButton.ensureDebugId(DebugId.DEVICE_NEW_BTN);
      newButton.setIcon(icon.add());
      
      Menu newMenu = new Menu();
      newMenu.add(createNewDeviceMenuItem());
      final MenuItem newCommandMemuItem = createNewCommandMenu();
      final MenuItem importCommandMemuItem = createImportMenuItem();
      final MenuItem newSensorMenuItem = createNewSensorMenu();
      final MenuItem newSliderMenuItem = createNewSliderMenu();
      final MenuItem newSwitchMenuItem = createNewSwitchMenu();
      
      newMenu.add(newCommandMemuItem);
      newMenu.add(importCommandMemuItem);
      newMenu.add(newSensorMenuItem);
      newMenu.add(newSliderMenuItem);
      newMenu.add(newSwitchMenuItem);
      
      newMenu.addListener(Events.BeforeShow, new Listener<MenuEvent>() {
         @Override
         public void handleEvent(MenuEvent be) {
            boolean enabled = false;
            BeanModel selectedBeanModel = tree.getSelectionModel().getSelectedItem();
            if (selectedBeanModel != null && selectedBeanModel.getBean() instanceof Device) {
               enabled = true;
            }
            newCommandMemuItem.setEnabled(enabled);
            importCommandMemuItem.setEnabled(enabled);
            newSensorMenuItem.setEnabled(enabled);
            newSliderMenuItem.setEnabled(enabled);
            newSwitchMenuItem.setEnabled(enabled);
         }
         
      });
      newButton.setMenu(newMenu);
      
      ToolBar toolBar = new ToolBar();
      toolBar.add(newButton);
      
      List<Button> editDelBtns = new ArrayList<Button>(); 
      Button editBtn = createEditButton();
      editBtn.setEnabled(false);
      Button deleteBtn = createDeleteButton();
      deleteBtn.setEnabled(false);
      editDelBtns.add(editBtn);
      editDelBtns.add(deleteBtn);
      
      toolBar.add(editBtn);
      toolBar.add(deleteBtn);
      selectionService.addListener(new EditDelBtnSelectionListener(editDelBtns));
      setTopComponent(toolBar);
   }

   /**
    * Creates the new device menu.
    * 
    * @return the menu item
    */
   private MenuItem createNewDeviceMenuItem() {
      MenuItem newDeviceItem = new MenuItem("New Device");
      newDeviceItem.ensureDebugId(DebugId.NEW_DEVICE_MENU_ITEM);
      newDeviceItem.setIcon(icon.addDevice());
      newDeviceItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         public void componentSelected(MenuEvent ce) {
            final DeviceWizardWindow deviceWindow = new DeviceWizardWindow(new Device().getBeanModel());
            deviceWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  deviceWindow.hide();
                  BeanModel deviceModel = be.getData();
                  tree.getStore().add(deviceModel, true);
                  
                  for (BeanModel deviceCommandModel : DeviceCommand.createModels(((Device) deviceModel.getBean())
                        .getDeviceCommands())) {
                     tree.getStore().add(deviceModel, deviceCommandModel, false);
                  }
                  tree.setExpanded(deviceModel, true);
                  
                  //create and select it.
                  tree.getSelectionModel().select(deviceModel, false);
                  Info.display("Info", "Add device " + deviceModel.get("name") + " success.");
                  
               }
            });
         }
      });
      return newDeviceItem;
   }
   
   /**
    * Creates the new command menu.
    * 
    * @return the menu item
    */
   private MenuItem createNewCommandMenu() {
      MenuItem newCommandItem = new MenuItem("New Command");
      newCommandItem.ensureDebugId(DebugId.NEW_COMMAND_ITEM);
      newCommandItem.setIcon(icon.addCmd());
      newCommandItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         public void componentSelected(MenuEvent ce) {
            createDeviceCommand();
         }

      });
      return newCommandItem;
   }
   
   private MenuItem createNewSensorMenu() {
      MenuItem newCommandItem = new MenuItem("New Sensor");
      newCommandItem.setIcon(icon.sensorAddIcon());
      newCommandItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         public void componentSelected(MenuEvent ce) {
            createSensor();
         }

      });
      return newCommandItem;
   }
   
   private MenuItem createNewSliderMenu() {
      MenuItem newCommandItem = new MenuItem("New Slider");
      newCommandItem.setIcon(icon.sliderAddIcon());
      newCommandItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         public void componentSelected(MenuEvent ce) {
            createSlider();
         }

      });
      return newCommandItem;
   }
   
   private MenuItem createNewSwitchMenu() {
      MenuItem newCommandItem = new MenuItem("New Switch");
      newCommandItem.setIcon(icon.switchAddIcon());
      newCommandItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         public void componentSelected(MenuEvent ce) {
            createSwitch();
         }

      });
      return newCommandItem;
   }
   /**
    * Creates the device command.
    */
   private void createDeviceCommand() {
      final BeanModel deviceModel = tree.getSelectionModel().getSelectedItem();
      if (deviceModel != null && deviceModel.getBean() instanceof Device) {
         final DeviceCommandWindow deviceCommandWindow = new DeviceCommandWindow((Device) deviceModel.getBean());
         deviceCommandWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
            @Override
            public void afterSubmit(SubmitEvent be) {
               BeanModel deviceCommandModel = be.getData();
               tree.getStore().add(deviceModel, deviceCommandModel, false);
               tree.setExpanded(deviceModel, true);
               deviceCommandWindow.hide();
            }
         });
      } else {
         MessageBox.info("Error", "Please select a device", null);
      }
   }
   
   private void createSensor() {
      final BeanModel deviceModel = tree.getSelectionModel().getSelectedItem();
      if (deviceModel != null && deviceModel.getBean() instanceof Device) {
         final SensorWindow sensorWindow = new SensorWindow((Device) deviceModel.getBean());
         sensorWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
            @Override
            public void afterSubmit(SubmitEvent be) {
               Sensor sensor = be.getData();
               tree.getStore().add(deviceModel, sensor.getBeanModel(), false);
               tree.setExpanded(deviceModel, true);
               sensorWindow.hide();
            }
         });
      } else {
         MessageBox.info("Error", "Please select a device", null);
      }
   }
   
   private void createSlider() {
      final BeanModel deviceModel = tree.getSelectionModel().getSelectedItem();
      if (deviceModel != null && deviceModel.getBean() instanceof Device) {
         final SliderWindow sliderWindow = new SliderWindow(null,(Device) deviceModel.getBean());
         sliderWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
            @Override
            public void afterSubmit(SubmitEvent be) {
               BeanModel sliderBeanModel = be.getData();
               tree.getStore().add(deviceModel, sliderBeanModel, false);
               tree.setExpanded(deviceModel, true);
               sliderWindow.hide();
            }
         });
      } else {
         MessageBox.info("Error", "Please select a device", null);
      }
   }
   
   private void createSwitch() {
      final BeanModel deviceModel = tree.getSelectionModel().getSelectedItem();
      if (deviceModel != null && deviceModel.getBean() instanceof Device) {
         final SwitchWindow switchWindow = new SwitchWindow(null,(Device) deviceModel.getBean());
         switchWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
            @Override
            public void afterSubmit(SubmitEvent be) {
               BeanModel switchBeanModel = be.getData();
               tree.getStore().add(deviceModel, switchBeanModel, false);
               tree.setExpanded(deviceModel, true);
               switchWindow.hide();
            }
         });
      } else {
         MessageBox.info("Error", "Please select a device", null);
      }
   }
   /**
    * Creates the edit button.
    * 
    * @return the button
    */
   private Button createEditButton() {
      Button editBtn = new Button("Edit");
      editBtn.setToolTip("Edit Device or DeviceCommand");
      editBtn.ensureDebugId(DebugId.DEVICE_EDIT_BTN);
      editBtn.setIcon(icon.edit());
      editBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         public void componentSelected(ButtonEvent ce) {
            BeanModel selectedModel = tree.getSelectionModel().getSelectedItem();
            if (selectedModel != null && selectedModel.getBean() instanceof Device) {
               editDevice(selectedModel);
            } else if (selectedModel != null && selectedModel.getBean() instanceof DeviceCommand) {
               editCommand(selectedModel);
            } else if (selectedModel != null && selectedModel.getBean() instanceof Sensor){
               editSensor(selectedModel);
            } else if (selectedModel != null && selectedModel.getBean() instanceof Slider){
               editSlider(selectedModel);
            } else if (selectedModel != null && selectedModel.getBean() instanceof Switch){
               editSwitch(selectedModel);
            } 
            
         }
      });
      return editBtn;
   }
   
   /**
    * Edits the device.
    * 
    * @param selectedModel the selected model
    */
   private void editDevice(BeanModel selectedModel) {
      final DeviceWindow editDeviceWindow = new DeviceWindow(selectedModel);
      editDeviceWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
         @Override
         public void afterSubmit(SubmitEvent be) {
            editDeviceWindow.hide();
            BeanModel deviceModel = be.getData();
            tree.getStore().update(deviceModel);
            Info.display("Info", "Edit device " + deviceModel.get("name") + " success.");
         }
      });
   }
   
   /**
    * Edits the command.
    * 
    * @param selectedModel the selected model
    */
   private void editCommand(BeanModel selectedModel) {
      final DeviceCommandWindow deviceCommandWindow = new DeviceCommandWindow((DeviceCommand) selectedModel.getBean());
      deviceCommandWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
         @Override
         public void afterSubmit(SubmitEvent be) {
            BeanModel deviceCommandModel = be.getData();
            tree.getStore().update(deviceCommandModel);
            Info.display("Info", "Edit device command " + deviceCommandModel.get("name") + " success.");
            deviceCommandWindow.hide();
//            tree.collapseAll();
//            tree.expandAll();
         }
      });
   }
   
   private void editSensor(BeanModel selectedModel) {
      final SensorWindow deviceCommandWindow = new SensorWindow(selectedModel);
      deviceCommandWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
         @Override
         public void afterSubmit(SubmitEvent be) {
            Sensor sensor = be.getData();
            tree.getStore().update(sensor.getBeanModel());
            Info.display("Info", "Edit device command " + sensor.getBeanModel().get("name") + " success.");
            deviceCommandWindow.hide();
         }
      });
   }
   
   private void editSlider(final BeanModel selectedModel) {
      Slider slider = selectedModel.getBean();
      final SliderWindow deviceCommandWindow = new SliderWindow(slider);
      deviceCommandWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
         @Override
         public void afterSubmit(SubmitEvent be) {
            BeanModel sliderBeanModel = be.getData();
            Slider slider = sliderBeanModel.getBean();
            tree.getStore().removeAll(selectedModel);
            tree.getStore().add(selectedModel, slider.getSetValueCmd().getBeanModel(), false);
            Info.display("Info", "Edit device command " + sliderBeanModel.get("name") + " success.");
            deviceCommandWindow.hide();
         }
      });
   }
   
   private void editSwitch(final BeanModel selectedModel) {
      Switch swh = selectedModel.getBean();
      final SwitchWindow switchWindow = new SwitchWindow(swh);
      switchWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
         @Override
         public void afterSubmit(SubmitEvent be) {
            BeanModel sliderBeanModel = be.getData();
            Switch swh = sliderBeanModel.getBean();
            tree.getStore().removeAll(selectedModel);
            tree.getStore().add(selectedModel, swh.getSwitchCommandOnRef().getBeanModel(), false);
            tree.getStore().add(selectedModel, swh.getSwitchCommandOffRef().getBeanModel(), false);
            Info.display("Info", "Edit device command " + sliderBeanModel.get("name") + " success.");
            switchWindow.hide();
         }
      });
   }
   /**
    * Creates the delete button.
    * 
    * @return the button
    */
   private Button createDeleteButton() {
      Button deleteBtn = new Button("Delete");
      deleteBtn.setToolTip("Delete Device or DeviceCommand");
      deleteBtn.ensureDebugId(DebugId.DELETE_DEVICE_BUTTON);
      deleteBtn.setIcon(icon.delete());
      deleteBtn.addSelectionListener(new ConfirmDeleteListener<ButtonEvent>() {
         public void onDelete(ButtonEvent ce) {
            List<BeanModel> selectedModels = tree.getSelectionModel().getSelectedItems();
            for (BeanModel selectedModel : selectedModels) {
               if (selectedModel != null && selectedModel.getBean() instanceof Device) {
                  deleteDevice(selectedModel);
               } else if (selectedModel != null && selectedModel.getBean() instanceof DeviceCommand) {
                  deleteCommand(selectedModel);
               } else if (selectedModel != null && selectedModel.getBean() instanceof Sensor){
                  deleteSensor(selectedModel);
               } else if (selectedModel!=null && selectedModel.getBean() instanceof Slider){
                  deleteSlider(selectedModel);
               } else if (selectedModel !=null && selectedModel.getBean() instanceof Switch){
                  deleteSwitch(selectedModel);
               }
            }
         }
      });
      return deleteBtn;
   }
   
   /**
    * Delete device.
    * 
    * @param deviceModel the device model
    */
   private void deleteDevice(final BeanModel deviceModel) {
      DeviceBeanModelProxy.deleteDevice(deviceModel, new AsyncSuccessCallback<Void>() {
         @Override
         public void onSuccess(Void result) {
            tree.getStore().remove(deviceModel);
            Info.display("Info", "Delete success.");
         }
         
      });
   }
   
   /**
    * Delete command.
    * 
    * @param deviceCommnadModel the device commnad model
    */
   private void deleteCommand(final BeanModel deviceCommnadModel) {
      DeviceCommandBeanModelProxy.deleteDeviceCommand(deviceCommnadModel, new AsyncSuccessCallback<Boolean>() {
         @Override
         public void onSuccess(Boolean result) {
            if (result) {
               tree.getStore().remove(deviceCommnadModel);
               Info.display("Info", "Delete success.");
            } else {
               MessageBox.alert("Warn", "The command cann't be delete, because it was refrenced by other sensor, switch or slider.", null);
            }
         }
      });
   }
   
   private void deleteSensor(final BeanModel sensorBeanModel) {
      SensorBeanModelProxy.deleteSensor(sensorBeanModel, new AsyncCallback<Boolean>() {
         @Override
         public void onSuccess(Boolean result) {
            if (result) {
               tree.getStore().remove(sensorBeanModel);
               Info.display("Info", "Delete success.");
            } else {
               MessageBox.alert("Warn", "The command cann't be delete, because it was refrenced by other sensor, switch or slider.", null);
            }
         }
         
         @Override
         public void onFailure(Throwable caught) {
           MessageBox.alert("Error", "The Sensor you are deleting is being used", null);
         }
      });
   }
   
   private void deleteSlider(final BeanModel sensorBeanModel) {
      SliderBeanModelProxy.delete(sensorBeanModel, new AsyncSuccessCallback<Void>() {
         @Override
         public void onSuccess(Void result) {
            tree.getStore().remove(sensorBeanModel);
            return;
         }
      });
   }
   
   private void deleteSwitch(final BeanModel sensorBeanModel) {
      SwitchBeanModelProxy.delete(sensorBeanModel, new AsyncSuccessCallback<Void>() {
         @Override
         public void onSuccess(Void result) {
            tree.getStore().remove(sensorBeanModel);
            return;
         }

         @Override
         public void onFailure(Throwable caught) {
           MessageBox.alert("Error", "The Switch you are deleting is being used", null);
         }
      });
   }
   /**
    * Creates the import menu item.
    * 
    * @return the menu item
    */
   private MenuItem createImportMenuItem() {
      MenuItem importCommandItem = new MenuItem("Import Commands");
      importCommandItem.setIcon(icon.importFromDB());
      importCommandItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         public void componentSelected(MenuEvent ce) {
            importIRCommand();
         }

      });
      return importCommandItem;
   }

   /**
    * Import ir command.
    */
   private void importIRCommand() {
      final BeanModel deviceModel = tree.getSelectionModel().getSelectedItem();
      if (deviceModel != null && deviceModel.getBean() instanceof Device) {
         final IRCommandImportWindow selectIRWindow = new IRCommandImportWindow(deviceModel);
         selectIRWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
            @Override
            public void afterSubmit(SubmitEvent be) {
               List<BeanModel> deviceCommandModels = be.getData();
               for (BeanModel deviceCommandModel : deviceCommandModels) {
                  tree.getStore().add(deviceModel, deviceCommandModel, false);
               }
               tree.setExpanded(deviceModel, true);
               selectIRWindow.hide();
            }
         });
      } else {
         MessageBox.alert("Notice", "You must select a device first.", null);
      }
   }
   
   private void addTreeStoreEventListener() {
      tree.getStore().addListener(Store.Add, new Listener<TreeStoreEvent<BeanModel>>() {
         public void handleEvent(TreeStoreEvent<BeanModel> be) {
            addChangeListenerToDeviceTree(be.getChildren());
         }
      });
      tree.getStore().addListener(Store.DataChanged, new Listener<TreeStoreEvent<BeanModel>>() {
         public void handleEvent(TreeStoreEvent<BeanModel> be) {
            addChangeListenerToDeviceTree(be.getChildren());
         }
      });
      tree.getStore().addListener(Store.Clear, new Listener<TreeStoreEvent<BeanModel>>() {
         public void handleEvent(TreeStoreEvent<BeanModel> be) {
            removeChangeListenerToDragSource(be.getChildren());
         }
      });
      tree.getStore().addListener(Store.Remove, new Listener<TreeStoreEvent<BeanModel>>() {
         public void handleEvent(TreeStoreEvent<BeanModel> be) {
            removeChangeListenerToDragSource(be.getChildren());
         }
      });
   }
   
   private void addChangeListenerToDeviceTree(List<BeanModel> models) {
      if (models == null) {
         return;
      }
      for (BeanModel beanModel : models) {
         if (beanModel.getBean() instanceof CommandRefItem) {
            BeanModelDataBase.deviceCommandTable.addChangeListener(BeanModelDataBase
                  .getOriginalCommandRefItemBeanModelId(beanModel), getDragSourceBeanModelChangeListener(beanModel));
            BeanModelDataBase.deviceTable.addChangeListener(BeanModelDataBase.getSourceBeanModelId(beanModel),
                  getDragSourceBeanModelChangeListener(beanModel));
         }
      }
   }
   
   private void removeChangeListenerToDragSource(List<BeanModel> models) {
      if (models == null) {
         return;
      }
      for (BeanModel beanModel : models) {
         if (beanModel.getBean() instanceof CommandRefItem) {
            BeanModelDataBase.deviceCommandTable.removeChangeListener(BeanModelDataBase
                  .getOriginalCommandRefItemBeanModelId(beanModel), getDragSourceBeanModelChangeListener(beanModel));
         }
         changeListenerMap.remove(beanModel);
      }
   }
   
   private ChangeListener getDragSourceBeanModelChangeListener(final BeanModel target) {
      if (changeListenerMap == null) {
         changeListenerMap = new HashMap<BeanModel, ChangeListener>();
      }
      ChangeListener changeListener = changeListenerMap.get(target);
      if (changeListener == null) {
         changeListener = new ChangeListener() {
            public void modelChanged(ChangeEvent changeEvent) {
               if (changeEvent.getType() == ChangeEventSupport.Remove) {
                  tree.getStore().remove(target);
               }
               if (changeEvent.getType() == ChangeEventSupport.Update) {
                  BeanModel source = (BeanModel) changeEvent.getItem();
                  if (source.getBean() instanceof DeviceCommand) {
                     DeviceCommand deviceCommand = (DeviceCommand) source.getBean();
                     CommandRefItem cmdRefItem = target.getBean();
                     cmdRefItem.setDeviceCommand(deviceCommand);
                  } else if (source.getBean() instanceof Device) {
                     Device device = (Device) source.getBean();
                     CommandRefItem targetCmdRefItem = target.getBean();
                     targetCmdRefItem.setDeviceName(device.getName());
                  }
                  tree.getStore().update(target);
               }
            }
         };
         changeListenerMap.put(target, changeListener);
      }
      return changeListener;
   }
}


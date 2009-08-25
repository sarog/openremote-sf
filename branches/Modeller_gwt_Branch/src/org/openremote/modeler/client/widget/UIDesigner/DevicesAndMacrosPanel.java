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
package org.openremote.modeler.client.widget.uidesigner;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.gxtextends.TreePanelDragSourceMacroDragExt;
import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.model.TreeFolderBean;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.widget.TreePanelBuilder;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroRef;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.data.ChangeEventSupport;
import com.extjs.gxt.ui.client.data.ChangeListener;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.TreeStoreEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;


/**
 * The Class DevicesAndMacrosPanel.
 */
public class DevicesAndMacrosPanel extends ContentPanel {

   /** The icons. */
   private Icons icons = GWT.create(Icons.class);

   /** The devicesAndMacrosTree. */
   private TreePanel<BeanModel> devicesAndMacrosTree = null;

   /** The devicesAndMacrosListContainer. */
   private LayoutContainer devicesAndMacrosTreeContainer = null;

   /** The changeListenerMap. */
   private Map<BeanModel, ChangeListener> changeListenerMap = null;

   /**
    * Instantiates a devicesAndMacrosPanel.
    */
   public DevicesAndMacrosPanel() {
      setHeading("Devices/Macros");
      setLayout(new FitLayout());
      createDevicesAndMacrosTree();
//      setIcon(icons.device());
   }


   /**
    * Creates the devicesAndMacrosTree.
    */
   private void createDevicesAndMacrosTree() {
      initDevicesAndMacrosTreeContainer();
      initDevicesAndMacrosTree();
   }
   
   /**
    * Inits the devicesAndMacrosTreeContainer.
    */
   private void initDevicesAndMacrosTreeContainer() {
      devicesAndMacrosTreeContainer = new LayoutContainer();
      devicesAndMacrosTreeContainer.setScrollMode(Scroll.AUTO);
      devicesAndMacrosTreeContainer.setStyleAttribute("backgroundColor", "white");
      devicesAndMacrosTreeContainer.setBorders(false);
      devicesAndMacrosTreeContainer.setLayoutOnChange(true);
      devicesAndMacrosTreeContainer.setHeight("100%");
      add(devicesAndMacrosTreeContainer);
   }

   /**
    * Inits the devicesAndMacrosTree.
    */
   private void initDevicesAndMacrosTree() {
      if (devicesAndMacrosTree == null) {
         devicesAndMacrosTree = TreePanelBuilder.buildDevicesAndMacrosTree();
         addTreeEventListeners();
         devicesAndMacrosTreeContainer.add(devicesAndMacrosTree);
      }
   }
   
   /**
    * Adds the TreeEventListeners.
    */
   private void addTreeEventListeners() {
      addTreeDragEventListener();
      addTreeStoreEventListener();
   }
   
   /**
    * Adds the TreeDragEventListener.
    */
   private void addTreeDragEventListener() {
      TreePanelDragSourceMacroDragExt dragSource = new TreePanelDragSourceMacroDragExt(devicesAndMacrosTree);
      dragSource.addDNDListener(new DNDListener() {
         @SuppressWarnings("unchecked")
         @Override
         public void dragStart(DNDEvent e) {
            TreePanel<BeanModel> tree = (TreePanel<BeanModel>) e.getComponent();
            BeanModel beanModel = tree.getSelectionModel().getSelectedItem();
            e.setCancelled(true);
            if ((beanModel.getBean() instanceof DeviceCommand) || (beanModel.getBean() instanceof DeviceMacro)) {
               e.setCancelled(false);
            }
            super.dragStart(e);
         }

      });
      dragSource.setGroup(Constants.BUTTON_DND_GROUP);
   } 

   /**
    * Adds the tree store event listener.
    */
   private void addTreeStoreEventListener() {
      devicesAndMacrosTree.getStore().addListener(Store.Add, new Listener<TreeStoreEvent<BeanModel>>() {
         public void handleEvent(TreeStoreEvent<BeanModel> be) {
            addChangeListenerToSource(be.getChildren());
         }
      });
      devicesAndMacrosTree.getStore().addListener(Store.DataChanged, new Listener<TreeStoreEvent<BeanModel>>() {
         public void handleEvent(TreeStoreEvent<BeanModel> be) {
            addChangeListenerToSource(be.getChildren());
         }
      });
      devicesAndMacrosTree.getStore().addListener(Store.Clear, new Listener<TreeStoreEvent<BeanModel>>() {
         public void handleEvent(TreeStoreEvent<BeanModel> be) {
            removeChangeListenerFromSource(be.getChildren());
         }
      });
      devicesAndMacrosTree.getStore().addListener(Store.Remove, new Listener<TreeStoreEvent<BeanModel>>() {
         public void handleEvent(TreeStoreEvent<BeanModel> be) {
            removeChangeListenerFromSource(be.getChildren());
         }
      });
   }

   /**
    * Adds the change listener to drag source.
    * 
    * @param models the models
    */
   private void addChangeListenerToSource(List<BeanModel> models) {
      if (models == null) {
         return;
      }
      BeanModelDataBase.deviceTable.addInsertListener(getBeanModelSourceChangeListener(null));
      BeanModelDataBase.deviceMacroTable.addInsertListener(getBeanModelSourceChangeListener(null));
      for (BeanModel beanModel : models) {
         if (beanModel.getBean() instanceof Device) {
            BeanModelDataBase.deviceCommandTable.addInsertListener(getBeanModelSourceChangeListener(null));
            BeanModelDataBase.deviceTable.addChangeListener(BeanModelDataBase.getSourceBeanModelId(beanModel), getBeanModelSourceChangeListener(beanModel));
         } else if (beanModel.getBean() instanceof DeviceCommand) {
            BeanModelDataBase.deviceCommandTable.addChangeListener(BeanModelDataBase.getSourceBeanModelId(beanModel), getBeanModelSourceChangeListener(beanModel));
         } else if (beanModel.getBean() instanceof DeviceMacro) {
            BeanModelDataBase.deviceMacroTable.addChangeListener(BeanModelDataBase.getSourceBeanModelId(beanModel), getBeanModelSourceChangeListener(beanModel));
         } else if (beanModel.getBean() instanceof DeviceMacroRef) {
            BeanModelDataBase.deviceMacroTable.addChangeListener(BeanModelDataBase.getOriginalDeviceMacroItemBeanModelId(beanModel), getBeanModelSourceChangeListener(beanModel));
         } else if (beanModel.getBean() instanceof DeviceCommandRef) {
            BeanModelDataBase.deviceTable.addChangeListener(BeanModelDataBase.getSourceBeanModelId(beanModel), getBeanModelSourceChangeListener(beanModel));
            BeanModelDataBase.deviceCommandTable.addChangeListener(BeanModelDataBase.getOriginalDeviceMacroItemBeanModelId(beanModel), getBeanModelSourceChangeListener(beanModel));
         }
      }
   }

   /**
    * Removes the change listener to drag source.
    * 
    * @param models the models
    */
   private void removeChangeListenerFromSource(List<BeanModel> models) {
      if (models == null) {
         return;
      }
      for (BeanModel beanModel : models) {
         if (beanModel.getBean() instanceof DeviceMacroRef) {
            BeanModelDataBase.deviceMacroTable.removeChangeListener(BeanModelDataBase
                  .getOriginalDeviceMacroItemBeanModelId(beanModel), getBeanModelSourceChangeListener(beanModel));

         }
         if (beanModel.getBean() instanceof DeviceCommandRef) {
            BeanModelDataBase.deviceCommandTable.removeChangeListener(BeanModelDataBase
                  .getOriginalDeviceMacroItemBeanModelId(beanModel), getBeanModelSourceChangeListener(beanModel));
         }
         changeListenerMap.remove(beanModel);
      }
   }
   
   /**
    * Gets source BeanModel change listener.
    * 
    * @param targetBeanModel the target
    * 
    * @return the source BeanModel change listener
    */
   private ChangeListener getBeanModelSourceChangeListener(final BeanModel targetBeanModel) {
      if (changeListenerMap == null) {
         changeListenerMap = new HashMap<BeanModel, ChangeListener>();
      }
      ChangeListener changeListener = changeListenerMap.get(targetBeanModel);

      if (changeListener == null) {
         changeListener = new ChangeListener() {
            public void modelChanged(ChangeEvent changeEvent) {
               BeanModel sourceBeanModel = (BeanModel) changeEvent.getItem();
               if (changeEvent.getType() == ChangeEventSupport.Remove) {
                  devicesAndMacrosTree.getStore().remove(targetBeanModel);
               }
               if (changeEvent.getType() == ChangeEventSupport.Add) {
                  if ((sourceBeanModel.getBean() instanceof DeviceMacro)) {
                     for(BeanModel rootBeanModel : devicesAndMacrosTree.getStore().getRootItems()) {
                        TreeFolderBean treeFolderBean = (TreeFolderBean)rootBeanModel.getBean();
                        if (Constants.MACROS.equals(treeFolderBean.getType())) {
                           devicesAndMacrosTree.getStore().add(rootBeanModel, sourceBeanModel, false);
                           devicesAndMacrosTree.setExpanded(rootBeanModel, false);
                           break;
                        }
                     }
                  }else if ((sourceBeanModel.getBean() instanceof Device)) {
                     for(BeanModel rootBeanModel : devicesAndMacrosTree.getStore().getRootItems()) {
                        TreeFolderBean treeFolderBean = (TreeFolderBean)rootBeanModel.getBean();
                        if(Constants.DEVICES.equals(treeFolderBean.getType())) {
                           devicesAndMacrosTree.getStore().add(rootBeanModel, sourceBeanModel, true);
                           devicesAndMacrosTree.setExpanded(rootBeanModel, false);
                           break;
                        }
                     }
                  } else if((sourceBeanModel.getBean() instanceof DeviceCommand)) {
                     DeviceCommand deviceCommand = (DeviceCommand)sourceBeanModel.getBean();
                     Iterator<BeanModel> iterator = devicesAndMacrosTree.getStore().getAllItems().iterator();
                     BeanModel find = null;
                     while(iterator.hasNext()) {
                        BeanModel beanModel = iterator.next();
                        if (beanModel.getBean() instanceof Device) {
                           Device device = (Device)beanModel.getBean();
                           if (device.getOid() == deviceCommand.getDevice().getOid()) {
                              find = beanModel;
                              break;
                           }
                        }
                     }
                     devicesAndMacrosTree.getStore().add(find, deviceCommand.getBeanModel(), true);
                     devicesAndMacrosTree.setExpanded(find, true);
                     System.out.println(deviceCommand.getDevice().getOid());
                  }
               }
               if (changeEvent.getType() == ChangeEventSupport.Update) {
                  
                  if (sourceBeanModel.getBean() instanceof Device) {
                     if (targetBeanModel.getBean() instanceof Device) {
                        for(String propertyName : sourceBeanModel.getPropertyNames()) {
                           targetBeanModel.set(propertyName, sourceBeanModel.get(propertyName));
                        }
                     } else if (targetBeanModel.getBean() instanceof DeviceCommandRef) {
                        DeviceCommandRef deviceCommandRef = (DeviceCommandRef)targetBeanModel.getBean();
                        Device device = (Device)sourceBeanModel.getBean();
                        deviceCommandRef.setDeviceName(device.getName());
                     }
                  } else if (sourceBeanModel.getBean() instanceof DeviceCommand) {
                     if (targetBeanModel.getBean() instanceof DeviceCommandRef) {
                        DeviceCommand deviceCommand = (DeviceCommand)sourceBeanModel.getBean();
                        DeviceCommandRef deviceCommandRef = (DeviceCommandRef)targetBeanModel.getBean();
                        deviceCommandRef.setDeviceCommand(deviceCommand);
                     } else if (targetBeanModel.getBean() instanceof DeviceCommand) {
                        for(String propertyName : sourceBeanModel.getPropertyNames()) {
                           targetBeanModel.set(propertyName, sourceBeanModel.get(propertyName));
                        }
                     }
                  } else if (sourceBeanModel.getBean() instanceof DeviceMacro) {
                     if (targetBeanModel.getBean() instanceof DeviceMacroRef) {
                        DeviceMacro sourceDeviceMacro = (DeviceMacro)sourceBeanModel.getBean();
                        DeviceMacroRef targetDeviceMacroRef = (DeviceMacroRef)targetBeanModel.getBean();
                        targetDeviceMacroRef.setTargetDeviceMacro(sourceDeviceMacro);
                     } else if (targetBeanModel.getBean() instanceof DeviceMacro) {
                        DeviceMacro sourceDeviceMacro = (DeviceMacro)sourceBeanModel.getBean();
                        //following three lines are useful when only modity the DeviceMacro node data and DeviceMacroItems datas in the tree.
                        DeviceMacro old = targetBeanModel.getBean();                        
                        old.setName(sourceDeviceMacro.getName());
                        old.setDeviceMacroItems(sourceDeviceMacro.getDeviceMacroItems());
                        //following several lines are useful when re-render the synchronized node and it's subnodes in the tree.
                        List<BeanModel> macroItemBeanModels = BeanModelDataBase.getBeanModelsByBeans(sourceDeviceMacro.getDeviceMacroItems(),
                              BeanModelDataBase.deviceMacroItemTable);
                        devicesAndMacrosTree.getStore().removeAll(targetBeanModel);
                        for (BeanModel itemBeanModel : macroItemBeanModels) {
                           devicesAndMacrosTree.getStore().add(targetBeanModel, itemBeanModel, false);
                        }
                     }                    
                  }
                  devicesAndMacrosTree.getStore().update(targetBeanModel);
               }
            }
         };
         changeListenerMap.put(targetBeanModel, changeListener);
      }
      return changeListener;
   }


   /**
    * After create device macro.
    * 
    * @param deviceMacro the device macro
    */
   /*private void afterCreateDeviceMacro(DeviceMacro deviceMacro) {
      BeanModel deviceBeanModel = deviceMacro.getBeanModel();
      devicesAndMacrosTree.getStore().add(deviceBeanModel, false);
      devicesAndMacrosTree.setExpanded(deviceBeanModel, true);
   }*/

   /**
    * On edit device macro btn clicked.
    */
/*   private void onEditDeviceMacroBtnClicked() {
      if (devicesAndMacrosTree.getSelectionModel().getSelectedItem() != null) {
         final BeanModel oldModel = devicesAndMacrosTree.getSelectionModel().getSelectedItem();
         final MacroWindow macroWindow = new MacroWindow(devicesAndMacrosTree.getSelectionModel().getSelectedItem());
         macroWindow.addListener(SubmitEvent.Submit, new SubmitListener() {
            @Override
            public void afterSubmit(SubmitEvent be) {
               afterUpdateDeviceMacroSubmit(oldModel, be.<DeviceMacro> getData());
               macroWindow.hide();
            }
         });
      }
   }*/

   /**
    * On delete device macro btn clicked.
    */
  /* private void onDeleteDeviceMacroBtnClicked() {
      if (devicesAndMacrosTree.getSelectionModel().getSelectedItems().size() > 0) {
         for (final BeanModel data : devicesAndMacrosTree.getSelectionModel().getSelectedItems()) {
            if (data.getBean() instanceof DeviceMacro) {
               DeviceMacroBeanModelProxy.deleteDeviceMacro(data, new AsyncSuccessCallback<Void>() {
                  @Override
                  public void onSuccess(Void result) {
                     devicesAndMacrosTree.getStore().remove(data);
                  }
               });
            }

         }
      }
   }*/

   /**
    * After update device macro submit.
    * 
    * @param dataModel the data model
    * @param deviceMacro the device macro
    */
   /*private void afterUpdateDeviceMacroSubmit(final BeanModel dataModel, DeviceMacro deviceMacro) {
      DeviceMacro old = dataModel.getBean();
      old.setName(deviceMacro.getName());
      old.setDeviceMacroItems(deviceMacro.getDeviceMacroItems());
      List<BeanModel> macroItemBeanModels = BeanModelDataBase.getBeanModelsByBeans(deviceMacro.getDeviceMacroItems(),
            BeanModelDataBase.deviceMacroItemMap);
      devicesAndMacrosTree.getStore().removeAll(dataModel);
      for (BeanModel beanModel : macroItemBeanModels) {
         devicesAndMacrosTree.getStore().add(dataModel, beanModel, false);
      }
      devicesAndMacrosTree.getStore().update(dataModel);
      devicesAndMacrosTree.setExpanded(dataModel, true);
   }*/
}

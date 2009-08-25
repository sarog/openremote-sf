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
import org.openremote.modeler.client.utils.BeanModelTable;
import org.openremote.modeler.client.widget.TreePanelBuilder;
import org.openremote.modeler.domain.BusinessEntity;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroRef;
import org.openremote.modeler.domain.Screen;

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

   /** The insert listener map. */
   private Map<BeanModel, ChangeListener> insertListenerMap = null;
   
   /** The update listener map. */
   private Map<BeanModel, ChangeListener> updateListenerMap = null;

   /**
    * Instantiates a devicesAndMacrosPanel.
    */
   public DevicesAndMacrosPanel() {
      setHeading("Devices/Macros");
      setLayout(new FitLayout());
      createDevicesAndMacrosTree();
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
    * @param models
    *           the models
    */
   private void addChangeListenerToSource(List<BeanModel> models) {
      if (models == null) {
         return;
      }
      for (BeanModel beanModel : models) {
         if (beanModel.getBean() instanceof Device) {
            BeanModelDataBase.deviceTable.addInsertListener(getInsertListener("deviceTable", getRootBeanModel(Constants.DEVICES)));
            BeanModelDataBase.deviceTable.addChangeListener(BeanModelDataBase.getSourceBeanModelId(beanModel), getUpdateListener(beanModel));
            BeanModelDataBase.deviceTable.addChangeListener(BeanModelDataBase.getSourceBeanModelId(beanModel), getRemoveListener(beanModel));
            BeanModelDataBase.deviceCommandTable.addInsertListener(getInsertListener("deviceCommandTable", beanModel));
         } else if (beanModel.getBean() instanceof DeviceCommand) {
            BeanModelDataBase.deviceCommandTable.addChangeListener(BeanModelDataBase.getSourceBeanModelId(beanModel), getUpdateListener(beanModel));
            BeanModelDataBase.deviceCommandTable.addChangeListener(BeanModelDataBase.getSourceBeanModelId(beanModel), getRemoveListener(beanModel));
         } else if (beanModel.getBean() instanceof DeviceMacro) {
            BeanModelDataBase.deviceMacroTable.addInsertListener(getInsertListener("deviceMacroTable", getRootBeanModel(Constants.MACROS)));
            BeanModelDataBase.deviceMacroTable.addChangeListener(BeanModelDataBase.getSourceBeanModelId(beanModel), getUpdateListener(beanModel));
            BeanModelDataBase.deviceMacroTable.addChangeListener(BeanModelDataBase.getSourceBeanModelId(beanModel), getRemoveListener(beanModel));
         } else if (beanModel.getBean() instanceof DeviceMacroRef) {
            BeanModelDataBase.deviceMacroTable.addChangeListener(BeanModelDataBase.getOriginalDeviceMacroItemBeanModelId(beanModel), getUpdateListener(beanModel));
            BeanModelDataBase.deviceMacroTable.addChangeListener(BeanModelDataBase.getOriginalDeviceMacroItemBeanModelId(beanModel), getRemoveListener(beanModel));
         } else if (beanModel.getBean() instanceof DeviceCommandRef) {
            BeanModelDataBase.deviceTable.addChangeListener(BeanModelDataBase.getSourceBeanModelId(beanModel), getUpdateListener(beanModel));
            BeanModelDataBase.deviceTable.addChangeListener(BeanModelDataBase.getSourceBeanModelId(beanModel), getRemoveListener(beanModel));
            BeanModelDataBase.deviceCommandTable.addChangeListener(BeanModelDataBase.getOriginalDeviceMacroItemBeanModelId(beanModel), getUpdateListener(beanModel));
            BeanModelDataBase.deviceCommandTable.addChangeListener(BeanModelDataBase.getOriginalDeviceMacroItemBeanModelId(beanModel), getRemoveListener(beanModel));
         }
      }
   }

   /**
    * Removes the change listener to drag source.
    * 
    * @param models
    *           the models
    */
   private void removeChangeListenerFromSource(List<BeanModel> models) {
      if (models == null) {
         return;
      }
      for (BeanModel beanModel : models) {
         if (beanModel.getBean() instanceof DeviceMacroRef) {
            BeanModelDataBase.deviceMacroTable.removeChangeListener(BeanModelDataBase
                  .getOriginalDeviceMacroItemBeanModelId(beanModel), getRemoveListener(beanModel));

         }
         if (beanModel.getBean() instanceof DeviceCommandRef) {
            BeanModelDataBase.deviceCommandTable.removeChangeListener(BeanModelDataBase
                  .getOriginalDeviceMacroItemBeanModelId(beanModel), getRemoveListener(beanModel));
         }
         changeListenerMap.remove(beanModel);
      }
   }

   /**
    * Gets source BeanModel change listener.
    * 
    * @param targetBeanModel
    *           the target
    * 
    * @return the source BeanModel change listener
    */
   private ChangeListener getRemoveListener(final BeanModel targetBeanModel) {
      if (changeListenerMap == null) {
         changeListenerMap = new HashMap<BeanModel, ChangeListener>();
      }
      ChangeListener changeListener = changeListenerMap.get(targetBeanModel);

      if (changeListener == null) {
         changeListener = new ChangeListener() {
            public void modelChanged(ChangeEvent changeEvent) {
               if (changeEvent.getType() == ChangeEventSupport.Remove) {
                  devicesAndMacrosTree.getStore().remove(targetBeanModel);
               }
            }
         };
         changeListenerMap.put(targetBeanModel, changeListener);
      }
      return changeListener;
   }

   /**
    * Gets the insert listener.
    * 
    * @param beanModelTable the bean model table
    * 
    * @return the insert listener
    */
   private ChangeListener getInsertListener(final String beanModelTableName, final BeanModel parentNode) {
      if (insertListenerMap == null) {
         insertListenerMap = new HashMap<BeanModel, ChangeListener>();
      }
      ChangeListener addListener = insertListenerMap.get(parentNode);
      if (addListener == null) {
         addListener = new ChangeListener() {
            public void modelChanged(ChangeEvent changeEvent) {
               BeanModel sourceBeanModel = (BeanModel) changeEvent.getItem();
               if (changeEvent.getType() == ChangeEventSupport.Add) {
                  if ((sourceBeanModel.getBean() instanceof DeviceCommand)) {
                     DeviceCommand deviceCommand = ((DeviceCommand)sourceBeanModel.getBean());
                     Device device = ((Device)parentNode.getBean());
                     if (deviceCommand.getDevice().getOid() == device.getOid()) {
                        devicesAndMacrosTree.getStore().add(parentNode, sourceBeanModel, true);
                        devicesAndMacrosTree.setExpanded(parentNode, false);
                     }
                  } else { //sourceBeanModel is DeviceBeanModel or DeviceMacroBeanModel
                     devicesAndMacrosTree.getStore().add(parentNode, sourceBeanModel, true);
                     devicesAndMacrosTree.setExpanded(parentNode, false);
                  }                  
               }
            }
         };
         insertListenerMap.put(parentNode, addListener);
      }
      return addListener;
   }

   /**
    * Gets the update listener.
    * 
    * @param targetBeanModel the target bean model
    * 
    * @return the update listener
    */
   private ChangeListener getUpdateListener(final BeanModel targetBeanModel) {
      if (updateListenerMap == null) {
         updateListenerMap = new HashMap<BeanModel, ChangeListener>();
      }
      ChangeListener updateListener = updateListenerMap.get(targetBeanModel);
      if (updateListener == null) {
         updateListener = new ChangeListener() {
            public void modelChanged(ChangeEvent changeEvent) {
               BeanModel sourceBeanModel = (BeanModel) changeEvent.getItem();
               if (changeEvent.getType() == ChangeEventSupport.Update) {
                  if (sourceBeanModel.getBean() instanceof Device) {
                     if (targetBeanModel.getBean() instanceof Device) {
                        for (String propertyName : sourceBeanModel.getPropertyNames()) {
                           targetBeanModel.set(propertyName, sourceBeanModel.get(propertyName));
                        }
                     } else if (targetBeanModel.getBean() instanceof DeviceCommandRef) {
                        DeviceCommandRef deviceCommandRef = (DeviceCommandRef) targetBeanModel.getBean();
                        Device device = (Device) sourceBeanModel.getBean();
                        deviceCommandRef.setDeviceName(device.getName());
                     }
                  } else if (sourceBeanModel.getBean() instanceof DeviceCommand) {
                     if (targetBeanModel.getBean() instanceof DeviceCommandRef) {
                        DeviceCommand deviceCommand = (DeviceCommand) sourceBeanModel.getBean();
                        DeviceCommandRef deviceCommandRef = (DeviceCommandRef) targetBeanModel.getBean();
                        deviceCommandRef.setDeviceCommand(deviceCommand);
                     } else if (targetBeanModel.getBean() instanceof DeviceCommand) {
                        for (String propertyName : sourceBeanModel.getPropertyNames()) {
                           targetBeanModel.set(propertyName, sourceBeanModel.get(propertyName));
                        }
                     }
                  } else if (sourceBeanModel.getBean() instanceof DeviceMacro) {
                     if (targetBeanModel.getBean() instanceof DeviceMacroRef) {
                        DeviceMacro sourceDeviceMacro = (DeviceMacro) sourceBeanModel.getBean();
                        DeviceMacroRef targetDeviceMacroRef = (DeviceMacroRef) targetBeanModel.getBean();
                        targetDeviceMacroRef.setTargetDeviceMacro(sourceDeviceMacro);
                     } else if (targetBeanModel.getBean() instanceof DeviceMacro) {
                        DeviceMacro sourceDeviceMacro = (DeviceMacro) sourceBeanModel.getBean();
                        // following three lines are useful when only modity the DeviceMacro node data and
                        // DeviceMacroItems datas in the tree.
                        DeviceMacro old = targetBeanModel.getBean();
                        old.setName(sourceDeviceMacro.getName());
                        old.setDeviceMacroItems(sourceDeviceMacro.getDeviceMacroItems());
                        // following several lines are useful when re-render the synchronized node and it's subnodes in
                        // the tree.
                        List<BeanModel> macroItemBeanModels = BeanModelDataBase.getBeanModelsByBeans(sourceDeviceMacro
                              .getDeviceMacroItems(), BeanModelDataBase.deviceMacroItemTable);
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
         updateListenerMap.put(targetBeanModel, updateListener);
      }
      return updateListener;
   }
   
   /**
    * Gets the root bean model.
    * 
    * @param beanType the bean type
    * 
    * @return the root bean model
    */
   private BeanModel getRootBeanModel(String beanType) {
      for (BeanModel rootBeanModel : devicesAndMacrosTree.getStore().getRootItems()) {
         TreeFolderBean treeFolderBean = (TreeFolderBean) rootBeanModel.getBean();
         if (beanType.equals(treeFolderBean.getType())) {
            return rootBeanModel;
         }
      }
      return null;
   }
}

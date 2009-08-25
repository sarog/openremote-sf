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
package org.openremote.modeler.client.widget;

import java.util.List;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.model.TreeFolderBean;
import org.openremote.modeler.client.proxy.DeviceBeanModelProxy;
import org.openremote.modeler.client.proxy.DeviceMacroBeanModelProxy;
import org.openremote.modeler.client.proxy.DevicesAndMacrosBeanModelProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.uidesigner.ScreenTab;
import org.openremote.modeler.client.widget.uidesigner.ScreenTabItem;
import org.openremote.modeler.domain.Activity;
import org.openremote.modeler.domain.BusinessEntity;
import org.openremote.modeler.domain.CommandDelay;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroRef;
import org.openremote.modeler.domain.Screen;

import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.data.ModelStringProvider;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * The Class is used for create tree.
 */
public class TreePanelBuilder {

   /**
    * Not be instantiated.
    */
   private TreePanelBuilder() {
   }

   /** The Constant icon. */
   private static final Icons ICON = GWT.create(Icons.class);

   /** The device command treestore. */
   private static TreeStore<BeanModel> deviceCommandTreestore = null;

   /** The macro tree store. */
   private static TreeStore<BeanModel> macroTreeStore = null;
   
   /** The activity tree store. */
   private static TreeStore<BeanModel> activityTreeStore = null;
   
   /** The devicesmacros tree store. */
   private static TreeStore<BeanModel> devicesAndMacrosTreeStore = null;

   /**
    * Builds a device command tree.
    * 
    * @return the a new device command tree
    */
   public static TreePanel<BeanModel> buildDeviceCommandTree() {
      if (deviceCommandTreestore == null) {
         RpcProxy<List<BeanModel>> loadDeviceRPCProxy = new RpcProxy<List<BeanModel>>() {
            @Override
            protected void load(Object o, final AsyncCallback<List<BeanModel>> listAsyncCallback) {
                DeviceBeanModelProxy.loadDevice((BeanModel) o, new AsyncSuccessCallback<List<BeanModel>>() {
                    public void onSuccess(List<BeanModel> result) {
                        listAsyncCallback.onSuccess(result);
                    }
                });
            }
        };
        TreeLoader<BeanModel> loadDeviceTreeLoader = new BaseTreeLoader<BeanModel>(loadDeviceRPCProxy) {
            @Override
            public boolean hasChildren(BeanModel beanModel) {
                if (beanModel.getBean() instanceof Device) {
                    return true;
                }
                return false;
            }
        };
        deviceCommandTreestore = new TreeStore<BeanModel>(loadDeviceTreeLoader);
//
//         deviceCommandTreestore.addListener(Store.Remove, new Listener<TreeStoreEvent<BeanModel>>() {
//            public void handleEvent(TreeStoreEvent<BeanModel> be) {
//               afterDeleteDeviceOrDeviceCommand(be.getChild());
//            }
//         });
      }
      final TreePanel<BeanModel> tree = new TreePanel<BeanModel>(deviceCommandTreestore);

      tree.setBorders(false);
      tree.setStateful(true);
      tree.setDisplayProperty("displayName");
      tree.setHeight("100%");
      tree.setIconProvider(new ModelIconProvider<BeanModel>() {

         public AbstractImagePrototype getIcon(BeanModel thisModel) {

            if (thisModel.getBean() instanceof DeviceCommand) {
               return ICON.deviceCmd();
            } else if (thisModel.getBean() instanceof Device) {
               return ICON.device();
            } else {
               return ICON.folder();
            }
         }

      });
      return tree;
   }

   /**
    * After delete device or device command.
    * 
    * @return the tree panel< bean model>
    */
   // private static void afterDeleteDeviceOrDeviceCommand(TreeDataModel deletedModel) {
   // if (deletedModel.getData() instanceof DeviceCommand) {
   // afterDeleteDeviceCommand((DeviceCommand) deletedModel.getData());
   // } else if (deletedModel.getData() instanceof Device) {
   // afterDeleteDevice((Device) deletedModel.getData());
   // }
   // }
   /**
    * After delete device.
    * 
    * @param device
    *           the device
    */
   // private static void afterDeleteDevice(Device device) {
   // for (DeviceCommand command : device.getDeviceCommands()) {
   // afterDeleteDeviceCommand(command);
   // }
   // }
   /**
    * After delete device command.
    * 
    * @param command
    *           the command
    */
   // private static void afterDeleteDeviceCommand(DeviceCommand command) {
   // DeviceCommandRef commandRef = new DeviceCommandRef(command);
   // TreeDataModel newDataModel = new TreeDataModel(commandRef, commandRef.getLabel());
   // Iterator<TreeDataModel> iterator = macroTreeStore.getAllItems().iterator();
   // while (iterator.hasNext()) {
   // TreeDataModel current = iterator.next();
   // if (macroTreeStore.getModelComparer().equals(current, newDataModel)) {
   // if (current.getParent() != null) {
   // if (current.getParent().get(TreeDataModel.getDataProperty()) instanceof DeviceMacro) {
   // DeviceMacro parent = (DeviceMacro) current.getParent().get(TreeDataModel.getDataProperty());
   // parent.getDeviceMacroItems().remove(current.getData());
   // }
   // }
   // iterator.remove();
   // macroTreeStore.remove(current);
   // }
   // }
   // }
   /**
    * After delete device macro.
    * 
    * @param deletedModel
    *           the deleted model
    */
   // private static void afterDeleteDeviceMacro(TreeDataModel deletedModel) {
   // if (deletedModel.getData() instanceof DeviceMacro) {
   // DeviceMacroRef deviceMacroRef = new DeviceMacroRef((DeviceMacro) deletedModel.getData());
   // TreeDataModel newDataModel = new TreeDataModel(deviceMacroRef, deviceMacroRef.getLabel());
   // Iterator<TreeDataModel> iterator = macroTreeStore.getAllItems().iterator();
   // while (iterator.hasNext()) {
   // TreeDataModel current = iterator.next();
   // if (macroTreeStore.getModelComparer().equals(current, newDataModel)) {
   // iterator.remove();
   // macroTreeStore.remove(current);
   // }
   // }
   // }
   // }
   /**
    * Builds a new macro tree.
    * 
    * @return a new macro tree
    */
   public static TreePanel<BeanModel> buildMacroTree() {
      if (macroTreeStore == null) {
         RpcProxy<List<BeanModel>> loadDeviceMacroRPCProxy = new RpcProxy<List<BeanModel>>() {

            protected void load(Object o, final AsyncCallback<List<BeanModel>> listAsyncCallback) {
               DeviceMacroBeanModelProxy.loadDeviceMaro((BeanModel) o, new AsyncSuccessCallback<List<BeanModel>>() {

                  public void onSuccess(List<BeanModel> result) {
                     listAsyncCallback.onSuccess(result);
                  }
               });
            }
         };
         BaseTreeLoader<BeanModel> loadDeviceMacroTreeLoader = new BaseTreeLoader<BeanModel>(loadDeviceMacroRPCProxy) {
            @Override
            public boolean hasChildren(BeanModel beanModel) {
               if (beanModel.getBean() instanceof DeviceMacro) {
                  return true;
               }
               return false;
            }
         };
         macroTreeStore = new TreeStore<BeanModel>(loadDeviceMacroTreeLoader);
      }

      final TreePanel<BeanModel> tree = new TreePanel<BeanModel>(macroTreeStore);
      tree.setStateful(true);
      tree.setBorders(false);
      tree.setHeight("100%");
      tree.setLabelProvider(new ModelStringProvider<BeanModel>() {

         public String getStringValue(BeanModel model, String property) {
//            if (model.getBean() instanceof DeviceMacro) {
//               return model.get("name");
//            } else {
//               DeviceMacroItem deviceMacroItem = (DeviceMacroItem) model.getBean();
//               return deviceMacroItem.getTreeNodeLabel();
//            }
            return ((BusinessEntity) model.getBean()).getDisplayName();
         }

      });
      tree.setIconProvider(new ModelIconProvider<BeanModel>() {

         public AbstractImagePrototype getIcon(BeanModel thisModel) {

            if (thisModel.getBean() instanceof DeviceMacro) {
               return ICON.macroIcon();
            } else if (thisModel.getBean() instanceof DeviceCommandRef) {
               return ICON.deviceCmd();
            } else if (thisModel.getBean() instanceof CommandDelay) {
               return ICON.delayIcon();
            } else {
               return ICON.macroIcon();
            }
         }

      });
      return tree;
   }
   
   /**
    * Builds a new activity tree.
    * 
    * @param screens the screens
    * 
    * @return a new activity tree.
    */
   public static TreePanel<BeanModel> buildActivityTree(final ScreenTab screenTab) {
      if (activityTreeStore == null) {
         activityTreeStore = new TreeStore<BeanModel>();
      }
      final TreePanel<BeanModel> activityTree = new TreePanel<BeanModel>(activityTreeStore){

         @Override
         public void onBrowserEvent(Event event) {
            if(event.getTypeInt() == Event.ONDBLCLICK){
               BeanModel beanModel = this.getSelectionModel().getSelectedItem();
               if(beanModel.getBean() instanceof Screen){
                  Screen screen = beanModel.getBean();
                  ScreenTabItem screenTabItem = null;
                  for (TabItem tabPanel : screenTab.getItems()) {
                     screenTabItem = (ScreenTabItem)tabPanel;
                     if(screen == screenTabItem.getScreen()){
                        screenTab.setSelection(screenTabItem);
                        return;
                     }else{
                        screenTabItem = null;
                     }
                  }
                  if(screenTabItem == null){
                     screenTabItem = new ScreenTabItem(screen);
                     screenTab.add(screenTabItem);
                     screenTab.setSelection(screenTabItem);
                  }
                  
               }
            }
            
            super.onBrowserEvent(event);
         }
         
      };
      activityTree.setStateful(true);
      activityTree.setBorders(false);
      activityTree.setHeight("100%");      
      activityTree.setDisplayProperty("displayName");
      
      activityTree.setIconProvider(new ModelIconProvider<BeanModel>() {
         public AbstractImagePrototype getIcon(BeanModel thisModel) {
            if (thisModel.getBean() instanceof Activity) {
               return ICON.activityIcon();
            } else if (thisModel.getBean() instanceof Screen) {
               return ICON.screenIcon();
            } else {
               return ICON.activityIcon();
            }
         }
      });
      
      return activityTree;
   }
   
   /**
    * Builds the DevicesMacros tree.
    * 
    * @return the tree panel<BeanModel>
    */
   public static TreePanel<BeanModel> buildDevicesAndMacrosTree() {
      initDevicesAndMacrosTreeStore();
      return initDevicesAndMacrosTree();
   }

   /**
    * Inits the devices and macros tree store.
    */
   private static void initDevicesAndMacrosTreeStore() {
      if(devicesAndMacrosTreeStore == null) {
         RpcProxy<List<BeanModel>> devicesAndMacrosProxy = new RpcProxy<List<BeanModel>>(){
            @Override
            protected void load(Object loadConfig, final AsyncCallback<List<BeanModel>> callback) {
               DevicesAndMacrosBeanModelProxy.loadDevicesMacros((BeanModel)loadConfig, new AsyncSuccessCallback<List<BeanModel>>(){
                  @Override
                  public void onSuccess(List<BeanModel> result) {
                     callback.onSuccess(result);
                  }                  
               });
            }            
         };
         TreeLoader<BeanModel> devicesAndMacrosTreeLoader = new BaseTreeLoader<BeanModel>(devicesAndMacrosProxy) {
            @Override
            public boolean hasChildren(BeanModel beanModel) {
               if((beanModel.getBean() instanceof Device) || (beanModel.getBean() instanceof DeviceMacro) || (beanModel.getBean() instanceof TreeFolderBean)) {
                  return true;
               }
               return false;
            }
         };
         devicesAndMacrosTreeStore = new TreeStore<BeanModel>(devicesAndMacrosTreeLoader);
         createRootFolder();
      }
   }

   /**
    * Creates the root folder.
    */
   private static void createRootFolder() {
      TreeFolderBean devicesBean = new TreeFolderBean();
      devicesBean.setDisplayName("Devices");
      devicesBean.setType(Constants.DEVICES);
      TreeFolderBean macrosBean = new TreeFolderBean();
      macrosBean.setDisplayName("Marcos");
      macrosBean.setType(Constants.MACROS);
      devicesAndMacrosTreeStore.add(devicesBean.getBeanModel(), true);
      devicesAndMacrosTreeStore.add(macrosBean.getBeanModel(), true);
   }
   
   /**
    * Inits the devices and macros tree.
    * 
    * @return the tree panel< bean model>
    */
   private static TreePanel<BeanModel> initDevicesAndMacrosTree() {
      TreePanel<BeanModel> devicesAndMacrosTree = new TreePanel<BeanModel>(devicesAndMacrosTreeStore);
      devicesAndMacrosTree.setBorders(false);
      devicesAndMacrosTree.setStateful(true);
      devicesAndMacrosTree.setDisplayProperty("displayName");
      devicesAndMacrosTree.setHeight("100%");
      devicesAndMacrosTree.setIconProvider(new ModelIconProvider<BeanModel>() {
         public AbstractImagePrototype getIcon(BeanModel beanModel) {
            if(beanModel.getBean() instanceof Device) {
               return ICON.device();
            } else if((beanModel.getBean() instanceof DeviceCommand) || (beanModel.getBean() instanceof DeviceCommandRef)) {
               return ICON.deviceCmd();
            } else if((beanModel.getBean() instanceof DeviceMacro) || (beanModel.getBean() instanceof DeviceMacroRef)) {
               return ICON.macroIcon();
            } else if(beanModel.getBean() instanceof CommandDelay) {
               return ICON.delayIcon();
            } else if(beanModel.getBean() instanceof TreeFolderBean) {
               if(((TreeFolderBean)beanModel.getBean()).getType().equals(Constants.DEVICES)){
                  return ICON.devicesRoot();
               }else if(((TreeFolderBean)beanModel.getBean()).getType().equals(Constants.MACROS)){
                  return ICON.macrosRoot();
               }
            }
            return ICON.folder();
         }
      });
      return devicesAndMacrosTree;
   }
}

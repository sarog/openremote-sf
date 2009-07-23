/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openremote.modeler.client.widget;

import java.util.List;

import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.proxy.DeviceBeanModelProxy;
import org.openremote.modeler.client.proxy.DeviceMacroBeanModelProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroItem;

import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.data.ModelStringProvider;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

// TODO: Auto-generated Javadoc
/**
 * The Class is used for create tree.
 */
public class TreePanelBuilder {

   private static final String TREE_DISPLAY_FIELD = "tree_label";

   /** The Constant icon. */
   private final static Icons icon = GWT.create(Icons.class);

   /** The device command treestore. */
   private static TreeStore<BeanModel> deviceCommandTreestore = null;

   /** The macro tree store. */
   private static TreeStore<BeanModel> macroTreeStore = null;

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
                DeviceBeanModelProxy.loadDevice((BeanModel) o,new AsyncSuccessCallback<List<BeanModel>>(){
                    public void onSuccess(List<BeanModel> result) {
                        listAsyncCallback.onSuccess(result);
                    }
                });
            }
        };
        TreeLoader<BeanModel> loadDeviceTreeLoader = new BaseTreeLoader<BeanModel>(loadDeviceRPCProxy){
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
      tree.setDisplayProperty("name");
      tree.setHeight("100%");
      tree.setIconProvider(new ModelIconProvider<BeanModel>() {

         public AbstractImagePrototype getIcon(BeanModel thisModel) {

            if (thisModel.getBean() instanceof DeviceCommand) {
               return icon.deviceCmd();
            } else if (thisModel.getBean() instanceof Device) {
               return icon.device();
            } else {
               return icon.folder();
            }
         }

      });
      return tree;
   }

   /**
    * After delete device or device command.
    * 
    * @param deletedModel
    *           the deleted model
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
            if (model.getBean() instanceof DeviceMacro) {
               return model.get("name");
            } else {
               DeviceMacroItem deviceMacroItem = (DeviceMacroItem) model.getBean();
               return deviceMacroItem.getLabel();
            }
         }

      });
      tree.setIconProvider(new ModelIconProvider<BeanModel>() {

         public AbstractImagePrototype getIcon(BeanModel thisModel) {

            if (thisModel.getBean() instanceof DeviceMacro) {
               return icon.macroIcon();
            } else if (thisModel.getBean() instanceof DeviceCommandRef) {
               return icon.deviceCmd();
            } else {
               return icon.macroIcon();
            }
         }

      });
      return tree;
   }
}

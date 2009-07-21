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

import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.model.TreeDataModel;
import org.openremote.modeler.client.proxy.DeviceMacroBeanModelProxy;
import org.openremote.modeler.client.rpc.*;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class is used for create tree.
 */
public class TreePanelBuilder {

   /** The Constant deviceService. */
   private final static DeviceRPCServiceAsync deviceService = (DeviceRPCServiceAsync) GWT.create(DeviceRPCService.class);
   
   /** The Constant deviceMacroServiceAsync. */
   private final static DeviceMacroRPCServiceAsync deviceMacroServiceAsync = (DeviceMacroRPCServiceAsync) GWT
         .create(DeviceMacroRPCService.class);

   /** The Constant icon. */
   private final static Icons icon = GWT.create(Icons.class);

   /** The device command treestore. */
   private static TreeStore<TreeDataModel> deviceCommandTreestore = null;
   
   /** The macro tree store. */
   private static TreeStore<BeanModel> macroTreeStore = null;

   /**
    * Builds a device command tree.
    * 
    * @return the a new device command tree
    */
   public static TreePanel<TreeDataModel> buildDeviceCommandTree() {
      boolean first = false;
      if (deviceCommandTreestore == null) {
         deviceCommandTreestore = new TreeStore<TreeDataModel>();
//
//         deviceCommandTreestore.addListener(Store.Remove, new Listener<TreeStoreEvent<TreeDataModel>>() {
//            public void handleEvent(TreeStoreEvent<TreeDataModel> be) {
//               afterDeleteDeviceOrDeviceCommand(be.getChild());
//            }
//         });
         first = true;
      }
      final TreePanel<TreeDataModel> tree = new TreePanel<TreeDataModel>(deviceCommandTreestore);
      tree.setBorders(false);
      if (first) {
         deviceService.loadAll(new AsyncSuccessCallback<List<Device>>() {

            public void onSuccess(List<Device> devices) {
               if (devices != null) {
                  for (Device device : devices) {
                     TreeDataModel deviceModel = new TreeDataModel(device, device.getName());
                     if (device.getDeviceCommands() != null) {
                        for (DeviceCommand deviceCommand : device.getDeviceCommands()) {
                           TreeDataModel commandModel = new TreeDataModel(deviceCommand, deviceCommand.getName());
                           deviceModel.add(commandModel);
                        }
                     }
                     deviceCommandTreestore.add(deviceModel, true);
                  }
               LayoutContainer treeContainer = (LayoutContainer)tree.getParent();
               treeContainer.unmask();
               }
            }
         });
      }

      tree.setDisplayProperty(TreeDataModel.getDisplayProperty());
      tree.setIconProvider(new ModelIconProvider<TreeDataModel>() {

         public AbstractImagePrototype getIcon(TreeDataModel thisModel) {

            if (thisModel.getData() instanceof DeviceCommand) {
               return icon.deviceCmd();
            } else if (thisModel.getData() instanceof Device) {
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
    * @param deletedModel the deleted model
    */
//   private static void afterDeleteDeviceOrDeviceCommand(TreeDataModel deletedModel) {
//      if (deletedModel.getData() instanceof DeviceCommand) {
//         afterDeleteDeviceCommand((DeviceCommand) deletedModel.getData());
//      } else if (deletedModel.getData() instanceof Device) {
//         afterDeleteDevice((Device) deletedModel.getData());
//      }
//   }

   /**
    * After delete device.
    * 
    * @param device the device
    */
//   private static void afterDeleteDevice(Device device) {
//      for (DeviceCommand command : device.getDeviceCommands()) {
//         afterDeleteDeviceCommand(command);
//      }
//   }

   /**
    * After delete device command.
    * 
    * @param command the command
    */
//   private static void afterDeleteDeviceCommand(DeviceCommand command) {
//      DeviceCommandRef commandRef = new DeviceCommandRef(command);
//      TreeDataModel newDataModel = new TreeDataModel(commandRef, commandRef.getLabel());
//      Iterator<TreeDataModel> iterator = macroTreeStore.getAllItems().iterator();
//      while (iterator.hasNext()) {
//         TreeDataModel current = iterator.next();
//         if (macroTreeStore.getModelComparer().equals(current, newDataModel)) {
//            if (current.getParent() != null) {
//               if (current.getParent().get(TreeDataModel.getDataProperty()) instanceof DeviceMacro) {
//                  DeviceMacro parent = (DeviceMacro) current.getParent().get(TreeDataModel.getDataProperty());
//                  parent.getDeviceMacroItems().remove(current.getData());
//               }
//            }
//            iterator.remove();
//            macroTreeStore.remove(current);
//         }
//      }
//   }

   /**
    * After delete device macro.
    * 
    * @param deletedModel the deleted model
    */
//   private static void afterDeleteDeviceMacro(TreeDataModel deletedModel) {
//      if (deletedModel.getData() instanceof DeviceMacro) {
//         DeviceMacroRef deviceMacroRef = new DeviceMacroRef((DeviceMacro) deletedModel.getData());
//         TreeDataModel newDataModel = new TreeDataModel(deviceMacroRef, deviceMacroRef.getLabel());
//         Iterator<TreeDataModel> iterator = macroTreeStore.getAllItems().iterator();
//         while (iterator.hasNext()) {
//            TreeDataModel current = iterator.next();
//            if (macroTreeStore.getModelComparer().equals(current, newDataModel)) {
//               iterator.remove();
//               macroTreeStore.remove(current);
//            }
//         }
//      }
//   }

   /**
    * Builds a new macro tree.
    * 
    * @return a new macro tree
    */
   public static TreePanel<BeanModel> buildMacroTree() {
      if (macroTreeStore == null) {
          RpcProxy<List<BeanModel>> loadDeviceMacroRPCProxy = new RpcProxy<List<BeanModel>>() {

              protected void load(Object o, final AsyncCallback<List<BeanModel>> listAsyncCallback) {
                  DeviceMacroBeanModelProxy.loadDeviceMaro((BeanModel) o,new AsyncSuccessCallback<List<BeanModel>>(){

                      public void onSuccess(List<BeanModel> result) {
                          listAsyncCallback.onSuccess(result);
                      }
                  });
              }
          };
          BaseTreeLoader<BeanModel> loadDeviceMacroTreeLoader = new BaseTreeLoader<BeanModel>(loadDeviceMacroRPCProxy){
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
      tree.setBorders(false);
    

      tree.setDisplayProperty(TreeDataModel.getDisplayProperty());
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

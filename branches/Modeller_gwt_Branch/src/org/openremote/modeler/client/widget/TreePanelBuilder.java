/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2009, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF site:
 * http://www.fsf.org.
 */
package org.openremote.modeler.client.widget;

import java.util.List;

import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.model.TreeDataModel;
import org.openremote.modeler.client.rpc.DeviceMacroService;
import org.openremote.modeler.client.rpc.DeviceMacroServiceAsync;
import org.openremote.modeler.client.rpc.DeviceService;
import org.openremote.modeler.client.rpc.DeviceServiceAsync;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroItem;
import org.openremote.modeler.domain.DeviceMacroRef;

import com.extjs.gxt.ui.client.data.ModelComparer;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * The Class is used for create tree.
 */
public class TreePanelBuilder {

   /** The Constant deviceService. */
   private final static DeviceServiceAsync deviceService = (DeviceServiceAsync) GWT.create(DeviceService.class);
   private final static DeviceMacroServiceAsync deviceMacroServiceAsync = (DeviceMacroServiceAsync) GWT
         .create(DeviceMacroService.class);

   /** The Constant icon. */
   private final static Icons icon = GWT.create(Icons.class);

   /**
    * Builds a device command tree.
    * 
    * @return the a new device command tree
    */
   @SuppressWarnings("unchecked")
   public static TreePanel<TreeDataModel> buildDeviceCommandTree() {
      final TreeStore<TreeDataModel> store = new TreeStore<TreeDataModel>();
      final TreePanel<TreeDataModel> tree = new TreePanel<TreeDataModel>(store);
      tree.setBorders(false);
      deviceService.loadAll(new AsyncCallback<List<Device>>() {
         public void onFailure(Throwable caught) {
            caught.printStackTrace();
            MessageBox.info("Error", caught.getMessage(), null);
         }

         public void onSuccess(List<Device> devices) {
            if (devices != null) {
               for (Device device : devices) {
                  TreeDataModel<Device> deviceModel = new TreeDataModel<Device>(device, device.getName());
                  if (device.getDeviceCommands() != null) {
                     for (DeviceCommand deviceCommand : device.getDeviceCommands()) {
                        TreeDataModel<DeviceCommand> commandModel = new TreeDataModel<DeviceCommand>(deviceCommand,
                              deviceCommand.getName());
                        deviceModel.add(commandModel);
                     }
                  }
                  store.add(deviceModel, true);
               }
            }
         }
      });
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
    * Builds a new macro tree.
    * 
    * @return a new macro tree
    */
   public static TreePanel<TreeDataModel> buildMacroTree() {
      TreeStore<TreeDataModel> store = new TreeStore<TreeDataModel>();
      final TreePanel<TreeDataModel> tree = new TreePanel<TreeDataModel>(store);
      tree.setBorders(false);
      
      deviceMacroServiceAsync.loadAll(new AsyncCallback<List<DeviceMacro>>() {
         public void onFailure(Throwable caught) {
            caught.printStackTrace();
            MessageBox.info("Error", caught.getMessage(), null);
         }

         public void onSuccess(List<DeviceMacro> macros) {
            if (macros != null) {
               for (DeviceMacro macro : macros) {
                  TreeDataModel<DeviceMacro> deviceMacroModel = new TreeDataModel<DeviceMacro>(macro, macro.getName());
                  if (macro.getDeviceMacroItems() != null) {
                     for (DeviceMacroItem deviceMacroItem : macro.getDeviceMacroItems()) {
                        if (deviceMacroItem instanceof DeviceMacroRef) {
                           DeviceMacroRef deviceMacroRef = (DeviceMacroRef) deviceMacroItem;
                           TreeDataModel<DeviceMacroRef> deviceMacroItemModel = new TreeDataModel<DeviceMacroRef>(
                                 deviceMacroRef, deviceMacroRef.getTargetDeviceMacro().getName());
                           deviceMacroModel.add(deviceMacroItemModel);
                        } else if (deviceMacroItem instanceof DeviceCommandRef) {
                           DeviceCommandRef commandRef = (DeviceCommandRef) deviceMacroItem;
                           TreeDataModel<DeviceCommandRef> deviceMacroItemModel = new TreeDataModel<DeviceCommandRef>(
                                 commandRef, commandRef.getDeviceCommand().getName());
                           deviceMacroModel.add(deviceMacroItemModel);
                        }
                     }
                  }
                  tree.getStore().add(deviceMacroModel, true);
               }
               
            }           
         }
      });
      tree.setDisplayProperty(TreeDataModel.getDisplayProperty());
      tree.setIconProvider(new ModelIconProvider<TreeDataModel>() {

         public AbstractImagePrototype getIcon(TreeDataModel thisModel) {

            if (thisModel.getData() instanceof DeviceMacro) {
               return icon.macroIcon();
            } else if (thisModel.getData() instanceof DeviceCommandRef) {
               return icon.deviceCmd();
            } else {
               return icon.macroIcon();
            }
         }

      });
      return tree;
   }
}

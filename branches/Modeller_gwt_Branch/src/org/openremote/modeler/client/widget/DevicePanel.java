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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.model.TreeDataModel;
import org.openremote.modeler.client.rpc.DeviceCommandService;
import org.openremote.modeler.client.rpc.DeviceCommandServiceAsync;
import org.openremote.modeler.client.rpc.DeviceService;
import org.openremote.modeler.client.rpc.DeviceServiceAsync;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.Protocol;
import org.openremote.modeler.domain.ProtocolAttr;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The Class DevicePanel.
 */
@SuppressWarnings("unchecked")
public class DevicePanel extends ContentPanel {

   private final DeviceServiceAsync deviceService = (DeviceServiceAsync) GWT.create(DeviceService.class);
   private TreeStore<ModelData> store;
   private TreePanel<ModelData> tree;

   /**
    * Instantiates a new device panel.
    */
   public DevicePanel() {
      setHeading("Device");
      setLayout(new FitLayout());
      createMenu();
      createTreeContainer();
      show();
   }

   /**
    * Creates the menu.
    */
   private void createMenu() {
      ToolBar toolBar = new ToolBar();
      Button newButton = new Button("New");
      Menu newMenu = new Menu();
      MenuItem newDeviceItem = new MenuItem("New device");
      MenuItem newCommandItem = new MenuItem("New command");

      newDeviceItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         public void componentSelected(MenuEvent ce) {
            final DeviceWindow deviceWindow = new DeviceWindow();
            deviceWindow.addSubmitListener(new Listener<AppEvent>() {
               public void handleEvent(AppEvent be) {
                  Map<String, String> map = be.getData();
                  Device device = new Device();
                  device.setName(map.get("name"));
                  device.setVendor(map.get("vendor"));
                  device.setModel(map.get("model"));
                  deviceService.saveDevice(device, new AsyncCallback<Device>() {
                     public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        MessageBox.info("Error", caught.getMessage(), null);
                     }
                     public void onSuccess(Device device) {
                        deviceWindow.hide();
                        TreeDataModel<Device> model = new TreeDataModel<Device>(device, device.getName());
                        store.add(model, true);
                        MessageBox.info("Info", "Add device " + device.getName() + " success.", null);
                     }
                  });
               }
            });
         }
      });

      newMenu.add(newDeviceItem);
      newMenu.add(newCommandItem);
      newMenu.add(createImportMenu());

      newButton.setMenu(newMenu);
      toolBar.add(newButton);

      Button edit = new Button("Edit");
      edit.addSelectionListener(new SelectionListener<ButtonEvent>(){
         public void componentSelected(ButtonEvent ce) {
            final ModelData selected = tree.getSelectionModel().getSelectedItem();
            if (selected.get(TreeDataModel.getDataProperty()) instanceof Device) {
               TreeDataModel<Device> deviceNode = (TreeDataModel<Device>) selected;
               final Device device = deviceNode.getData();
               final DeviceWindow editDeviceWindow = new DeviceWindow(device);
               editDeviceWindow.addSubmitListener(new Listener<AppEvent>(){
                  public void handleEvent(AppEvent be) {
                     Map<String, String> map = be.getData();
                     device.setName(map.get("name"));
                     device.setVendor(map.get("vendor"));
                     device.setModel(map.get("model"));
                     deviceService.saveDevice(device, new AsyncCallback<Device>() {
                        public void onFailure(Throwable caught) {
                           caught.printStackTrace();
                           MessageBox.info("Error", caught.getMessage(), null);
                        }
                        public void onSuccess(Device device) {
                           editDeviceWindow.hide();
                           TreeDataModel<Device> model = new TreeDataModel<Device>(device,device.getName());
                           store.remove(selected);
                           store.add(model, true);
                           MessageBox.info("Info", "Edit device " + device.getName() + " success.", null);
                        }
                     });
                  }
                  
               });
            }
         }
         
      });
      toolBar.add(edit);

      Button delete = new Button("Delete");
      toolBar.add(delete);
      delete.addSelectionListener(new SelectionListener<ButtonEvent>() {

         public void componentSelected(ButtonEvent ce) {
            ModelData selected = tree.getSelectionModel().getSelectedItem();
            // DeviceTreeModel device = new DeviceTreeModel("sub");
            // store.add(selected, device, false);

            if (selected.get(TreeDataModel.getDataProperty()) instanceof Device) {
               TreeDataModel<Device> deviceNode = (TreeDataModel<Device>) selected;
               Device device = deviceNode.getData();

               deviceService.removeDevice(device, new AsyncCallback<Void>() {
                  public void onFailure(Throwable caught) {
                     caught.printStackTrace();
                     MessageBox.info("Error", caught.getMessage(), null);
                  }

                  public void onSuccess(Void result) {
                     MessageBox.info("Info", "Remove success.", null);
                  }

               });
            }
            store.remove(selected);
         }

      });
      setTopComponent(toolBar);

   }

   /**
    * Creates the tree container.
    */
   private void createTreeContainer() {
      LayoutContainer treeContainer = new LayoutContainer();
      treeContainer.setScrollMode(Scroll.AUTO);
      treeContainer.setStyleAttribute("backgroundColor", "white");
      treeContainer.setBorders(true);

      store = new TreeStore<ModelData>();
      tree = new TreePanel<ModelData>(store);
      tree.setDisplayProperty(TreeDataModel.getDisplayProperty());
      Icons icon = GWT.create(Icons.class);
      tree.getStyle().setLeafIcon(icon.folder());
      treeContainer.add(tree);

      add(treeContainer);
   }

   private MenuItem createImportMenu() {
      MenuItem importCommandItem = new MenuItem("Import commands");
      importCommandItem.addSelectionListener(new SelectionListener<MenuEvent>() {

         @Override
         public void componentSelected(MenuEvent ce) {
            if (tree.getSelectionModel().getSelectedItem() != null) {
               final SelectIRWindow selectIRWindow = new SelectIRWindow();
               add(selectIRWindow);
               selectIRWindow.show();
               selectIRWindow.addSubmitListener(new Listener<AppEvent>() {
                  public void handleEvent(AppEvent be) {
                     List<ModelData> datas = be.getData();
                     importIRCommand(datas,selectIRWindow);
                  }

               });
            } else {
               MessageBox.alert("Notice", "You must select a device first.", null);
            }

         }

      });
      return importCommandItem;
   }

   private void importIRCommand(List<ModelData> datas,final SelectIRWindow selectIRWindow) {
      if (tree.getSelectionModel().getSelectedItem() != null) {
         if (tree.getSelectionModel().getSelectedItem().get(TreeDataModel.getDataProperty()) instanceof Device) {
            final TreeDataModel<Device> deviceNode = (TreeDataModel<Device>) tree.getSelectionModel().getSelectedItem();
            Device device = deviceNode.getData();
            List<DeviceCommand> deviceCommands = new ArrayList<DeviceCommand>();
            for (ModelData m : datas) {
               Protocol protocol = new Protocol();
               protocol.setType(Constants.INFRARED_TYPE);

               ProtocolAttr nameAttr = new ProtocolAttr();
               nameAttr.setName("name");
               nameAttr.setValue(m.get("remoteName").toString());
               nameAttr.setProtocol(protocol);
               protocol.getAttributes().add(nameAttr);

               ProtocolAttr commandAttr = new ProtocolAttr();
               commandAttr.setName("command");
               commandAttr.setValue(m.get("name").toString());
               commandAttr.setProtocol(protocol);
               protocol.getAttributes().add(commandAttr);

               DeviceCommand deviceCommand = new DeviceCommand();
               deviceCommand.setDevice(device);
               deviceCommand.setProtocol(protocol);
               deviceCommand.setName(m.get("name").toString());

               protocol.setDeviceCommand(deviceCommand);

               device.getDeviceCommands().add(deviceCommand);

               deviceCommands.add(deviceCommand);
            }

            DeviceCommandServiceAsync deviceCommandServiceAsync = (DeviceCommandServiceAsync) GWT
                  .create(DeviceCommandService.class);
            deviceCommandServiceAsync.saveAll(deviceCommands, new AsyncCallback<List<DeviceCommand>>() {

               public void onFailure(Throwable e) {
                  MessageBox.alert("Error", "Import IR Command occur " + e.getMessage(), null);
               }

               public void onSuccess(List<DeviceCommand> deviceCommands) {
                  for (DeviceCommand command : deviceCommands) {
                     TreeDataModel<DeviceCommand> deviceCommandNode = new TreeDataModel<DeviceCommand>(command, command
                           .getName());
                     tree.getStore().add(deviceNode, deviceCommandNode, false);
                     
                  }
                  tree.setExpanded(deviceNode, true);
                  selectIRWindow.hide();

               }

            });

         }
      }
   }
}

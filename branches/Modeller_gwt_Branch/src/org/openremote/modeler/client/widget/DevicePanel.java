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
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
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

/**
 * The Class DevicePanel.
 */
@SuppressWarnings("unchecked")
public class DevicePanel extends ContentPanel {

   private final DeviceServiceAsync deviceService = (DeviceServiceAsync) GWT.create(DeviceService.class);
   private final DeviceCommandServiceAsync deviceCommandServiceAsync = (DeviceCommandServiceAsync) GWT
         .create(DeviceCommandService.class);

   private TreePanel<TreeDataModel> tree;
   private Icons icon = GWT.create(Icons.class);

   /**
    * Instantiates a new device panel.
    */
   public DevicePanel() {
      setHeading("Device");
      setIcon(icon.device());
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
      newButton.setIcon(icon.add());
      Menu newMenu = new Menu();
      MenuItem newDeviceItem = new MenuItem("New Device");
      newDeviceItem.setIcon(icon.addDevice());
      MenuItem newCommandItem = new MenuItem("New Command");
      newCommandItem.setIcon(icon.addCmd());
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
                  deviceService.saveDevice(device, new AsyncSuccessCallback<Device>() {
                     public void onSuccess(Device device) {
                        deviceWindow.hide();
                        TreeDataModel model = new TreeDataModel(device, device.getName());
                        tree.getStore().add(model, true);
                        //create and select it.
                        tree.getSelectionModel().select(model, false);
                        Info.display("Info", "Add device " + device.getName() + " success.");
                     }
                  });
               }
            });
         }
      });

      newCommandItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         public void componentSelected(MenuEvent ce) {
            final TreeDataModel deviceNode = tree.getSelectionModel().getSelectedItem();
            if (deviceNode != null && deviceNode.getData() instanceof Device) {
               final DeviceCommandWindow deviceCommandWindow = new DeviceCommandWindow();
               deviceCommandWindow.show();
               deviceCommandWindow.addSubmitListener(new Listener<AppEvent>() {
                  public void handleEvent(AppEvent be) {
                     Device device = (Device) deviceNode.getData();
                     Map<String, String> map = be.getData();

                     DeviceCommand deviceCommand = new DeviceCommand();
                     deviceCommand.setName(map.get("name"));
                     deviceCommand.setDevice(device);

                     Protocol protocol = new Protocol();
                     protocol.setType(map.get("protocol"));
                     protocol.setDeviceCommand(deviceCommand);

                     for (String key : map.keySet()) {
                        System.out.println(key + ": " + map.get(key));
                        if ("name".equals(key) || "protocol".equals(key)) {
                           continue;
                        }
                        ProtocolAttr protocolAttr = new ProtocolAttr();
                        protocolAttr.setName(key);
                        protocolAttr.setValue((map.get(key)));
                        protocolAttr.setProtocol(protocol);
                        protocol.getAttributes().add(protocolAttr);
                     }

                     deviceCommand.setProtocol(protocol);
                     device.getDeviceCommands().add(deviceCommand);

                     deviceCommandServiceAsync.save(deviceCommand, new AsyncSuccessCallback<DeviceCommand>() {
                        public void onSuccess(DeviceCommand deviceCommand) {
                           TreeDataModel deviceCommandNode = new TreeDataModel(
                                 deviceCommand, deviceCommand.getName());
                           tree.getStore().add(deviceNode, deviceCommandNode, false);
                           tree.setExpanded(deviceNode, true);
                           deviceCommandWindow.hide();
                        }
                     });
                  }
               });
            } else {
               MessageBox.info("Error", "Please select a device", null);
            }
         }
      });

      newMenu.add(newDeviceItem);
      newMenu.add(newCommandItem);
      newMenu.add(createImportMenu());

      newButton.setMenu(newMenu);
      toolBar.add(newButton);

      Button edit = new Button("Edit");
      edit.setIcon(icon.edit());
      edit.addSelectionListener(new SelectionListener<ButtonEvent>() {
         public void componentSelected(ButtonEvent ce) {
            final TreeDataModel selectedNode = tree.getSelectionModel().getSelectedItem();
            if (selectedNode != null && selectedNode.getData() instanceof Device) {
               final Device device = (Device) selectedNode.getData();
               final DeviceWindow editDeviceWindow = new DeviceWindow(device);
               editDeviceWindow.addSubmitListener(new Listener<AppEvent>() {
                  public void handleEvent(AppEvent be) {
                     Map<String, String> map = be.getData();
                     device.setName(map.get("name"));
                     device.setVendor(map.get("vendor"));
                     device.setModel(map.get("model"));
                     deviceService.updateDevice(device, new AsyncSuccessCallback<Void>() {
                        public void onSuccess(Void result) {
                           editDeviceWindow.hide();
                           selectedNode.set(TreeDataModel.getDisplayProperty(), device.getName());
                           tree.getStore().update(selectedNode);
                           Info.display("Info", "Edit device " + device.getName() + " success.");
                        }
                     });
                  }

               });
            } else if (selectedNode != null && selectedNode.getData() instanceof DeviceCommand) {
               final DeviceCommand deviceCommand = (DeviceCommand) selectedNode.getData();
               deviceCommandServiceAsync.loadById(deviceCommand.getOid(), new AsyncSuccessCallback<DeviceCommand>(){
                  public void onSuccess(DeviceCommand command) {
                     deviceCommand.setProtocol(command.getProtocol());
                     final DeviceCommandWindow deviceCommandWindow = new DeviceCommandWindow(deviceCommand);
                     deviceCommandWindow.show();
                     deviceCommandWindow.addSubmitListener(new Listener<AppEvent>() {
                        public void handleEvent(AppEvent be) {
                           Map<String, String> map = be.getData();
                           
                           deviceCommand.setName(map.get("name"));
                           List<ProtocolAttr> attrs = deviceCommand.getProtocol().getAttributes();
                           for (int i = 0; i < attrs.size(); i++) {
                              deviceCommand.getProtocol().getAttributes().get(i).setValue(map.get(attrs.get(i).getName()));
                           };
                           deviceCommandServiceAsync.update(deviceCommand, new AsyncSuccessCallback<Void>() {
                              public void onSuccess(Void result) {
                                 deviceCommandWindow.hide();
                                 selectedNode.set(TreeDataModel.getDisplayProperty(), deviceCommand.getName());
                                 tree.getStore().update(selectedNode);
                                 Info.display("Info", "Edit device command " + deviceCommand.getName() + " success.");
                              }
                           });
                        }
                     });
                  }
               });
            }
         }
      });
      toolBar.add(edit);

      Button delete = new Button("Delete");
      delete.setIcon(icon.delete());
      toolBar.add(delete);
      delete.addSelectionListener(new SelectionListener<ButtonEvent>() {

         public void componentSelected(ButtonEvent ce) {
            TreeDataModel selected = tree.getSelectionModel().getSelectedItem();
            if (selected != null && selected.getData() instanceof Device) {
               Device device = (Device) selected.getData();
               deviceService.deleteDevice(device.getOid(), new AsyncSuccessCallback<Void>() {
                  public void onSuccess(Void result) {
                     Info.display("Info", "Delete success.");
                  }
               });
            } else if (selected != null && selected.getData() instanceof DeviceCommand) {
               DeviceCommand deviceCommand = (DeviceCommand) selected.getData();
               deviceCommandServiceAsync.deleteCommand(deviceCommand.getOid(), new AsyncSuccessCallback<Void>() {
                  public void onSuccess(Void result) {
                     Info.display("Info", "Delete success.");
                  }
               });
            }
            tree.getStore().remove(selected);
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
      treeContainer.setBorders(false);
     
      tree = TreePanelBuilder.buildDeviceCommandTree();
      
      treeContainer.add(tree);

      add(treeContainer);
   }

   private MenuItem createImportMenu() {
      MenuItem importCommandItem = new MenuItem("Import commands");
      importCommandItem.setIcon(icon.importFromDB());
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
                     importIRCommand(datas, selectIRWindow);
                  }

               });
            } else {
               MessageBox.alert("Notice", "You must select a device first.", null);
            }

         }

      });
      return importCommandItem;
   }

   private void importIRCommand(List<ModelData> datas, final SelectIRWindow selectIRWindow) {
      if (tree.getSelectionModel().getSelectedItem() != null) {
         if (tree.getSelectionModel().getSelectedItem().getData() instanceof Device) {
            final TreeDataModel deviceNode = tree.getSelectionModel().getSelectedItem();
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
            deviceCommandServiceAsync.saveAll(deviceCommands, new AsyncSuccessCallback<List<DeviceCommand>>() {
               public void onSuccess(List<DeviceCommand> deviceCommands) {
                  for (DeviceCommand command : deviceCommands) {
                     TreeDataModel deviceCommandNode = new TreeDataModel(command, command
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

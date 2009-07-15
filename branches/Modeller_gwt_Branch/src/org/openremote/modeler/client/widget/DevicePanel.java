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
import org.openremote.modeler.client.rpc.ProtocolService;
import org.openremote.modeler.client.rpc.ProtocolServiceAsync;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.Protocol;
import org.openremote.modeler.domain.ProtocolAttr;
import org.openremote.modeler.protocol.ProtocolDefinition;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * The Class DevicePanel.
 */
@SuppressWarnings("unchecked")
public class DevicePanel extends ContentPanel {

   private final DeviceServiceAsync deviceService = (DeviceServiceAsync) GWT.create(DeviceService.class);
   private final DeviceCommandServiceAsync deviceCommandServiceAsync = (DeviceCommandServiceAsync) GWT.create(DeviceCommandService.class);
   private TreeStore<ModelData> store;
   private TreePanel<ModelData> tree;
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
                  deviceService.saveDevice(device, new AsyncCallback<Device>() {
                     public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        MessageBox.info("Error", caught.getMessage(), null);
                     }
                     public void onSuccess(Device device) {
                        deviceWindow.hide();
                        TreeDataModel<Device> model = new TreeDataModel<Device>(device, device.getName());
                        store.add(model, true);
                        Info.display("Info", "Add device " + device.getName() + " success.");
                     }
                  });
               }
            });
         }
      });
      
      final ProtocolServiceAsync protocolService = (ProtocolServiceAsync) GWT.create(ProtocolService.class);
      newCommandItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         public void componentSelected(MenuEvent ce) {
            final ModelData selected = tree.getSelectionModel().getSelectedItem();
            if (selected != null && selected.get(TreeDataModel.getDataProperty()) instanceof Device) {
               protocolService.getProtocols(new AsyncCallback<Map<String, ProtocolDefinition>>() {
                  public void onFailure(Throwable caught) {
                     caught.printStackTrace();
                     MessageBox.info("Error", caught.getMessage(), null);
                  }

                  public void onSuccess(Map<String, ProtocolDefinition> protocols) {
                     final DeviceCommandWindow deviceCommandWindow = new DeviceCommandWindow(protocols);
                     deviceCommandWindow.show();
                     deviceCommandWindow.addSubmitListener(new Listener<AppEvent>() {
                        public void handleEvent(AppEvent be) {
                           final TreeDataModel<Device> deviceNode = (TreeDataModel<Device>) selected;
                           Device device = deviceNode.getData();
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

                           deviceCommandServiceAsync.save(deviceCommand, new AsyncCallback<DeviceCommand>() {
                              public void onFailure(Throwable caught) {
                                 caught.printStackTrace();
                                 MessageBox.info("Error", caught.getMessage(), null);
                              }

                              public void onSuccess(DeviceCommand deviceCommand) {
                                 TreeDataModel<DeviceCommand> deviceCommandNode = new TreeDataModel<DeviceCommand>(
                                       deviceCommand, deviceCommand.getName());
                                 tree.getStore().add(deviceNode, deviceCommandNode, false);
                                 tree.setExpanded(deviceNode, true);
                                 deviceCommandWindow.hide();
                              }

                           });
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
      edit.addSelectionListener(new SelectionListener<ButtonEvent>(){
         public void componentSelected(ButtonEvent ce) {
            final ModelData selected = tree.getSelectionModel().getSelectedItem();
            if (selected != null && selected.get(TreeDataModel.getDataProperty()) instanceof Device) {
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
                           TreeDataModel<Device> deviceModel = new TreeDataModel<Device>(device,device.getName());
                           int index = store.indexOf(selected);
                           store.remove(selected);
                           List<DeviceCommand> deviceCommands = device.getDeviceCommands();
                           if(deviceCommands!=null){
                              for (DeviceCommand deviceCommand : deviceCommands) {
                                 TreeDataModel<DeviceCommand> commandModel = new TreeDataModel<DeviceCommand>(deviceCommand, deviceCommand.getName());
                                 deviceModel.add(commandModel);
                              }
                           }
                           store.insert(deviceModel, index, true);
                           Info.display("Info", "Edit device " + device.getName() + " success.");
                        }
                     });
                  }
                  
               });
            }else if(selected != null && selected.get(TreeDataModel.getDataProperty()) instanceof DeviceCommand){
               TreeDataModel<DeviceCommand> deviceCommandNode = (TreeDataModel<DeviceCommand>) selected;
               DeviceCommand deviceCommand = deviceCommandNode.getData();
               final DeviceCommandWindow deviceCommandWindow = new DeviceCommandWindow(deviceCommand);
               deviceCommandWindow.show();
               deviceCommandWindow.addSubmitListener(new Listener<AppEvent>(){
                  public void handleEvent(AppEvent be) {
                     if (tree.getSelectionModel().getSelectedItem() != null) {
                        if (tree.getSelectionModel().getSelectedItem().get(TreeDataModel.getDataProperty()) instanceof DeviceCommand) {
                           final TreeDataModel<DeviceCommand> deviceCommandNode = (TreeDataModel<DeviceCommand>) tree.getSelectionModel().getSelectedItem();
                           DeviceCommand deviceCommand = deviceCommandNode.getData();
                           Map<String, String> map = be.getData();
                           
                           deviceCommand.setName(map.get("name"));
                           List<ProtocolAttr> attrs = deviceCommand.getProtocol().getAttributes();
                           for (int i = 0; i < attrs.size(); i++) {
                              deviceCommand.getProtocol().getAttributes().get(i).setValue(map.get(attrs.get(i).getName()));
                           };
                           
                           deviceCommandServiceAsync.save(deviceCommand, new AsyncCallback<DeviceCommand>() {
                              public void onFailure(Throwable caught) {
                                 caught.printStackTrace();
                                 MessageBox.info("Error", caught.getMessage(), null);
                              }
                              public void onSuccess(DeviceCommand deviceCommand) {
                                 TreeDataModel<DeviceCommand> deviceCommandNode = new TreeDataModel<DeviceCommand>(deviceCommand, deviceCommand.getName());
                                 ModelData parent = store.getParent(selected);
                                 int index = store.getChildren(parent).indexOf(selected);
                                 store.remove(selected);
                                 store.insert(parent, deviceCommandNode, index, false);
                                 deviceCommandWindow.hide();
                              }
                              
                           });
                        }
                     }
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
            ModelData selected = tree.getSelectionModel().getSelectedItem();
            if (selected != null && selected.get(TreeDataModel.getDataProperty()) instanceof Device) {
               TreeDataModel<Device> deviceNode = (TreeDataModel<Device>) selected;
               Device device = deviceNode.getData();

               deviceService.removeDevice(device, new AsyncCallback<Void>() {
                  public void onFailure(Throwable caught) {
                     caught.printStackTrace();
                     MessageBox.info("Error", caught.getMessage(), null);
                  }

                  public void onSuccess(Void result) {
                     Info.display("Info", "Remove success.");
                  }

               });
            }else if(selected != null && selected.get(TreeDataModel.getDataProperty()) instanceof DeviceCommand){
               TreeDataModel<DeviceCommand> deviceCommandNode = (TreeDataModel<DeviceCommand>) selected;
               DeviceCommand deviceCommand = deviceCommandNode.getData();
               deviceCommandServiceAsync.removeCommand(deviceCommand, new AsyncCallback<Void>() {
                  public void onFailure(Throwable caught) {
                     caught.printStackTrace();
                     MessageBox.info("Error", caught.getMessage(), null);
                  }
                  public void onSuccess(Void result) {
                     Info.display("Info", "Remove success.");
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
      treeContainer.setBorders(false);

      store = new TreeStore<ModelData>();
      tree = new TreePanel<ModelData>(store);
      tree.setBorders(false);
      deviceService.loadAll(new AsyncCallback<List<Device>>(){
         public void onFailure(Throwable caught) {
            caught.printStackTrace();
            MessageBox.info("Error", caught.getMessage(), null);
         }
         public void onSuccess(List<Device> devices) {
            if(devices != null){
               for (Device device : devices) {
                  TreeDataModel<Device> deviceModel = new TreeDataModel<Device>(device, device.getName());
                  if(device.getDeviceCommands() != null){
                     for (DeviceCommand deviceCommand : device.getDeviceCommands()) {
                        TreeDataModel<DeviceCommand> commandModel = new TreeDataModel<DeviceCommand>(deviceCommand, deviceCommand.getName());
                        deviceModel.add(commandModel);
                     }
                  }
                  store.add(deviceModel, true);
               }
            }
         }
      });
      tree.setDisplayProperty(TreeDataModel.getDisplayProperty());
      tree.setIconProvider(new ModelIconProvider<ModelData>(){

         public AbstractImagePrototype getIcon(ModelData model) {
            TreeDataModel thisModel = (TreeDataModel)model;
            
            if(thisModel.getData() instanceof DeviceCommand){
               return icon.deviceCmd();
            }else if (thisModel.getData() instanceof Device){
               return icon.device();
            }else {
               return icon.folder();
            }
         }
         
      });
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

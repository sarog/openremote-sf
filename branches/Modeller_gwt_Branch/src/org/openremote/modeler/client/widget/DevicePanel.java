/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
 * 
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
import org.openremote.modeler.client.rpc.AsyncServiceFactory;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.data.ChangeEventSource;
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
import com.google.gwt.user.client.Element;

/**
 * The Class DevicePanel.
 */
public class DevicePanel extends ContentPanel {

   /** The tree. */
   private TreePanel<BeanModel> tree;
   
   /** The icon. */
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
    * Creates the tree container.
    */
   private void createTreeContainer() {
      tree = TreePanelBuilder.buildDeviceCommandTree();
      LayoutContainer treeContainer = new LayoutContainer() {
         @Override
         protected void onRender(Element parent, int index) {
            super.onRender(parent, index);
            add(tree);
         }
      };
      treeContainer.setScrollMode(Scroll.AUTO);
      treeContainer.setStyleAttribute("backgroundColor", "white");
      treeContainer.setBorders(false);

      add(treeContainer);
   }
   
   /**
    * Creates the menu.
    */
   private void createMenu() {
      ToolBar toolBar = new ToolBar();
      Button newButton = new Button("New");
      newButton.setIcon(icon.add());
      
      Menu newMenu = new Menu();
      newMenu.add(createNewDeviceMenu());
      newMenu.add(createNewCommandMenu());
      newMenu.add(createImportMenu());

      newButton.setMenu(newMenu);
      
      toolBar.add(newButton);
      toolBar.add(createEditButton());
      toolBar.add(createDeleteButton());
      
      setTopComponent(toolBar);
   }

   /**
    * Creates the new device menu.
    * 
    * @return the menu item
    */
   private MenuItem createNewDeviceMenu() {
      MenuItem newDeviceItem = new MenuItem("New Device");
      newDeviceItem.setIcon(icon.addDevice());
      newDeviceItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         public void componentSelected(MenuEvent ce) {
            createDevice();
         }
      });
      return newDeviceItem;
   }
   
   /**
    * Creates the device.
    */
   private void createDevice() {
      final DeviceWindow deviceWindow = new DeviceWindow();
      deviceWindow.addSubmitListener(new Listener<AppEvent>() {
         public void handleEvent(AppEvent be) {
            deviceWindow.hide();
            BeanModel deviceModel = be.getData();
            tree.getStore().add(deviceModel, true);
            //create and select it.
            tree.getSelectionModel().select(deviceModel, false);
            Info.display("Info", "Add device " + deviceModel.get("name") + " success.");
         }
      });
   }
   
   /**
    * Creates the new command menu.
    * 
    * @return the menu item
    */
   private MenuItem createNewCommandMenu() {
      MenuItem newCommandItem = new MenuItem("New Command");
      newCommandItem.setIcon(icon.addCmd());
      newCommandItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         public void componentSelected(MenuEvent ce) {
            createDeviceCommand();
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
         final DeviceCommandWindow deviceCommandWindow = new DeviceCommandWindow((Device)deviceModel.getBean());
         deviceCommandWindow.addSubmitListener(new Listener<AppEvent>() {
            public void handleEvent(AppEvent be) {
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
   
   /**
    * Creates the edit button.
    * 
    * @return the button
    */
   private Button createEditButton() {
      Button editBtn = new Button("Edit");
      editBtn.setIcon(icon.edit());
      editBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         public void componentSelected(ButtonEvent ce) {
            BeanModel selectedModel = tree.getSelectionModel().getSelectedItem();
            if (selectedModel != null && selectedModel.getBean() instanceof Device) {
               editDevice(selectedModel);
            } else if (selectedModel != null && selectedModel.getBean() instanceof DeviceCommand) {
               editCommand(selectedModel);
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
      editDeviceWindow.addSubmitListener(new Listener<AppEvent>() {
         public void handleEvent(AppEvent be) {
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
      deviceCommandWindow.addSubmitListener(new Listener<AppEvent>() {
         public void handleEvent(AppEvent be) {
            BeanModel deviceCommandModel = be.getData();
            tree.getStore().update(deviceCommandModel);
            Info.display("Info", "Edit device command " + deviceCommandModel.get("name") + " success.");
            deviceCommandWindow.hide();
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
      deleteBtn.setIcon(icon.delete());
      deleteBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         public void componentSelected(ButtonEvent ce) {
            BeanModel selectedModel = tree.getSelectionModel().getSelectedItem();
            if (selectedModel != null && selectedModel.getBean() instanceof Device) {
               deleteDevice(selectedModel);
            } else if (selectedModel != null && selectedModel.getBean() instanceof DeviceCommand) {
               deleteCommand(selectedModel);
            }
         }
      });
      return deleteBtn;
   }
   
   /**
    * Delete device.
    * 
    * @param selectedModel the selected model
    */
   private void deleteDevice(final BeanModel selectedModel) {
      Device device = (Device) selectedModel.getBean();
      AsyncServiceFactory.getDeviceServiceAsync().deleteDevice(device.getOid(), new AsyncSuccessCallback<Void>() {
         public void onSuccess(Void result) {
            selectedModel.notify(new ChangeEvent(ChangeEventSource.Remove, tree.getStore().getParent(selectedModel), selectedModel));
            tree.getStore().remove(selectedModel);
            Info.display("Info", "Delete success.");
         }
      });
   }
   
   /**
    * Delete command.
    * 
    * @param selectedModel the selected model
    */
   private void deleteCommand(final BeanModel selectedModel) {
      DeviceCommand deviceCommand = (DeviceCommand) selectedModel.getBean();
      AsyncServiceFactory.getDeviceCommandServiceAsync().deleteCommand(deviceCommand.getOid(), new AsyncSuccessCallback<Void>() {
         public void onSuccess(Void result) {
            selectedModel.notify(new ChangeEvent(ChangeEventSource.Remove, tree.getStore().getParent(selectedModel), selectedModel));
            tree.getStore().remove(selectedModel);
            Info.display("Info", "Delete success.");
         }
      });
   }
   
   /**
    * Creates the import menu.
    * 
    * @return the menu item
    */
   private MenuItem createImportMenu() {
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
         final SelectIRWindow selectIRWindow = new SelectIRWindow((Device)deviceModel.getBean());
         selectIRWindow.addSubmitListener(new Listener<AppEvent>() {
            public void handleEvent(AppEvent be) {
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
}

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
package org.openremote.modeler.client.widget.buildingmodeler;

import java.util.List;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.listener.ConfirmDeleteListener;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.proxy.DeviceBeanModelProxy;
import org.openremote.modeler.client.proxy.DeviceCommandBeanModelProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.TreePanelBuilder;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.selenium.DebugId;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
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
      treeContainer.ensureDebugId(DebugId.DEVICE_TREE_CONTAINER);
      treeContainer.setScrollMode(Scroll.AUTO);
      treeContainer.setStyleAttribute("backgroundColor", "white");
      treeContainer.setBorders(false);
      add(treeContainer);
   }
   
   /**
    * Creates the menu.
    */
   private void createMenu() {      
      Button newButton = new Button("New");
      newButton.setToolTip("Create Device or DeviceCommand");
      newButton.ensureDebugId(DebugId.DEVICE_NEW_BTN);
      newButton.setIcon(icon.add());
      
      Menu newMenu = new Menu();
      newMenu.add(createNewDeviceMenuItem());
      newMenu.add(createNewCommandMenu());
      newMenu.add(createImportMenuItem());

      newButton.setMenu(newMenu);
      
      ToolBar toolBar = new ToolBar();
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
   private MenuItem createNewDeviceMenuItem() {
      MenuItem newDeviceItem = new MenuItem("New Device");
      newDeviceItem.ensureDebugId(DebugId.NEW_DEVICE_MENU_ITEM);
      newDeviceItem.setIcon(icon.addDevice());
      newDeviceItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         public void componentSelected(MenuEvent ce) {
            final DeviceWizardWindow deviceWindow = new DeviceWizardWindow(new Device().getBeanModel());
            deviceWindow.addListener(SubmitEvent.Submit, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  deviceWindow.hide();
                  BeanModel deviceModel = be.getData();
                  tree.getStore().add(deviceModel, true);
                  
                  for (BeanModel deviceCommandModel : DeviceCommand.createModels(((Device)deviceModel.getBean()).getDeviceCommands())) {
                     tree.getStore().add(deviceModel, deviceCommandModel, false);
                  }
                  tree.setExpanded(deviceModel, true);
                  
                  //create and select it.
                  tree.getSelectionModel().select(deviceModel, false);
                  Info.display("Info", "Add device " + deviceModel.get("name") + " success.");
                  
               }
            });
         }
      });
      return newDeviceItem;
   }
   
   /**
    * Creates the new command menu.
    * 
    * @return the menu item
    */
   private MenuItem createNewCommandMenu() {
      MenuItem newCommandItem = new MenuItem("New Command");
      newCommandItem.ensureDebugId(DebugId.NEW_COMMAND_ITEM);
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
         final DeviceCommandWindow deviceCommandWindow = new DeviceCommandWindow((Device) deviceModel.getBean());
         deviceCommandWindow.addListener(SubmitEvent.Submit, new SubmitListener() {
            @Override
            public void afterSubmit(SubmitEvent be) {
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
      editBtn.setToolTip("Edit Device or DeviceCommand");
      editBtn.ensureDebugId(DebugId.DEVICE_EDIT_BTN);
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
      editDeviceWindow.addListener(SubmitEvent.Submit, new SubmitListener() {
         @Override
         public void afterSubmit(SubmitEvent be) {
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
      deviceCommandWindow.addListener(SubmitEvent.Submit, new SubmitListener() {
         @Override
         public void afterSubmit(SubmitEvent be) {
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
      deleteBtn.setToolTip("Delete Device or DeviceCommand");
      deleteBtn.ensureDebugId(DebugId.DELETE_DEVICE_BUTTON);
      deleteBtn.setIcon(icon.delete());
      deleteBtn.addSelectionListener(new ConfirmDeleteListener<ButtonEvent>() {
         public void onDelete(ButtonEvent ce) {
            List<BeanModel> selectedModels = tree.getSelectionModel().getSelectedItems();
            for (BeanModel selectedModel : selectedModels) {
               if (selectedModel != null && selectedModel.getBean() instanceof Device) {
                  deleteDevice(selectedModel);
               } else if (selectedModel != null && selectedModel.getBean() instanceof DeviceCommand) {
                  deleteCommand(selectedModel);
               }
            }
         }
      });
      return deleteBtn;
   }
   
   /**
    * Delete device.
    * 
    * @param deviceModel the device model
    */
   private void deleteDevice(final BeanModel deviceModel) {
      DeviceBeanModelProxy.deleteDevice(deviceModel, new AsyncSuccessCallback<Void>() {
         @Override
         public void onSuccess(Void result) {
            tree.getStore().remove(deviceModel);
            Info.display("Info", "Delete success.");
         }
         
      });
   }
   
   /**
    * Delete command.
    * 
    * @param deviceCommnadModel the device commnad model
    */
   private void deleteCommand(final BeanModel deviceCommnadModel) {
      DeviceCommandBeanModelProxy.deleteDeviceCommand(deviceCommnadModel, new AsyncSuccessCallback<Void>() {
         @Override
         public void onSuccess(Void result) {
            tree.getStore().remove(deviceCommnadModel);
            Info.display("Info", "Delete success.");
         }
      });
   }
   
   /**
    * Creates the import menu item.
    * 
    * @return the menu item
    */
   private MenuItem createImportMenuItem() {
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
         final IRCommandImportWindow selectIRWindow = new IRCommandImportWindow(deviceModel);
         selectIRWindow.addListener(SubmitEvent.Submit, new SubmitListener() {
            @Override
            public void afterSubmit(SubmitEvent be) {
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

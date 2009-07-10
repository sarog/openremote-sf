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

import java.util.Map;

import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.model.DeviceTreeModel;
import org.openremote.modeler.client.rpc.DeviceService;
import org.openremote.modeler.client.rpc.DeviceServiceAsync;
import org.openremote.modeler.domain.Device;

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
   private void createMenu(){
      ToolBar toolBar = new ToolBar();
      Button newButton = new Button("New");
      Menu newMenu = new Menu();
      MenuItem newDeviceItem = new MenuItem("New device");
      MenuItem newCommandItem = new MenuItem("New command");
      MenuItem importCommandItem = new MenuItem("Import commands");
      
      newDeviceItem.addSelectionListener(new SelectionListener<MenuEvent>(){
         public void componentSelected(MenuEvent ce) {
            final DeviceForm deviceForm = new DeviceForm();
            deviceForm.addSubmitListener(new Listener<AppEvent>() {
               public void handleEvent(AppEvent be) {
                  Map<String, String> map = be.getData();
                  deviceService.saveDevice(map, new AsyncCallback<Device>(){

                     public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        MessageBox.info("Error", caught.getMessage(), null);
                     }

                     public void onSuccess(Device device) {
                        deviceForm.close();
                        DeviceTreeModel model = new DeviceTreeModel(device);
                        store.add(model, true);
                        MessageBox.info("Info", "Add device "+device.getName()+" success.", null);
                     }
                     
                  });               
               }
            });
         }
      });
      
      newMenu.add(newDeviceItem);
      newMenu.add(newCommandItem);
      newMenu.add(importCommandItem);
      
      newButton.setMenu(newMenu);
      toolBar.add(newButton);
      
      Button edit = new Button("Edit");
      toolBar.add(edit);
      
      Button delete = new Button("Delete");
      toolBar.add(delete);
      delete.addSelectionListener(new SelectionListener<ButtonEvent>(){
         public void componentSelected(ButtonEvent ce) {
            ModelData selected = tree.getSelectionModel().getSelectedItem();
//            DeviceTreeModel device = new DeviceTreeModel("sub");
//            store.add(selected, device, false);
            store.remove(selected);
         }
         
      });
      setTopComponent(toolBar);
      
   }
   
   /**
    * Creates the tree container.
    */
   private void createTreeContainer(){
      LayoutContainer treeContainer = new LayoutContainer();
      treeContainer.setScrollMode(Scroll.AUTO);
      treeContainer.setStyleAttribute("backgroundColor", "white");
      treeContainer.setBorders(true);
      
      store = new TreeStore<ModelData>();
      tree = new TreePanel<ModelData>(store); 
      tree.setDisplayProperty("name");
      Icons icon = GWT.create(Icons.class);
      tree.getStyle().setLeafIcon(icon.folder());
      treeContainer.add(tree);
      
      add(treeContainer);
   }
}

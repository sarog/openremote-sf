package org.openremote.modeler.client.widget;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

public class DevicePanel extends ContentPanel {
   
   public DevicePanel() {
      setHeading("Device");
      setLayout(new FitLayout());
      createMenu();
      createTreeContainer();
      show();
   }
   
   private void createMenu(){
      ToolBar toolBar = new ToolBar();
      Button newButton = new Button("New");
      Menu newMenu = new Menu();
      MenuItem newDeviceItem = new MenuItem("New device");
      MenuItem newCommandItem = new MenuItem("New command");
      MenuItem importCommandItem = new MenuItem("Import command");
      
      newDeviceItem.addSelectionListener(new SelectionListener<MenuEvent>(){
         public void componentSelected(MenuEvent ce) {
            DeviceForm deviceForm = new DeviceForm();
            deviceForm.show();
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
      add(treeContainer);
   }
}

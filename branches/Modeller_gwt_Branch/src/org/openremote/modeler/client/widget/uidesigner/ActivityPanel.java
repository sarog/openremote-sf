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
package org.openremote.modeler.client.widget.uidesigner;

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.listener.ConfirmDeleteListener;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.model.Position;
import org.openremote.modeler.client.proxy.ActivityBeanModelProxy;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.proxy.ScreenBeanModelProxy;
import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.client.widget.TreePanelBuilder;
import org.openremote.modeler.domain.Activity;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroRef;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.UIButton;
import org.openremote.modeler.selenium.DebugId;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
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
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Element;

/**
 * The Class ActivityPanel.
 * 
 * @author handy.wang
 */
public class ActivityPanel extends ContentPanel {

   /** The tree. */
   private TreePanel<BeanModel> tree;
   
   /** The icon. */
   private Icons icon = GWT.create(Icons.class);

   /**
    * Instantiates a new activity panel.
    */
   public ActivityPanel(ScreenTab screenTab) {
      setHeading("Activity");
      setIcon(icon.activityIcon());
      setLayout(new FitLayout());
      createMenu();
      createActivityTree(screenTab);
   }

   /**
    * Creates the activity tree.
    */
   private void createActivityTree(ScreenTab screenTab) {
      tree = TreePanelBuilder.buildActivityTree(screenTab);    
      LayoutContainer treeContainer = new LayoutContainer() {
         @Override
         protected void onRender(Element parent, int index) {
            super.onRender(parent, index);
            add(tree);
         }
      };
      treeContainer.ensureDebugId(DebugId.ACTIVITY_TREE_CONTAINER);
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
      toolBar.add(createNewBtn());
      toolBar.add(createEditBtn());
      toolBar.add(createDeleteBtn());
      setTopComponent(toolBar);
   }

   /**
    * Creates the "new" btn.
    * 
    * @return the button
    */
   private Button createNewBtn() {
      Button newButton = new Button("New");
      newButton.ensureDebugId(DebugId.ACTIVITY_NEW_BTN);
      newButton.setIcon(icon.add());

      Menu newMenu = new Menu();
      newMenu.add(createNewActivityMenuItem());
      newMenu.add(createNewScreenMenuItem());
      newButton.setMenu(newMenu);
      return newButton;
   }

   /**
    * Creates the new activity menu item.
    * 
    * @return the "newActivity" menu item.
    */
   private MenuItem createNewActivityMenuItem() {
      MenuItem newActivityMenuItem = new MenuItem("New Activity");
      newActivityMenuItem.ensureDebugId(DebugId.NEW_ACTIVITY_MENU_ITEM);
      newActivityMenuItem.setIcon(icon.addActivityIcon());
      newActivityMenuItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         @Override
         public void componentSelected(MenuEvent ce) {
            createActivity();
         }
      });
      return newActivityMenuItem;
   }

   /**
    * Creates the activity.
    */
   protected void createActivity() {
      final ActivityWindow activityWindow = new ActivityWindow();
      activityWindow.addListener(SubmitEvent.Submit, new SubmitListener() {
         @Override
         public void afterSubmit(SubmitEvent be) {
            activityWindow.hide();
            BeanModel activityModel = be.getData();
            tree.getStore().add(activityModel, true);
            // create and select it.
            tree.getSelectionModel().select(activityModel, false);
            Info.display("Info", "Add activity " + activityModel.get("name") + " success.");
         }
      });
   }

   /**
    * Creates the new screen menu item.
    * 
    * @return the "newScreen" menu item.
    */
   private MenuItem createNewScreenMenuItem() {
      MenuItem newScreenMenuItem = new MenuItem("New Screen");
      newScreenMenuItem.ensureDebugId(DebugId.NEW_SCREEN_MENU_ITEM);
      newScreenMenuItem.setIcon(icon.addScreenIcon());
      newScreenMenuItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         @Override
         public void componentSelected(MenuEvent ce) {
            createScreen();
         }
      });
      return newScreenMenuItem;
   }

   /**
    * Creates a new screen.
    */
   protected void createScreen() {
      final BeanModel activityModel = tree.getSelectionModel().getSelectedItem();
      if (activityModel != null && (activityModel.getBean() instanceof Activity)) {
         final ScreenWindow screenWindow = new ScreenWindow((Activity)activityModel.getBean());
         screenWindow.addListener(SubmitEvent.Submit, new SubmitListener() {
            @Override
            public void afterSubmit(SubmitEvent be) {
               screenWindow.hide();
               BeanModel screenModel = be.getData();
               tree.getStore().add(activityModel, screenModel, false);
               tree.setExpanded(activityModel, true);
               Info.display("Info", "Add screen " + screenModel.get("name") + " success.");
            }
         });
      } else {
         MessageBox.info("Error", "Please select a activity", null);
      }
   }

   /**
    * Creates the "edit" btn.
    * 
    * @return a edit buttion
    */
   private Component createEditBtn() {
      Button editBtn = new Button("Edit");
      editBtn.ensureDebugId(DebugId.ACTIVITY_EDIT_BTN);
      editBtn.setIcon(icon.edit());
      editBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            BeanModel selectedModel = tree.getSelectionModel().getSelectedItem();
            if (selectedModel != null && (selectedModel.getBean() instanceof Activity)) {
               editActivity(selectedModel);
            } else if(selectedModel != null && (selectedModel.getBean() instanceof Screen)) {
               editScreen(selectedModel);
            }
         }
      });
      return editBtn;
   }

   /**
    * Edits the activity.
    * 
    * @param selectedModel the selected model
    */
   protected void editActivity(BeanModel selectedModel) {
      final ActivityWindow activityWindow = new ActivityWindow(selectedModel);
      activityWindow.addListener(SubmitEvent.Submit, new SubmitListener() {
         @Override
         public void afterSubmit(SubmitEvent be) {
            activityWindow.hide();
            BeanModel activityModel = be.getData();
            tree.getStore().update(activityModel);
            Info.display("Info", "Edit activity " + activityModel.get("name") + " success.");
         }
      });
   }

   /**
    * Edits the screen.
    * 
    * @param selectedModel the selected model
    */
   protected void editScreen(BeanModel selectedModel) {
      final ScreenWindow screenWindow = new ScreenWindow((Screen)selectedModel.getBean());
      screenWindow.addListener(SubmitEvent.Submit, new SubmitListener(){
         @Override
         public void afterSubmit(SubmitEvent be) {
            screenWindow.hide();
            BeanModel screenModel = be.getData();
            tree.getStore().update(screenModel);
            Info.display("Info", "Edit screen " + screenModel.get("name") + " success.");
         }
      });
   }

   /**
    * Creates the "delete" btn.
    * 
    * @return a delete button
    */
   private Component createDeleteBtn() {
      Button deleteBtn = new Button("Delete");
      deleteBtn.ensureDebugId(DebugId.ACTIVITY_DELETE_BTN);
      deleteBtn.setIcon(icon.delete());
      deleteBtn.addSelectionListener(new ConfirmDeleteListener<ButtonEvent>() {
         @Override
         public void onDelete(ButtonEvent ce) {
            List<BeanModel> selectedModels = tree.getSelectionModel().getSelectedItems();
            for (BeanModel selectedModel : selectedModels) {
               if (selectedModel != null && (selectedModel.getBean() instanceof Activity)) {
                  ActivityBeanModelProxy.deleteActivity(selectedModel);
                  tree.getStore().remove(selectedModel);
                  Info.display("Info", "Delete activity " + selectedModel.get("name") + " success.");
               }else if(selectedModel != null && (selectedModel.getBean() instanceof Screen)) {
                  ScreenBeanModelProxy.deleteScreen(selectedModel);
                  tree.getStore().remove(selectedModel);
                  Info.display("Info", "Delete screen " + selectedModel.get("name") + " success.");
               }
            }
         }
      });
      return deleteBtn;
   }

   /**
    * Gets the tree.
    * 
    * @return the tree
    */
   public TreePanel<BeanModel> getTree() {
      return tree;
   }
   
   /**
    * Clear tree.
    */
   public void reRenderTree(String activitiesJSON, ImportWindow importWindow) {
      if (activitiesJSON == null || "".equals(activitiesJSON)) {
         MessageBox.info("Error", "Import fail, server is busy now and try again later.", null);
      }
      tree.getStore().removeAll();
      BeanModelDataBase.screenTable.clear();
      BeanModelDataBase.activityTable.clear();      
      List<Activity> activities = parseJson(activitiesJSON);
      for (Activity activity : activities) {
         BeanModel activityBeanModel = activity.getBeanModel();
         tree.getStore().add(activityBeanModel, false);
         BeanModelDataBase.activityTable.insert(activityBeanModel);
         for (Screen screen : activity.getScreens()) {
            BeanModel screenBeanModel = screen.getBeanModel();
            tree.getStore().add(activityBeanModel, screenBeanModel, false);
            BeanModelDataBase.screenTable.insert(screenBeanModel);
         }
      }
//      System.out.println(activitiesJSON);
      importWindow.hide();
   }
   
   @SuppressWarnings("finally")
   private List<Activity> parseJson(String importJsonString) {
      List<Activity> activities = new ArrayList<Activity>();
      try {
         JSONArray activityJSONArray = JSONParser.parse(importJsonString).isArray();
         for (int i = 0; i < activityJSONArray.size(); i++) {
            JSONObject activityJSON = activityJSONArray.get(i).isObject();
            Activity activity = new Activity();
            activity.setOid(IDUtil.nextID());
            activity.setName(activityJSON.get("name").isString().stringValue());
            JSONArray screenJSONArray = activityJSON.get("screens").isArray();
            for (int j = 0; j < screenJSONArray.size(); j++) {
               JSONObject screenJSON = screenJSONArray.get(j).isObject();
               Screen screen = new Screen();
               screen.setOid(IDUtil.nextID());
               activity.addScreen(screen);
               screen.setActivity(activity);
               screen.setColumnCount(Integer.parseInt(screenJSON.get("columnCount").isNumber().toString()));
               screen.setRowCount(Integer.parseInt(screenJSON.get("rowCount").toString()));
               screen.setName(screenJSON.get("name").isString().stringValue());
               JSONArray uiButtonJSONArray = screenJSON.get("buttons").isArray();
               for (int m = 0; m < uiButtonJSONArray.size(); m++) {
                  JSONObject uiButtonJSON = uiButtonJSONArray.get(m).isObject();
                  UIButton uiButton = new UIButton();
                  uiButton.setOid(IDUtil.nextID());
                  screen.addButton(uiButton);
                  String iconStr = ("null".equals(uiButtonJSON.get("icon").toString())) ? "" : uiButtonJSON.get("icon").isString().stringValue();
                  uiButton.setIcon(iconStr);
                  uiButton.setLabel(uiButtonJSON.get("label").isString().stringValue());
                  uiButton.setHeight(Integer.parseInt(uiButtonJSON.get("height").toString()));
                  uiButton.setWidth(Integer.parseInt(uiButtonJSON.get("width").toString()));
                  Position position = new Position();
                  position.setPosX(Integer.parseInt(uiButtonJSON.get("position").isObject().get("posX").toString()));
                  position.setPosY(Integer.parseInt(uiButtonJSON.get("position").isObject().get("posY").toString()));
                  uiButton.setPosition(position);
                  JSONObject uiCommandJSON = uiButtonJSON.get("uiCommand").isObject();
                  JSONValue targetDeviceMacroJSONValue = uiCommandJSON.get("targetDeviceMacro");
                  JSONValue deviceCommandJSONValue = uiCommandJSON.get("deviceCommand");
                  if (targetDeviceMacroJSONValue != null) {
                     JSONObject targetDeviceMacroJSON = targetDeviceMacroJSONValue.isObject();
                     DeviceMacroRef deviceMacroRef = new DeviceMacroRef();
                     DeviceMacro targetDeviceMacro = new DeviceMacro();
                     targetDeviceMacro.setOid(Integer.parseInt(targetDeviceMacroJSON.get("oid").toString()));
                     deviceMacroRef.setTargetDeviceMacro(targetDeviceMacro);
                     uiButton.setUiCommand(deviceMacroRef);
                  } else if (deviceCommandJSONValue != null) {
                     JSONObject deviceCommandJSON = deviceCommandJSONValue.isObject();
                     DeviceCommandRef deviceCommandRef = new DeviceCommandRef();
                     DeviceCommand deviceCommand = new DeviceCommand();
                     deviceCommand.setOid(Integer.parseInt(deviceCommandJSON.get("oid").toString()));
                     deviceCommandRef.setDeviceCommand(deviceCommand);
                     uiButton.setUiCommand(deviceCommandRef);
                  }
               }
            }
            activities.add(activity);
         }
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         return activities;
      }
   }
}

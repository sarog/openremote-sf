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
package org.openremote.modeler.client.widget.UIDesigner;

import java.util.List;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.widget.TreePanelBuilder;
import org.openremote.modeler.domain.Activity;
import org.openremote.modeler.domain.Screen;
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
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;

/**
 * The Class ActivityPanel.
 * 
 * @author handy.wang
 */
public class ActivityPanel extends ContentPanel {

   /** The tree. */
   private TreePanel<BeanModel> tree;
   
   private TabPanel screens;
   /** The icon. */
   private Icons icon = GWT.create(Icons.class);

   /**
    * Instantiates a new activity panel.
    */
   public ActivityPanel(TabPanel screens) {
      this.screens = screens;
      setHeading("Activity");
//      setIcon(icon.activityIcon());
      setLayout(new FitLayout());
      createMenu();
      createActivityTree();
   }

   /**
    * Creates the activity tree.
    */
   private void createActivityTree() {
      tree = TreePanelBuilder.buildActivityTree(screens);
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
      newButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            
         }
         
      });
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
         final ScreenWindow screenWindow = new ScreenWindow();
         screenWindow.addListener(SubmitEvent.Submit, new SubmitListener() {
            @Override
            public void afterSubmit(SubmitEvent be) {
               screenWindow.hide();
               BeanModel screenModel = be.getData();
               tree.getStore().add(activityModel, screenModel, false);
               tree.setExpanded(activityModel, true);
               Screen screen = screenModel.getBean();
               ScreenPanel screenPanel = new ScreenPanel(screen);
               screens.add(screenPanel);
               screens.setSelection(screenPanel);
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
      final ScreenWindow screenWindow = new ScreenWindow(selectedModel);
      screenWindow.addListener(SubmitEvent.Submit, new SubmitListener(){
         @Override
         public void afterSubmit(SubmitEvent be) {
            screenWindow.hide();
            BeanModel activityModel = be.getData();
            tree.getStore().update(activityModel);
            Info.display("Info", "Edit screen " + activityModel.get("name") + " success.");
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
      deleteBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            List<BeanModel> selectedModels = tree.getSelectionModel().getSelectedItems();
            for (BeanModel selectedModel : selectedModels) {
               if (selectedModel != null && (selectedModel.getBean() instanceof Activity)) {
                  tree.getStore().remove(selectedModel);
                  Info.display("Info", "Delete activity " + selectedModel.get("name") + " success.");
               }else if(selectedModel != null && (selectedModel.getBean() instanceof Screen)) {
                  tree.getStore().remove(selectedModel);
                  Info.display("Info", "Delete screen " + selectedModel.get("name") + " success.");
               }
            }
         }
      });
      return deleteBtn;
   }
}

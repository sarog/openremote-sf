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
import org.openremote.modeler.client.gxtextends.SelectionServiceExt;
import org.openremote.modeler.client.gxtextends.SourceSelectionChangeListenerExt;
import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.listener.ConfirmDeleteListener;
import org.openremote.modeler.client.listener.EditDelBtnSelectionListener;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.proxy.UtilsProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.TreePanelBuilder;
import org.openremote.modeler.domain.GroupRef;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.ScreenRef;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;

/**
 * The ProfilePanel .
 */
public class ProfilePanel extends ContentPanel {


   private TreePanel<BeanModel> panelTree;
   private Icons icon = GWT.create(Icons.class);
   private SelectionServiceExt<BeanModel> selectionService;

   /**
    * Instantiates a new profile panel.
    */
   public ProfilePanel(ScreenTab screenTab) {
      selectionService = new SelectionServiceExt<BeanModel>();
      setHeading("Panel");
      setIcon(icon.panelIcon());
      setLayout(new FitLayout());
      createMenu();
      createPanelTree(screenTab);
   }

   /**
    * Creates the menu.
    */
   private void createMenu() {
      ToolBar toolBar = new ToolBar();
      List<Button> editDelBtns = new ArrayList<Button>();
      toolBar.add(createNewBtn());

      Button editBtn = createEditBtn();
      editBtn.setEnabled(false);
      Button deleteBtn = createDeleteBtn();
      deleteBtn.setEnabled(false);

      toolBar.add(editBtn);
      toolBar.add(deleteBtn);
      editDelBtns.add(editBtn);
      editDelBtns.add(deleteBtn);
      selectionService.addListener(new EditDelBtnSelectionListener(editDelBtns) {
         @Override
         protected boolean isEditableAndDeletable(List<BeanModel> sels) {
            BeanModel selectModel = sels.get(0);
            if (selectModel != null) {
               return true;
            }
            return false;
         }
      });

      setTopComponent(toolBar);
   }

   /**
    * Creates the screen tree.
    */
   private void createPanelTree(ScreenTab screenTab) {
      panelTree = TreePanelBuilder.buildPanelTree(screenTab);
      selectionService.addListener(new SourceSelectionChangeListenerExt(panelTree.getSelectionModel()));
      selectionService.register(panelTree.getSelectionModel());
      LayoutContainer treeContainer = new LayoutContainer() {
         @Override
         protected void onRender(Element parent, int index) {
            super.onRender(parent, index);
            add(panelTree);
         }
      };
//      initTreeWithAutoSavedPanels();
      treeContainer.setScrollMode(Scroll.AUTO);
      treeContainer.setStyleAttribute("backgroundColor", "white");
      treeContainer.setBorders(false);
      add(treeContainer);

   }

   private void initTreeWithAutoSavedPanels() {
      UtilsProxy.loadPanelsFromSession(new AsyncSuccessCallback<List<Panel>>(){
         @Override
         public void onSuccess(List<Panel> panels) {
            if(panels.size() > 0) {
               panelTree.getStore().removeAll();
               BeanModelDataBase.panelTable.clear();
               for (Panel panel : panels) {
                  BeanModel panelBeanModel = panel.getBeanModel();
                  panelTree.getStore().add(panelBeanModel, false);
                  for (GroupRef groupRef : panel.getGroupRefs()) {
                     panelTree.getStore().add(panelBeanModel, groupRef.getBeanModel(), false);
                  }
                  BeanModelDataBase.panelTable.insert(panelBeanModel);
               }
               panelTree.expandAll();
            }
         }
         
      });
   }
   
   /**
    * Creates the new btn.
    * 
    * @return the button
    */
   private Button createNewBtn() {
      Button newButton = new Button("New");
      newButton.setIcon(icon.add());
      Menu newMenu = new Menu();
      newMenu.add(createNewPanelMenuItem());
      final MenuItem newCommandMemuItem = createNewGroupMenuItem();
      final MenuItem importCommandMemuItem = createNewScreenMenuItem();
      newMenu.add(newCommandMemuItem);
      newMenu.add(importCommandMemuItem);
      newButton.setMenu(newMenu);
      return newButton;
   }

   /**
    * Creates the edit btn.
    * 
    * @return the button
    */
   private Button createEditBtn() {
      Button editBtn = new Button("Edit");
      editBtn.setIcon(icon.edit());
      editBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            BeanModel panelModel = panelTree.getSelectionModel().getSelectedItem();
            if (panelModel != null && (panelModel.getBean() instanceof Panel)) {
               editPanel(panelModel);
            }
         }
      });
      return editBtn;
   }

   /**
    * Edits the group.
    * 
    * @param panelBeanModel
    *           the group bean model
    */
   private void editPanel(final BeanModel panelBeanModel) {
      final PanelWindow panelWindow = new PanelWindow(panelBeanModel);
      panelWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
         @Override
         public void afterSubmit(SubmitEvent be) {
            panelWindow.hide();
            Panel panel = be.<Panel> getData();
            panelTree.getStore().update(panel.getBeanModel());
            Info.display("Info", "Edit panel " + panel.getName() + " success.");
         }
         
      });
   }

   /**
    * Creates the delete btn.
    * 
    * @return the button
    */
   private Button createDeleteBtn() {
      Button deleteBtn = new Button("Delete");
      deleteBtn.setIcon(icon.delete());
      deleteBtn.addSelectionListener(new ConfirmDeleteListener<ButtonEvent>() {
         @Override
         public void onDelete(ButtonEvent ce) {
            List<BeanModel> selectedModels = panelTree.getSelectionModel().getSelectedItems();
            for (BeanModel selectedModel : selectedModels) {
               if (selectedModel != null && selectedModel.getBean() instanceof Panel) {
                  Panel panel = selectedModel.getBean();
                  BeanModelDataBase.panelTable.delete(selectedModel);
                  panelTree.getStore().remove(selectedModel);
                  for (GroupRef groupRef : panel.getGroupRefs()) {
                     groupRef.getGroup().releaseRef();
                     if (groupRef.getGroup().getRefCount() == 0) {
                        BeanModelDataBase.groupTable.delete(groupRef.getGroupId());
                     }
                     for (ScreenRef screenRef : groupRef.getGroup().getScreenRefs()) {
                        screenRef.getScreen().releaseRef();
                        if (screenRef.getScreen().getRefCount() == 0) {
                           BeanModelDataBase.screenTable.delete(screenRef.getScreenId());
                        }
                     }
                  }
                  Info.display("Info", "Delete panel " + selectedModel.get("name") + " success.");
               }
            }
         }
      });
      return deleteBtn;
   }

   private void afterCreatePanel(Panel panel) {
      BeanModel panelBeanModel = panel.getBeanModel();
      panelTree.getStore().add(panelBeanModel, false);
      for (GroupRef groupRef : panel.getGroupRefs()) {
         panelTree.getStore().add(panelBeanModel, groupRef.getBeanModel(), false);
         for (ScreenRef screenRef : groupRef.getGroup().getScreenRefs()) {
            panelTree.getStore().add(groupRef.getBeanModel(), screenRef.getBeanModel(), false);
         }
      }
      panelTree.setExpanded(panelBeanModel, true, true);
      panelTree.getSelectionModel().select(panelBeanModel, false);
      Info.display("Info", "Create Panel " + panel.getName() + " success.");
   }

   private MenuItem createNewPanelMenuItem() {
      MenuItem newPanelItem = new MenuItem("New Panel");
      newPanelItem.setIcon(icon.panelIcon());
      newPanelItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         public void componentSelected(MenuEvent ce) {
            final PanelWindow panelWindow = new PanelWindow();
            panelWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  panelWindow.hide();
                  afterCreatePanel(be.<Panel> getData());
               }

            });
            
         }
      });
      return newPanelItem;
   }
   
   private MenuItem createNewGroupMenuItem() {
      MenuItem newGroupItem = new MenuItem("New Group");
      newGroupItem.setIcon(icon.activityIcon());
      return newGroupItem;
   }
   
   private MenuItem createNewScreenMenuItem() {
      MenuItem newScreenItem = new MenuItem("New Screen");
      newScreenItem.setIcon(icon.screenIcon());
      return newScreenItem;
   }
}

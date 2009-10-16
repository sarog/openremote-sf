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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.gxtextends.SelectionServiceExt;
import org.openremote.modeler.client.gxtextends.SourceSelectionChangeListenerExt;
import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.listener.ConfirmDeleteListener;
import org.openremote.modeler.client.listener.EditDelBtnSelectionListener;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.widget.TreePanelBuilder;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.ScreenRef;
import org.openremote.modeler.domain.UIScreen;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.data.ChangeEventSupport;
import com.extjs.gxt.ui.client.data.ChangeListener;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.TreeStoreEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;

/**
 * The Class GroupPanel.
 */
public class GroupPanel extends ContentPanel {

   /** The group tree. */
   private TreePanel<BeanModel> groupTree;
   
   /** The icon. */
   private Icons icon = GWT.create(Icons.class);
   
   /** The selection service. */
   private SelectionServiceExt<BeanModel> selectionService;
   
   /** The change listener map. */
   private Map<BeanModel, ChangeListener> changeListenerMap = null;
   
   /**
    * Instantiates a new group panel.
    */
   public GroupPanel() {
      selectionService = new SelectionServiceExt<BeanModel>();
      setHeading("Group");
      setIcon(icon.activityIcon());
      setLayout(new FitLayout());
      createMenu();
      createScreenTree();
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
            if (selectModel.getBean() instanceof Group) {
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
   private void createScreenTree() {
      groupTree = TreePanelBuilder.buildGroupTree();
      
//      TreePanelDragSource source = new TreePanelDragSource(groupTree);
//      source.addDNDListener(new DNDListener() {
//         @Override
//         public void dragStart(DNDEvent e) {
//            ModelData sel = groupTree.getSelectionModel().getSelectedItem();
//            if (sel != null && sel == groupTree.getStore().getRootItems().get(0)) {
//               e.setCancelled(true);
//               e.getStatus().setStatus(false);
//               return;
//            }
//            super.dragStart(e);
//         }
//      });
//
//      TreePanelDropTarget target = new TreePanelDropTarget(groupTree);
//      target.setAllowSelfAsSource(true);
//      target.setFeedback(Feedback.BOTH);
      
      selectionService.addListener(new SourceSelectionChangeListenerExt(groupTree.getSelectionModel()));
      selectionService.register(groupTree.getSelectionModel());
      LayoutContainer treeContainer = new LayoutContainer() {
         @Override
         protected void onRender(Element parent, int index) {
            super.onRender(parent, index);
            addTreeStoreEventListener();
            add(groupTree);
         }
      };
//      initTreeWithAutoSavedJson(screenTab);
      treeContainer.setScrollMode(Scroll.AUTO);
      treeContainer.setStyleAttribute("backgroundColor", "white");
      treeContainer.setBorders(false);
      add(treeContainer);
   }
   
   /**
    * Creates the new btn.
    * 
    * @return the button
    */
   private Button createNewBtn() {
      Button newButton = new Button("New");
      newButton.setIcon(icon.add());
      newButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            createGroup();
         }
         
      });
      return newButton;
   }
   
   /**
    * Creates the group.
    */
   private void createGroup() {
      GroupWindow groupWindow = new GroupWindow();
      groupWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener(){
         @Override
         public void afterSubmit(SubmitEvent be) {
            afterCreateGroup(be.<Group> getData());
         }
         
      });
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
            BeanModel groupModel = groupTree.getSelectionModel().getSelectedItem();
            if (groupModel != null && (groupModel.getBean() instanceof Group)) {
               editGroup(groupModel);
            }
         }
      });
      return editBtn;
   }
   
   /**
    * Edits the group.
    * 
    * @param groupBeanModel the group bean model
    */
   private void editGroup(final BeanModel groupBeanModel) {
      GroupWindow groupWindow = new GroupWindow(groupBeanModel);
      groupWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener(){
         @Override
         public void afterSubmit(SubmitEvent be) {
            afterUpdateGroup(groupBeanModel, be.<Group> getData());
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
            List<BeanModel> selectedModels = groupTree.getSelectionModel().getSelectedItems();
            for (BeanModel selectedModel : selectedModels) {
               if (selectedModel != null && selectedModel.getBean() instanceof Group) {
                  BeanModelDataBase.groupTable.delete(selectedModel);
                  groupTree.getStore().remove(selectedModel);
                  Info.display("Info", "Delete group " + selectedModel.get("name") + " success.");
               } 
            }
         }
      });
      return deleteBtn;
   }

   /**
    * After create group.
    * 
    * @param group the group
    */
   private void afterCreateGroup(Group group) {
      BeanModel groupBeanModel = group.getBeanModel();
      groupTree.getStore().add(groupBeanModel, false);
      for (ScreenRef screenRef : group.getScreenRefs()) {
         groupTree.getStore().add(groupBeanModel, screenRef.getBeanModel(), false);
      }
      groupTree.setExpanded(groupBeanModel, true);
      Info.display("Info", "Create Group " + group.getName()+ " success.");
   }
   
   /**
    * After update group.
    * 
    * @param groupBeanModel the group bean model
    * @param group the group
    */
   private void afterUpdateGroup(final BeanModel groupBeanModel, Group group) {
      Group oldGroup = groupBeanModel.getBean();
      oldGroup.setName(group.getName());
      oldGroup.setScreenRefs(group.getScreenRefs());
      
      groupTree.getStore().removeAll(groupBeanModel);
      
      for (ScreenRef screenRef : group.getScreenRefs()) {
         groupTree.getStore().add(groupBeanModel, screenRef.getBeanModel(), false);
      }
      
      groupTree.getStore().update(groupBeanModel);
      groupTree.setExpanded(groupBeanModel, true);
      Info.display("Info", "Edit Group " + group.getName()+ " success.");
   }
   
   /**
    * Adds the tree store event listener.
    */
   private void addTreeStoreEventListener() {
      groupTree.getStore().addListener(Store.Add, new Listener<TreeStoreEvent<BeanModel>>() {
         public void handleEvent(TreeStoreEvent<BeanModel> be) {
            addChangeListenerToDragSource(be.getChildren());
         }
      });
      groupTree.getStore().addListener(Store.DataChanged, new Listener<TreeStoreEvent<BeanModel>>() {
         public void handleEvent(TreeStoreEvent<BeanModel> be) {
            addChangeListenerToDragSource(be.getChildren());
         }
      });
      groupTree.getStore().addListener(Store.Clear, new Listener<TreeStoreEvent<BeanModel>>() {
         public void handleEvent(TreeStoreEvent<BeanModel> be) {
            removeChangeListenerToDragSource(be.getChildren());
         }
      });
      groupTree.getStore().addListener(Store.Remove, new Listener<TreeStoreEvent<BeanModel>>() {
         public void handleEvent(TreeStoreEvent<BeanModel> be) {
            removeChangeListenerToDragSource(be.getChildren());
         }
      });
   }
   
   /**
    * Adds the change listener to drag source.
    * 
    * @param models the models
    */
   private void addChangeListenerToDragSource(List<BeanModel> models) {
      if (models == null) {
         return;
      }
      for (BeanModel beanModel : models) {
         if (beanModel.getBean() instanceof ScreenRef) {
            BeanModelDataBase.screenTable.addChangeListener(BeanModelDataBase
                  .getOriginalScreenRefBeanModelId(beanModel), getDragSourceBeanModelChangeListener(beanModel));
         }
      }
   }
   
   /**
    * Removes the change listener to drag source.
    * 
    * @param models the models
    */
   private void removeChangeListenerToDragSource(List<BeanModel> models) {
      if (models == null) {
         return;
      }
      for (BeanModel beanModel : models) {
         if (beanModel.getBean() instanceof ScreenRef) {
            BeanModelDataBase.screenTable.removeChangeListener(BeanModelDataBase
                  .getOriginalScreenRefBeanModelId(beanModel), getDragSourceBeanModelChangeListener(beanModel));
         }
         changeListenerMap.remove(beanModel);
      }
   }
   
   /**
    * Gets the drag source bean model change listener.
    * 
    * @param target the target
    * 
    * @return the drag source bean model change listener
    */
   private ChangeListener getDragSourceBeanModelChangeListener(final BeanModel target) {
      if (changeListenerMap == null) {
         changeListenerMap = new HashMap<BeanModel, ChangeListener>();
      }
      ChangeListener changeListener = changeListenerMap.get(target);
      if (changeListener == null) {
         changeListener = new ChangeListener() {
            public void modelChanged(ChangeEvent changeEvent) {
               if (changeEvent.getType() == ChangeEventSupport.Remove) {
                  if (target.getBean() instanceof ScreenRef) {
                     ScreenRef screenRef = (ScreenRef)target.getBean();
                     Group group = screenRef.getGroup();
                     group.deleteScreenRef(screenRef);
                  }
                  groupTree.getStore().remove(target);
               }
               if (changeEvent.getType() == ChangeEventSupport.Update) {
                  BeanModel source = (BeanModel) changeEvent.getItem();
                  if (source.getBean() instanceof UIScreen) {
                     UIScreen screen = (UIScreen) source.getBean();
                     ScreenRef screenRef = (ScreenRef) target.getBean();
                     screenRef.setScreen(screen);
                  } 
                  groupTree.getStore().update(target);
               }
            }
         };
         changeListenerMap.put(target, changeListener);
      }
      return changeListener;
   }
}

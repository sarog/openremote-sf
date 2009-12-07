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
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openremote.modeler.client.Constants;
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
import org.openremote.modeler.client.utils.BeanModelTable;
import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.client.widget.TreePanelBuilder;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.GroupRef;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.ScreenRef;
import org.openremote.modeler.domain.component.UITabbarItem;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.data.ChangeEventSupport;
import com.extjs.gxt.ui.client.data.ChangeListener;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.TreePanelDragSource;
import com.extjs.gxt.ui.client.dnd.TreePanelDropTarget;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.dnd.DND.Operation;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.TreeStoreEvent;
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
   private ScreenTab screenTab = null;
   private Map<BeanModel, ChangeListener> changeListenerMap = null;
   /**
    * Instantiates a new profile panel.
    */
   public ProfilePanel(ScreenTab screenTab) {
      this.screenTab = screenTab;
      selectionService = new SelectionServiceExt<BeanModel>();
      setHeading("Panel");
      setIcon(icon.panelIcon());
      setLayout(new FitLayout());
      createMenu();
      createPanelTree(screenTab);
      createDragSource4PanelTree();
      createDropTarget4PanelTree();
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
            addTreeStoreEventListener();
            add(panelTree);
         }
      };
      initTreeWithAutoSavedPanels();
      treeContainer.setScrollMode(Scroll.AUTO);
      treeContainer.setStyleAttribute("backgroundColor", "white");
      treeContainer.setBorders(false);
      add(treeContainer);

   }

   private void initTreeWithAutoSavedPanels() {
      UtilsProxy.loadMaxID(new AsyncSuccessCallback<Long>() {
         @Override
         public void onSuccess(Long maxID) {
            if (maxID > 0) {              // set the layout component's max id after refresh page.
               IDUtil.setCurrentID(maxID.longValue());
            }
         }
         
      });
      
      UtilsProxy.loadPanelsFromSession(new AsyncSuccessCallback<List<Panel>>() {
         @Override
         public void onSuccess(List<Panel> panels) {
            if (panels.size() > 0) {
               initModelDataBase(panels);
               panelTree.getStore().removeAll();
               for (Panel panel : panels) {
                  BeanModel panelBeanModel = panel.getBeanModel();
                  panelTree.getStore().add(panelBeanModel, false);
                  for (GroupRef groupRef : panel.getGroupRefs()) {
                     panelTree.getStore().add(panelBeanModel, groupRef.getBeanModel(), false);
                     for (ScreenRef screenRef : groupRef.getGroup().getScreenRefs()) {
                        panelTree.getStore().add(groupRef.getBeanModel(), screenRef.getBeanModel(), false);
                     }
                  }
               }
               panelTree.expandAll();
               BeanModelDataBase.screenTable.addInsertListener(Constants.SCREEN_TABLE_OID, new ChangeListener() {
                  public void modelChanged(ChangeEvent event) {
                     if (event.getType() == BeanModelTable.ADD) {
                        BeanModel beanModel = (BeanModel) event.getItem();
                        if (beanModel.getBean() instanceof Screen) {
                           ScreenTabItem screenTabItem = new ScreenTabItem((Screen) beanModel.getBean());
                           screenTab.add(screenTabItem);
                           screenTab.setSelection(screenTabItem);
                        }
                     }
                  }

               });
            }
         }
         
         private void initModelDataBase(Collection<Panel> panels) {
            BeanModelDataBase.panelTable.clear();
            BeanModelDataBase.groupTable.clear();
            BeanModelDataBase.screenTable.clear();
            Set<Group> groups = new LinkedHashSet<Group>();
            Set<Screen> screens = new LinkedHashSet<Screen>();
            for (Panel panel : panels) {
               List<GroupRef> groupRefs = panel.getGroupRefs();
               for (GroupRef groupRef : groupRefs) {
                  groups.add(groupRef.getGroup());
               }
               BeanModelDataBase.panelTable.insert(panel.getBeanModel());
            }

            for (Group group : groups) {
               List<ScreenRef> screenRefs = group.getScreenRefs();
               for (ScreenRef screenRef : screenRefs) {
                  screens.add(screenRef.getScreen());
                  BeanModelDataBase.screenTable.insert(screenRef.getScreen().getBeanModel());
               }
               BeanModelDataBase.groupTable.insert(group.getBeanModel());
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
      newMenu.add(createNewGroupMenuItem());
      newMenu.add(createNewScreenMenuItem());
      final MenuItem configTabbarItem = createConfigTabbarMenuItem();
      newMenu.add(configTabbarItem);
      newMenu.addListener(Events.BeforeShow, new Listener<MenuEvent>() {
         @Override
         public void handleEvent(MenuEvent be) {
            boolean enabled = false;
            BeanModel selectedBeanModel = panelTree.getSelectionModel().getSelectedItem();
            if (selectedBeanModel != null && !(selectedBeanModel.getBean() instanceof ScreenRef)) {
               enabled = true;
            }
            configTabbarItem.setEnabled(enabled);
         }
         
      });
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
            BeanModel selectedModel = panelTree.getSelectionModel().getSelectedItem();
            if (selectedModel != null) {
               if (selectedModel.getBean() instanceof Panel) {
                  editPanel(selectedModel);
               } else if (selectedModel.getBean() instanceof GroupRef) {
                  editGroup(selectedModel);
               } else if (selectedModel.getBean() instanceof ScreenRef) {
                  editScreen(selectedModel);
               }
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
   private void editGroup(final BeanModel groupRefBeanModel) {
      final GroupWizardWindow groupWindow = new GroupWizardWindow(groupRefBeanModel, true);
      groupWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
         @Override
         public void afterSubmit(SubmitEvent be) {
            groupWindow.hide();
            BeanModel groupRefModel = be.getData();
            GroupRef groupRef = groupRefModel.getBean();
            panelTree.getStore().removeAll(groupRefModel);
            for (ScreenRef screenRef : groupRef.getGroup().getScreenRefs()) {
               panelTree.getStore().add(groupRefModel, screenRef.getBeanModel(), false);
            }
            panelTree.getStore().update(groupRefModel);
            BeanModelDataBase.groupTable.update(groupRef.getGroup().getBeanModel());
            panelTree.setExpanded(groupRefModel, true);
            panelTree.getSelectionModel().select(groupRefModel, false);
            Info.display("Info", "Add Group " + groupRef.getGroup().getName() + " success.");
         }
      });
   }
   private void editScreen(final BeanModel panelBeanModel) {
      final ScreenWizard screenWizard = new ScreenWizard(screenTab, panelBeanModel, true);
      screenWizard.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
         @Override
         public void afterSubmit(SubmitEvent be) {
            ScreenRef screenRef = be.<ScreenRef> getData();
           /* BeanModel oldGroupBeanModel = screenWizard.getSelectedGroupRefModel();
            BeanModel newGroupBeanModel = panelTree.getStore().getParent(screenWizard.getSelectItem());
            if(!oldGroupBeanModel.equals(newGroupBeanModel)){
               panelTree.getStore().remove(screenRef.getBeanModel());
               panelTree.getStore().add(newGroupBeanModel, screenRef.getBeanModel(),false);
            }*/
            screenWizard.hide();
            panelTree.getStore().update(screenRef.getBeanModel());
            BeanModelDataBase.screenTable.update(screenRef.getScreen().getBeanModel());
            Info.display("Info", "Edit screen " + screenRef.getScreen().getName() + " success.");
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
               } else if (selectedModel != null && selectedModel.getBean() instanceof GroupRef) {
                  panelTree.getStore().remove(selectedModel);
                  GroupRef groupRef = selectedModel.getBean();
                  groupRef.getPanel().removeGroupRef(groupRef);
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
               } else if (selectedModel != null && selectedModel.getBean() instanceof ScreenRef) {
                  ScreenRef screenRef = (ScreenRef) selectedModel.getBean();
                  screenRef.getGroup().removeScreenRef(screenRef);
                  panelTree.getStore().remove(selectedModel);
                  screenRef.getScreen().releaseRef();
                  if (screenRef.getScreen().getRefCount() == 0) {
                     BeanModelDataBase.screenTable.delete(screenRef.getScreenId());
                  }
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
      newGroupItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         @Override
         public void componentSelected(MenuEvent ce) {
            Group group = new Group();
            group.setOid(IDUtil.nextID());
            GroupRef groupRef = new GroupRef(group);
            BeanModel selectedBeanModel = panelTree.getSelectionModel().getSelectedItem();
            if (selectedBeanModel != null && selectedBeanModel.getBean() instanceof Panel) {
               groupRef.setPanel((Panel) selectedBeanModel.getBean());
            }
            final GroupWizardWindow  groupWizardWindow = new GroupWizardWindow(groupRef.getBeanModel(), false);
            groupWizardWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  groupWizardWindow.hide();
                  BeanModel groupRefModel = be.getData();
                  GroupRef groupRef = groupRefModel.getBean();
                  panelTree.getStore().add(groupRef.getPanel().getBeanModel(), groupRefModel, false);
                  for (ScreenRef screenRef : groupRef.getGroup().getScreenRefs()) {
                     panelTree.getStore().add(groupRefModel, screenRef.getBeanModel(), false);
                  }
                  BeanModelDataBase.groupTable.insert(groupRef.getGroup().getBeanModel());
                  panelTree.setExpanded(groupRefModel, true);
                  panelTree.getSelectionModel().select(groupRefModel, false);
                  Info.display("Info", "Add Group " + groupRef.getGroup().getName() + " success.");
                  
               }
            });
         }
         
      });
      return newGroupItem;
   }
   
   private MenuItem createNewScreenMenuItem() {
      MenuItem newScreenItem = new MenuItem("New Screen");
      newScreenItem.setIcon(icon.screenIcon());
      newScreenItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         public void componentSelected(MenuEvent ce) {
            BeanModel selectItem = panelTree.getSelectionModel().getSelectedItem();
            final ScreenWizard screenWindow = new ScreenWizard(screenTab, selectItem);
            screenWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  screenWindow.hide();
                  ScreenRef screenRef = be.<ScreenRef>getData();
                  panelTree.getStore().add(screenWindow.getSelectedGroupRefModel(), screenRef.getBeanModel(), false);
                  panelTree.setExpanded(screenWindow.getSelectedGroupRefModel(), true);
                  panelTree.getSelectionModel().select(screenRef.getBeanModel(), false);
               }

            });

         }
      });
      return newScreenItem;
   }

   private MenuItem createConfigTabbarMenuItem() {
      MenuItem configTabbarItem = new MenuItem("Config tabbar");
      configTabbarItem.setIcon(icon.tabbarConfigIcon());
      configTabbarItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         @Override
         public void componentSelected(MenuEvent ce) {
            final BeanModel selectItem = panelTree.getSelectionModel().getSelectedItem();
            if (selectItem != null) {
               if (selectItem.getBean() instanceof Panel) {
                  final TabbarWindow tabbarWindow = new TabbarWindow(true, ((Panel) selectItem.getBean())
                        .getTabbarItems(), (Panel) selectItem.getBean());
                  tabbarWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
                     @Override
                     public void afterSubmit(SubmitEvent be) {
                        ((Panel) selectItem.getBean()).setTabbarItems(be.<List<UITabbarItem>> getData());
                        tabbarWindow.hide();
                     }
                  });
               } else if (selectItem.getBean() instanceof GroupRef) {
                  final Group group = ((GroupRef) selectItem.getBean()).getGroup();
                  final TabbarWindow tabbarWindow = new TabbarWindow(false, group.getTabbarItems(),
                        ((GroupRef) selectItem.getBean()).getPanel());
                  tabbarWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
                     @Override
                     public void afterSubmit(SubmitEvent be) {
                        group.setTabbarItems(be.<List<UITabbarItem>>getData());
                        tabbarWindow.hide();
                     }
                  });
               }
               panelTree.getStore().update(selectItem);
            }
         }
      });
      return configTabbarItem;
   }
   public TreePanel<BeanModel> getPanelTree() {
      return panelTree;
   }

   public void setPanelTree(TreePanel<BeanModel> panelTree) {
      this.panelTree = panelTree;
   }

   public ScreenTab getScreenTab() {
      return screenTab;
   }

   public void setScreenTab(ScreenTab screenTab) {
      this.screenTab = screenTab;
   }
   
   private void createDragSource4PanelTree() {
      new TreePanelDragSource(this.panelTree) {

         @Override
         protected void onDragStart(DNDEvent e) {
            if (panelTree.getSelectionModel().getSelectedItems().size() > 1) {
               e.setCancelled(true);
               return;
            }
            super.onDragStart(e);
         }

         @Override
         protected void onDragDrop(DNDEvent event) {
            return;
         }

         @Override
         public void setGroup(String group) {
            super.setGroup("REORDER_PANEL");
         }
      };
   }
   
   private void createDropTarget4PanelTree() {
      final TreePanelDropTarget target = new TreePanelDropTarget(panelTree) {
         @SuppressWarnings("unchecked")
         @Override
         protected void onDragDrop(DNDEvent event) {
            boolean successed = false;

            if (activeItem == null || event.getData() == null) {
               event.setCancelled(true);
               return;
            }

            BeanModel targetNode = (BeanModel) activeItem.getModel();
            BeanModel sourceNode = ((List<ModelData>) event.getData()).get(0).get("model");
            BeanModel sourceParentNode = (BeanModel) tree.getStore().getParent(sourceNode);
            BeanModel targetParentNode = (BeanModel) tree.getStore().getParent(targetNode);

            if (status == -1) { // append operation
               tree.getView().onDropChange(activeItem, false);
               if (sourceParentNode == targetNode) {
                  tree.getStore().remove(sourceNode);
                  handleAppendDrop(event, activeItem);
                  doAppend(sourceParentNode, sourceNode, targetNode);
                  successed = true;
               } else if (sourceNode.getBean() instanceof ScreenRef && targetNode.getBean() instanceof GroupRef
                     && inSamePanel(sourceParentNode, targetNode) && canMove(sourceNode, targetNode)) {
                  tree.getStore().remove(sourceNode);
                  handleAppendDrop(event, activeItem);
                  appendScreen(sourceParentNode, sourceNode, targetNode);
                  successed = true;
               }
            } else if (targetParentNode == sourceParentNode) { // insert operation
               tree.getStore().remove(sourceNode);
               handleInsertDrop(event, activeItem, status);
               doInsert(sourceParentNode, sourceNode, targetNode);
               successed = true;
            } else if (sourceNode.getBean() instanceof ScreenRef && targetParentNode.getBean() instanceof GroupRef
                  && inSamePanel(sourceParentNode, targetParentNode) && canMove(sourceNode, targetParentNode)) {
               tree.getStore().remove(sourceNode);
               handleInsertDrop(event, activeItem, status);
               reorderScreen(sourceParentNode, sourceNode, targetNode);
               successed = true;
            }
            if (!successed) {
               event.setCancelled(true);
            }
         }
         
         @Override
         public void setGroup(String group) {
            super.setGroup("REORDER_PANEL");
         }
         private boolean canMove(BeanModel scrRefBean, BeanModel groupRefBean) {
            GroupRef groupRef = groupRefBean.getBean();
            ScreenRef scrRef = scrRefBean.getBean();
            return -1 == groupRef.getGroup().getScreenRefs().indexOf(scrRef);
         }
         private boolean inSamePanel(BeanModel sourceGroupRef, BeanModel targetGroupRef) {
            BeanModel sourceGrandFatherNode = (BeanModel) tree.getStore().getParent(sourceGroupRef);
            BeanModel targetGrandFatherNode = (BeanModel) tree.getStore().getParent(targetGroupRef);
            return sourceGrandFatherNode.equals(targetGrandFatherNode);
         }
         private void appendScreen(BeanModel sourceGroupRefBeanModel, BeanModel sourceScreenRefBeanModel,
               BeanModel targetGroupRefBeanModel) {
            ScreenRef sourceScreenRef = sourceScreenRefBeanModel.getBean();
            GroupRef targetGroupRef = targetGroupRefBeanModel.getBean();
            targetGroupRef.getGroup().addScreenRef(sourceScreenRef);
            GroupRef sourceGroupRef = sourceGroupRefBeanModel.getBean();
            sourceGroupRef.getGroup().removeScreenRef(sourceScreenRef);
         }
         private void reorderScreen(BeanModel sourceGroupRefBean, BeanModel fromBean, BeanModel toBean) {
            Group sourceGroup = ((GroupRef) sourceGroupRefBean.getBean()).getGroup();
            Group targetGroup = sourceGroup;
            ScreenRef from = fromBean.getBean();
            ScreenRef to = toBean.getBean();
            if (!sourceGroup.equals(to.getGroup())) {
               targetGroup = to.getGroup();
            }
            sourceGroup.removeScreenRef(from);
            targetGroup.insertScreenRef(to, from);
         }

         private void doAppend(BeanModel sourceParent, BeanModel source, BeanModel target) {
            if (sourceParent.getBean() instanceof GroupRef && source.getBean() instanceof ScreenRef
                  && target.getBean() instanceof GroupRef) {
               appendScreen(sourceParent, source, target);
            } else if (sourceParent.getBean() instanceof Panel && source.getBean() instanceof GroupRef
                  && target.getBean() instanceof Panel) {
               appendGroup(sourceParent, source, target);
            }
         }

         private void doInsert(BeanModel sourceParent, BeanModel source, BeanModel insertTo) {
            if (sourceParent.getBean() instanceof GroupRef && source.getBean() instanceof ScreenRef
                  && insertTo.getBean() instanceof ScreenRef) {
               reorderScreen(sourceParent, source, insertTo);
            } else if (sourceParent.getBean() instanceof Panel && source.getBean() instanceof GroupRef
                  && insertTo.getBean() instanceof GroupRef) {
               reorderGroup(sourceParent, source, insertTo);
            }
         }

         private void appendGroup(BeanModel sourcePanelBean, BeanModel groupRefBean, BeanModel targetPanelBean) {
            Panel sourcePanel = sourcePanelBean.getBean();
            Panel targetpanel = targetPanelBean.getBean();
            GroupRef groupRef = groupRefBean.getBean();
            sourcePanel.removeGroupRef(groupRef);
            targetpanel.addGroupRef(groupRef);
         }
         private void reorderGroup(BeanModel sourcePanelBean, BeanModel fromBean, BeanModel toBean) {
            Panel panel = sourcePanelBean.getBean();
            GroupRef from = fromBean.getBean();
            panel.removeGroupRef(from);
            GroupRef to = toBean.getBean();
            panel.insertGroupRef(to, from);
         }
      };

      target.setAllowSelfAsSource(true);
      target.setOperation(Operation.MOVE);
      target.setAutoExpand(false);
      target.setFeedback(Feedback.BOTH);
      target.setAllowDropOnLeaf(true);
   }
   
   /**
    * Adds the tree store event listener.
    */
   private void addTreeStoreEventListener() {
      panelTree.getStore().addListener(Store.Add, new Listener<TreeStoreEvent<BeanModel>>() {
         public void handleEvent(TreeStoreEvent<BeanModel> be) {
            addChangeListenerToDragSource(be.getChildren());
         }
      });
      panelTree.getStore().addListener(Store.DataChanged, new Listener<TreeStoreEvent<BeanModel>>() {
         public void handleEvent(TreeStoreEvent<BeanModel> be) {
            addChangeListenerToDragSource(be.getChildren());
         }
      });
      panelTree.getStore().addListener(Store.Clear, new Listener<TreeStoreEvent<BeanModel>>() {
         public void handleEvent(TreeStoreEvent<BeanModel> be) {
            removeChangeListenerToDragSource(be.getChildren());
         }
      });
      panelTree.getStore().addListener(Store.Remove, new Listener<TreeStoreEvent<BeanModel>>() {
         public void handleEvent(TreeStoreEvent<BeanModel> be) {
            removeChangeListenerToDragSource(be.getChildren());
         }
      });
   }

   /**
    * Adds the change listener to drag source.
    * 
    * @param models
    *           the models
    */
   private void addChangeListenerToDragSource(List<BeanModel> models) {
      if (models == null) {
         return;
      }
      for (BeanModel beanModel : models) {
         if (beanModel.getBean() instanceof ScreenRef) {
            BeanModelDataBase.screenTable.addChangeListener(BeanModelDataBase
                  .getOriginalDesignerRefBeanModelId(beanModel), getDragSourceBeanModelChangeListener(beanModel));
         }
      }
   }

   /**
    * Removes the change listener to drag source.
    * 
    * @param models
    *           the models
    */
   private void removeChangeListenerToDragSource(List<BeanModel> models) {
      if (models == null) {
         return;
      }
      for (BeanModel beanModel : models) {
         if (beanModel.getBean() instanceof ScreenRef) {
            BeanModelDataBase.screenTable.removeChangeListener(BeanModelDataBase
                  .getOriginalDesignerRefBeanModelId(beanModel), getDragSourceBeanModelChangeListener(beanModel));
         }
         changeListenerMap.remove(beanModel);
      }
   }

   /**
    * Gets the drag source bean model change listener.
    * 
    * @param target
    *           the target
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
                     ScreenRef screenRef = (ScreenRef) target.getBean();
                     Group group = screenRef.getGroup();
                     group.removeScreenRef(screenRef);
                  }
                  panelTree.getStore().remove(target);
               }
               if (changeEvent.getType() == ChangeEventSupport.Update) {
                  BeanModel source = (BeanModel) changeEvent.getItem();
                  if (source.getBean() instanceof Screen) {
                     Screen screen = (Screen) source.getBean();
                     ScreenRef screenRef = (ScreenRef) target.getBean();
                     screenRef.setScreen(screen);
                  }
                  panelTree.getStore().update(target);
               }
            }
         };
         changeListenerMap.put(target, changeListener);
      }
      return changeListener;
   }
}
